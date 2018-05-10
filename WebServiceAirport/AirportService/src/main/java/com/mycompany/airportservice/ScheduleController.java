package com.mycompany.airportservice;


import com.mycompany.airportservice.exceptions.WebException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import org.json.JSONException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")

public class ScheduleController{
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    AirplaneRepository airplaneRepository;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping("/schedules")
    public List<Schedule> getAllSchedules() throws JSONException{
        List<Schedule> schedules = scheduleRepository.findAll();
        Schedule schedule;
        for (int i = 0; i < schedules.size(); i++){
            schedule = schedules.get(i);
            if(schedule.getWeatherId() != null){
                Weather weather = restTemplate.getForObject("http://app2:5000/locations/" + schedule.getWeatherId(), Weather.class);
                schedule.setWeather(weather);
            }
        }
        return schedules;
    }

    @GetMapping("/schedules/{id}")
    public Schedule getScheduleById(@PathVariable(value = "id") Long scheduleId) throws JSONException{
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("GET api/schedules/id", "- no schedule found"));
        if(schedule.getWeatherId() != null){
            Weather weather = restTemplate.getForObject("http://app2:5000/locations/" + schedule.getWeatherId(), Weather.class);
            schedule.setWeather(weather);
        }
        return schedule;
    }

    @PostMapping("/schedules")
    public Schedule createSchedule(@Valid @RequestBody Schedule schedule, HttpServletResponse response){
        if (schedule.getId() != null && scheduleRepository.existsById(schedule.getId())){
            throw new WebException("POST api/schedules", "- schedule exists with this id: " + schedule.getId());
        }
        if (schedule.getStartPort() == null || schedule.getDestination() == null ||schedule.getAirplaneId() == 0){
            throw new WebException("POST api/schedules", "- missing fields");
        }
        if(schedule.getWeather() != null && (schedule.getWeather().getCity() == null || schedule.getWeather().getTemperature() == null || schedule.getWeather().getDate() == null)){
            throw new WebException("PUT api/schedules", "- weather - missing fields");
        }
        airplaneRepository.findById(schedule.getAirplaneId()).orElseThrow(() -> new WebException("POST api/schedules", "- no such airplane with id: " + schedule.getAirplaneId()));
        Weather weather = null;
        if(schedule.getWeatherId() != null && schedule.getWeather() == null){
            weather = getWeather(schedule.getWeatherId());  
        }
        else if(schedule.getWeather() != null){
            weather = postWeather(schedule.getWeather());
        }
        response.setStatus(201);
        Schedule scheduleNew = scheduleRepository.save(schedule);
        response.addHeader("Location", "api/schedules/" + scheduleNew.getId());
        scheduleNew.setWeather(weather);
        return scheduleNew;
    }

    @PutMapping("/schedules/{id}")
    public Schedule updateSchedule(@PathVariable(value = "id") Long scheduleId, @Valid @RequestBody Schedule newSchedule, HttpServletResponse response){
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("PUT api/schedules/id", "- no such schedule"));
        if (newSchedule.getAirplaneId() == null || newSchedule.getStartPort() == null || newSchedule.getDestination() == null){
            throw new WebException("PUT api/schedules", "- missing fields");
        }
        if(newSchedule.getWeather() != null && (newSchedule.getWeather().getCity() == null || newSchedule.getWeather().getTemperature() == null || newSchedule.getWeather().getDate() == null)){
            throw new WebException("PUT api/schedules", "- weather - missing fields");
        }
        airplaneRepository.findById(schedule.getAirplaneId()).orElseThrow(() -> new WebException("PUT api/schedules", "- airplane with id: " + schedule.getAirplaneId() + " does not exist."));
        schedule.setStartPort(newSchedule.getStartPort());
        schedule.setDestination(newSchedule.getDestination());
        schedule.setAirplaneId(newSchedule.getAirplaneId());
        Weather weather = null;
        if(newSchedule.getWeatherId() != null && newSchedule.getWeather() == null){
            weather = getWeather(newSchedule.getWeatherId());		
        }
        else if(newSchedule.getWeatherId() != null && newSchedule.getWeather() != null){
            weather = putWeather(newSchedule.getWeatherId(), newSchedule.getWeather());
        }
        else if(newSchedule.getWeatherId() == null && newSchedule.getWeather() != null){
            weather = postWeather(newSchedule.getWeather());
        }
        if(weather != null){
            schedule.setWeatherId(weather.getId());
            schedule.setWeather(weather);
        }
        
        response.setStatus(201);
        response.addHeader("Location", "api/schedules/" + scheduleId);
        return scheduleRepository.save(schedule);
    }
    @PatchMapping("/schedules/{id}")
    public Schedule patchSchedule(@PathVariable(value = "id") Long scheduleId, @Valid @RequestBody Schedule newSchedule, HttpServletResponse response) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("PATCH api/sechedules/id", "- no such schedule"));
        if(newSchedule.getStartPort() != null){
            schedule.setStartPort(newSchedule.getStartPort());
        }
        if(newSchedule.getDestination() != null){
            schedule.setDestination(newSchedule.getDestination());
        }
        if(newSchedule.getAirplaneId() != null){
            schedule.setAirplaneId(newSchedule.getAirplaneId());
        }
        
        Weather weather = null;
        if(newSchedule.getWeatherId() != null && newSchedule.getWeather() == null){
            weather = getWeather(newSchedule.getWeatherId());
        }
        else if(newSchedule.getWeatherId() != null && newSchedule.getWeather() != null){
            weather = patchWeather(newSchedule.getWeatherId(), newSchedule.getWeather());
        }
        else if(newSchedule.getWeather() != null){
            weather = postWeather(schedule.getWeather());
        }
        
        if(weather != null){
            schedule.setWeatherId(weather.getId());
            schedule.setWeather(weather);
        }
        response.setStatus(202);
        response.addHeader("Location", "api/schedules/" + scheduleId);
        return scheduleRepository.save(schedule);
    }
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable(value = "id") Long scheduleId){
        Schedule post = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("DELETE api/schedules/id", "- airplane with id: " + scheduleId + " does not exist."));
        scheduleRepository.delete(post);
        return ResponseEntity.noContent().build();
    }
    
    private Weather getWeather(long id){
        RestTemplate restTemplate = new RestTemplate();
        Weather weather = null;
        try{
            weather = restTemplate.getForObject("http://app2:5000/locations/" + id, Weather.class);
        }
        catch(Exception e){
            throw new WebException("GET api/schedules", "- no weather found");
        }    
        return weather;
    }
    private Weather postWeather(Weather w){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Weather> requestBody = new HttpEntity<>(w, headers);	
        try{
            HttpEntity<String> msg = restTemplate.exchange("http://app2:5000/locations",HttpMethod.POST, requestBody, String.class);
            ResponseEntity<Weather[]> responseEntity = restTemplate.getForEntity("http://app2:5000/locations", Weather[].class);
            Weather[] weatherList = responseEntity.getBody();
            return weatherList[weatherList.length - 1];
        }
        catch(Exception e){
            throw new WebException("POST api/schedules", "- weather - " + e.getMessage());
        }
    }
    
    private Weather putWeather(Long id, Weather w){
        //Weather weather = null;
        try{
            restTemplate.getForObject("http://app2:5000/locations/" + id, Weather.class);
        }
        catch(Exception e){
            throw new WebException("PUT api/schedules/id", "- no weather found");
        }    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Weather> requestBody = new HttpEntity<>(w, headers);
        try{
            HttpEntity<String> msg = restTemplate.exchange("http://app2:5000/locations/" + id,HttpMethod.PUT, requestBody, String.class);
            ResponseEntity<Weather[]> responseEntity = restTemplate.getForEntity("http://app2:5000/locations", Weather[].class);
            Weather[] weatherList = responseEntity.getBody();
            return weatherList[id.intValue() - 1];
        }
        catch(Exception e){
                throw new WebException("PUT api/schedules/id", "- weather - " + e.getMessage());
        }
    }
    private Weather patchWeather(Long id, Weather w){
        Weather weather = null;
        try{
            weather = restTemplate.getForObject("http://app2:5000/locations/" + id, Weather.class);
            if(w.getTemperature() != null){
                weather.setTemperature(w.getTemperature());
            }
            if(w.getCity() != null){
                weather.setCity(w.getCity());
            }
            if(w.getDate() != null){
                weather.setDate(w.getDate());
            }
        }
        catch(Exception e){
            throw new WebException("PATCH api/schedules/id", "- no weather found");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Weather> requestBody = new HttpEntity<>(weather, headers);
        try{
            HttpEntity<String> msg = restTemplate.exchange("http://app2:5000/locations/" + id,HttpMethod.PUT, requestBody, String.class);
            ResponseEntity<Weather[]> responseEntity = restTemplate.getForEntity("http://app2:5000/locations", Weather[].class);
            Weather[] weatherList = responseEntity.getBody();
            return weatherList[id.intValue() - 1];
        }
        catch(Exception e){
                throw new WebException("PUT api/schedules/id", "- weather - " + e.getMessage());
        }
    }
}
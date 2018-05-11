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
    public List<Schedule> getAllSchedules(@RequestParam(value = "embed", required = false) String embed) throws JSONException{
        List<Schedule> schedules = scheduleRepository.findAll();
        Schedule schedule = null;
		
		// airplane;
        for (int i = 0; i < schedules.size(); i++){
            schedule = schedules.get(i);
			if(schedule.getAirplaneId() != null && embed != null && embed.contains("airplanes")){
				final Airplane airplane = airplaneRepository.findById(schedule.getAirplaneId()).orElseThrow(() -> new WebException("GET api/schedules", "- no such airplane with id: "  ));
				
				schedule.setAirplane(airplane);
			}
            if(schedule.getWeatherId() != null && embed != null && embed.contains("weathers")){
				try{
					Weather weather = restTemplate.getForObject("http://app2:5000/locations/" + schedule.getWeatherId(), Weather.class);
					schedule.setWeather(weather);
				}
				catch(Exception e){
					if(!e.getMessage().contains("I/O error")){
						throw new WebException("GET api/schedules", "- no weather found");
					}
					return null;
				}    
            }
        }
        return schedules;
    }

    @GetMapping("/schedules/{id}")
    public Schedule getScheduleById(@PathVariable(value = "id") Long scheduleId, @RequestParam(value = "embed", required = false) String embed) throws JSONException{
		//Airplane airplane;
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("GET api/schedules/id", "- no schedule found"));
		if(schedule.getAirplaneId() != null && embed != null && embed.contains("airplanes")){
			final Airplane airplane = airplaneRepository.findById(schedule.getAirplaneId()).orElseThrow(() -> new WebException("GET api/schedules", "- no such airplane with id: " + schedule.getAirplaneId()));
			schedule.setAirplane(airplane);
			}
        if(schedule.getWeatherId() != null && embed != null && embed.contains("weathers")){
			try{
				Weather weather = restTemplate.getForObject("http://app2:5000/locations/" + schedule.getWeatherId(), Weather.class);
				schedule.setWeather(weather);
			}
			catch(Exception e){
				if(!e.getMessage().contains("I/O error")){
					throw new WebException("GET api/schedules/id", "- no weather found");
				}
			return null;
        }    
        }
        return schedule;
    }

    @PostMapping("/schedules")
    public Schedule createSchedule(@Valid @RequestBody Schedule schedule, HttpServletResponse response, @RequestParam(value = "embed", required = false) String embed){
        if (schedule.getId() != null && scheduleRepository.existsById(schedule.getId())){
            throw new WebException("POST api/schedules", "- schedule exists with this id: " + schedule.getId());
        }
        if (schedule.getStartPort() == null || schedule.getDestination() == null ||schedule.getAirplaneId() == 0){
            throw new WebException("POST api/schedules", "- missing fields");
        }
        if(schedule.getWeather() != null && (schedule.getWeather().getCity() == null || schedule.getWeather().getTemperature() == null || schedule.getWeather().getDate() == null)){
            throw new WebException("PUT api/schedules", "- weather - missing fields");
        }
        final Airplane airplane = airplaneRepository.findById(schedule.getAirplaneId()).orElseThrow(() -> new WebException("POST api/schedules", "- no such airplane with id: " + schedule.getAirplaneId()));
		if (embed != null && embed.contains("airplanes")){
			schedule.setAirplane(airplane);
		}
        Weather weather = null;
        if(schedule.getWeatherId() != null && schedule.getWeather() == null){
            weather = getWeather(schedule.getWeatherId());  
        }
        else if(schedule.getWeather() != null){
            weather = postWeather(schedule.getWeather());
        }
		
		if(weather != null){
            schedule.setWeatherId(weather.getId());
			if (embed != null && embed.contains("weathers")){
				schedule.setWeather(weather);
			}
        }
        response.setStatus(201);
        Schedule scheduleNew = scheduleRepository.save(schedule);
        response.addHeader("Location", "api/schedules/" + scheduleNew.getId());
        scheduleNew.setWeather(weather);
        return scheduleNew;
    }

    @PutMapping("/schedules/{id}")
    public Schedule updateSchedule(@PathVariable(value = "id") Long scheduleId, @Valid @RequestBody Schedule newSchedule, HttpServletResponse response, @RequestParam(value = "embed", required = false) String embed){
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("PUT api/schedules/id", "- no such schedule"));
        if (newSchedule.getAirplaneId() == null || newSchedule.getStartPort() == null || newSchedule.getDestination() == null){
            throw new WebException("PUT api/schedules", "- missing fields");
        }
        if(newSchedule.getWeather() != null && (newSchedule.getWeather().getCity() == null || newSchedule.getWeather().getTemperature() == null || newSchedule.getWeather().getDate() == null)){
            throw new WebException("PUT api/schedules", "- weather - missing fields");
        }
        final Airplane airplane = airplaneRepository.findById(newSchedule.getAirplaneId()).orElseThrow(() -> new WebException("PUT api/schedules/id", "- no such airplane with id: " + newSchedule.getAirplaneId()));
		
        schedule.setStartPort(newSchedule.getStartPort());
        schedule.setDestination(newSchedule.getDestination());
        schedule.setAirplaneId(newSchedule.getAirplaneId());
		if (embed != null && embed.contains("airplanes")){
			schedule.setAirplane(airplane);
		}
		
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
			if (embed != null && embed.contains("weathers")){
				schedule.setWeather(weather);
			}
        }
        
        response.setStatus(201);
        response.addHeader("Location", "api/schedules/" + scheduleId);
        return scheduleRepository.save(schedule);
    }
    @PatchMapping("/schedules/{id}")
    public Schedule patchSchedule(@PathVariable(value = "id") Long scheduleId, @Valid @RequestBody Schedule newSchedule, HttpServletResponse response, @RequestParam(value = "embed", required = false) String embed) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new WebException("PATCH api/sechedules/id", "- no such schedule"));
        if(newSchedule.getStartPort() != null){
            schedule.setStartPort(newSchedule.getStartPort());
        }
        if(newSchedule.getDestination() != null){
            schedule.setDestination(newSchedule.getDestination());
        }
        if(newSchedule.getAirplaneId() != null){
			final Airplane airplane = airplaneRepository.findById(newSchedule.getAirplaneId()).orElseThrow(() -> new WebException("PUT api/schedules/id", "- no such airplane with id: " + newSchedule.getAirplaneId()));
			
            schedule.setAirplaneId(newSchedule.getAirplaneId());
			if (embed != null && embed.contains("airplanes")){
				schedule.setAirplane(airplane);
			}
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
			if (embed != null && embed.contains("weathers")){
				schedule.setWeather(weather);
			}
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
			if(!e.getMessage().contains("I/O error")){
				throw new WebException("GET api/schedules", "- no weather found");
			}
			return null;
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
			if(!e.getMessage().contains("I/O error")){
				throw new WebException("POST api/schedules", "- weather - " + e.getMessage());
			}
			return null;
        }    
    }
    
    private Weather putWeather(Long id, Weather w){
        Weather weather = null;
        try{
            weather = restTemplate.getForObject("http://app2:5000/locations/" + id, Weather.class);
        }
        catch(Exception e){
			if(!e.getMessage().contains("I/O error")){
				throw new WebException("PUT api/schedules/id", "- no weather found");
			}
			return null;
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
			if(!e.getMessage().contains("I/O error")){
				throw new WebException("PUT api/schedules/id", "- weather - " + e.getMessage());
			}
			return null;
        }   
    }
    private Weather patchWeather(Long id, Weather w){
        Weather weather = null;
        try{
            weather = restTemplate.getForObject("http://app2:5000/locations/" + id, Weather.class);
			if(weather == null){
				throw new WebException("PATCH api/schedules/id", "- no weather found");
			}
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
			if(!e.getMessage().contains("I/O error")){
				throw new WebException("PATCH api/schedules/id", "- no weather found");
			}
			return null;
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
			if(!e.getMessage().contains("I/O error")){
				throw new WebException("PUT api/schedules/id", "- weather - " + e.getMessage());
			}
			return null;
        }   
    }
}
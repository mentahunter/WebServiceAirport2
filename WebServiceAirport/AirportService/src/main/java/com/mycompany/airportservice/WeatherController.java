/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.airportservice;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.mycompany.airportservice.exceptions.WebException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
/**
 *
 * @author User
 */
@RestController
@RequestMapping("/api")
public class WeatherController {
    @Autowired
    ScheduleRepository scheduleRepository;
    private RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping(value = "/weathers")
    public  ResponseEntity<Weather[]> getAllWeathers(){
        
        return  restTemplate.getForEntity("http://app2:5000/locations", Weather[].class);
    }
    @GetMapping(value = "/weathers/{id}")
    public Weather getWeatherById(@PathVariable(value = "id") Long weatherId){
        Weather result = null;
        try{
            result = restTemplate.getForObject("http://app2:5000/locations/" + weatherId, Weather.class);
        }
        catch(Exception e){
            throw new WebException("GET api/weathers", "- no weather found");
        }
        return result;
    }
    
    @DeleteMapping(value = "/weathers/{id}")
    public ResponseEntity<?> deleteWeather(@PathVariable(value = "id") Long weatherId){
        restTemplate.delete("http://app2:5000/locations/" + weatherId);
        List<Schedule> schedules = scheduleRepository.findAll();
        Schedule schedule;
        for (int i = 0; i < schedules.size(); i++){
            schedule = schedules.get(i);
            if(schedule.getWeatherId() == weatherId){
                schedule.setWeatherId(null);
                schedule.setWeather(null);
				scheduleRepository.save(schedule);
            }
        }
        return ResponseEntity.noContent().build();
    }
}


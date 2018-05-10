package com.mycompany.airportservice;

import com.mycompany.airportservice.exceptions.WebException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AirplaneController{
    @Autowired
    private AirplaneRepository airplaneRepository;
    
    @GetMapping("/airplanes")
    public List<Airplane> getAllAirplanes(){
        return airplaneRepository.findAll();
    }
    
    @GetMapping("/airplanes/{id}")
    public Airplane getAirplaneById(@PathVariable(value = "id") Long airplaneId){
        return airplaneRepository.findById(airplaneId).orElseThrow(() -> new WebException("GET api/airplanes/id", "- no user found"));
    }

    @PostMapping("/airplanes")
    public Airplane createAirplane(@Valid @RequestBody Airplane airplane, HttpServletResponse response){
        if (airplaneRepository.existsById(airplane.getId())){
            throw new WebException("POST api/airplanes", "- airplane exists with this id: " + airplane.getId());
        }

        if (airplane.getModel() == null || airplane.getCapacity() <= 0){
            throw new WebException("POST api/airplanes", "- missing fields");
        }
        response.setStatus(201);
        Airplane airplaneNew = airplaneRepository.save(airplane);
        response.addHeader("Location", "api/airplanes/" + airplaneNew.getId());
        return airplaneNew;
    }
    
    @PutMapping("/airplanes/{id}")
    public Airplane updateAirplane(@PathVariable(value = "id") Long airplaneId, @Valid @RequestBody Airplane newAirplane, HttpServletResponse response){
        Airplane airplane = airplaneRepository.findById(airplaneId).orElseThrow(() -> new WebException("PUT api/airplanes/id", "- no such airplane"));
        if (newAirplane.getModel() == null || newAirplane.getCapacity() <= 0){
            throw new WebException("PUT api/schedules", "- missing fields");
        }
        airplane.setModel(newAirplane.getModel());
        airplane.setCapacity(newAirplane.getCapacity());
        response.setStatus(201);
        response.addHeader("Location", "api/airplanes/" + airplaneId);
        return airplaneRepository.save(airplane);
    }
    
    
    @PatchMapping("/airplanes/{id}")
    public Airplane patchAirplane(@PathVariable(value = "id") Long airplaneId, @Valid @RequestBody Airplane newAirplane, HttpServletResponse response) {
        Airplane airplane = airplaneRepository.findById(airplaneId).orElseThrow(() -> new WebException("PATCH api/airplanes/id", "- no such airplane"));
            if (newAirplane.getModel() == null && newAirplane.getCapacity() <= 0) {
                throw new WebException("PATCH api/airplanes", "empty path request body");
            }
            if(newAirplane.getModel() != null){
                airplane.setModel(newAirplane.getModel());    
            }
            if (newAirplane.getCapacity() > 0){
                airplane.setCapacity(newAirplane.getCapacity());
            }
            
            response.setStatus(202);
            response.addHeader("Location", "api/airplanes/" + airplaneId);
            return airplaneRepository.save(airplane);
    }
    
    @DeleteMapping("/airplanes/{id}")
    public ResponseEntity<?> deleteAirplane(@PathVariable(value = "id") Long airplaneId){
        Airplane airplane = airplaneRepository.findById(airplaneId).orElseThrow(() -> new WebException("DELETE api/airplanes/id", "- no such user"));
        airplaneRepository.delete(airplane);
        return ResponseEntity.noContent().build();
    }
}

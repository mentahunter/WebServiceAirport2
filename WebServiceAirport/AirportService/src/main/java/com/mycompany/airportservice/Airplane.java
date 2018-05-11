package com.mycompany.airportservice;
import javax.persistence.*;

@Entity
@Table(name = "airplanes")
public class Airplane{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String model;
    private Long capacity;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getModel(){
        return model;
    }

    public void setModel(String model){
        this.model = model;
    }

    public Long getCapacity(){
        return capacity;
    }

    public void setCapacity(Long capacity){
        this.capacity = capacity;
    }
}

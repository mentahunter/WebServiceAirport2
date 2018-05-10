package com.mycompany.airportservice;
import javax.persistence.*;

@Entity
@Table(name = "airplanes")
public class Airplane{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private String model;
    private long capacity;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getModel(){
        return model;
    }

    public void setModel(String model){
        this.model = model;
    }

    public long getCapacity(){
        return capacity;
    }

    public void setCapacity(long capacity){
        this.capacity = capacity;
    }
}

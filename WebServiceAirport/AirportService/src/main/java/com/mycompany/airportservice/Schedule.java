package com.mycompany.airportservice;
import javax.persistence.*;

@Entity
@Table(name = "schedules")
public class Schedule{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String startPort;
    private String destination;
    private Long airplaneId;
	private transient Airplane airplane;
    private Long weatherId;
    private transient  Weather weather;
    
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getStartPort(){
        return startPort;
    }

    public void setStartPort(String startPort){
        this.startPort = startPort;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }

    public Long getAirplaneId(){
        return airplaneId;
    }

    public void setAirplaneId(Long airplaneId){
        this.airplaneId = airplaneId;
    }

    public Long getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(Long weatherId) {
        this.weatherId = weatherId;
    }
	public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }
    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}

# WebServiceAirport2

Using OruServisas https://github.com/ggabriusee/OruServisas </br>

docker-compose up -d </br>
port: 80 </br>

# Old endpoints
Airplane endpoint: </br>
api/airplanes GET - no params </br>
api/airplanes/id GET - in url - id </br>
api/airplanes POST - in body - model(string), capacity(int) </br>
api/airplanes/id PUT - in url - id - in body - model(string), capacity(int) </br>
api/airplanes/id PATCH - in url - id - in body - model(string), capacity(int) </br>
api/airplanes/id DELETE - in url - id </br>

Schedules endpoint: 

api/schedules GET - no params </br>
api/schedules/id GET - in url - id </br>
api/schedules POST - in body - startPort(string), destination(string), airplaneId(int) </br>
api/schedules/id PUT - in url - id - in body - startPort(string), destination(string), airplaneId(int) </br>
api/schedules/id PATCH - in url - id - in body - startPort(string), destination(string), airplaneId(int) </br>
api/schedules/id DELETE - in url - id </br>

# New endpoints

Note: Weather contains - temperature(double), city(string), date(string) </br>

Weathers endpoint:

api/weathers GET - no params </br>
api/weathers/id GET - in url - id </br>
api/weathers/id DELETE - in url - id </br>

Schedules endpoint: 

api/schedules POST - in body - startPort(string), destination(string), airplaneId(int), weatherId(int) - assings exsiting Weather </br> 
api/schedules POST - in body - startPort(string), destination(string), airplaneId(int), weather(Weather) - creates new Weather </br> 
api/schedules/id PUT - in url - id - in body - startPort(string), destination(string), airplaneId(int), weatherId(int) - assings exsiting Weather </br>   
api/schedules/id PUT - in url - id - in body - startPort(string), destination(string), airplaneId(int), weather(Weather) - creates new Weather </br>
api/schedules/id PUT - in url - id - in body - startPort(string), destination(string), airplaneId(int), weatherId(int), weather(Weather) - puts assigned Weather </br>
api/schedules/id PATCH - in url - id - in body - startPort(string), destination(string), airplaneId(int), weatherId(int) - assings exsiting Weather </br>  
api/schedules/id PATCH - in url - id - in body - startPort(string), destination(string), airplaneId(int), weather(Weather) - creates new Weather </br>
api/schedules/id PATCH - in url - id - in body - startPort(string), destination(string), airplaneId(int), weatherId(int), weather(Weather) - puts assigned Weather </br>

# WebServiceAirport2

Using OruServisas https://github.com/ggabriusee/OruServisas

docker-compose up -d 
port: 80 

# Old endpoints
Airplane endpoint: 
api/airplanes GET - no params 
api/airplanes/id GET - in url - id 
api/airplanes POST - in body - model(string), capacity(int) 
api/airplanes/id PUT - in url - id - in body - model(string), capacity(int) 
api/airplanes/id PATCH - in url - id - in body - model(string), capacity(int) 
api/airplanes/id DELETE - in url - id 

Schedules endpoint: 

api/schedules GET - no params 
api/schedules/id GET - in url - id 
api/schedules POST - in body - startPort(string), destination(string), airplaneId(int) 
api/schedules/id PUT - in url - id - in body - startPort(string), destination(string), airplaneId(int) 
api/schedules/id PATCH - in url - id - in body - startPort(string), destination(string), airplaneId(int) 
api/schedules/id DELETE - in url - id 

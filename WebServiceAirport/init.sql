
DROP TABLE IF EXISTS airplanes;
CREATE TABLE airplanes(
id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
model VARCHAR(20),
capacity BIGINT
);

INSERT INTO airplanes (id, model, capacity) VALUES (1, "Boeing 747", 400);
INSERT INTO airplanes (id, model, capacity) VALUES (2, "Boeing 777", 314);
INSERT INTO airplanes (id, model, capacity) VALUES (3, "A321", 236);
INSERT INTO airplanes (id, model, capacity) VALUES (4, "A330", 335);

DROP TABLE IF EXISTS schedules;
CREATE TABLE schedules(
id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
start_port VARCHAR(20),
destination VARCHAR(20),
airplane_id INT NOT NULL,
weather_id INT DEFAULT NULL
);

INSERT INTO schedules(id, start_port, destination, airplane_id) VALUES (1, "Paris", "London", 1);
INSERT INTO schedules(id, start_port, destination, airplane_id) VALUES (2, "Tokyo", "New York", 3);
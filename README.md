By default, JAX-RS resource classes normally use a per-request lifecycle. This means that a new instance of the resource class is created for each incoming HTTP request. Because of this, it would not be reliable to store important application data inside normal instance variables in a resource class, as the data may not be shared properly between requests.

For this project, the rooms, sensors and sensor readings are stored in shared in-memory data structures. This makes the data available across multiple requests while the application is running. Since more than one request could happen at the same time, the project needs to manage these data structures carefully. Using safer structures such as ConcurrentHashMap helps reduce problems such as race conditions, where two requests try to read or update the same data at the same time.

Part 1.2 - Discovery Endpoint and HATEOAS

The discovery endpoint gives clients a simple starting point for the API. Instead of the client having to guess where the main resources are, the response gives useful links such as the rooms endpoint and the sensors endpoint.

This is related to HATEOAS, where an API response includes links that help the client navigate the system. This makes the API easier to understand because developers can discover available routes from the API itself. It also reduces the need to rely only on static documentation, which can become outdated if the API changes.

Part 2.1 - Returning Room IDs vs Full Room Objects

Returning only room IDs can be more efficient because the response is smaller. This is useful when there are many rooms and the client only needs a basic list. It reduces network usage and makes the response faster to transfer.

However, returning full room objects gives the client more useful information straight away, such as the room name, capacity and assigned sensors. This can reduce the number of extra requests the client has to make. In this project, returning full room objects is useful because the API is small and it helps the client see the room details clearly.

Part 2.2 - DELETE Idempotency

The DELETE operation should be treated as idempotent because sending the same DELETE request more than once should leave the system in the same final state. For example, if a room is successfully deleted the first time, the room no longer exists.

If the same DELETE request is sent again, the API may return a 404 because the room is already gone. Even though the response may be different, the final state is still the same: the room does not exist. This is why DELETE is considered idempotent.

Part 3.1 - @Consumes JSON

The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that the method expects the request body to be in JSON format. This is important for POST requests because the API needs to convert the JSON into a Java object.

If a client sends a different format, such as text/plain or application/xml, JAX-RS may reject the request because the content type does not match what the method accepts. In that case, the server can return an error such as 415 Unsupported Media Type. This helps protect the API from processing data in the wrong format.

Part 3.2 - Query Parameter Filtering

Using a query parameter such as /sensors?type=CO2 is a good design because filtering is an optional condition on a collection. The main resource is still sensors, but the query parameter narrows down the results.

Putting the type in the path, such as /sensors/type/CO2, is less flexible because it makes the URL structure more complicated. Query parameters are better for searching and filtering because more filters can be added later, such as status or room ID, without changing the main resource path.

Part 4.1 - Sub-Resource Locator Pattern

The sub-resource locator pattern is useful because it separates nested resource logic into smaller classes. In this project, sensor reading logic belongs under a specific sensor, so the path /sensors/{sensorId}/readings can be handled by a separate SensorReadingResource class.

This makes the API easier to maintain because all the sensor reading code does not need to be placed inside one large sensor resource class. It also keeps the code more organised and easier to understand, especially as the API grows.

Part 5.2 - Why 422 Instead of 404

HTTP 422 can be more accurate than 404 when the request URL exists and the JSON body is valid, but the data inside the request cannot be processed. For example, when creating a sensor, the endpoint exists, and the JSON may be correctly written, but the roomId inside the JSON may refer to a room that does not exist.

A 404 usually means the requested URL or resource was not found. In this case, the problem is not the endpoint itself, but a missing linked resource inside the request body. That is why 422 gives a clearer explanation of the problem.

Part 5.4 - Stack Trace Security Risks

Exposing Java stack traces to API users is a security risk because it can reveal internal details about the application. A stack trace may show class names, package names, file names, method names, and sometimes library information.

An attacker could use this information to understand how the system is built and look for weaknesses. For this reason, the API should return a safe and general error message for unexpected server errors, instead of exposing raw Java errors to the client.

Part 5.5 - Why Use Filters for Logging

JAX-RS filters are useful for logging because logging is a cross-cutting concern. This means it applies to many parts of the application, not just one resource method.

Using a filter avoids the need to write Logger.info() manually inside every endpoint. This keeps the resource classes cleaner and makes the logging more consistent. A request and response filter can automatically log the HTTP method, URI and response status for every request handled by the API.

# Smart Campus Sensor & Room Management API

## API Overview

This project is a RESTful API created for the Smart Campus system. The idea behind the system is to help manage rooms across a university campus and keep track of the sensors installed in those rooms.

The API allows users to create and view rooms, register sensors, filter sensors by type, add sensor readings, and view the reading history for each sensor. It also includes error handling so that the API returns clear JSON error messages instead of raw Java errors.

The project uses Java, Maven, JAX-RS and Jersey. It does not use a database. All data is stored in memory using Java collections such as maps and lists, which matches the coursework requirement.

The main resources in the API are:

- Rooms
- Sensors
- Sensor readings

The base URL for the API is:

```text
http://localhost:8080/api/v1


**The main endpoints are:**

GET     /api/v1
GET     /api/v1/rooms
POST    /api/v1/rooms
GET     /api/v1/rooms/{roomId}
DELETE  /api/v1/rooms/{roomId}

GET     /api/v1/sensors
GET     /api/v1/sensors?type=CO2
POST    /api/v1/sensors

GET     /api/v1/sensors/{sensorId}/readings
POST    /api/v1/sensors/{sensorId}/readings


**How to Build and Run**
1. Clone the repository
git clone YOUR_GITHUB_REPO_LINK_HERE

Then move into the project folder:

cd smart-campus-api
2. Build the project

Make sure Maven is installed, then run:

mvn clean package

This will compile the project and create a runnable .jar file inside the target folder.

3. Run the server
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
4. Open the API

Once the server is running, the API should be available at:

http://localhost:8080/api/v1

You can test it in a browser, Postman, or using curl commands from the terminal.

Sample curl Commands
1. Discovery endpoint

This checks that the API is running and returns basic information about the service.

curl -X GET http://localhost:8080/api/v1
2. Create a new room

This creates a room called Library Quiet Study.

curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":40}'
3. Get all rooms

This returns all rooms currently stored in the system.

curl -X GET http://localhost:8080/api/v1/rooms
4. Get a specific room

This returns the details for room LIB-301.

curl -X GET http://localhost:8080/api/v1/rooms/LIB-301
5. Create a new sensor

This creates a CO2 sensor and links it to the room LIB-301.

curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":420.5,"roomId":"LIB-301"}'
6. Get all sensors

This returns all registered sensors.

curl -X GET http://localhost:8080/api/v1/sensors
7. Filter sensors by type

This returns only sensors where the type is CO2.

curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
8. Add a sensor reading

This adds a new reading to the sensor CO2-001.

curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":430.2}'
9. Get sensor reading history

This returns the stored readings for sensor CO2-001.

curl -X GET http://localhost:8080/api/v1/sensors/CO2-001/readings
10. Try to delete a room with sensors

This should return an error if the room still has sensors assigned to it.

curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

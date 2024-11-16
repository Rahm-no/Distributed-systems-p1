# Distributed-systems-p1

# Resource Management System (RMI)

This project implements a distributed resource management system using Java RMI (Remote Method Invocation). The architecture consists of three servers, a middleware, and multiple clients:

- **Servers**: Manage the following resources:
  - **Flights**
  - **Cars**
  - **Rooms**
- **Middleware**: Acts as a mediator between clients and servers, handling client requests and routing them to the appropriate resource manager using RMI.

## Architecture Overview

### Middleware
The main component of the system is the `RMIMiddleware` class, which connects clients to the resource managers. Key features include:

- **Remote Stubs**: The middleware uses the following remote stubs to communicate with the resource managers:
  - `flight_RM_stub` for flights
  - `car_RM_stub` for cars
  - `room_RM_stub` for rooms
- **ConnectServer Method**: Connects to the servers and retrieves a reference to the `IResourceManager` object for performing operations like adding, deleting, and querying resources.
- **Client Functionality**: Implements client-facing functions, including:
  - Adding, deleting, and querying flights, cars, rooms, and customers.
  - A `bundle` function for multi-resource reservations.

### Bundle Function
The `bundle` function enables a client to reserve multiple resources (e.g., flights, cars, and rooms) in a single transaction. It follows these steps:

1. **Reserve Car** (if requested):
   - Calls `car_RM_stub.reserveCar`.
   - Rolls back if the reservation fails.
2. **Reserve Room** (if requested):
   - Calls `room_RM_stub.reserveRoom`.
   - Rolls back car reservation if room reservation fails.
3. **Reserve Flights**:
   - Iterates over the requested flight numbers, calling `flight_RM_stub.reserveFlight` for each.
   - Rolls back all previous reservations if any flight reservation fails.
4. **Finalization**:
   - Confirms the reservation if all steps succeed.

### Middleware Initialization
The `RMIMiddleware` is set up as follows:

1. Accepts command-line arguments for the hostnames of the three resource managers.
2. Creates and exports a remote object stub using `UnicastRemoteObject.exportObject`.
3. Registers the stub with an RMI registry on port `4010` using `Registry.rebind`.
4. Adds a shutdown hook to unbind the stub upon termination.

## Testing

The following scenario demonstrates the system's functionality:

### Scenario
1. Add resources:
   - Flights: `AddFlight,14,1,100`, `AddFlight,15,2,100`
   - Cars: `AddCars,Montreal,4,40`
   - Rooms: `AddRooms,Montreal,4,50`
   - Customers: `AddCustomerID,3`, `AddCustomerID,4`
2. Perform bundle reservations:
   - Incorrect bundle: `Bundle,3,14,15,Mon,0,1` → *"Bundle could not be reserved"*
   - Correct bundle: `Bundle,3,14,15,Montreal,0,1` → *"Bundle Reserved"*
3. Query resources to verify rollback and successful reservations:
   - Flights: `QueryFlight,15` → *Seats available: 1*
   - Flights: `QueryFlight,14` → *Seats available: 0*
   - Rooms: `QueryRooms,Montreal` → *Rooms available: 3*
   - Cars: `QueryCars,Montreal` → *Cars available: 4*
4. Test invalid commands:
   - `AddRooms,Toronto,3` → *"Invalid number of arguments"*
   - `QueryRoo,Toronto` → *"Command not found"*

## Commands

### Adding Resources
```bash
AddFlight,<FlightNumber>,<Seats>,<Price>
AddCars,<Location>,<Count>,<Price>
AddRooms,<Location>,<Count>,<Price>
AddCustomerID,<CustomerID>
```


# TCP Resource Management System

This project implements a distributed resource management system using TCP Sockets. It includes multiple clients, a middleware, and three resource managers (Flight, Room, Car). The middleware acts as the central processing unit, routing requests and ensuring consistency across the resource managers.

## Architecture

For TCP, we built our architecture based on the project description. Multiple clients send requests to the Middleware. According to the required command, the Middleware forwards the request to the appropriate resource manager (Flight, Room, Car). The resource manager processes the request and sends back a String response to the Middleware, which then forwards it to the client.

Initially, we separated customer management from the resource managers and handled it entirely at the middleware level. However, this approach led to data inconsistency problems since the resource managers also needed customer information in their storage system. To resolve this, we decided that customer-related updates would be handled by the Middleware, which calls all three resource managers to ensure consistency.

![TCP Architecture Diagram](image.png)

## Java Serialization

The `Request` class is a Java serializable class designed for TCP communication. It encapsulates:
- A command represented as a String.
- Parameters represented as an array of Objects.

This class allows seamless serialization and deserialization of requests for communication between the clients, middleware, and resource managers.

## Roadmap

1. **Client-Side Input**:
   - The user's input is parsed by the client into a `Request` object.
   - The `Request` is serialized and sent to the Middleware using an `ObjectOutputStream`.

2. **Middleware Logic**:
   - The Middleware deserializes the `Request` and processes its `command`.
   - Based on the command:
     - **Resource Manager Commands**: The request is forwarded to the appropriate manager (Flight, Room, Car).
     - **Customer Commands**: The request is sent to all three resource managers for consistency. If all responses are identical, one response is returned to the client; otherwise, all responses are returned to identify discrepancies.
     - **Help Command**: A list of valid commands is sent as a response to the client.
     - **Bundle Command**: Handled between the Middleware and resource managers. If any reservation step fails, the Middleware rolls back the previous reservations using an `undoReservation` function.

3. **Error Handling**:
   - Invalid commands: *"Command does not exist"* error.
   - Missing parameters: *"Missing arguments, Expected: command, <parameter1>, <parameter2>, ..."* error.
   - Client disconnection: The Middleware detects and retries connection.
   - Resource manager disconnection: The Middleware notifies the client and waits for reconnection.

## Concurrency Testing

To test concurrency:
- We introduced a delay of 2ms in one client (`ClientSocket_delayed`) using `Thread.sleep(2000)`.
- Two clients were executed simultaneously to simulate concurrent operations.
- Example scenario:
  - If multiple cars are added at a location and one client reserves a car, the other client sees an updated availability (decreased by 1).

## Error Handling Summary

- **Invalid Command**: Returns an error message indicating that the command does not exist.
- **Missing Parameters**: Returns a message specifying the required parameters.
- **Disconnected Client**: The middleware detects the disconnection and attempts to reconnect.
- **Disconnected Resource Manager**: The middleware informs the client to try again later while listening for reconnection.

## Features

- **Transaction Support**: Ensures atomic operations, especially in Bundle reservations.
- **Scalability**: Supports multiple clients and resource managers.
- **Data Consistency**: Maintains synchronized customer states across all resource managers.

---

This README provides an overview of the TCP part of the project. Let me know if further refinements are needed!

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

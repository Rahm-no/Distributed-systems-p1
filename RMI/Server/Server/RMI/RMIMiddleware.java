// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware implements IResourceManager {
	private static String s_serverName = "Server";
	// TODO: ADD YOUR GROUP NUMBER TO COMPLETE
	private static String s_rmiPrefix = "group_10_";
	private static String hostName_1;
	private static String hostName_2;
	private static String hostName_3;
    private IResourceManager flight_RM_stub;
	private IResourceManager car_RM_stub;
	private IResourceManager room_RM_stub;

	public RMIMiddleware(String hostName_1, String hostName_2, String hostName_3) throws RemoteException, NotBoundException {
		this.hostName_1 = hostName_1;
		this.hostName_2 = hostName_2;
		this.hostName_3 = hostName_3;

		// Get access to the proxy of hostName_1_flights_resource_manager
		Registry registry1 = LocateRegistry.getRegistry(hostName_1, 3010);
		flight_RM_stub = (IResourceManager) registry1.lookup("group_10_Flights");

		// Get access to the proxy of hostName_2_cars_resource_manager
		Registry registry2 = LocateRegistry.getRegistry(hostName_2, 3010);
		car_RM_stub = (IResourceManager) registry2.lookup("group_10_Cars");

		// Get access to the proxy of hostName_3_rooms_resource_manager
		Registry registry3 = LocateRegistry.getRegistry(hostName_3, 3010);
		room_RM_stub = (IResourceManager) registry3.lookup("group_10_Rooms");
	}
	
	//Methods for Flights
	public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException  {
		return flight_RM_stub.addFlight(flightNum, flightSeats, flightPrice);
	}
	
	
	public boolean addCars(String location, int numCars, int price) throws RemoteException {
		return car_RM_stub.addCars(location, numCars, price);
		
	}
	
	/**
	 * Add room at a location.
	 */
	public boolean addRooms(String location, int numRooms, int price) throws RemoteException
	{
		return room_RM_stub.addRooms(location, numRooms,  price);
		
	}

	/**
	 * Add customer.
	 */
	public int newCustomer() throws RemoteException
	{
		return room_RM_stub.newCustomer();
	}

	/**
	 * Add customer with id.

	 */
	public boolean newCustomer(int cid) throws RemoteException
	{
		return room_RM_stub.newCustomer(cid);
	}

	/**
	 * Delete the flight.
	 *
	 */
	public boolean deleteFlight(int flightNum) throws RemoteException
	{
		return flight_RM_stub.deleteFlight(flightNum);
	}

	/**
	 * Delete all cars at a location.
	 *
	 *
	 */
	public boolean deleteCars(String location)  throws RemoteException
	{
		
		return flight_RM_stub.deleteCars(location);
	}

	/**
	 * Delete all rooms at a location.
	 *
	 *
	 */
	public boolean deleteRooms(String location) throws RemoteException
	{
		return room_RM_stub.deleteRooms(location);
	}

	/**
	 * Delete a customer and associated reservations.
	
	 */
	public boolean deleteCustomer(int customerID) throws RemoteException
	
	{
		return room_RM_stub.deleteCustomer(customerID);
	}

	/**
	 * Query the status of a flight.
	 *
	 * @return Number of empty seats
	 */
	public int queryFlight(int flightNumber) throws RemoteException
	{
		return flight_RM_stub.queryFlight(flightNumber);
	}
	/**
	 * Query the status of a car location.
	 *
	 * @return Number of available cars at this location
	 */
	public int queryCars(String location) throws RemoteException
	
	{
		return car_RM_stub.queryCars( location);
	}

	/**
	 * Query the status of a room location.
	 *
	 * @return Number of available rooms at this location
	 */
	public int queryRooms(String location) throws RemoteException
	{
		return room_RM_stub.queryRooms(location);
	}

	/**
	 * Query the customer reservations.
	 *
	 * @return A formatted bill for the customer
	 */
	public String queryCustomerInfo(int customerID) throws RemoteException
	
	{
		return room_RM_stub.queryCustomerInfo(customerID);
		
	}

	/**
	 * Query the status of a flight.
	 *
	 * @return Price of a seat in this flight
	 */
	public int queryFlightPrice(int flightNumber) throws RemoteException
	{
		return flight_RM_stub.queryFlightPrice( flightNumber);
	}
	/**
	 * Query the status of a car location.
	 *
	 * @return Price of car
	 */
	public int queryCarsPrice(String location) throws RemoteException
	{
		return car_RM_stub.queryCarsPrice( location);
	}
	

	/**
	 * Query the status of a room location.
	 *
	 * @return Price of a room
	 */
	public int queryRoomsPrice(String location) throws RemoteException
	{
		return room_RM_stub.queryRoomsPrice( location);
	}

	/**
	 * Reserve a seat on this flight.
	 *
	 * @return Success
	 */
	public boolean reserveFlight(int customerID, int flightNumber) throws RemoteException
	{
		return flight_RM_stub.reserveFlight( customerID,  flightNumber);
	}

	/**
	 * Reserve a car at this location.
	 *
	 * @return Success
	 */
	public boolean reserveCar(int customerID, String location) throws RemoteException
	{
		return car_RM_stub.reserveCar( customerID,  location);
	}

	/**
	 * Reserve a room at this location.
	 *
	 * @return Success
	 */
	public boolean reserveRoom(int customerID, String location) throws RemoteException
	{
		return room_RM_stub.reserveRoom( customerID,  location);
		
	}
	

	/**
	 * Reserve a bundle for the trip.
	 *
	 * @return Success
	 */
	public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException
	{
	      return false;
	}

	/**
	 * Convenience for probing the resource manager.
	 *
	 * @return Name
	 */
	public String getName() throws RemoteException
	{
		return room_RM_stub.getName();
	}


	public static void main(String args[])
	{
		if (args.length > 2)
		{
			hostName_1 = args[0];
                        hostName_2 = args[1];
			hostName_3 = args[2];
                        
		}
			
		// Create the RMI server entry
		try {
			// Create a new Server object
			RMIMiddleware middleware_server = new RMIMiddleware(hostName_1, hostName_2, hostName_3);

			// Dynamically generate the stub (client proxy)
			IResourceManager middleware_stub = (IResourceManager)UnicastRemoteObject.exportObject(middleware_server, 0);

			// Bind the remote object's stub in the registry; adjust port if appropriate
			Registry l_registry;
			try {
				l_registry = LocateRegistry.createRegistry(3010);
			} catch (RemoteException e) {
				l_registry = LocateRegistry.getRegistry(3010);
			}
			final Registry registry = l_registry;
			registry.rebind(s_rmiPrefix + s_serverName, middleware_stub );
                
                                


			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						registry.unbind(s_rmiPrefix + s_serverName);
						System.out.println("'" + s_serverName + "' resource manager unbound");
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});      
                                 
			System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

	}


}

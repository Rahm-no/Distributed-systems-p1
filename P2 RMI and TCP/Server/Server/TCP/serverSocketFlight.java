package TCP;
import java.io.EOFException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import Server.Common.*;

public class serverSocketFlight {

    private String processRequest(Request readRequest, ResourceManager flightRM) {
        String command = readRequest.getCommand();
        String response = ""; // Output of processRequest
        boolean out;


        if (command.equals("AddFlight")) {
            Object[] parameters = readRequest.getParameters();
            int flightNum = Integer.parseInt((String) parameters[0]);
            int flightSeats = Integer.parseInt((String) parameters[1]);
            int flightPrice = Integer.parseInt((String) parameters[2]);


            response = flightRM.addFlight(flightNum, flightSeats, flightPrice);
        } else if (command.equals("GetCustomerID")) {
            int customerID = flightRM.newCustomer();
            response = "Your CustomerID is " + customerID;
        } else if (command.equals("AddCustomerID")) {
            Object[] parameters = readRequest.getParameters();
            int customerID = Integer.parseInt((String) parameters[0]);
             out = flightRM.newCustomer(customerID);
             if (out){
                 response = "New Customer "+ customerID +" is added";
             }
             else{
                 response="newCustomer(" + customerID + ") failed--customer already exists";
             }

        } else if (command.equals("DeleteCustomer")) {
            Object[] parameters = readRequest.getParameters();
            int customerID = Integer.parseInt((String) parameters[0]);
            out = flightRM.deleteCustomer(customerID);
            if (out){
                response = "deleteCustomer(" + customerID + ") succeeded";
            }
            else{
                response="deleteCustomer(" + customerID + ") failed--customer doesn't exist";
            }




        } else if (command.equals("DeleteFlight")) {
            Object[] parameters = readRequest.getParameters();
            int flightNum = Integer.parseInt((String) parameters[0]);
            response = flightRM.deleteFlight(flightNum);



        } else if (command.equals("QueryFlight")) {
            Object[] parameters = readRequest.getParameters();
            int flightNum = Integer.parseInt((String) parameters[0]);
            int numberOfSeats = flightRM.queryFlight(flightNum);
            response = "Number of available seats for this flight Number " + flightNum + " is " + numberOfSeats;
        } else if (command.equals("QueryCustomer")) {
            // Add your logic here for the "QueryCustomer" command
        } else if (command.equals("QueryFlightPrice")) {
            Object[] parameters = readRequest.getParameters();
            int flightNum = Integer.parseInt((String) parameters[0]);
            int flightPrice = flightRM.queryFlightPrice(flightNum);
            response = "The price of a seat in flight " + flightNum + " is " + flightPrice;
        } else if (command.equals("ReserveFlight")) {
            Object[] parameters = readRequest.getParameters();
            int customerID = Integer.parseInt((String) parameters[0]);
            int flightNum = Integer.parseInt((String) parameters[1]);

            response = flightRM.reserveFlight( customerID, flightNum);




        } else if (command.equals("Bundle")) {
            // Add your logic here for the "Bundle" command
            }
         else {
        // Command not recognized
        response = "Command incorrect. Please try again using the user guide.";
    }

        return response;
    }
    private void handleClient(Socket socket, ResourceManager flightRM) {
        new Thread(() -> {



            try {

                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Received request from Middleware:");
                // Receive the middleware request
                while (true) {
                    try {


                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Request readRequest = (Request) ois.readObject();

                        String response = processRequest(readRequest, flightRM);

                        // Send server response back to client
                        outToClient.println(response);
                    } catch (EOFException e) {
                        // Client closed the connection
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Flight_ResourceManager server started on port " + port);
            ResourceManager flightRM = new ResourceManager("Flight");
            while (true) {
                Socket socket = serverSocket.accept();
                handleClient(socket,flightRM);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        serverSocketFlight serverFlight = new serverSocketFlight();
        serverFlight.startServer(3010);
    }
}

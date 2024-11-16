package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMiddleware {

    private String hostName1;

    public TCPMiddleware(String hostName1) {
        this.hostName1 = hostName1;
    }

    public void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Middleware is running and waiting for connections...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");

                handleClient(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        new Thread(() -> {
            try {
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);

                // Read client request
                System.out.println("Received request from Client:");

                // Receive the client request
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    try {
                        Request clientRequest = (Request) ois.readObject();

                        // Forward the client request to hostName1

                        System.out.println("Forwarding request to the appropriate resource manager");
                        String response = forwardRequestToServer(hostName1, clientRequest);

                        // Send the server response back to the client
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

    private String forwardRequestToServer(String serverHost, Request request) {
        String response = null;

        try {
            // Connect to the server (e.g., hostName1)
            try (Socket serverSocket = new Socket(serverHost, 3010);
                 ObjectOutputStream oos = new ObjectOutputStream(serverSocket.getOutputStream());
                 BufferedReader inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {

                // Send the client request to the server
                oos.writeObject(request);

                // Receive the server response as a string
                response = inFromServer.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java TCPMiddleware <hostName1>");
            System.exit(1);
        }

        String hostName1 = args[0];

        TCPMiddleware middlewareServer = new TCPMiddleware(hostName1);
        middlewareServer.startServer(3010);
    }
}

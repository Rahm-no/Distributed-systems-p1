package TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientSocket {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java ClientSocket <serverName>");
            System.exit(1);
        }

        String serverName = args[0];
        Socket socket = new Socket(serverName, 3010);

        PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected to the server");

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        while (true) {
            String readerInput = bufferedReader.readLine();
            if (readerInput.equals("quit"))
                break;

            Request request = parseRequest(readerInput);
            oos.writeObject(request);
            oos.flush(); // Flush the ObjectOutputStream to send the request immediately

            String response = inFromServer.readLine();
            System.out.println("result: " + response);
        }

        // Close the ObjectOutputStream and the socket when done
        socket.close();
    }

    private static Request parseRequest(String input) {
        String[] parts = input.split(",", 2);
        String command = parts[0].trim();
        String parameters = parts[1].trim();

        String[] parameterArray = parameters.split(",");
        Object[] parameterObjects = new Object[parameterArray.length];
        for (int i = 0; i < parameterArray.length; i++) {
            parameterObjects[i] = parameterArray[i].trim();
        }

        return new Request(command, parameterObjects);
    }
}

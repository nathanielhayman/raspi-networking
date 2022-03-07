import java.net.*;
import java.util.Scanner;
import java.io.*;

public class NetworkClient implements Runnable {
    private int port;
    private Socket client;

    volatile boolean keepRunning = true;

    public NetworkClient(int port) {
        this.port = port;
    }

    public void run() {
        while (keepRunning) {
            try {
                String message = readInput();
                System.out.println("Message from server: " + message);
            } catch (IOException e) {
                System.out.println("Unhandled IOException: " + e);
            }
        }
    } 

    public void connect(String serverName) {
        try {
            this.client = new Socket(serverName, this.port);
            System.out.println("Connected to the server!");
        } catch (UnknownHostException e) {
            System.out.println("Could not find the host: " + e);
        } catch (IOException e) {
            System.out.println("Uknown IO Exception: " + e);
        }
    }

    public boolean sendMessage(String message) {
        if (this.client == null)
            return false;
        
        try {
            DataOutputStream stream = new DataOutputStream(this.client.getOutputStream());
            stream.writeUTF(message);
            stream.flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String readInput() throws IOException {
        DataInputStream stream = new DataInputStream(this.client.getInputStream());

        return stream.readUTF();
    } 

    public boolean terminate() {
        try {
            this.client.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        NetworkClient client = new NetworkClient(port);
        
        client.connect(serverName);

        Thread clientThread = new Thread(client);

        clientThread.start();
        boolean keepAlive = true;
        Scanner input = new Scanner(System.in);

        while (keepAlive) {
            System.out.print("\n» ");
            while (!input.hasNextLine());
            
            switch (input.nextLine()) {
                case "exit":
                    keepAlive = false;
                    break;
                default:
                    client.sendMessage(input.nextLine());
            }
        } 

        input.close();
        clientThread.interrupt();
        client.terminate();
    }
}
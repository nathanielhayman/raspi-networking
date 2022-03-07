import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class NetworkServer implements Runnable {
    private int port;
    private ServerSocket server;
    private HashMap<String, ClientSocket> threads;

    volatile boolean keepRunning = true;

    public NetworkServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
            this.server = new ServerSocket(this.port);

            System.out.println("Started server!");

            while (keepRunning) {
                Socket s = this.server.accept();
                
                ClientSocket thread = new ClientSocket(s);
                
                this.threads.put(thread.getIP(), thread);
                thread.start();
            }  
        } catch (IOException e) {
            System.out.println("Uknown IO Exception: " + e);
        }
    }

    public boolean sendMessage(String message, String ip) {
        return this.threads.get(ip).sendMessage(message);
    }

    public void broadcast(String message) {
        System.out.println("Broadcasing this message: " + message);
        for (ClientSocket socket : this.threads.values()) {
            System.out.println(socket.sendMessage(message) 
                ? "Updated client " + socket.getIP() : "Could not update " + socket.getIP());
        }
    }

    public boolean terminate() {
        try {
            this.server.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        //int port = 2442;
        NetworkServer server = new NetworkServer(port);

        Thread serverThread = new Thread(server);

        serverThread.start();

        boolean keepAlive = true;
        Scanner input = new Scanner(System.in);

        while (keepAlive) {
            System.out.print("\n» ");
            while (!input.hasNextLine());

            String content = input.nextLine();

            String[] components = content.split(" ", 2);

            if (components[0].equals("exit")) {
                keepAlive = false;
            } else if (components[0].equals("broadcast")) {
                server.broadcast(components[1]);
            } else if (components[0].equals("message")) {
                server.broadcast(components[1]);
            }
        } 

        input.close();
        serverThread.interrupt();
        server.terminate();
    }
}
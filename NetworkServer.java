import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class NetworkServer {
    private int port;
    private ServerSocket server;
    private ArrayList<ClientSocket> threads;

    public NetworkServer(int port) {
        this.port = port;
    }

    public void init() {
        try {
            this.server = new ServerSocket(this.port);

            while (true) {
                Socket s = this.server.accept();
                
                ClientSocket thread = new ClientSocket(s);
                
                this.threads.add(thread);
                thread.start();
            }  
        } catch (IOException e) {
            System.out.println("Uknown IO Exception: " + e);
        }
    }

    public boolean sendMessage(String message, int index) {
        return this.threads.get(index).sendMessage(message);
    }

    public void broadcast(String message) {
        System.out.println("Broadcasing this message: " + message);
        for (ClientSocket socket : this.threads) {
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
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        NetworkClient client = new NetworkClient(port);

        client.connect(serverName);
        client.sendMessage("Hello World!");
    }
}
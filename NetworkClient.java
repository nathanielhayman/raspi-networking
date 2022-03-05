import java.net.*;
import java.io.*;

public class NetworkClient {
    private int port;
    private Socket client;

    public NetworkClient(int port) {
        this.port = port;
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
        client.sendMessage("Hello World!");
    }
}
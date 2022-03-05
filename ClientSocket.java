import java.net.*;
import java.io.*;

public class ClientSocket extends Thread {
    private Socket socket;
    
    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            String contents = read();
            System.out.println("Recieved a message from client @ " + this.socket.getRemoteSocketAddress().toString() + ": " + contents);
        } catch (IOException e) {
            System.out.println("Error reading input from client!");
        }
    }

    public boolean sendMessage(String message) {
        if (this.socket == null)
            return false;
        try {
            DataOutputStream stream = new DataOutputStream(this.socket.getOutputStream());
            stream.writeUTF(message);
            stream.flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String getIP() {
        return this.socket.getRemoteSocketAddress().toString();
    }

    public String read() throws IOException {
        DataInputStream stream = new DataInputStream(this.socket.getInputStream());

        return stream.readUTF();
    }
}
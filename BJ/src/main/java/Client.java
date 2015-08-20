import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client {

    static private Socket connection;
    static private ObjectOutputStream outputStream;
    static private ObjectInputStream inputStream;

    public static void main(String[] args) {
        //new Thread(new Client()).start();
        //new Thread(new Server()).start();

        String line = null;
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        try {

            connection = new Socket(InetAddress.getByName("127.0.0.1"), 5678);
            System.out.println("Find a server");
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
            System.out.println("PLz put rate \n");

            while (true) {
                line = keyboard.readLine();
                outputStream.writeUTF(line);
                outputStream.flush();
                line = inputStream.readUTF();
                System.out.println("-----Server-----" + line);
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

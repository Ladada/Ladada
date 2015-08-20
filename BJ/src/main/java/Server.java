import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    static private ServerSocket serverSocket;
    static private Socket connection;
    static private ObjectOutputStream outputStream;
    static private ObjectInputStream inputStream;
    static String line = null;

    static private ArrayList<clientThread> threadList = new ArrayList();

    static ArrayList<Integer> cards = new ArrayList<Integer>();
    static ArrayList<Integer> playerCards = new ArrayList<Integer>();
    static int rate = 0;
    static int summPl = 0;

    public static void main(String[] args) throws IOException {

        serverSocket = new ServerSocket(5678,5);
        System.out.println("Server is runable");
        connection = serverSocket.accept();
        System.out.println("Got a client!");

        outputStream = new ObjectOutputStream(connection.getOutputStream());
        inputStream = new ObjectInputStream(connection.getInputStream());

            while (true) {

                clientThread client = new clientThread(connection,threadList);
                threadList.add(client);
                client.start();


                boolean flag = true;
                for (clientThread t : threadList) {
                    if (t != null){
                        System.out.println("------");
                        flag = false;
                    } else {
                        break;
                    }
                }
                if (flag) {
                    System.out.println("Client is not online");
                } else {
                    for (clientThread cl : threadList) {
                        line = inputStream.readUTF();
                        System.out.println();
                        logic(line);
                        System.out.println("Waiting...");
                    }
                }
                for (clientThread cl : threadList)
                    if (cl == null){
                        System.out.println("The client is leaving" );
                    }
            }
    }

    protected static void logic(String string) throws IOException {

        if (line.matches("\\d+") && rate != 0 /*& playerCards.size() != 0*/){
            outputStream.writeUTF("\n U ardy have rate:" + rate + " plz \"start\" game");
            outputStream.flush();

            System.out.println(playerCards.toString());
            System.out.println(summPl);

        } else {
            if (line.matches("\\d+")){
                setRate(Integer.parseInt(line));
                outputStream.writeUTF("\n Rate: " + rate);
                outputStream.flush();

                System.out.println(playerCards.toString());
                System.out.println(summPl);
            }
        }

        if (!line.matches("\\d+") & rate != 0){

            if (summPl == 0) {
                if (line.matches("start") && playerCards.size() == 0 && rate != 0){
                    getCards();
                    giveCard(cards, playerCards);
                    giveCard(cards, playerCards);
                    summCards(playerCards);
                    checkSumm();

                    System.out.println(playerCards.toString());
                    System.out.println(summPl);

                } else {
                    if (line.matches("hit") || line.matches("hold")){
                        outputStream.writeUTF("\n plz first \"start\" game");
                        outputStream.flush();

                        System.out.println(playerCards.toString());
                        System.out.println(summPl);
                    }
                }
            }

            if (summPl < 21 & summPl > 0){

                if (line.matches("start") && playerCards.size() != 0 && rate != 0) {
                    outputStream.writeUTF("\n U alrdy have cards plz chose nest step: hit, hold");
                    outputStream.flush();

                    System.out.println(playerCards.toString());
                    System.out.println(summPl);
                } else {
                    if (line.matches("hit") && playerCards.size() != 0 && rate != 0) {
                        giveCard(cards, playerCards);
                        summCards(playerCards);
                        checkSumm();

                        System.out.println(playerCards.toString());
                        System.out.println(summPl);
                    } else {
                        if (line.matches("hold") && playerCards.size() != 0 && rate != 0) {
                            outputStream.writeUTF("\n Your rate:" + rate +
                                    ",\n cards:" + playerCards.toString() +
                                    " summ:" + summPl +
                                    "\n U win =)" + rate * 1.5);
                            outputStream.flush();
                            rate = 0;
                            summPl = 0;
                            cards.clear();

                            System.out.println(playerCards.toString());
                            System.out.println(summPl);
                        }
                    }
                }
            }

            if (summPl == 21) {
                outputStream.writeUTF("\n Your rate:" + rate +
                        ",\n cards:" + playerCards.toString() +
                        "summ:" + summPl +
                        "\n BLACK JACK" +
                        "\n U win =)" + rate * 1.5);
                outputStream.flush();
                rate = 0;
                summPl = 0;
                cards.clear();

                System.out.println(playerCards.toString());
                System.out.println(summPl);
            }
            if (summPl > 21){
                outputStream.writeUTF("\n Your rate:" + rate +
                        ",\n cards:" + playerCards.toString() +
                        "summ:" + summPl +
                        "\n Use lose =(" +
                        "\n U win =(" + rate * 0);
                outputStream.flush();
                rate = 0;
                summPl = 0;
                cards.clear();

                System.out.println(playerCards.toString());
                System.out.println(summPl);
            }

        } else {
            if (!line.matches("\\d+")){
                outputStream.writeUTF("\n plz put rate and jose comand: start, hit ,hold");
                outputStream.flush();

                System.out.println(playerCards.toString());
                System.out.println(summPl);
            }
        }
    }

    private static void checkSumm() throws IOException {
        if (summPl < 21) {
            outputStream.writeUTF("\n Your rate:" + rate +
                    ",\n cards:" + playerCards.toString() +
                    " summ:" + summPl);
            outputStream.flush();

            System.out.println(playerCards.toString());
            System.out.println(summPl);
        }
    }

    private static void setRate(int r) {
        rate+=r;
    }

    private static void summCards(ArrayList<Integer> arrayList){

        summPl = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            summPl += arrayList.get(i);
        }
    }


    private static void getCards(){
        for (int i = 2; i <= 11; i++) {
            for (int j = 0; j < 4; j++) {
                cards.add(i);
            }
        }
    }

    private static void giveCard(ArrayList<Integer> a1, ArrayList<Integer> a2) {
        for (int i = 0; i < 1; i++) {
            int k = (int)(Math.random() * a1.size());
            a2.add(a1.get(k));
            a1.remove(k);
        }

        System.out.println(playerCards.toString());
        System.out.println(summPl);
    }
}

class clientThread extends Thread{
    private final Socket clientSocket;
    private final List<clientThread> t;

    public clientThread(Socket clientSocket, List<clientThread> t){
        this.clientSocket=clientSocket;
        this.t=t;
    }
}


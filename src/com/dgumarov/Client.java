package com.dgumarov;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {
    static Logger logger = Logger.getLogger(Client.class.getName());
    private static Socket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public static void main(String[] args) throws InterruptedException, IOException {

        try {
                socket = new Socket("localhost", 8888);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch (Exception e)
        {}

        ConsoleInputWorker consoleInputWorker = new ConsoleInputWorker();
        SocketInputWorker socketInputWorker = new SocketInputWorker();

        consoleInputWorker.start();
        socketInputWorker.start();


    }

    private static void sendMessage(String message)
    {
        try{
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
        catch (Exception e)
        {}
    }

    public static class ConsoleInputWorker extends Thread{

        @Override
        public void run() {
            while (true)
            {
                String userInput;

                try (Scanner scanner = new Scanner(System.in)) {
                    while (scanner.hasNext()) {
                        userInput = scanner.nextLine();
                        sendMessage(userInput);
                    }
                }
            }
        }
    }

    public static class SocketInputWorker extends Thread{
        @Override
        public void run() {

            String line;

            try {
                while ((line = reader.readLine()) != null)
                System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

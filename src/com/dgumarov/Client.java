package com.dgumarov;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by user on 03.12.16.
 */
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



            /*try (//Socket socket = new Socket("localhost", 8888);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ) {
                writer.write(String.valueOf(LocalDate.now()));
                writer.newLine();
                writer.flush();
                String line = reader.readLine();

                logger.log(Level.INFO, "Line received: {0}", line);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

}

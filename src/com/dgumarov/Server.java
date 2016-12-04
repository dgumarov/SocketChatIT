package com.dgumarov;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    static Logger logger = Logger.getLogger(Server.class.getName());
    private static Map<String, Socket> clientsMap = new Hashtable<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("client connected");
                new Worker(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Worker extends Thread {
        Socket socket;
        String username;
        private BufferedReader reader;
        private BufferedWriter writer;

        public Worker(Socket socket) {
            this.socket = socket;

            try
            {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            }
            catch (IOException io)
            {}
        }

        @Override
        public void run() {

            authenticate();

            try {

                String line;
                while ((line = reader.readLine()) != null) {
                    logger.log(Level.INFO, "Line received: {0}", line);
                    if (line.equals("exit")) {
                        writer.write("Goodbye, " + username);
                        break;
                    }

                    writeToAllStreams(username + ": " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientsMap.remove(username);
                writeToAllStreams("User '" + username + "' has left the chat.");
                logger.info("Client disconnected. Connected clients count: " + clientsMap.size());
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private void authenticate()
        {
            try
            {
                writeToCurrentStream("Enter your name");

                while (true)
                {
                    username = reader.readLine();

                    if (clientsMap.containsKey(username)) {
                        writeToCurrentStream("User with such name already registered at the moment. Please enter another name");
                    }

                    else
                    {
                        clientsMap.put(username, socket);
                        logger.info("connected clients count: " + clientsMap.size());
                        writeToCurrentStream("Welcome to chat " + username);
                        break;
                    }

                }
            }
            catch (Exception e)
            {}
        }

        private void writeToCurrentStream(String message)
        {
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void writeToAllStreams(String message)
        {
            Iterator<String> iterator = clientsMap.keySet().iterator();

            while (iterator.hasNext())
            {
                try
                {
                    BufferedWriter currentWriter = new BufferedWriter(new OutputStreamWriter(clientsMap.get(iterator.next()).getOutputStream()));
                    currentWriter.write(message);
                    currentWriter.newLine();
                    currentWriter.flush();
                }
                catch (Exception e)
                {}
            }
        }
    }

}

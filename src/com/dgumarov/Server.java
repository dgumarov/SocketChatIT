package com.dgumarov;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by user on 03.12.16.
 */
public class Server {

    static Logger logger = Logger.getLogger(Server.class.getName());
    private static List<Socket> clientList = new Vector<>();
    private static List<String> clientNames = new Vector<>();
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

        public Worker(Socket socket) {
            this.socket = socket;
            logger.info("connected clients count: " + clientList.size());
        }

        @Override
        public void run() {

            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("Enter your name");
                writer.newLine();
                writer.flush();

                while (true)
                {
                    username = reader.readLine();

                    if (clientNames.contains(username)) {
                        writer.write("User with such name already registered at the moment. Please enter another name");
                        writer.newLine();
                        writer.flush();
                    }

                    else
                    {
                        clientList.add(socket);
                        break;
                    }

                }

                clientsMap.put(username, socket);

            }
            catch (Exception e)
            {}


            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ) {

                String line;
                while ((line = reader.readLine()) != null) {
                    logger.log(Level.INFO, "Line received: {0}", line);
                    if (line.equals("exit"))
                        break;

                    for (int i = 0; i<clientList.size(); i++)
                    {
                        try
                        {
                            BufferedWriter currentWriter = new BufferedWriter(new OutputStreamWriter(clientList.get(i).getOutputStream()));
                            currentWriter.write(username + ": " + line);
                            currentWriter.newLine();
                            currentWriter.flush();
                        }
                        catch (Exception e)
                        {}
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                logger.info("client disconnected");
                clientList.remove(socket);
                logger.info("connected clients count: " + clientList.size());
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

package com.example.messengerclient;

import java.io.*;
import java.net.Socket;

/**
 * ClientTransceiver handles all Client Side Networking Elements
 */
public class ClientTransceiver {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    /**
     * Initializes the client socket and
     * establishes input and output buffers.
     *
     * @param socket the client's socket
     */

    public ClientTransceiver(Socket socket) {
        try {
            this.clientSocket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void shutdownClient(){
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(clientUsername + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public void sendLoginCredentials(String user, String pass) throws IOException {
        sendCredentials(user, pass, "Login");
    }

    public void sendNewAccountCredentials(String user, String pass) throws IOException {
        sendCredentials(user, pass, "Create");
    }

    public String waitForResponse() {
        String msgFromSvr = "Invalid";
        try {
            System.out.println("Waiting for response...");
            msgFromSvr = bufferedReader.readLine();
            System.out.println("Response from Server: " + msgFromSvr);
        } catch (IOException e) {
            shutdownClient();
        }
        return msgFromSvr;
    }

    public void receiveMessageWithHook(ExtractionFunction func) {
        new Thread(() -> {
            String msgFromSvr;
            while (clientSocket.isConnected()) {
                try {
                    msgFromSvr = bufferedReader.readLine();
                    func.getData(msgFromSvr);
                } catch (IOException e) {
                    shutdownClient();
                }
            }
        }).start();
    }

    private void sendCredentials(String user, String pass, String header) throws IOException {
        clientUsername = user;
        bufferedWriter.write(header);
        bufferedWriter.newLine();
        bufferedWriter.write(user);
        bufferedWriter.newLine();
        bufferedWriter.write(pass);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}

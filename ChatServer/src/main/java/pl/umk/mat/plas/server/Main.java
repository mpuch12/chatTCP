package pl.umk.mat.plas.server;

import pl.umk.mat.plas.service.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(10005);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket socket = null;
        System.out.println("Działam...");
        while (true) {
            try {
                socket = serverSocket.accept();
                new ClientThread(socket).start();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}

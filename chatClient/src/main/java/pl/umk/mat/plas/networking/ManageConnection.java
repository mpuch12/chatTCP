package pl.umk.mat.plas.networking;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.mvc.ChatViewController;
import pl.umk.mat.plas.mvc.LoginPanelController;

import java.io.IOException;
import java.net.Socket;

public class ManageConnection {
    private Socket socket;
    public static ClientApp clientApp;
    private LoginPanelController loginController;

    public boolean initChat(String ipAddress, int port){
        try {
            loginController = Main.loginPanelController;
            socket = new Socket(ipAddress, port);
            clientApp = new ClientApp(socket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tryRegister() throws IOException {
        return clientApp.tryRegister();
    }

    public boolean startChat() throws IOException {
        try {
            if(!clientApp.tryLogin()) {
                return false;
            }

            clientApp.sendMessageToServer("STARTCHAT");

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return true;
    }

    public void startServer(){
        ServerApp listenerServerApp = new ServerApp(socket, loginController.getNicknameString(), clientApp.getServerStreamRead(),
                clientApp.getServerPrintWriter(), clientApp.getObjectInputStream());
        listenerServerApp.start();
    }
}

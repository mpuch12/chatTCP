package pl.umk.mat.plas.networking;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.model.Link;
import pl.umk.mat.plas.model.Room;
import pl.umk.mat.plas.mvc.ChatViewController;
import pl.umk.mat.plas.mvc.GroupGeneratorController;
import pl.umk.mat.plas.mvc.InvitationViewController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerApp extends Thread {
    private Socket socket;
    private String nick;
    private BufferedReader streamRead;
    private PrintWriter printWriter;
    private ChatViewController chatViewController;
    private InvitationViewController invitationViewController;
    private GroupGeneratorController groupGeneratorController;
    private ObjectInputStream objectInputStream;
    private ArrayList<Room> rooms;
    private ArrayList<Link> linkList;

    public ServerApp(Socket socket, String nick, BufferedReader streamRead, PrintWriter printWriter, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.streamRead = streamRead;
        this.printWriter = printWriter;
        this.nick = nick;
        chatViewController = Main.chatViewController;
        invitationViewController = Main.invitationViewController;
        groupGeneratorController = Main.groupGeneratorController;
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        readRooms();
        manageReceivedMessage();
    }

    private void readRooms(){
        try {
            rooms = (ArrayList<Room>) objectInputStream.readObject();
            chatViewController.createListView(rooms);
            groupGeneratorController.fillFriendsListView(rooms);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readLinks(){
        try {
            linkList = (ArrayList<Link>) objectInputStream.readObject();
            chatViewController.setLinkList(linkList);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void readInvitations(){
        try {
            ArrayList<String> invitations = (ArrayList<String>) objectInputStream.readObject();
            invitationViewController.fillInvitationsListView(invitations);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void manageReceivedMessage() {
        Platform.runLater(()->{ManageConnection.clientApp.sendMessageToServer("SENDINVITATIONLIST");});
        readInvitations();
        Platform.runLater(()->{ManageConnection.clientApp.sendMessageToServer("LINKING");});
        readLinks();
        String response;
        int i =0;
        try {
            while (true) {
                response = streamRead.readLine();
                if (response.equals("CONNECTIONCLOSED"))
                    break;
                switch (response){
                    case "SERVER#INVITATION_ACCEPTED":
                        chatViewController.showPopUp("Pomyślnie wysłano zaproszenie.", Alert.AlertType.CONFIRMATION);
                        break;
                    case "SERVER#INVITATION_REJECTED":
                        chatViewController.showPopUp("Użytkownik już został wcześniej zaproszony. Oczekuj na zaakcpetowanie.", Alert.AlertType.WARNING);
                        break;
                    case "SERVER#INVITATION_ERROR":
                        chatViewController.showPopUp("Taki użytkownik nie istnieje.", Alert.AlertType.ERROR);
                        break;
                    case "NEWINVITATION":
                        chatViewController.showPopUp("Masz nowe zaproszenie.", Alert.AlertType.INFORMATION);
                        readInvitations();
                        break;
                    case "UPDATEROOMLIST":
                        Platform.runLater(()->{ManageConnection.clientApp.sendMessageToServer("STARTCHAT");});
                        readRooms();
                        Platform.runLater(()->{ManageConnection.clientApp.sendMessageToServer("LINKING");});
                        readLinks();
                        break;
                    case "CREATEGROUP_OK":
                        chatViewController.showPopUp("Pomyślnie utworzono grupę", Alert.AlertType.INFORMATION);
                        //Main.groupGeneratorStage.hide();
                        break;
                    case "CREATEGROUP_ERROR":
                        chatViewController.showPopUp("Wystąpił błąd. Spróbuj ponownie później.", Alert.AlertType.ERROR);
                        //Main.groupGeneratorStage.hide();
                        break;
                    default:
                        String finalResponse = response;
                        Platform.runLater(()->{
                            Main.chatViewController.addMessageToChatTextArea(finalResponse);
                        });
                }

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

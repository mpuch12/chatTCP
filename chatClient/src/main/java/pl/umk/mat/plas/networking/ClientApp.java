package pl.umk.mat.plas.networking;

import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.mvc.ChatViewController;
import pl.umk.mat.plas.mvc.LoginPanelController;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class ClientApp {
    private Socket socket;
    private BufferedReader consoleRead;
    private BufferedReader serverStreamRead;
    private PrintWriter serverPrintWriter;
    private ObjectInputStream objectInputStream;
    private String nick;
    private LoginPanelController loginController;

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public BufferedReader getServerStreamRead() {
        return serverStreamRead;
    }

    public PrintWriter getServerPrintWriter() {
        return serverPrintWriter;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public ClientApp(Socket socket) throws IOException {
        this.socket = socket;
        this.loginController = Main.loginPanelController;
        initializeStreams();
    }

    public void initializeStreams() throws IOException {
        consoleRead = new BufferedReader(new InputStreamReader(System.in));
        serverStreamRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverPrintWriter = new PrintWriter(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public boolean tryLogin() throws IOException {
        String response = "", loginNick = null, loginPassword = null, login;
        loginNick = loginController.getNicknameString();
        loginPassword = loginController.getPasswordString();
        login = "TRYLOGIN#"+loginNick+"#"+loginPassword;
        sendMessageToServer(login);

        response = serverStreamRead.readLine();

        if(isLoggedIn(response)){
            setNick(loginNick);
        }else
            return false;

        return true;
    }

    public boolean tryRegister() throws IOException {
        String response = "", loginNick = null, registerPassword = null, login;
        loginNick = loginController.getNicknameString();
        registerPassword = loginController.getPasswordString();
        login = "TRYREGISTER#"+loginNick+"#"+registerPassword;
        sendMessageToServer(login);

        response = serverStreamRead.readLine();

        return isRegistered(response);
    }

    private boolean isRegistered(String response){
        if ((response.equals("#REGISTER_OK#"))) {
            return true;
        }else if((response.equals("#REGISTER_BAD#"))){
            return false;
        }
        return false;
    }

    private boolean isLoggedIn(String response){
        if ((response.equals("#LOGIN_OK#"))) {
            return true;
        }else if((response.equals("#LOGIN_BAD#"))){
            return false;
        }
        return false;
    }

    public void sendInvitationResponse(String chosenNick, String option){
        String response = "INVITATIONRESPONSE#"+ option+"#"+chosenNick;
        sendMessageToServer(response);
    }

    public void sendInvitation(String toInvite){
        String message = "INVITATION#"+toInvite;
        sendMessageToServer(message);
    }

    public void sendMessageToServer(String message){
        serverPrintWriter.println(message);
        serverPrintWriter.flush();
    }

    public void sendDeleteFriend(int idRoom) {
        sendMessageToServer("DELETEFRIEND#"+idRoom);
    }

    public void createGroup(String groupName, List<String> groupUsers) {
        StringBuilder sb = new StringBuilder("");
        sb.append("CREATEGROUP#");
        sb.append(groupName);
        sb.append("#");
        for (int i = 0; i < groupUsers.size(); i++) {
            sb.append(groupUsers.get(i));
            if(i != groupUsers.size()-1){
                sb.append("#");
            }
        }
        sb.append("#");
        sb.append(nick);
        sendMessageToServer(sb.toString());
    }

    public void renameGroupRoomName(int idRoom, String name) {
        sendMessageToServer("RENAMEROOM#"+idRoom+"#"+name);
    }

    public void deleteUserFromGroup(int idRoom, String nickname) {
        sendMessageToServer("DELETEFROMGROUP#"+idRoom+"#"+nickname);
    }

    public void addUserToGroup(int idRoom, String nickname) {
        sendMessageToServer("ADDUSERTOGROUP#"+idRoom+"#"+nickname);
    }

    public void deleteGroup(int idRoom) {
        sendMessageToServer("DELETEFRIEND#"+idRoom);
    }

    public void logoutUser() {
        ManageConnection.clientApp.sendLogoutRequest();
        Main.chatViewStage.hide();
        Main.loginViewStage.show();
        Main.loginPanelController.clearLoginData();
    }

    private void sendLogoutRequest() {
        sendMessageToServer("LOGOUTUSER");
    }

    public void sendStopConnection() {
        sendMessageToServer("ENDCOMMUNICATION");
    }
}
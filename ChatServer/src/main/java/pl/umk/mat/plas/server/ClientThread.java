package pl.umk.mat.plas.server;

import pl.umk.mat.plas.model.Link;
import pl.umk.mat.plas.model.Room;
import pl.umk.mat.plas.service.*;

import java.io.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientThread extends Thread {
    protected Socket socket;
    private static HashMap<String, PrintWriter> mapOfNicksAndOutputStreams = new HashMap<>();
    private BufferedReader streamBufferedReader;
    private ObjectOutputStream objectOutputStream;
    private PrintWriter printWriter;
    private String nick, password;

    public ClientThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        try {
            initializeStreams();
            chooseOption();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    private void initializeStreams() throws IOException {
        InputStream inp = socket.getInputStream();
        streamBufferedReader = new BufferedReader(new InputStreamReader(inp));
        printWriter = new PrintWriter(socket.getOutputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    private void chooseOption() throws IOException {
        String option;
        do {
            String response = streamBufferedReader.readLine();

            option = response.split("#")[0];
            System.out.println(nick + " " + option);

            switch (option) {
                case "TRYLOGIN":
                    loginUser(response);
                    break;
                case "TRYREGISTER":
                    registerUser(response);
                    break;
                case "STARTCHAT":
                    sendRoomList();
                    break;
                case "LINKING":
                    sendLinkList();
                    break;
                case "SENDINVITATIONLIST":
                    sendInvitationList();
                    break;
                case "INVITATION":
                    saveInvitation(response);
                    sendInvitationToUserIfActive(response);
                    break;
                case "INVITATIONRESPONSE":
                    manageInvitationResponse(response);
                    break;
                case "MESSAGE":
                    sendToRoom(response);
                    break;
                case "DELETEFRIEND":
                    deleteFriend(response);
                    break;
                case "DELETEFROMGROUP":
                    deleteUserFromGroup(response);
                    break;
                case "CREATEGROUP":
                    createGroup(response);
                    createConnectionsToGroup(response);
                    break;
                case "RENAMEROOM":
                    renameGroup(response);
                    break;
                case "ADDUSERTOGROUP":
                    addUserToGroup(response);
                    break;
                case "LOGOUTUSER":
                    logoutUser();
                    sendMessageToUser("CONNECTIONCLOSED");
                    break;
            }
        } while (!option.equals("ENDCOMMUNICATION"));
        printWriter.close();
        objectOutputStream.close();
        streamBufferedReader.close();
    }


    private void loginUser(String response) {
        UserService userService = new UserService();

        nick = response.split("#")[1];

        if (userService.tryLoginUser(response)) {
            mapOfNicksAndOutputStreams.put(nick, printWriter);
            sendMessageToUser("#LOGIN_OK#");
        } else
            sendMessageToUser("#LOGIN_BAD#");
    }


    private void registerUser(String response) {
        UserService userService = new UserService();

        if (userService.tryRegisterUser(response)) {
            sendMessageToUser("#REGISTER_OK#");
        } else
            sendMessageToUser("#REGISTER_BAD#");
    }

    private void sendRoomList() {
        RoomService roomService = new RoomService();
        ArrayList<Room> rooms = roomService.readUserRooms(nick);
        try {
            objectOutputStream.writeObject(rooms);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLinkList() {
        ConnectionService connectionService = new ConnectionService();
        ArrayList<Link> linkList = connectionService.readLinkList();
        try {
            objectOutputStream.writeObject(linkList);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInvitationList() {
        InvitationService invitationService = new InvitationService();
        ArrayList<String> invitations = invitationService.readInvitationListForUser(nick);
        try {
            objectOutputStream.writeObject(invitations);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveInvitation(String response) {
        String invitedNick = response.split("#")[1];
        InvitationService invitationService = new InvitationService();
        //!!!!!
        if (invitationService.checkIfDontExist(nick, invitedNick)) {
            if (invitationService.saveInvitation(nick, invitedNick))
                sendMessageToUser("SERVER#INVITATION_ACCEPTED");
            else
                sendMessageToUser("SERVER#INVITATION_ERROR");
        } else
            sendMessageToUser("SERVER#INVITATION_REJECTED");

    }

    private void sendInvitationToUserIfActive(String response) {
        String[] data = response.split("#");
        String invitedNick = data[1];

        send(invitedNick, "NEWINVITATION");
    }

    private void manageInvitationResponse(String response) {
        String[] data = response.split("#");
        String option = data[1];
        String chosenNick = data[2];
        InvitationService invitationService = new InvitationService();
        ConnectionService connectionService = new ConnectionService();
        RoomService roomService = new RoomService();

        if (option.equals("ACCEPT")) {
            String name = nick + "#" + chosenNick;
            roomService.createRoom(name, false);
            int idRoom = roomService.readRoomID(name);
            connectionService.createUserConnection(nick, idRoom);
            connectionService.createUserConnection(chosenNick, idRoom);
        }

        invitationService.deleteInvitation(nick, chosenNick);
        send(chosenNick, "UPDATEROOMLIST");
        sendMessageToUser("UPDATEROOMLIST");
    }

    private void sendToRoom(String response) {
        String[] data = response.split("#");
        int idRoom = Integer.parseInt(data[1]);
        StringBuilder message = new StringBuilder();

        for (int i = 2; i < data.length; i++) {
            if(i == data.length - 1)
                message.append(data[i]);
            else
                message.append(data[i]).append("#");
        }

        ConnectionService connectionService = new ConnectionService();
        ArrayList<String> nicks = connectionService.readNickFromRoomThatHaveIdRoom(idRoom);

        saveMessageInDatabase(idRoom, message.toString());
        sendToUsers(nicks, message.toString(), idRoom);
    }

    private void saveMessageInDatabase(int idRoom, String message) {
        MessageService messageService = new MessageService();
        messageService.addMessageInRoomThatHaveIdRoom(idRoom, message);
    }

    private void deleteFriend(String response) {
        int idRoom = Integer.parseInt(response.split("#")[1]);
        RoomService roomService = new RoomService();
        String name = roomService.readRoomName(idRoom);
        String[] data = name.split("#");
        String friendNick;
        if(data[0].equals(nick))
            friendNick = data[1];
        else
            friendNick = data[0];
        roomService.deleteRoom(idRoom);
        send(friendNick, "UPDATEROOMLIST");
        sendMessageToUser("UPDATEROOMLIST");
    }

    private void deleteUserFromGroup(String response) {
        String[] data = response.split("#");
        String nickname = data[2];
        int idRoom = Integer.parseInt(data[1]);
        ConnectionService connectionService = new ConnectionService();
        ArrayList<String> listOfUsersInGroup = connectionService.readNickFromRoomThatHaveIdRoom(idRoom);
        connectionService.deleteUserConnection(idRoom, nickname);

        /*for(String nick: listOfUsersInGroup){
            if(mapOfNicksAndOutputStreams.containsKey(nick)){
                send(nick, "UPDATEROOMLIST");
            }

        }*/
        listOfUsersInGroup.stream().filter(nick -> mapOfNicksAndOutputStreams.containsKey(nick)).forEach(nick -> send(nick,"UPDATEROOMLIST"));
    }

    private void createGroup(String response) {
        String[] data = response.split("#");
        String groupName = data[1];

        RoomService roomService = new RoomService();
        if(roomService.createRoom(groupName, true)){
            sendMessageToUser("CREATEGROUP_OK");
        }else
            sendMessageToUser("CREATEGROUP_ERROR");
    }

    private void createConnectionsToGroup(String response) {
        String[] data = response.split("#");
        String groupName = response.split("#")[1];

        RoomService roomService = new RoomService();
        ConnectionService connectionService = new ConnectionService();

        int idRoom = roomService.readRoomID(groupName);

        for (int i = 2; i < data.length; i++) {
            connectionService.createUserConnection(data[i], idRoom);
        }

        for (int i = 2; i < data.length; i++) {
            if (mapOfNicksAndOutputStreams.containsKey(data[i])) {
                send(data[i], "UPDATEROOMLIST");
            }
        }
    }

    private void renameGroup(String response) {
        String[] data = response.split("#");
        String name = data[2];
        int idRoom = Integer.parseInt(data[1]);
        RoomService roomService = new RoomService();
        roomService.renameRoom(idRoom, name);

        ConnectionService connectionService = new ConnectionService();

        ArrayList<String> listOfUsersInGroup = connectionService.readNickFromRoomThatHaveIdRoom(idRoom);

        /*for(String nick: listOfUsersInGroup){
            if(mapOfNicksAndOutputStreams.containsKey(nick)){
                send(nick, "UPDATEROOMLIST");
            }

        }*/

        listOfUsersInGroup.stream().filter(nick -> mapOfNicksAndOutputStreams.containsKey(nick)).forEach(nick -> send(nick,"UPDATEROOMLIST"));
    }


    private void addUserToGroup(String response) {
        String[] data = response.split("#");
        String nickname = data[2];
        int idRoom = Integer.parseInt(data[1]);
        ConnectionService connectionService = new ConnectionService();

        connectionService.createUserConnection(nickname, idRoom);

        ArrayList<String> listOfUsersInGroup = connectionService.readNickFromRoomThatHaveIdRoom(idRoom);

       /*for(String nick: listOfUsersInGroup){
            if(mapOfNicksAndOutputStreams.containsKey(nick)){
                send(nick, "UPDATEROOMLIST");
            }
        }*/

        listOfUsersInGroup.stream().filter(nick -> mapOfNicksAndOutputStreams.containsKey(nick)).forEach(nick -> send(nick,"UPDATEROOMLIST"));

    }

    private void logoutUser() {
        UserService userService = new UserService();
        userService.logoutUser(nick);
        mapOfNicksAndOutputStreams.remove(nick);
    }


    private void sendMessageToUser(String message) {
        printWriter.flush();
        printWriter.println(message);
        printWriter.flush();
    }

    private void sendToUsers(ArrayList<String> nicks, String message, int idRoom) {
        message = "MESSAGE#" + idRoom + "#" + message;

       /* for (int i = 0; i < nicks.size(); i++) {
            if (mapOfNicksAndOutputStreams.containsKey(nicks.get(i))) {
                send(nicks.get(i), message);
            }
        }*/

        String finalMessage = message;
        nicks.stream().filter(nick -> mapOfNicksAndOutputStreams.containsKey(nick)).forEach(nick -> send(nick, finalMessage));

    }

    private void send(String user, String message) {
        if(mapOfNicksAndOutputStreams.containsKey(user)) {
            mapOfNicksAndOutputStreams.get(user).println(message);
            mapOfNicksAndOutputStreams.get(user).flush();
        }
    }




















}

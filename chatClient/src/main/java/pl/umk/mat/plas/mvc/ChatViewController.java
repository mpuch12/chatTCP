package pl.umk.mat.plas.mvc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.model.Link;
import pl.umk.mat.plas.model.Room;
import pl.umk.mat.plas.networking.ManageConnection;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatViewController {

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField messageTextField;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<Room> roomsListView;


    @FXML
    private Button logoutButton;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu friendsMenu;

    @FXML
    private MenuItem addFriendItem;

    @FXML
    private MenuItem invitesItem;

    @FXML
    private MenuItem deleteFriendItem;

    @FXML
    private Menu groupsMenu;

    @FXML
    private MenuItem createNewGroupItem;

    @FXML
    private MenuItem renameGroupItem;

    @FXML
    private MenuItem deleteGroupItem;

    @FXML
    private MenuItem addFriendTGroupItem;

    @FXML
    private MenuItem deleteFriendFromGroupItem;

    private int activeRoomChatIndex;

    private ArrayList<Link> linkList;

    public ArrayList<Link> getLinkList() {
        return linkList;
    }

    public void setLinkList(ArrayList<Link> linkList) {
        this.linkList = linkList;
    }

    public void initialize() {


        if(!roomsListView.getItems().isEmpty()) {
            roomsListView.getSelectionModel().selectFirst();
            activeRoomChatIndex = roomsListView.getSelectionModel().getSelectedIndex();
        }

        invitesItem.setOnAction(event->{

            if(Main.invitationViewController.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText(null);
                alert.setContentText("Nie masz żadnych zaporszeń :(");
                alert.show();
            }else
                Main.invitationStage.show();
        });

        createNewGroupItem.setOnAction(event->{
            Main.groupGeneratorStage.show();
        });


        roomsListView.setOnMouseClicked(event->{
            activeRoomChatIndex = roomsListView.getSelectionModel().getSelectedIndex();
            fillChatTextAreaWithMessageFromRoomAtActualIndex();
            updateGroupMenuItemName();
            //unmarkRoomWithUnseenMessage();
        });

        sendButton.setOnAction(event -> {
            String message = messageTextField.getText();
            String nick = ManageConnection.clientApp.getNick();
            int idRoom = roomsListView.getItems().get(activeRoomChatIndex).getIdRoom();
            message = "MESSAGE#"+ idRoom + "#["+ nick + "]> "+ message;
            ManageConnection.clientApp.sendMessageToServer(message);
            messageTextField.clear();

        });

        addFriendItem.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Zaproszenie");
            dialog.setHeaderText(null);
            dialog.setContentText("Podaj nick:");
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("Zaproś");
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()){
                ManageConnection.clientApp.sendInvitation(result.get());
            }
        });

        deleteFriendItem.setOnAction(event ->{
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Usuwanie");
            dialog.setHeaderText(null);
            dialog.setContentText("Podaj nick:");
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("Usuń");
            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()){
                int id = findRoomID(result.get());
                if(result.get().equals(ManageConnection.clientApp.getNick()))
                    showPopUp("Nie możesz usunąć siebie", Alert.AlertType.INFORMATION);
                else if(id == -1)
                    showPopUp("Nie masz takiego znajomego", Alert.AlertType.INFORMATION);
                else
                    ManageConnection.clientApp.sendDeleteFriend(id);
            }
        });

        renameGroupItem.setOnAction(event -> {
            if(checkIfActualIndexRoomIsGroup()) {
                String name = roomsListView.getItems().get(activeRoomChatIndex).toString();
                int idRoom = findRoomID(name);

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Zmiana nazwy grupy " + name);
                dialog.setHeaderText(null);
                dialog.setContentText("Podaj nową nazwę");
                ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("Zmień");
                ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent())
                    ManageConnection.clientApp.renameGroupRoomName(idRoom, result.get());
            }
        });

        deleteFriendFromGroupItem.setOnAction(event -> {
            if(checkIfActualIndexRoomIsGroup()) {
                int idRoom = roomsListView.getItems().get(activeRoomChatIndex).getIdRoom();
                Link link = findLink(idRoom);
                ArrayList<String> nicknames = link.getNicknames();

                ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(nicknames.get(0), nicknames);
                choiceDialog.setTitle("Usuń z grupy");
                choiceDialog.setHeaderText(null);
                choiceDialog.setContentText("Wybierz: ");
                Optional<String> result = choiceDialog.showAndWait();
                if (result.isPresent()) {
                    ManageConnection.clientApp.deleteUserFromGroup(link.getIdRoom(), result.get());
                }
            }
        });

        addFriendTGroupItem.setOnAction(event->{
            if(checkIfActualIndexRoomIsGroup()) {
                int idRoom = roomsListView.getItems().get(activeRoomChatIndex).getIdRoom();
                ArrayList<String> nicknames = getNicknamesThatDontHaveConnectionToIdRoom(idRoom);

                ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("wybierz", nicknames);
                choiceDialog.setTitle("Dodaj do grupy");
                choiceDialog.setHeaderText(null);
                choiceDialog.setContentText("Wybierz: ");
                Optional<String> result = choiceDialog.showAndWait();
                if (result.isPresent()) {
                    ManageConnection.clientApp.addUserToGroup(idRoom, result.get());
                }
            }
        });

        deleteGroupItem.setOnAction(event -> {
            if(checkIfActualIndexRoomIsGroup()) {
                int idRoom = findRoomID(roomsListView.getItems().get(activeRoomChatIndex).toString());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdzenie");
                alert.setHeaderText(null);
                alert.setContentText("Czy chcesz usunąć tą grupę ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ManageConnection.clientApp.deleteGroup(idRoom);
                }
            }
        });

        logoutButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Wylogowywanie");
            alert.setHeaderText(null);
            alert.setContentText("Czy chcesz się wylogować");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                ManageConnection.clientApp.logoutUser();
                Main.loginPanelController.getDisconnectButton().fire();
            }
        });
    }

    private ArrayList<String> getNicknamesThatDontHaveConnectionToIdRoom(int idRoom) {
        ArrayList<String> allFriendsNicknames = new ArrayList<>();
        /*for(Room room: roomsListView.getItems()){
            if(!room.isGroupRoom()){
                allFriendsNicknames.add(room.toString());
            }
        }*/
        roomsListView.getItems().stream().filter(room -> !room.isGroupRoom()).forEach(room -> allFriendsNicknames.add(room.toString()));

        ArrayList<String> friendsInGroup = findLink(idRoom).getNicknames();
        ArrayList<String> friendNotInGroup = new ArrayList<>();
       /* for(String nickname: allFriendsNicknames){
            if(!friendsInGroup.contains(nickname)){
                friendNotInGroup.add(nickname);
            }
        }*/

        allFriendsNicknames.stream().filter(nickname -> !friendsInGroup.contains(nickname)).forEach(friendNotInGroup::add);
        return friendNotInGroup;
    }


    public void createListView(ArrayList<Room> rooms){
        Platform.runLater(()->{
            roomsListView.getItems().clear();

            /*for(Room room: rooms){
                roomsListView.getItems().add(room);
            }*/

            rooms.stream().forEach(room -> roomsListView.getItems().add(room));
        });
    }

    private void updateGroupMenuItemName() {
        if(checkIfActualIndexRoomIsGroup()){
            String nameGroup = roomsListView.getItems().get(activeRoomChatIndex).toString();
            renameGroupItem.setText("Zmień nazwę grupy '" + nameGroup + "'");
            deleteGroupItem.setText("Usuń grupę '" + nameGroup + "'");
            addFriendTGroupItem.setText("Dodaj znajomego do grupy '" + nameGroup + "'");
            deleteFriendFromGroupItem.setText("Usuń znajomegeo/siebie z grupy '" + nameGroup + "'");
        }else {
            renameGroupItem.setText("Zmień nazwę grupy (wybierz grupę)");
            deleteGroupItem.setText("Usuń grupę (wybierz grupę)");
            addFriendTGroupItem.setText("Dodaj znajomego do grupy (wybierz grupę)");
            deleteFriendFromGroupItem.setText("Usuń znajomegeo/siebie z grupy (wybierz grupę)");

        }
    }

    private boolean checkIfActualIndexRoomIsGroup() {
        return roomsListView.getItems().get(activeRoomChatIndex).isGroupRoom();
    }

    public void showPopUp(String contentText, Alert.AlertType alertType){
        Platform.runLater(()->{
            Alert alert = new Alert(alertType);
            alert.setTitle("Informacja");
            alert.setHeaderText(null);
            alert.setContentText(contentText);

            alert.showAndWait();
        });
    }

    private void fillChatTextAreaWithMessageFromRoomAtActualIndex(){
        if(activeRoomChatIndex != -1) {
            ArrayList<String> messages = roomsListView.getItems().get(activeRoomChatIndex).getRoomMessages();

            StringBuilder stringBuilder = new StringBuilder();
            for (String message : messages) {
                stringBuilder.append(message + "\n");
            }
            chatTextArea.setText(stringBuilder.toString());
        }
    }

    public void addMessageToChatTextArea(String response){
        String[] data = response.split("#");
        int idRoom = Integer.parseInt(data[1]);
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < data.length; i++) {
            if(i == data.length-1)
                sb.append(data[i]);
            else
                sb.append(data[i]+"#");
        }
        Room room = findRoom(idRoom);
        ArrayList<String> actualMessagesList = room.getRoomMessages();
        actualMessagesList.add(sb.toString());
        room.setRoomMessages(actualMessagesList);

        fillChatTextAreaWithMessageFromRoomAtActualIndex();
    }

    private Link findLink(int idRoom){
        for(Link link: linkList){
            if(link.getIdRoom() == idRoom)

                return link;
        }
        return null;
    }

    private Room findRoom(int idRoom){
        for (Room room: roomsListView.getItems()){
            if(room.getIdRoom() == idRoom)
                return room;
        }
        return null;
    }

    private int findRoomID(String name){
        for (Room room: roomsListView.getItems()){
            if(room.toString().equals(name))
                return room.getIdRoom();
        }
        return -1;
    }


}

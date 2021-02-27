package pl.umk.mat.plas.mvc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.model.Room;
import pl.umk.mat.plas.networking.ManageConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupGeneratorController {

    @FXML
    private TextField groupNameTextField;

    @FXML
    private ListView<String> friendsListView;

    @FXML
    private Button friendsToGroupButton;


    @FXML
    private Button groupToFriendsButton;

    @FXML
    private ListView<String> groupListView;

    @FXML
    private Button createGroupButton;


    public void initialize(){
        friendsListView.getSelectionModel().selectFirst();
        groupListView.getSelectionModel().selectFirst();

        friendsToGroupButton.setOnAction(event -> {
            int index = friendsListView.getSelectionModel().getSelectedIndex();
            if(index != -1){
                groupListView.getItems().add(friendsListView.getItems().get(index));
                friendsListView.getItems().remove(index);
            }
        });

        groupToFriendsButton.setOnAction(event -> {
            int index = groupListView.getSelectionModel().getSelectedIndex();
            if(index != -1){
                friendsListView.getItems().add(groupListView.getItems().get(index));
                groupListView.getItems().remove(index);
            }
        });


        createGroupButton.setOnAction(event -> {
            if(groupNameTextField.getText().isEmpty()){
                showPopUp("Nazwa grupy nie może być pusta", Alert.AlertType.INFORMATION);
            }else {
                if(groupListView.getItems().size() < 2){
                    showPopUp("W grupie muszą być conajmniej dwie osoby", Alert.AlertType.INFORMATION);
                }else {
                    String groupName = groupNameTextField.getText();
                    List<String> groupUsers = observableListToList();
                    ManageConnection.clientApp.createGroup(groupName, groupUsers);
                    Main.groupGeneratorStage.close();
                }
            }
        });



    }

    public void fillFriendsListView(ArrayList<Room> roomArrayList){
        Platform.runLater(()->{
            List<String> list = roomArrayList.stream().filter(room -> room.isGroupRoom() == false).map(Room::toString).collect(Collectors.toList());
            for(String s:list){
                friendsListView.getItems().add(s);
            }
        });

    }

    private List<String> observableListToList(){
        List<String> list = new ArrayList<>();
        for(String s:groupListView.getItems()){
            list.add(s);
        }
        return list;
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
}

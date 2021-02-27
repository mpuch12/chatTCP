package pl.umk.mat.plas.mvc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import pl.umk.mat.plas.main.Main;

import java.util.ArrayList;

public class InvitationViewController {

    @FXML
    private ListView<CustomCell> invitationListView;

    private boolean isEmpty;

    public boolean isEmpty() {
        return isEmpty;
    }

    public void initialize(){
        invitationListView.setFocusTraversable(false);

    }

    public void fillInvitationsListView(ArrayList<String> invitations){
        Platform.runLater(()->{
            CustomCell customCell;
            isEmpty = invitations.isEmpty();
            for (int i = 0; i < invitations.size(); i++) {
                customCell = new CustomCell();
                customCell.updateItem(invitations.get(i), true);
                invitationListView.getItems().add(i, customCell);
            }
        });
    }

    public void deleteRecordFromListView(String nick){
        Platform.runLater(()->{
            for (int i = 0; i < invitationListView.getItems().size(); i++) {
                if( nick.equals(invitationListView.getItems().get(i).getName()) ){
                    invitationListView.getItems().remove(i);
                    isEmpty = invitationListView.getItems().isEmpty();
                    if(isEmpty){
                        Main.invitationStage.hide();
                    }
                    break;
                }
            }
        });
    }
}
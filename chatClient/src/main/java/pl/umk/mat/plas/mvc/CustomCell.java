package pl.umk.mat.plas.mvc;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.networking.ManageConnection;

public class CustomCell extends ListCell<String> {
    private Button acceptButton, rejectButton;
    private Label name ;
    private GridPane pane ;

    public String getName() {
        return name.getText();
    }

    public CustomCell() {
        super();

        acceptButton = new Button("Akceptuj");
        acceptButton.setMinWidth(70);

        acceptButton.setOnAction(event -> {
            ManageConnection.clientApp.sendInvitationResponse(name.getText(), "ACCEPT");
            Main.invitationViewController.deleteRecordFromListView(name.getText());
        });

        rejectButton = new Button("Odrzuć");
        rejectButton.setMinWidth(70);

        rejectButton.setOnAction(event->{
            Main.invitationViewController.deleteRecordFromListView(name.getText());
            ManageConnection.clientApp.sendInvitationResponse(name.getText(), "REJECT");
        });

        name = new Label();
        name.setMinWidth(330);
        pane = new GridPane();
        pane.add(name, 0, 0);
        pane.add(acceptButton, 1, 0);
        pane.add(rejectButton, 2,0);
        setText(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setEditable(false);
        if (item != null) {
            name.setText(item);
            setGraphic(pane);
        } else {
            setGraphic(null);
        }
    }


}
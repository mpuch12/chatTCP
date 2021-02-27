package pl.umk.mat.plas.main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.umk.mat.plas.mvc.ChatViewController;
import pl.umk.mat.plas.mvc.GroupGeneratorController;
import pl.umk.mat.plas.mvc.InvitationViewController;
import pl.umk.mat.plas.mvc.LoginPanelController;
import pl.umk.mat.plas.networking.ManageConnection;


public class Main extends Application {
    public static LoginPanelController loginPanelController;
    public static ChatViewController chatViewController;
    public static InvitationViewController invitationViewController;
    public static GroupGeneratorController groupGeneratorController;
    public static Scene actualScene;
    public static Stage invitationStage;
    public static Stage groupGeneratorStage;
    public static Stage chatViewStage;
    public static Stage loginViewStage;

    public static void main(String[] args) {
        launch(args);


    }
        @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginPanel.fxml"));

        Parent root = loader.load();


        //Now we have access to getController() through the instance... don't forget the type cast
        loginPanelController = (LoginPanelController) loader.getController();


        Scene scene = new Scene(root);
        actualScene = scene;
        stage.setScene(scene);
        stage.setTitle("Chat");
        stage.setResizable(false);
        Main.loginViewStage = stage;
            stage.setOnCloseRequest(e->{
                try {
                    ManageConnection.clientApp.sendStopConnection();
                }catch (NullPointerException ex){

                }
                stage.close();
            });
        stage.show();
    }
}

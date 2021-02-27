package pl.umk.mat.plas.mvc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.umk.mat.plas.main.Main;
import pl.umk.mat.plas.networking.ManageConnection;

import java.io.IOException;

public class LoginPanelController {

    @FXML
    private Label IPAddressLabel;

    @FXML
    private TextField IPAddressTextField;

    @FXML
    private TextField PortTextField;

    @FXML
    private Label PortLabel;

    @FXML
    private Label nicknameLabel;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    public String getIPAddressString() {
        return IPAddressTextField.getText();
    }

    public Button getDisconnectButton() {
        return disconnectButton;
    }

    public String getNicknameString() {
        return nicknameTextField.getText();
    }

    public String getPasswordString() {
        return passwordField.getText();
    }


    public void initialize() {
        ManageConnection manageConnection = new ManageConnection();

        disconnectButton.setDisable(true);
        nicknameTextField.setDisable(true);
        passwordField.setDisable(true);
        loginButton.setDisable(true);
        registerButton.setDisable(true);

        connectButton.setOnAction(actionEvent ->{
            if(!IPAddressTextField.getText().isEmpty() && !PortTextField.getText().isEmpty()) {
                String IP = IPAddressTextField.getText();
                int port = Integer.parseInt(PortTextField.getText());

                if (manageConnection.initChat(IP, port)) {
                    showPopupWindow("Po\u0142\u0105czono", Alert.AlertType.CONFIRMATION);

                    PortTextField.setDisable(true);
                    IPAddressTextField.setDisable(true);
                    loginButton.setDisable(false);
                    nicknameTextField.setDisable(false);
                    passwordField.setDisable(false);
                    registerButton.setDisable(false);
                    connectButton.setDisable(true);
                    disconnectButton.setDisable(false);
                } else
                    showPopupWindow("Brak po\u0142\u0105czenia", Alert.AlertType.ERROR);
            }else {
                showPopupWindow("Brak po\u0142\u0105czenia", Alert.AlertType.ERROR);
            }
        } );
        disconnectButton.setOnAction(event -> {
            ManageConnection.clientApp.sendStopConnection();
            PortTextField.setDisable(false);
            IPAddressTextField.setDisable(false);
            nicknameTextField.setDisable(true);
            passwordField.setDisable(true);
            registerButton.setDisable(true);
            loginButton.setDisable(true);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
        });
        registerButton.setOnAction(event -> {
            try {
                if(manageConnection.tryRegister()){
                    showPopupWindow("Pomy\u015blnie zarejstrowano.", Alert.AlertType.CONFIRMATION);
                }else {
                    showPopupWindow("Podany nick istnieje w naszej bazie.", Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        loginButton.setOnAction(event->{
            String nick = nicknameTextField.getCharacters().toString();
            String password = passwordField.getCharacters().toString();

            if(!nick.isEmpty() && !password.isEmpty()){
                boolean isStarted = false;
                    try {
                        isStarted = manageConnection.startChat();
                        if(isStarted){
                            showPopupWindow("Pomy\u015blnie zalogowano", Alert.AlertType.CONFIRMATION);
                            changeWindow();
                            manageConnection.startServer();
                        }else {
                            showPopupWindow("U\u017cytkownik o takim loginie i ha\u015ble nie istnieje", Alert.AlertType.ERROR);
                            return;
                        }

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    private void changeWindow() throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatView.fxml"));
        root = loader.load();

        Main.loginViewStage.hide();
        Main.chatViewController = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("chat");
        Main.actualScene = new Scene(root);
        stage.setScene(Main.actualScene);
        Main.chatViewStage = stage;
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e->{
            ManageConnection.clientApp.logoutUser();
            ManageConnection.clientApp.sendStopConnection();
            disconnectButton.fire();
            stage.close();
        });


        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/invitationView.fxml"));
        Parent root1 = loader1.load();

        Main.invitationViewController = loader1.getController();

        Stage stage1 = new Stage();
        stage1.setTitle("Zaproszenia");
        stage1.setScene(new Scene(root1));
        stage1.setResizable(false);
        Main.invitationStage = stage1;


        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/groupGeneratorView.fxml"));
        Parent root2 = loader2.load();

        Main.groupGeneratorController = loader2.getController();

        Stage stage2 = new Stage();
        stage2.setTitle("Tworzenie grupy.");
        stage2.setScene(new Scene(root2));
        stage2.setResizable(false);
        Main.groupGeneratorStage = stage2;

    }

    private void closeProgram() {

    }

    private void showPopupWindow(String text, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
    }

    public void clearLoginData() {
        nicknameTextField.clear();
        passwordField.clear();
    }
}


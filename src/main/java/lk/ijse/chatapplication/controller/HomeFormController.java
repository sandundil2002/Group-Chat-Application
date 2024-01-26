package lk.ijse.chatapplication.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.chatapplication.HelloApplication;
import lk.ijse.chatapplication.util.DateTimeUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;

public class HomeFormController {

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private TextField txtName;

    static String name;

    private final HashSet<String> clientNames = new HashSet<>();

    public void initialize(){
        lblTime.setText(DateTimeUtil.getFormattedTime());
        lblDate.setText(DateTimeUtil.getFormattedDate());
        loadServerForm();
    }

    @FXML
    private void btnJoinOnAction() {
        if (validateName() && checkClientName()){
            loadClientForm();
            clearField();
        }
    }

    private boolean validateName() {
        name = txtName.getText();
        boolean isNameValidated = Pattern.compile("^[A-Za-z]{1,20}$").matcher(name).matches();

        if (!isNameValidated) {
            new Alert(Alert.AlertType.WARNING,"Please enter a valid name").show();
            txtName.setStyle("-fx-border-color:#ff0000;");
            txtName.requestFocus();
            clearField();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkClientName(){
        String inputName = txtName.getText();

        if (clientNames.contains(inputName)){
            new Alert(Alert.AlertType.WARNING, "This name is already using").show();
            txtName.setStyle("-fx-border-color:#ff0000;");
            txtName.requestFocus();
            clearField();
            return false;
        } else {
            clientNames.add(inputName);
            return true;
        }
    }

    private void loadClientForm(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/view/client_form.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(Event::consume);
            stage.setTitle(txtName.getText()+" Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadServerForm(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/view/server_form.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(Event::consume);
            double leftX = 0;
            double topY = 100;
            stage.setX(leftX);
            stage.setY(topY);
            stage.setTitle("Server Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearField(){
        txtName.clear();
        txtName.requestFocus();
    }
}
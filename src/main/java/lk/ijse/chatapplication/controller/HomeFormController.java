package lk.ijse.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.chatapplication.HelloApplication;

import java.io.IOException;
import java.util.regex.Pattern;

public class HomeFormController {
    @FXML
    private TextField txtName;
    String name = "";

    @FXML
    private void btnJoinOnAction(ActionEvent actionEvent) {
        if (validateName()){
            loadClientForm();
            clearField();
        } else {
            new Alert(Alert.AlertType.WARNING,"Please enter a valid name").show();
            clearField();
        }
    }

    private boolean validateName() {
        name = txtName.getText();
        boolean isNameValidated = Pattern.compile("^[A-z]{1,20}$").matcher(name).matches();

        if (!isNameValidated) {
            txtName.requestFocus();
            txtName.setStyle("-fx-border-color:#ff0000;");
            return false;
        } else
            return true;
    }

    private void loadClientForm(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/view/client_form.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Client");
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
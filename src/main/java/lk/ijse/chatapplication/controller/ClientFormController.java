package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.chatapplication.util.EmojiUtil;
import lk.ijse.chatapplication.util.TimeUtil;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientFormController {

    @FXML
    private Label lblTime;

    @FXML
    private AnchorPane emojiPane;

    @FXML
    private VBox txtVbox;

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblName;

    @FXML
    private JFXButton btnBack;

    private Socket socket;

    private DataOutputStream dataOutputStream;

    private DataInputStream dataInputStream;

    public static String clientName;

    private static String message;

    private String displayMsg;

    private final EmojiUtil emojiUtil = new EmojiUtil();

    public void initialize() {
        lblName.setText(HomeFormController.name);
        clientName = lblName.getText();
        TimeUtil.updateRealTime(lblTime);
        emojiPane.setVisible(false);

        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3002);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    message = dataInputStream.readUTF();
                    if (!message.startsWith(lblName.getText()) && message.contains(":")){
                        Platform.runLater(() -> {
                            Label msg = new Label();
                            String style = "-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5px;";
                            msg.setStyle(style);
                            msg.setText(message);
                            Platform.runLater(()->txtVbox.getChildren().addAll(msg));
                        });

                    } else if (message.startsWith("img")){
                        //Image img = new Image();
                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);

                        Label imageLabel = new Label();
                        String style = "-fx-background-color: gray; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5px;";
                        imageLabel.setStyle(style);

                        HBox hBox = new HBox(12, imageView, imageLabel);
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                        txtVbox.setAlignment(Pos.BOTTOM_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);

                        Platform.runLater(() -> txtVbox.getChildren().add(hBox));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean displayMsg(){
        emojiPane.setVisible(false);
        if (validateMsg()) {
            String newMsg = txtMsg.getText();
            Label label = new Label(newMsg);
            HBox hBox = new HBox(label);
            String style = "-fx-background-color: #44BCFB; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5px;";
            label.setStyle(style);
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.setStyle("-fx-border-radius: 5px");
            Platform.runLater(()->txtVbox.getChildren().addAll(hBox));
            return true;
        } else {
            return false;
        }
    }

    @FXML
    private void btnSendOnAction() {
        emojiPane.setVisible(false);
        if (displayMsg()) {
            try {
                String sendMsg = lblName.getText() + " : " + txtMsg.getText();
                dataOutputStream.writeUTF(sendMsg);
                dataOutputStream.flush();
                txtMsg.clear();
                txtMsg.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnBackOnAction() {
        try {
            String disconnect = lblName.getText();
            dataOutputStream.writeUTF(disconnect);
            dataOutputStream.flush();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnImageOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        try {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                String imagePath = selectedFile.toURI().toString();

                Image img = new Image(imagePath);
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);

                Label image = new Label(lblName.getText(), imageView);
                String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5px;";
                image.setStyle(style);

                HBox hBox = new HBox(12, imageView, image);
                hBox.setAlignment(Pos.CENTER_RIGHT);
                txtVbox.setAlignment(Pos.TOP_LEFT);

                Platform.runLater(() -> txtVbox.getChildren().add(hBox));

                byte[] imageBytes = convertImageToByteArray(imagePath);

                dataOutputStream.writeInt(imageBytes.length);
                dataOutputStream.write(imageBytes);
                //dataOutputStream.writeUTF(lblName.getText());
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] convertImageToByteArray(String filePath){
        try {
            URI uri = URI.create(filePath);
            Path path = Paths.get(uri);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateMsg(){
        if (txtMsg.getText().trim().isEmpty()) {
            txtMsg.requestFocus();
            txtMsg.setStyle("-fx-border-color:#ff0000;");
            return false;
        } else {
            txtMsg.setStyle("-fx-border-color:black;");
            return true;
        }
    }

    @FXML
    private void emoji1OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj1);
    }

    @FXML
    private void emoji2OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj2);
    }

    @FXML
    private void emoji3OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj3);
    }

    @FXML
    private void emoji4OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj4);
    }

    @FXML
    private void emoji5OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj5);
    }

    @FXML
    private void emoji6OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj6);
    }

    @FXML
    private void emoji7OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj7);
    }

    @FXML
    private void emoji8OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj8);
    }

    @FXML
    private void emoji9OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj9);
    }

    @FXML
    private void emoji10OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj10);
    }

    @FXML
    private void emoji11OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj11);
    }

    @FXML
    private void emoji12OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj12);
    }

    @FXML
    public void emoji13OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj13);
    }

    @FXML
    public void emoji14OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj14);
    }

    @FXML
    public void emoji15OnAction() {
        displayMsg = txtMsg.getText();
        txtMsg.setText(displayMsg + emojiUtil.emj15);
    }

    @FXML
    private void btnEmojiOnAction() {emojiPane.setVisible(false); }

    @FXML
    private void emojiPaneOnAction() { emojiPane.setVisible(true); }

    @FXML
    private void TextFieldMoveOnAction() { emojiPane.setVisible(false);}

    @FXML
    private void txtPaneMoveOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void imgMoveOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void sendMoveOnAction() { emojiPane.setVisible(false); }
}

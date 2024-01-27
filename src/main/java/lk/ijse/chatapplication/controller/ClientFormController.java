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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
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
                    if (!message.startsWith(lblName.getText()) && !message.contains("-")){
                        Platform.runLater(() -> {
                            Text textNode = new Text(message);
                            txtVbox.getChildren().add(textNode);
                        });

                    } else if (!message.startsWith(lblName.getText()) && message.contains("-")){
                        HBox hBox = new HBox(15);
                        Label emoji = new Label();
                        String style = "-fx-background-color: #FAF56E; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 5px;";
                        emoji.setStyle(style);
                        emoji.setText(message);
                        hBox.getChildren().add(emoji);
                        Platform.runLater(()->txtVbox.getChildren().addAll(hBox));

                    } else if (!message.startsWith(lblName.getText())){
                        Image img = new Image(message);
                        ImageView imageView = new ImageView(img);
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);

                        Label imageLabel = new Label();
                        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5px;";
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
            String newMsg = "Me: " + txtMsg.getText();
            Text textNode = new Text(newMsg);
            TextFlow textFlow = new TextFlow(textNode);
            textFlow.setTextAlignment(TextAlignment.RIGHT);
            txtVbox.getChildren().add(textFlow);
            return true;
        }
        return false;
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

    private void sendEmoji(String emo){
        try {
            String sendEmoji = lblName.getText() + " - " + emo;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji1OnAction() {
        txtMsg.setText(emojiUtil.emj1);
    }

    @FXML
    private void emoji2OnAction() {
        txtMsg.setText(emojiUtil.emj2);
    }

    @FXML
    private void emoji3OnAction() {
        txtMsg.setText(emojiUtil.emj3);
    }

    @FXML
    private void emoji4OnAction() {
        txtMsg.setText(emojiUtil.emj4);
    }

    @FXML
    private void emoji5OnAction() {
        txtMsg.setText(emojiUtil.emj5);
    }

    @FXML
    private void emoji6OnAction() {
        txtMsg.setText(emojiUtil.emj6);
    }

    @FXML
    private void emoji7OnAction() {
        txtMsg.setText(emojiUtil.emj7);
    }

    @FXML
    private void emoji8OnAction() {
        txtMsg.setText(emojiUtil.emj8);
    }

    @FXML
    private void emoji9OnAction() {
        txtMsg.setText(emojiUtil.emj9);
    }

    @FXML
    private void emoji10OnAction() {
        txtMsg.setText(emojiUtil.emj10);
    }

    @FXML
    private void emoji11OnAction() {
        txtMsg.setText(emojiUtil.emj11);
    }

    @FXML
    private void emoji12OnAction() {
        txtMsg.setText(emojiUtil.emj12);
    }

    @FXML
    public void emoji13OnAction() {
        txtMsg.setText(emojiUtil.emj13);
    }

    @FXML
    public void emoji14OnAction() {
        txtMsg.setText(emojiUtil.emj14);
    }

    @FXML
    public void emoji15OnAction() {
        txtMsg.setText(emojiUtil.emj15);
    }

    @FXML
    private void btnEmojiOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void emojiPaneOnAction() {
        emojiPane.setVisible(true);
    }

    @FXML
    private void TextFieldMoveOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void txtPaneMoveOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void imgMoveOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void sendMoveOnAction() {
        emojiPane.setVisible(false);
    }
}

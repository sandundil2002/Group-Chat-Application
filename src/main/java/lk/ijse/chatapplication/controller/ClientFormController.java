package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ClientFormController {
    //@FXML
    //private ImageView img1;

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

    Socket socket;

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    static String clientName;

    static String message;

    PrintWriter printWriter;

    private final byte[] emojiByteCode = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x82};

    private final String img1 = new String(emojiByteCode, StandardCharsets.UTF_8);

    public void initialize() {
        lblName.setText(HomeFormController.name);
        clientName = lblName.getText();
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
                        emoji.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                        emoji.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
                        emoji.setStyle("-fx-font-size: 18");
                        emoji.setText(message);
                        hBox.getChildren().add(emoji);
                        Platform.runLater(()->txtVbox.getChildren().addAll(hBox));
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
    private void btnSendOnAction(ActionEvent actionEvent) {
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
    private void btnBackOnAction(ActionEvent actionEvent) {
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
    private void btnEmojiOnAction(ActionEvent actionEvent) {
        emojiPane.setVisible(false);
    }

    @FXML
    private void btnImageOnAction(ActionEvent actionEvent) {
        emojiPane.setVisible(false);
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
    private void mouseMove(MouseEvent mouseEvent) {
        emojiPane.setVisible(true);
    }

    @FXML
    private void btnTextFieldOnAction(MouseEvent mouseEvent) {
        emojiPane.setVisible(false);
    }

    private void displayEmoji(){
        HBox hBox = new HBox(15);
        String emoji =("Me - "+img1);
        Label label = new Label();
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        label.setStyle("-fx-font-size: 18");
        label.setText(emoji);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(label);
        Platform.runLater(()->txtVbox.getChildren().addAll(hBox));
    }

    @FXML
    private void emoji1OnAction(MouseEvent mouseEvent) {
        emojiPane.setVisible(false);
        displayEmoji();
        try {
            String sendEmoji = lblName.getText() + " - " + img1;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

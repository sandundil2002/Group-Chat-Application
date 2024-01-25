package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    private final byte[] emojiByteCode1 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x82};
    private final byte[] emojiByteCode2 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8D};
    private final byte[] emojiByteCode3 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xAD};
    private final byte[] emojiByteCode4 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xB5};
    private final byte[] emojiByteCode5 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xB6};
    private final byte[] emojiByteCode6 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x87};
    private final byte[] emojiByteCode7 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xB1};
    private final byte[] emojiByteCode8 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xA1};
    private final byte[] emojiByteCode9 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xB4};
    private final byte[] emojiByteCode10 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x97};
    private final byte[] emojiByteCode11 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xB0};
    private final byte[] emojiByteCode12 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8D};
    private final byte[] emojiByteCode13 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0xAc};
    private final byte[] emojiByteCode14 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8F};
    private final byte[] emojiByteCode15 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xAF};

    private final String emj1 = new String(emojiByteCode1, StandardCharsets.UTF_8);
    private final String emj2 = new String(emojiByteCode2, StandardCharsets.UTF_8);
    private final String emj3 = new String(emojiByteCode3, StandardCharsets.UTF_8);
    private final String emj4 = new String(emojiByteCode4, StandardCharsets.UTF_8);
    private final String emj5 = new String(emojiByteCode5, StandardCharsets.UTF_8);
    private final String emj6 = new String(emojiByteCode6, StandardCharsets.UTF_8);
    private final String emj7 = new String(emojiByteCode7, StandardCharsets.UTF_8);
    private final String emj8 = new String(emojiByteCode8, StandardCharsets.UTF_8);
    private final String emj9 = new String(emojiByteCode9, StandardCharsets.UTF_8);
    private final String emj10 = new String(emojiByteCode10, StandardCharsets.UTF_8);
    private final String emj11 = new String(emojiByteCode11, StandardCharsets.UTF_8);
    private final String emj12 = new String(emojiByteCode12, StandardCharsets.UTF_8);
    private final String emj13 = new String(emojiByteCode13, StandardCharsets.UTF_8);
    private final String emj14 = new String(emojiByteCode14, StandardCharsets.UTF_8);
    private final String emj15 = new String(emojiByteCode15, StandardCharsets.UTF_8);


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
    private void btnEmojiOnAction() {
        emojiPane.setVisible(false);
    }

    @FXML
    private void btnImageOnAction() {
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

    private void displayEmoji(String imoji){
        HBox hBox = new HBox(15);
        String emoji =("Me - " + imoji);
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
    private void emoji1OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj1);
        try {
            String sendEmoji = lblName.getText() + " - " + emj1;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji2OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj2);
        try {
            String sendEmoji = lblName.getText() + " - " + emj2;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji3OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj3);
        try {
            String sendEmoji = lblName.getText() + " - " + emj3;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji4OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj4);
        try {
            String sendEmoji = lblName.getText() + " - " + emj4;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji5OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj5);
        try {
            String sendEmoji = lblName.getText() + " - " + emj5;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji6OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj6);
        try {
            String sendEmoji = lblName.getText() + " - " + emj6;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji7OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj7);
        try {
            String sendEmoji = lblName.getText() + " - " + emj7;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji8OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj8);
        try {
            String sendEmoji = lblName.getText() + " - " + emj8;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji9OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj9);
        try {
            String sendEmoji = lblName.getText() + " - " + emj9;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji10OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj10);
        try {
            String sendEmoji = lblName.getText() + " - " + emj10;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji11OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj11);
        try {
            String sendEmoji = lblName.getText() + " - " + emj11;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void emoji12OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj12);
        try {
            String sendEmoji = lblName.getText() + " - " + emj12;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void emoji13OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj13);
        try {
            String sendEmoji = lblName.getText() + " - " + emj13;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void emoji14OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj14);
        try {
            String sendEmoji = lblName.getText() + " - " + emj14;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void emoji15OnAction() {
        emojiPane.setVisible(false);
        displayEmoji(emj15);
        try {
            String sendEmoji = lblName.getText() + " - " + emj15;
            dataOutputStream.writeUTF(sendEmoji);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

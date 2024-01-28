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
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Base64;

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

                    } else if (!message.startsWith(lblName.getText()) && message.contains("-")){
                        String modifiedMessage = message.substring(message.indexOf("-")+1);
                        String removedMessage = message.substring(0, message.indexOf("-"));
                        System.out.println(removedMessage);
                        Image image = convertStringToImage(modifiedMessage);
                        ImageView imageView = new ImageView(image);
                        Platform.runLater(() -> txtVbox.getChildren().addAll(imageView));
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
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(txtMsg.getScene().getWindow());

        if (file != null) {
            try {
                ImageView imageView = new ImageView(file.toURI().toString());
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);

                HBox hBox = new HBox(12, imageView);
                hBox.setAlignment(Pos.CENTER_RIGHT);
                txtVbox.setAlignment(Pos.TOP_LEFT);

                Platform.runLater(() -> txtVbox.getChildren().add(hBox));

                String imageAsTextToSend = convertImageToString(imageView.getImage());
                String imgText = imageAsTextToSend;
                System.out.println(imgText);
                dataOutputStream.writeUTF(lblName.getText() + "-" + imgText);
                dataOutputStream.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private Image convertStringToImage(String imageAsString) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(imageAsString);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        return new Image(inputStream);
    }

    private String convertImageToString(Image image) throws IOException {
        double maxWidth = 600;
        double maxHeight = 400;
        double width = image.getWidth();
        double height = image.getHeight();

        if (width > maxWidth || height > maxHeight) {
            double scaleFactor = Math.min(maxWidth / width, maxHeight / height);
            width *= scaleFactor;
            height *= scaleFactor;
        }

        BufferedImage resizedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(SwingFXUtils.fromFXImage(image, null), 0, 0, (int) width, (int) height, null);
        g.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);

        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
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

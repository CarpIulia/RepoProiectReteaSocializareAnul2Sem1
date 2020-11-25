package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CererePrietenieService;

import java.awt.*;

public class FriendshipRequestMessageController {
    private CererePrietenieService cererePrietenieService;
    private Utilizator utilizator;
    private Utilizator newFriend;
    Stage dialogStage;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private void initialize() {
    }

    public void setServices(CererePrietenieService cererePrietenieService) {
        this.cererePrietenieService = cererePrietenieService;
    }

    public void setUsers(Utilizator utilizator, Utilizator newFriend) {
        this.utilizator = utilizator;
        this.newFriend = newFriend;
    }

    public void setStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    public void skipHandle() {
        cererePrietenieService.addCerere(utilizator.getId(), newFriend.getId(), "pending", "Fara mesaj.");
        AlertErrorMessage.showMessage(null, Alert.AlertType.CONFIRMATION, "Friendship request" ,"Friendship request sent.");
        dialogStage.close();
    }

    public void sendHandle() {
        cererePrietenieService.addCerere(utilizator.getId(), newFriend.getId(), "pending", messageTextArea.getText());
        AlertErrorMessage.showMessage(null, Alert.AlertType.CONFIRMATION, "Friendship request" ,"Friendship request sent.");
        dialogStage.close();
    }

    public void cancelHandle(ActionEvent actionEvent) {
        dialogStage.close();
    }
}

package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import javax.swing.text.Style;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ComposeMessageController {
    ObservableList<Utilizator> modelPrieteniUtilizator = FXCollections.observableArrayList();

    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    CererePrietenieService cererePrietenieService;
    MessageService messageService;
    Utilizator utilizator;
    Stage composeMessageStage;

    private  List<Utilizator> to = new ArrayList<>();

    @FXML
    private TextField currentUser;
    @FXML
    private TableView<Utilizator> tableViewPrieteniTo;
    @FXML
    private TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    private TableColumn<Utilizator, String> tableColumnLastName;
    @FXML
    private TextField textFieldTo;
    @FXML
    private TextArea textAreaMessage;

    @FXML
    private void initialize() {
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableViewPrieteniTo.setItems(modelPrieteniUtilizator);
        textFieldTo.setEditable(false);
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService prietenieService, CererePrietenieService cererePrietenieService, MessageService messageService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.cererePrietenieService = cererePrietenieService;
        this.messageService =messageService;
    }

    public void setCurrentUser(Utilizator utilizator) {
        this.utilizator=utilizator;
        this.utilizator = utilizator;
        String username = "Hello " + utilizator.getFirstName() + " " + utilizator.getLastName() + "!";
        currentUser.setText(username);
        currentUser.setEditable(false);
        initModelPrieteniUtilizator();
    }

    private void initModelPrieteniUtilizator() {
        Iterable<Utilizator> prieteni = utilizator.getFriends();
        List<Utilizator> prieteniList = StreamSupport.stream(prieteni.spliterator(), false)
                .collect(Collectors.toList());
        modelPrieteniUtilizator.setAll(prieteniList);
    }

    public void createToList() {
        Utilizator prieten = tableViewPrieteniTo.getSelectionModel().getSelectedItem();
        if(prieten != null) {
            if(!to.contains(prieten)) {
                to.add(prieten);
                textFieldTo.setText(textFieldTo.getText() + prieten.getLastName() + " " + prieten.getFirstName() + ", ");
            }
            else
                AlertErrorMessage.showErrorMessage(null, "Already added.");
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");
    }

    public void sendMessage(ActionEvent actionEvent) {
        if(to.isEmpty())
            AlertErrorMessage.showErrorMessage(null, "Add who do you want to send the message to.");
        else {
            if (textAreaMessage.getText().isEmpty())
                AlertErrorMessage.showErrorMessage(null, "Write a message.");
            else
            {
                messageService.addMessage(utilizator, to, textAreaMessage.getText(), LocalDateTime.now());
                textAreaMessage.clear();
                textFieldTo.clear();
                to.clear();
                AlertErrorMessage.showMessage(null, Alert.AlertType.CONFIRMATION, "Message", "Message sent successfully.");
            }
        }

    }

    public void backToHomePage(ActionEvent actionEvent) {
        try {
            FXMLLoader homePageLoader = new FXMLLoader();
            homePageLoader.setLocation(getClass().getResource("/views/homePageView.fxml"));
            AnchorPane homePageLayout = homePageLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Home Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(homePageLayout);
            dialogStage.setScene(scene);

            HomePageController homePageController = homePageLoader.getController();
            homePageController.setServices(utilizatorService, prietenieService, cererePrietenieService, messageService);
            homePageController.setCurrentUser(utilizator);
            homePageController.setStage(dialogStage);

            dialogStage.show();
            composeMessageStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage composeMessageStage) {
        this.composeMessageStage=composeMessageStage;
    }

    public void clearMessageFields() {
        textAreaMessage.clear();
        to.clear();
        textFieldTo.clear();
    }

    public void handleInbox(ActionEvent actionEvent) {
        try {
            FXMLLoader inboxPageLoader = new FXMLLoader();
            inboxPageLoader.setLocation(getClass().getResource("/views/inboxView.fxml"));
            AnchorPane inboxPageLayout = inboxPageLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Inbox");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(inboxPageLayout);
            dialogStage.setScene(scene);

            InboxController inboxController = inboxPageLoader.getController();
            inboxController.setServices(utilizatorService,prietenieService, cererePrietenieService, messageService);
            inboxController.setCurrentUser(utilizator);
            inboxController.setStage(dialogStage);

            dialogStage.show();
            composeMessageStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleYourRequests(ActionEvent actionEvent) {
        try {
            FXMLLoader requestsPageLoader = new FXMLLoader();
            requestsPageLoader.setLocation(getClass().getResource("/views/friendshipRequestsView.fxml"));
            AnchorPane requestsPageLayout = requestsPageLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(requestsPageLayout);
            dialogStage.setScene(scene);

            FriendshipRequestsController requestsController = requestsPageLoader.getController();
            requestsController.setServices(utilizatorService,prietenieService, cererePrietenieService, messageService);
            requestsController.setCurrentUser(utilizator);
            requestsController.setStage(dialogStage);

            dialogStage.show();
            composeMessageStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.Constants;
import socialnetwork.utils.events.EntityChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InboxController implements Observer<EntityChangeEvent> {
    ObservableList<MessageDTO> modelMessages = FXCollections.observableArrayList();
    ObservableList<String> modelConversation = FXCollections.observableArrayList();

    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    CererePrietenieService cererePrietenieService;
    MessageService messageService;
    Utilizator utilizator;
    Stage inboxStage;

    @FXML
    private TextField currentUser;
    @FXML
    private TableView<MessageDTO> tableViewMessages;
    @FXML
    private TableColumn<MessageDTO, String> tableColumnFirstName;
    @FXML
    private TableColumn<MessageDTO, String> tableColumnLastName;
    @FXML
    private TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    private TableColumn<MessageDTO, LocalDateTime> tableColumnDate;
    @FXML
    private TextArea replyMessageTextArea;
    @FXML
    private ListView<String> conversationListView;

    @FXML
    private void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("lastName"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<MessageDTO, LocalDateTime>("date"));
        tableViewMessages.setItems(modelMessages);
        conversationListView.setItems(modelConversation);
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService prietenieService, CererePrietenieService cererePrietenieService, MessageService messageService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService= prietenieService;
        this.cererePrietenieService = cererePrietenieService;
        this.messageService = messageService;
        messageService.addObserver(this);
    }

    public void setCurrentUser(Utilizator utilizator) {
        this.utilizator = utilizator;
        String username = "Hello " + utilizator.getFirstName() + " " + utilizator.getLastName() + "!";
        currentUser.setText(username);
        currentUser.setEditable(false);
        initModelMessages();
    }

    public void setStage(Stage inboxStage) {
        this.inboxStage = inboxStage;
    }

    private void initModelMessages() {
        Iterable<Message> messages = messageService.getAll();
        List<Message> messagesList = StreamSupport.stream(messages.spliterator(), false)
                .filter(m->{
                    final boolean[] ms = {false};
                    m.getTo().forEach(u->{
                        if(u.getId()==utilizator.getId())
                            ms[0] =true;
                    });
                    return ms[0];
                })
                .collect(Collectors.toList());
        List<MessageDTO> messagesDTOList = messagesList
                .stream()
                .map(m->{
                    return new MessageDTO(m.getId(),m.getFrom(), m.getMessage(), m.getDate().format(Constants.DATE_TIME_FORMATTER));
                })
                .collect(Collectors.toList());
        modelMessages.setAll(messagesDTOList);
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
            inboxStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleComposeMessage(ActionEvent actionEvent) {
        try {
            FXMLLoader composeMessagePageLoader = new FXMLLoader();
            composeMessagePageLoader.setLocation(getClass().getResource("/views/composeMessageView.fxml"));
            AnchorPane composeMessagePageLayout = composeMessagePageLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Compose Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(composeMessagePageLayout);
            dialogStage.setScene(scene);

            ComposeMessageController composeMessageController = composeMessagePageLoader.getController();
            composeMessageController.setServices(utilizatorService,prietenieService, cererePrietenieService, messageService);
            composeMessageController.setCurrentUser(utilizator);
            composeMessageController.setStage(dialogStage);

            dialogStage.show();
            inboxStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMessagesHandle() {
        MessageDTO messageDTO = tableViewMessages.getSelectionModel().getSelectedItem();
        Message message = messageService.findOne(messageDTO.getId());
        List<String> conversationList = new ArrayList<>();
        do {
            String m = message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + ": " + message.getMessage();
            conversationList.add(m);
            message = messageService.findOne(message.getReply());
        } while(message != null);
        modelConversation.setAll(conversationList);
    }

    public void replyToMessage(ActionEvent actionEvent) {
        MessageDTO messageDTO = tableViewMessages.getSelectionModel().getSelectedItem();
        if(messageDTO != null) {
            Message message = messageService.findOne(messageDTO.getId());

            String messageText = replyMessageTextArea.getText();
            if (!messageText.isEmpty()) {
                List<Utilizator> to = new ArrayList<>();
                to.add(message.getFrom());
                message.getTo().forEach(u -> {
                    if (u.getId() != utilizator.getId())
                        to.add(u);
                });
                messageService.addReplyMessage(utilizator, to, message, messageText, LocalDateTime.now());
                replyMessageTextArea.clear();
            }
            else
                AlertErrorMessage.showErrorMessage(null, "Write a message.");
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        showMessagesHandle();
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
            inboxStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

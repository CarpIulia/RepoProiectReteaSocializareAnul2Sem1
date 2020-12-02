package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.EntityChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRequestsController implements Observer<EntityChangeEvent> {
    ObservableList<String> modelCereriPrietenieTrimise = FXCollections.observableArrayList();

    @FXML
    private TextField currentUser;
    @FXML
    private ListView<String> listViewCereriPrietenieTrimise;

    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    CererePrietenieService cererePrietenieService;
    MessageService messageService;
    Utilizator utilizator;
    Stage requestsPageStage;

    @FXML
    private void initialize() {
        listViewCereriPrietenieTrimise.setItems(modelCereriPrietenieTrimise);
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService prietenieService, CererePrietenieService cererePrietenieService, MessageService messageService) {
        this.utilizatorService=utilizatorService;
        this.prietenieService = prietenieService;
        this.cererePrietenieService = cererePrietenieService;
        this.messageService = messageService;
        cererePrietenieService.addObserver(this);
    }

    public void setCurrentUser(Utilizator utilizator) {
        this.utilizator = utilizator;
        String username = "Hello " + utilizator.getFirstName() + " " + utilizator.getLastName() + "!";
        currentUser.setText(username);
        currentUser.setEditable(false);
        initModelCereriPrietenieTrimise();
    }

    public void setStage(Stage requestsPageStage) {
        this.requestsPageStage=requestsPageStage;
    }

    private void initModelCereriPrietenieTrimise() {
        List<String> cereriPrietenieTrimiseList = new ArrayList<>();
        cererePrietenieService.getAll().forEach(c -> {
            if(c.getId1() == utilizator.getId() && c.getStatus().equals("pending")) {
                Utilizator uc = utilizatorService.findOne(c.getId2());
                cereriPrietenieTrimiseList.add(c.getId() + ". " +uc.getLastName() + " " + uc.getFirstName());
            }
        });
        modelCereriPrietenieTrimise.setAll(cereriPrietenieTrimiseList);
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
            requestsPageStage.close();

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
            requestsPageStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            requestsPageStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModelCereriPrietenieTrimise();
    }

    public void deleteRequestHandle(ActionEvent actionEvent) {
        String cerere = listViewCereriPrietenieTrimise.getSelectionModel().getSelectedItem();
        if(cerere != null) {
            Long idc = Long.parseLong(String.valueOf(cerere.charAt(0)));
            cererePrietenieService.removeCerere(idc);
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");
    }
}

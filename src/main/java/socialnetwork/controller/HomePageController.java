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
import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.EntityChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomePageController implements Observer<EntityChangeEvent> {
    ObservableList<Utilizator> modelPrieteniUtilizator = FXCollections.observableArrayList();
    ObservableList<String> modelCereriPrietenie = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelAltiUtilizatori = FXCollections.observableArrayList();

    @FXML
    private TextField currentUser;
    @FXML
    private TableView<Utilizator> tableViewPrieteni;
    @FXML
    private TableColumn<Utilizator, String> tableColumnNume;
    @FXML
    private TableColumn<Utilizator, String> tableColumnPrenume;
    @FXML
    private ListView<String> listViewCereriPrietenie;
    @FXML
    private TextField textFieldStatus;
    @FXML
    private TableView<Utilizator> tableViewAltiUtilizatori;
    @FXML
    private TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    private TableColumn<Utilizator, String> tableColumnLastName;
    @FXML
    private TextField textFieldSearch;

    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    CererePrietenieService cererePrietenieService;
    Utilizator utilizator;

    @FXML
    private void initialize() {
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableViewPrieteni.setItems(modelPrieteniUtilizator);
        listViewCereriPrietenie.setItems(modelCereriPrietenie);
        tableViewAltiUtilizatori.setItems(modelAltiUtilizatori);
        textFieldSearch.textProperty().addListener(e->handleFilterUtilizatori());
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService prietenieService, CererePrietenieService cererePrietenieService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.cererePrietenieService = cererePrietenieService;
        prietenieService.addObserver(this);
        cererePrietenieService.addObserver(this);
    }

    public void setCurrentUser(Utilizator utilizator) {
        this.utilizator = utilizator;
        String username = "Hello " + utilizator.getFirstName() + " " + utilizator.getLastName() + "!";
        currentUser.setText(username);
        currentUser.setEditable(false);
        initModelPrieteniUtilizator();
        initModelCereriPrietenie();
        initModelAltiUtilizatori();
    }

    private void initModelPrieteniUtilizator() {
        Iterable<Utilizator> prieteni = utilizator.getFriends();
        List<Utilizator> prieteniList = StreamSupport.stream(prieteni.spliterator(), false)
                .collect(Collectors.toList());
        modelPrieteniUtilizator.setAll(prieteniList);
    }

    private void initModelCereriPrietenie() {
        List<String> cereriPrietenieList = new ArrayList<>();
        cererePrietenieService.getAll().forEach(c -> {
            if(c.getId2() == utilizator.getId()) {
                Utilizator uc = utilizatorService.findOne(c.getId1());
                cereriPrietenieList.add(c.getId() + ". " +uc.getLastName() + " " + uc.getFirstName() + ": " + c.getMesaj());
            }
        });
        modelCereriPrietenie.setAll(cereriPrietenieList);
    }

    private void initModelAltiUtilizatori() {
        Iterable<Utilizator> utilizatori = utilizatorService.getAll();
        List<Utilizator> utilizatoriList = StreamSupport.stream(utilizatori.spliterator(), false)
                .filter(otherUtilizator->otherUtilizator.getId() != utilizator.getId())
                .filter(otherUtilizator->!utilizator.getFriends().contains(otherUtilizator))
                .collect(Collectors.toList());
        modelAltiUtilizatori.setAll(utilizatoriList);
    }


    public void handleStergePrieten() {
        Utilizator prieten = tableViewPrieteni.getSelectionModel().getSelectedItem();
        if(prieten != null) {
            prietenieService.removePrietenie(utilizator.getId(), prieten.getId());
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModelPrieteniUtilizator();
        initModelCereriPrietenie();
        initModelAltiUtilizatori();
    }

    public void mouseSelectHandle() {
        String cerere = listViewCereriPrietenie.getSelectionModel().getSelectedItem();
        Long id = Long.parseLong(String.valueOf(cerere.charAt(0)));
        cererePrietenieService.getAll().forEach(c -> {
            if (c.getId() == id)
                textFieldStatus.setText(c.getStatus());
        });
        textFieldStatus.setEditable(false);
        textFieldStatus.deselect();
    }

    public void approveRequestHandle() {
        String cerere = listViewCereriPrietenie.getSelectionModel().getSelectedItem();
        if(cerere != null) {
            Long idc = Long.parseLong(String.valueOf(cerere.charAt(0)));
            Long idu = utilizator.getId();
            CererePrietenie cererePrietenie = cererePrietenieService.findOne(idc);
            if(cererePrietenie.getStatus().equals("pending")) {
                prietenieService.addPrietenie(cererePrietenie.getId1(), idu, LocalDateTime.now());
                cererePrietenie.setStatus("approved");
                cererePrietenieService.schimbareStatus(cererePrietenie);
                textFieldStatus.setText("approved");
            }
            else
                AlertErrorMessage.showErrorMessage(null, "You already responded to this request.");
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");

    }

    public void rejectRequestHandle() {
        String cerere = listViewCereriPrietenie.getSelectionModel().getSelectedItem();
        if(cerere != null) {
            Long idc = Long.parseLong(String.valueOf(cerere.charAt(0)));
            CererePrietenie cererePrietenie = cererePrietenieService.findOne(idc);
            if(cererePrietenie.getStatus().equals("pending")) {
                cererePrietenie.setStatus("rejected");
                cererePrietenieService.schimbareStatus(cererePrietenie);
                textFieldStatus.setText("rejected");
            }
            else
                AlertErrorMessage.showErrorMessage(null, "You already responded to this request.");
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");
    }

    private void handleFilterUtilizatori() {
        modelAltiUtilizatori.setAll(StreamSupport.stream(utilizatorService.getAll().spliterator(), false)
                .filter(u->u.getLastName().startsWith(textFieldSearch.getText()) || u.getFirstName().startsWith(textFieldSearch.getText()))
                .filter(otherUtilizator->otherUtilizator.getId() != utilizator.getId())
                .filter(otherUtilizator->!utilizator.getFriends().contains(otherUtilizator))
                .collect(Collectors.toList())
        );
    }

    public void friendshipRequestHandle() {
        Utilizator newFriend = tableViewAltiUtilizatori.getSelectionModel().getSelectedItem();
        if(newFriend != null) {
            showFriendshipRequestMessageDialog(newFriend);
        }
        else
            AlertErrorMessage.showErrorMessage(null, "Nothing selected.");
    }

    public void showFriendshipRequestMessageDialog(Utilizator newFriend) {
        try {
            FXMLLoader messageFRLoader = new FXMLLoader();
            messageFRLoader.setLocation(getClass().getResource("/views/friendshipRequestMessageView.fxml"));
            AnchorPane messageFRLayout = messageFRLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Request Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(messageFRLayout);
            dialogStage.setScene(scene);

            FriendshipRequestMessageController friendshipRequestMessageController = messageFRLoader.getController();
            friendshipRequestMessageController.setServices(cererePrietenieService);
            friendshipRequestMessageController.setUsers(utilizator, newFriend);
            friendshipRequestMessageController.setStage(dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

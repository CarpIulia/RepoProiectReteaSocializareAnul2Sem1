package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;

public class AuthenticationController {
    @FXML
    private TextField textFieldId;
    Stage primaryStage;

    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    CererePrietenieService cererePrietenieService;
    MessageService messageService;

    @FXML
    private void initialize() {

    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService prietenieService, CererePrietenieService cererePrietenieService, MessageService messageService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.cererePrietenieService = cererePrietenieService;
        this.messageService = messageService;
    }

    public void handleConectare() {
        Long userId = Long.parseLong(textFieldId.getText());
        Utilizator utilizator = utilizatorService.findOne(userId);
        if(utilizator != null) {
            showHomePageDialog(utilizator);
        }
    }

    private void showHomePageDialog(Utilizator utilizator) {
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
            primaryStage.close();
            textFieldId.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }
}

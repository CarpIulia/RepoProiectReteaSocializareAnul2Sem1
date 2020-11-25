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
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;

public class AuthenticationController {
    @FXML
    private TextField textFieldId;

    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    CererePrietenieService cererePrietenieService;

    @FXML
    private void initialize() {

    }

    public void setUtilizatorService(UtilizatorService utilizatorService, PrietenieService prietenieService, CererePrietenieService cererePrietenieService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.cererePrietenieService = cererePrietenieService;
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
            homePageController.setServices(utilizatorService, prietenieService, cererePrietenieService);
            homePageController.setCurrentUser(utilizator);

            dialogStage.show();
            textFieldId.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

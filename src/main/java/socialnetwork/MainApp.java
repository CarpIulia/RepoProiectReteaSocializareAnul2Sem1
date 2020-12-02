package socialnetwork;
import javafx.scene.image.Image;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.CererePrietenieValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.CererePrietenieFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.controller.AuthenticationController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;

public class MainApp extends Application {

    Repository<Long, Utilizator> userFileRepository;
    UtilizatorService utilizatorService;

    Repository<Tuple<Long,Long>, Prietenie> friendsFileRepository;
    PrietenieService prietenieService;

    Repository<Long, CererePrietenie>  cererePrietenieFileRepository;
    CererePrietenieService cererePrietenieService;

    Repository<Long, Message> messageFileRepository;
    MessageService messageService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String userFileName="data/users.csv";
        userFileRepository = new UtilizatorFile(userFileName, new UtilizatorValidator());
        utilizatorService = new UtilizatorService(userFileRepository);

        String friendsFileName = "data/friends.csv";
        friendsFileRepository =  new PrietenieFile(friendsFileName, new PrietenieValidator());
        prietenieService = new PrietenieService(friendsFileRepository, userFileRepository);

        String cerereFileName = "data/cerere.csv";
        cererePrietenieFileRepository = new CererePrietenieFile(cerereFileName, new CererePrietenieValidator());
        cererePrietenieService = new CererePrietenieService(cererePrietenieFileRepository);

        String messageFileName = "data/messages.csv";
        messageFileRepository = new MessageFile(messageFileName, new MessageValidator());
        messageService = new MessageService(messageFileRepository);

        initView(primaryStage);
        primaryStage.setWidth(400);
        primaryStage.setHeight(600);
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader authenthicationLoader = new FXMLLoader();
        authenthicationLoader.setLocation(getClass().getResource("/views/authenticationView.fxml"));
        AnchorPane authenthicationLayout = authenthicationLoader.load();
        authenthicationLayout.setId("authPane");
        primaryStage.setScene(new Scene(authenthicationLayout));
        primaryStage.getIcons().add(new Image("/images/icon.png"));

        AuthenticationController authenticationController = authenthicationLoader.getController();
        authenticationController.setServices(utilizatorService, prietenieService, cererePrietenieService, messageService);
        authenticationController.setStage(primaryStage);

    }
}

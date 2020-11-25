package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.CererePrietenieFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.UI;

public class Main {
    public static void main(String[] args) {
        //pgAdmin
        //String userFileName=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        /*String userFileName="data/users.csv";
        String friendsFileName = "data/friends.csv";
        String messageFileName = "data/messages.csv";
        String cerereFileName = "data/cerere.csv";
        Repository<Long,Utilizator> userFileRepository = new UtilizatorFile(userFileName, new UtilizatorValidator());
        UtilizatorService utilizatorService = new UtilizatorService(userFileRepository);
        Repository<Tuple<Long, Long>, Prietenie> friendsFileRepository =  new PrietenieFile(friendsFileName, new PrietenieValidator());
        PrietenieService prietenieService = new PrietenieService(friendsFileRepository, userFileRepository);
        Repository<Long, Message> messageFileRepository = new MessageFile(messageFileName, new MessageValidator());
        MessageService messageService = new MessageService(messageFileRepository);
        Repository<Long, CererePrietenie>  cererePrietenieFileRepository = new CererePrietenieFile(cerereFileName, new CererePrietenieValidator());
        CererePrietenieService cererePrietenieService = new CererePrietenieService(cererePrietenieFileRepository);
        UI ui = new UI(utilizatorService, prietenieService, messageService, cererePrietenieService);
        ui.run();
        */
        MainApp.main(args);
    }
}



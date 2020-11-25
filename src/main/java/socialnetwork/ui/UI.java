package socialnetwork.ui;
import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepoException;
import socialnetwork.service.CererePrietenieService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UI {
    private UtilizatorService utilizatorService;
    private PrietenieService prietenieService;
    private MessageService messageService;
    private CererePrietenieService cererePrietenieService;
    private Scanner scan = new Scanner(System.in);

    public UI(UtilizatorService utilizatorService, PrietenieService prietenieService, MessageService messageService, CererePrietenieService cererePrietenieService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.messageService = messageService;
        this.cererePrietenieService = cererePrietenieService;
    }

    public void uiAddUtilizator() {
        System.out.println("Introduceti prenumele: ");
        String firstName = scan.nextLine();
        System.out.println("Introduceti numele: ");
        String lastName = scan.nextLine();
        try {
            try {
                utilizatorService.addUtilizator(firstName, lastName);
                System.out.println("Utilizator adaugat cu succes.\n");
            } catch (RepoException repoException) {
                System.out.println("Utilizatorul" + repoException.getMessage());
            }
        } catch (ValidationException validationException) {
            System.out.println(validationException.getMessage());
        }
    }

    public void uiRemoveUtilizator() {
        System.out.println("Introduceti id-ul: ");
        long id = Long.valueOf(scan.nextLine());
        try {
            Utilizator utilizator = utilizatorService.findOne(id);
            List<Long> prieteni  = new ArrayList<>();
            utilizator.getFriends().forEach(prieten->{
                prieteni.add(prieten.getId());
            });
            prieteni.forEach(prieten->{
                prietenieService.removePrietenie(utilizator.getId(), prieten);
            });
            utilizatorService.removeUtilizator(id);
            System.out.println("Utilizatorul a fost sters.\n");
        } catch (RepoException repoException) {
            System.out.println("Utilizatorul" + repoException.getMessage());
        }
    }

    public void uiAddPrietenie() {
        System.out.println("Introduceti primul id: ");
        long id1 = Long.valueOf(scan.nextLine());
        System.out.println("Introduceti al doilea id: ");
        long id2 = Long.valueOf(scan.nextLine());
        if(id1 > id2) {
            Long aux=id1;
            id1=id2;
            id2=aux;
        }
        try {
            prietenieService.addPrietenie(id1, id2, LocalDateTime.now());
            System.out.println("Prietenie adaugata cu succes.\n");
        } catch (RepoException repoException) {
            System.out.println("Prietenia" + repoException.getMessage());
        }
    }

    public void uiAfisarePrieteni() {
        System.out.println("Introduceti id-ul utilizatorului: ");
        long id = Long.valueOf(scan.nextLine());
        Utilizator utilizator = utilizatorService.findOne(id);
        System.out.println("Lista de prieteni ai utilizatorului " + utilizator.getFirstName() + " " + utilizator.getLastName() + ":");
        List<String> prieteni = new ArrayList<String>();
        utilizator.getFriends().forEach(prieten->{
            Prietenie prietenie = prietenieService.findOne(id, prieten.getId());
            prieteni.add(prieten.getFirstName() + " | " + prieten.getLastName() + " | " + prietenie.getDate().format(Constants.DATE_TIME_FORMATTER));
        });
        prieteni
                .stream()
                .forEach(System.out::println);
    }

    public void uiAfisarePrieteniLuna() {
        System.out.println("Introduceti id-ul utilizatorului: ");
        long id = Long.valueOf(scan.nextLine());
        System.out.println("Introduceti luna: ");
        int luna = Integer.valueOf(scan.nextLine());
        System.out.println("Introduceti anul: ");
        int an = Integer.valueOf(scan.nextLine());
        Utilizator utilizator = utilizatorService.findOne(id);
        System.out.println("Lista de prieteni ai utilizatorului " + utilizator.getFirstName() + " " + utilizator.getLastName() + ":");
        List<String> prieteni = new ArrayList<>();
        utilizator.getFriends().forEach(prieten->{
            Prietenie prietenie = prietenieService.findOne(id, prieten.getId());
            prieteni.add(prieten.getFirstName() + " | " + prieten.getLastName() + " | " + prietenie.getDate().format(Constants.DATE_TIME_FORMATTER));
        });
        prieteni
                .stream()
                .filter(x->{
                    if(luna <=9)
                        return x.contains(an + "-0" + luna + "-");
                    else
                        return x.contains(an + "-" + luna + "-");
                })
                .forEach(System.out::println);
    }



    public void uiRemovePrietenie() {
        System.out.println("Introduceti primul id: ");
        long id1 = Long.valueOf(scan.nextLine());
        System.out.println("Introduceti al doilea id: ");
        long id2 = Long.valueOf(scan.nextLine());
        if(id1 > id2) {
            Long aux=id1;
            id1=id2;
            id2=aux;
        }
        try {
            prietenieService.removePrietenie(id1, id2);
            System.out.println("Prietenie stearsa cu succes.\n");
        } catch (RepoException repoException) {
            System.out.println("Prietenia" + repoException.getMessage());
        }
    }

    public void uiNrComunitati() {
        int nr = prietenieService.nrComunitati();
        System.out.println("Numarul de comunitati este: " + nr);
    }

    public void uiAddMessage() {
        System.out.println("Introduceti id-ul utilizatorului care trimite mesajul: ");
        Long idFrom = Long.parseLong(scan.nextLine());
        Utilizator utilizator = utilizatorService.findOne(idFrom);
        List<Utilizator> to = new ArrayList<>();
        System.out.println("Introduceti id-ul destinatarului: ");
        Long idTo = Long.parseLong(scan.nextLine());
        Utilizator utilizatorTo = utilizatorService.findOne(idTo);
        to.add(utilizatorTo);
        boolean tf = true;
        while(tf) {
            System.out.println("Doriti sa adaugati inca un destinatar?[da/nu].");
            String raspuns = scan.nextLine();
            if(raspuns.equals("da")) {
                System.out.println("Introduceti id-ul: ");
                idTo = Long.parseLong(scan.nextLine());
                utilizatorTo = utilizatorService.findOne(idTo);
                to.add(utilizatorTo);
            }
            else
                tf=false;
        }
        System.out.println("Introduceti mesajul: ");
        String messageText = scan.nextLine();
        try {
            messageService.addMessage(utilizator, to, messageText, LocalDateTime.now());
            System.out.println("Mesaj trimis cu succes.\n");
        } catch (RepoException repoException) {
            System.out.println("Mesajul " + repoException.getMessage());
        }
    }

    public void uiReplyToMessage() {
        System.out.println("Introduceti id-ul utilizatorului: ");
        Long id = Long.parseLong(scan.nextLine());
        System.out.println("Mesajele utilizatorului sunt: ");
        messageService.getAll().forEach(message -> {
            if(message.getTo().contains(utilizatorService.findOne(id))) {
                System.out.println("Id: " + message.getId());
                System.out.println("From: " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                System.out.println("To: ");
                message.getTo().forEach(utilizator -> {
                        System.out.println(utilizator.getFirstName() + " " + utilizator.getLastName());
                });
                System.out.println("Message: " + message.getMessage());
                System.out.println("Date: " + message.getDate().format(Constants.DATE_TIME_FORMATTER));
                if(message.getReply() != null)
                    System.out.println("Original message: " + message.getReply().getMessage() + " " + message.getDate().format(Constants.DATE_TIME_FORMATTER));
                System.out.println();
            }
        });
        System.out.println("Introduceti id-ul mesajului la care vreti sa raspundeti: ");
        Long idM = Long.parseLong(scan.nextLine());
        System.out.println("Introduceti mesajul: ");
        String messageReply = scan.nextLine();
        Message message = messageService.findOne(idM);
        List<Utilizator> to = new ArrayList<>();
        to.add(message.getFrom());
        message.getTo().forEach(utilizator -> {
            if(utilizator.getId() != id)
                to.add(utilizator);
        });
        try {
            messageService.addReplyMessage(utilizatorService.findOne(id), to, message, messageReply, LocalDateTime.now());
            System.out.println("Raspuns trimis cu succes.\n");
        } catch (RepoException repoException) {
            System.out.println("Mesajul " + repoException.getMessage());
        }
    }

    public void uiTrimiteCererePrietenie() {
        System.out.println("Introduceti primul id: ");
        Long id1 = Long.parseLong(scan.nextLine());
        System.out.println("Introduceti al doilea id: ");
        Long id2 = Long.parseLong(scan.nextLine());
        System.out.println("Doriti sa trimiteti si un mesaj?[da/nu]");
        String raspuns = scan.nextLine();
        String mesaj;
        if(raspuns.equals("da")) {
            System.out.println("Introduceti mesajul: ");
            mesaj = scan.nextLine();
        }
        else
            mesaj="Fara mesaj.";
        try {
            cererePrietenieService.addCerere(id1, id2, "pending", mesaj);
            System.out.println("Cerere trimisa cu succes.\n");
        } catch (RepoException repoException) {
            System.out.println("Cererea " + repoException.getMessage());
        }
    }

    public void uiRaspundeCerereaPrietenie() {
        System.out.println("Introduceti id-ul utilizatorului: ");
        Long id = Long.parseLong(scan.nextLine());
        System.out.println("Cererile de prietenie ale utilizatorului sunt: ");
        cererePrietenieService.getAll().forEach(cerere-> {
            if(cerere.getId2() == id) {
                System.out.println("Id: " + cerere.getId());
                Utilizator utilizator = utilizatorService.findOne(cerere.getId1());
                System.out.println("From: " + utilizator.getFirstName() + " " + utilizator.getLastName());
                System.out.println("Status: " + cerere.getStatus());
                System.out.println("Mesaj: " + cerere.getMesaj());
                System.out.println();
            }
        });
        System.out.println("Introduceti id-ul cererii la care doriti sa raspundeti: ");
        Long idc = Long.parseLong(scan.nextLine());
        CererePrietenie cererePrietenie = cererePrietenieService.findOne(idc);
        System.out.println("Introduceti raspunsul[approved=1/rejected=0]: ");
        int r = Integer.parseInt(scan.nextLine());
        if(r == 1) {
            long id1 = cererePrietenie.getId1();
            long id2 = cererePrietenie.getId2();
            if(id1 > id2) {
                Long aux=id1;
                id1=id2;
                id2=aux;
            }
            try {
                prietenieService.addPrietenie(id1, id2, LocalDateTime.now());
                System.out.println("Prietenie adaugata cu succes.\n");
            } catch (RepoException repoException) {
                System.out.println("Prietenia" + repoException.getMessage());
            }
            cererePrietenie.setStatus("approved");
        }
        else {
            cererePrietenie.setStatus("rejected");
            cererePrietenieService.addCerere(cererePrietenie.getId1(), cererePrietenie.getId2(), cererePrietenie.getStatus(), cererePrietenie.getMesaj());
            System.out.println("Cerere prietenie refuzata.");
        }
        cererePrietenieService.schimbareStatus(cererePrietenie);
    }

    public void afisareMesaje() {
        messageService.getAll().forEach(message -> {
            System.out.println("Id: " + message.getId());
            System.out.println("From: " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
            System.out.println("To: ");
            message.getTo().forEach(utilizator -> {
                System.out.println(utilizator.getFirstName() + " " + utilizator.getLastName());
            });
            System.out.println("Message: " + message.getMessage());
            System.out.println("Date: " + message.getDate().format(Constants.DATE_TIME_FORMATTER));
        });
    }

    public void run() {
        while(true) {
            System.out.println("MENIU:\n" + "0. Iesire\n" + "1. Adauga utilizator\n" + "2. Sterge utilizator\n" + "3. Adauga prietenie\n" + "4. Sterge prietenie\n" + "5. Afisare prieteni utilizator\n" + "6. Nr. comunitati\n" + "7. Afisare prieteni utilizator -> luna\n" + "8. Trimite mesaj\n" + "9. Raspunde la mesaj\n" + "10. Trimite cerere de prietenie\n" + "11. Raspunde la o cerere de prietenie\n");
            System.out.println("Introduceti comanda: ");
            int cmd = Integer.valueOf(scan.nextLine());
            switch (cmd) {
                case 0:
                    System.out.println("Ati iesit. :)");
                    return;
                case 1:
                    uiAddUtilizator();
                    break;
                case 2:
                    uiRemoveUtilizator();
                    break;
                case 3:
                    uiAddPrietenie();
                    break;
                case 4:
                    uiRemovePrietenie();
                    break;
                case 5:
                    uiAfisarePrieteni();
                    break;
                case 6:
                    uiNrComunitati();
                    break;
                case 7:
                    uiAfisarePrieteniLuna();
                    break;
                case 8:
                    uiAddMessage();
                    break;
                case 9:
                    uiReplyToMessage();
                    break;
                case 10:
                    uiTrimiteCererePrietenie();
                    break;
                case 11:
                    uiRaspundeCerereaPrietenie();
                    break;
                case 12:
                    afisareMesaje();
                    break;
            }
        }
    }
}

package socialnetwork.repository.file;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageFile extends AbstractFileRepository<Long, Message> {

    public MessageFile(String fileName, Validator<Message> validator) {
        super(fileName, validator);
    }

    @Override
    public Message extractEntity(List<String> attributes)  {
        Utilizator utilizator = new Utilizator(attributes.get(2), attributes.get(3));
        utilizator.setId(Long.parseLong(attributes.get(1)));
        int nrTo=Integer.parseInt(attributes.get(4));
        int k=5;
        List<Utilizator> to = new ArrayList<>();
        Utilizator utilizatorTo = null;
        while(nrTo>0) {
            Long id = Long.valueOf(attributes.get(k++));
            utilizatorTo = new Utilizator(attributes.get(k++), attributes.get(k++));
            utilizatorTo.setId(id);
            to.add(utilizatorTo);
            nrTo--;
        }
        Message message = new Message(utilizator, to, attributes.get(k++), LocalDateTime.parse(attributes.get(k++)), Long.parseLong(attributes.get(k)));
        message.setId(Long.parseLong(attributes.get((0))));
        return message;
    }

    @Override
    protected String createEntityAsString(Message entity) {
        String text=";";
        List<String> to = new ArrayList<>();
        entity.getTo().forEach(utilizator -> to.add(utilizator.getId() + ";" + utilizator.getFirstName() + ";" +utilizator.getLastName() + ";"));
        for(String t: to)
            text = text + t;
        return entity.getId() + ";" + entity.getFrom().getId() + ";" + entity.getFrom().getFirstName() + ";" + entity.getFrom().getLastName() + ";" + entity.getTo().size() + text + entity.getMessage() + ";" + entity.getDate();
    }

}


package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.GenerateId;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.EntityChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements Observable<EntityChangeEvent> {
    private Repository<Long, Message> repo;

    public MessageService(Repository<Long, Message> repo) { this.repo = repo; }

    /**
     * ads a new message
     * @param from - the user that sent the message
     * @param to - the users that get the message
     * @param messageText - the text of the message
     * @param date - the hour and date when the message was sent
     * @return null- if the message is saved
     *         otherwise returns the entity (a message with the same id already exists)
     */
    public Message addMessage(Utilizator from, List<Utilizator> to, String messageText, LocalDateTime date) {
        Message message = new Message(from, to, messageText, date, 0);
        GenerateId id = new GenerateId(getMaxId());
        message.setId(id.newId());
        return repo.save(message);
    }

    /**
     * ads a reply to a message
     * @param from - the user that sent the message
     * @param to - the users that get the message
     * @param originalMessage - the original message
     * @param messageText - the text of the message
     * @param date - the hour and date when the message was sent
     * @return null- if the message is saved
     *         otherwise returns the entity (a message with the same id already exists)
     */
    public Message addReplyMessage(Utilizator from, List<Utilizator> to, Message originalMessage, String messageText, LocalDateTime date) {
        Message messageReply = new Message(from, to, messageText, date, 0);
        GenerateId id = new GenerateId(getMaxId());
        messageReply.setId(id.newId());
        originalMessage.setReply(messageReply.getId());
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, originalMessage));
        return repo.save(messageReply);
    }

    /**
     * removes an existing message based on the id
     * @param id - the id of the message that has to be removed
     * @return the entity - if the message is removed
     *         otherwise returns null (there is no message with the provided id)
     */
    public Message removeMessage(long id) {
        return repo.delete(id);
    }

    /**
     * returns an iterable list with all the messages
     */
    public Iterable<Message> getAll(){
        return repo.findAll();
    }

    /**
     * returns the max value of an id that can be assigned to a new message
     */
    public Long getMaxId() {
        final Long[] maxId = {Long.valueOf(0)};
        Iterable<Message> mesaje = repo.findAll();
        mesaje.forEach(m->{
            if(m.getId() > maxId[0])
                maxId[0] = m.getId();
        });
        return maxId[0];
    }

    /**
     * finds an existing message based on the id
     * @param id - the id of the message that has to be found
     * @return the entity - if the message is found
     *         otherwise returns null (the message doesn't exist)
     */
    public Message findOne(Long id) {
        return repo.findOne(id);
    }

    private List<Observer<EntityChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {
        //to do
    }

    @Override
    public void notifyObservers(EntityChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}

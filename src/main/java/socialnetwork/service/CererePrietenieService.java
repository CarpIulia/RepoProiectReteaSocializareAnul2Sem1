package socialnetwork.service;

import socialnetwork.domain.CererePrietenie;
import socialnetwork.repository.Repository;
import socialnetwork.utils.GenerateId;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.EntityChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class CererePrietenieService implements Observable<EntityChangeEvent> {
    private Repository<Long, CererePrietenie> repo;

    public CererePrietenieService(Repository<Long, CererePrietenie> repo) {
        this.repo = repo;
    }

    /**
     * ads a new user
     * @param id1 - the id of the user that sends the request
     * @param id2 - the id of the user that gets the request
     * @param status - the status of the request: approved, pending, rejected
     * @param mesaj - the user that send the request can leave a message
     * @return null- if the request is saved
     *         otherwise returns the entity (the request already exists)
     */
    public CererePrietenie addCerere(Long id1, Long id2, String status, String mesaj) {
        CererePrietenie cererePrietenie = new CererePrietenie(id1, id2, status, mesaj);
        GenerateId id = new GenerateId(getMaxId());
        cererePrietenie.setId(id.newId());
        return repo.save(cererePrietenie);
    }

    /**
     * removes an existing user based on the id
     * @param id - the id of the request that has to be removed
     * @return the entity - if the request is removed
     *         otherwise returns null (the request doesn't exist)
     */
    public CererePrietenie removeCerere(long id) {
        CererePrietenie cererePrietenie = repo.delete(id);
        notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, findOne(id)));
        return cererePrietenie;
    }

    /**
     * returns an iterable list with all the requests
     */
    public Iterable<CererePrietenie> getAll(){
        return repo.findAll();
    }

    /**
     * returns the max value of an id that can be assigned to a new request
     */
    public Long getMaxId() {
        final Long[] maxId = {Long.valueOf(0)};
        Iterable<CererePrietenie> utilizatori = repo.findAll();
        utilizatori.forEach(u->{
            if(u.getId() > maxId[0])
                maxId[0] = u.getId();
        });
        return maxId[0];
    }

    /**
     * finds an existing request based on the id
     * @param id - the id of the request that has to be found
     * @return the entity - if the request is found
     *         otherwise returns null (the request doesn't exist)
     */
    public CererePrietenie findOne(Long id) {
        return repo.findOne(id);
    }

    public void schimbareStatus(CererePrietenie cererePrietenie) {
        removeCerere(cererePrietenie.getId());
        addCerere(cererePrietenie.getId1(), cererePrietenie.getId2(), cererePrietenie.getStatus(), cererePrietenie.getMesaj());
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

package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.EntityChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * service pentru gestionarea prieteniilor
 */
public class PrietenieService  implements Observable<EntityChangeEvent> {
    private Repository<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private Repository<Long, Utilizator> repoUtilizator;
    private Map<Long, List> adiacenta = new HashMap<Long, List>();
    private int[] viz;

    public PrietenieService(Repository<Tuple<Long,Long>, Prietenie> repoPrietenie, Repository<Long, Utilizator> repoUtilizator) {
        this.repoPrietenie = repoPrietenie;
        this.repoUtilizator = repoUtilizator;
        createFriendshipLists();
    }

    /**
     * adds a new friendship
     * @param id1,id2 - the id's of the users that
     *             form the friendship
     * @param date - the date and time when the friendship was made
     * @return null- if the friendship is saved
             otherwise returns the entity (the friendship already exists)
     */
    public Prietenie addPrietenie(Long id1, Long id2, LocalDateTime date) {
        if(id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }
        Prietenie prietenie = new Prietenie(id1, id2, date);
        Tuple<Long, Long> tuple = new Tuple(id1, id2);
        prietenie.setId(tuple);
        Utilizator util1 = repoUtilizator.findOne(id1);
        Utilizator util2 = repoUtilizator.findOne(id2);
        util1.addFriend(util2);
        util2.addFriend(util1);
        Prietenie prietenieS = repoPrietenie.save(prietenie);
        if(prietenieS == null)
            notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, prietenie));
        return prietenieS;
    }

    /**
     * removes an existing friendship based on the ID
     * @param id1,id2 - the id's of the users that
     *             form the friendship
     * @return the entity - if the friendship is removed
    otherwise returns null (the friendship doesn't exist)
     */
    public Prietenie removePrietenie(Long id1, Long id2) {
        if(id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }
        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        Utilizator util1 = repoUtilizator.findOne(id1);
        Utilizator util2 = repoUtilizator.findOne(id2);
        util1.removeFriend(util2);
        util2.removeFriend(util1);
        Prietenie prietenie = repoPrietenie.delete(tuple);
        if(prietenie != null)
            notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, prietenie));
        return prietenie;
    }

    /**
     * finds an existing friendship based on the id's
     * @param id1,id2 - the id's of the users that
     *             form the friendship
     * @return the entity - if the friendship exists
    otherwise returns null (the friendship doesn't exist)
     */
    public Prietenie findOne(Long id1, Long id2) {
        if(id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }
        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        return repoPrietenie.findOne(tuple);
    }

    /**
     * creates friendship lists for all the existing users when the app starts
     */
    private void createFriendshipLists() {
        Iterable<Prietenie> prietenii = repoPrietenie.findAll();
        prietenii.forEach(prietenie->{
            Utilizator util1 = repoUtilizator.findOne(prietenie.getId1());
            Utilizator util2 = repoUtilizator.findOne(prietenie.getId2());
            util1.addFriend(util2);
            util2.addFriend(util1);
        });
    }

    /**
     * returns an iterable list with all the friendships
     */
    public Iterable<Prietenie> getAll(){
        return repoPrietenie.findAll();
    }

    public int nrComunitati() {
        repoUtilizator.findAll().forEach(utilizator -> {
            List<Long> prieteni = new ArrayList<>();
            utilizator.getFriends().forEach(prieten -> {
                prieteni.add(prieten.getId());
            });
            adiacenta.put(utilizator.getId(), prieteni);
        });

        Long dim = Long.valueOf(-1);
        for (Utilizator utilizator : repoUtilizator.findAll())
            if (utilizator.getId() > dim)
                dim = utilizator.getId();

        viz = new int[Math.toIntExact(dim + 1)];
        for (int i = 1; i <= dim; i++)
            viz[i] = -1;
        for (Utilizator utilizator : repoUtilizator.findAll())
            viz[Math.toIntExact(utilizator.getId())] = 0;
        int nr = 0;
        for (int i = 1; i <= dim; i++) {
            if (viz[i] == 0) {
                Long x = Long.valueOf(i);
                dfs(x);
                nr++;
            }
        }
        return nr;
    }


    private void dfs(Long x) {
        viz[x.intValue()] = 1;
        List<Long> prieteni = adiacenta.get(x);
        prieteni.forEach(prieten-> {
            if(viz[Math.toIntExact(prieten)] == 0)
                dfs(prieten);
        });
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
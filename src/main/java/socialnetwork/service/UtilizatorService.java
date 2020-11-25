package socialnetwork.service;

import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.GenerateId;

import java.util.List;

public class UtilizatorService  {
    private Repository<Long, Utilizator> repo;

    public UtilizatorService(Repository<Long, Utilizator> repo) {
        this.repo = repo;
    }

    /**
     * ads a new user
     * @param firstName,lastName - user's first and last name
     * @return null- if the user is saved
     *         otherwise returns the entity (the user already exists)
     */
    public Utilizator addUtilizator(String firstName, String lastName) {
        Utilizator utilizator = new Utilizator(firstName, lastName);
        GenerateId id = new GenerateId(getMaxId());
        utilizator.setId(id.newId());
        return repo.save(utilizator);
    }

    /**
     * removes an existing user based on the id
     * @param id - the id of the user that has to be removed
     * @return the entity - if the user is removed
     *         otherwise returns null (the user doesn't exist)
     */
    public Utilizator removeUtilizator(long id) {
        return repo.delete(id);
    }

    /**
     * returns an iterable list with all the users
     */
    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    /**
     * returns the max value of an id that can be assigned to a new user
     */
    public Long getMaxId() {
        final Long[] maxId = {Long.valueOf(0)};
        Iterable<Utilizator> utilizatori = repo.findAll();
        utilizatori.forEach(u->{
            if(u.getId() > maxId[0])
                maxId[0] = u.getId();
        });
        return maxId[0];
    }

    public Utilizator findOne(Long id) {
        return repo.findOne(id);
    }
    ///TO DO: add other methods
}

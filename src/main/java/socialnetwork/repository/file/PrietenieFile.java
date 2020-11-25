package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class PrietenieFile extends AbstractFileRepository<Tuple<Long,Long>, Prietenie> {
    public PrietenieFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);

    }

    @Override
    public Prietenie extractEntity(List<String> attributes) {
       Prietenie prietenie = new Prietenie(Long.valueOf(attributes.get(0)), Long.valueOf(attributes.get(1)), LocalDateTime.parse(attributes.get(2)));
       Tuple<Long, Long> tuple = new Tuple(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)));
       prietenie.setId(tuple);
       return prietenie;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }

}



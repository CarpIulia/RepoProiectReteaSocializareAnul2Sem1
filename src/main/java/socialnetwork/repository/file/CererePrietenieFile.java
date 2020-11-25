package socialnetwork.repository.file;

import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class CererePrietenieFile extends AbstractFileRepository<Long, CererePrietenie>{
    public CererePrietenieFile(String fileName, Validator<CererePrietenie> validator) {
        super(fileName, validator);
    }

    @Override
    public CererePrietenie extractEntity(List<String> attributes) {
        CererePrietenie cererePrietenie = new CererePrietenie(Long.parseLong(attributes.get(1)), Long.parseLong(attributes.get(2)), attributes.get(3), attributes.get(4));
        cererePrietenie.setId(Long.parseLong(attributes.get(0)));
        return cererePrietenie;
    }

    @Override
    protected String createEntityAsString(CererePrietenie entity) {
        return entity.getId() + ";" + entity.getId1() + ";" + entity.getId2() + ";" + entity.getStatus() + ";" + entity.getMesaj();
    }


}

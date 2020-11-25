package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

import java.util.regex.Pattern;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        Pattern pattern = Pattern.compile("[A-Z].[a-z]*");
        if(firstName.isEmpty())
            throw new ValidationException("Trbuie sa introduceti un prenume!\n");
        if(lastName.isEmpty())
            throw new ValidationException("Trebuie sa introduceti un nume!\n");
        if(firstName.length() < 3 || firstName.length() > 20)
            throw new ValidationException("Prenumele trebuie sa contina cel putin 3 caractere si cel mult 19!\n");
        if(lastName.length() < 3 || lastName.length() > 20)
            throw new ValidationException("Numele trebuie sa contina cel putin 3 caractere si cel mult 19!\n");
        if(!pattern.matcher(firstName).find() || !pattern.matcher(lastName).find())
            throw new ValidationException("Numele si prenumele trebuie formate dor din litere!\n");
    }
}

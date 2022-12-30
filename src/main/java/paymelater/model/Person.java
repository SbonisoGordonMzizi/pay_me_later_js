package paymelater.model;

/*
 ** DO NOT CHANGE!!
 */


import com.google.common.base.Objects;
import org.apache.commons.validator.routines.EmailValidator;
import paymelater.WeShareException;

/**
 * Person that uses WeShare
 */
public class Person extends PersistentModel<Person> {
    private String email;

    /**
     * Create a new Person with their email address
     * @param email should be a valid email address
     */
    public Person(String email) {
        if (!EmailValidator.getInstance().isValid(email))
            throw new WeShareException("Bad email address");
        this.email = email;
    }

    /**
     * The name of the person is extracted from their email address
     * @return the prefix of the email address (everything before the `@`)
     */
    public String getName() {
        String pseudonym = this.email.split("@")[0];
        return pseudonym.substring(0, 1).toUpperCase() + pseudonym.substring(1);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Person person = (Person) o;
        return Objects.equal(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), email);
    }
}

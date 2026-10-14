package academy.tochkavhoda.competition.model;

import java.util.Objects;

/**
 * Участник конкурса. Имя и фамилия — данные руководителя фирмы.
 */
public class Participant extends User {
    private final String companyName;

    public Participant(String firstName, String lastName, String companyName, String login, String password) {
        super(firstName, lastName, login, password);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        return Objects.equals(companyName, ((Participant) o).companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), companyName);
    }
}

package academy.tochkavhoda.competition.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Expert extends User {
    private final List<String> directions;

    public Expert(String firstName, String lastName, List<String> directions, String login, String password) {
        super(firstName, lastName, login, password);
        this.directions = new ArrayList<>(directions);
    }

    public List<String> getDirections() {
        return Collections.unmodifiableList(directions);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        return Objects.equals(directions, ((Expert) o).directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), directions);
    }
}

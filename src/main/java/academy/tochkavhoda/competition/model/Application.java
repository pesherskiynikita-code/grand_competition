package academy.tochkavhoda.competition.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Заявка на грант. После подачи не меняется.
 * Номер (id) выдаёт Database, по нему заявки и сравниваются.
 */
public class Application {
    private int id;
    private final Participant participant;
    private final String title;
    private final String description;
    private final List<String> directions;
    private final int amount;

    public Application(Participant participant, String title, String description,
                       List<String> directions, int amount) {
        this.participant = participant;
        this.title = title;
        this.description = description;
        this.directions = new ArrayList<>(directions);
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDirections() {
        return Collections.unmodifiableList(directions);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Application) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

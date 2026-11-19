package academy.tochkavhoda.competition.dto.request;

import java.util.List;

/**
 * Одна заявка внутри запроса AddApplicationsDtoRequest.
 */
public class ApplicationDtoRequest {
    private String title;
    private String description;
    private List<String> directions;
    private int amount;

    public ApplicationDtoRequest() {
    }

    public ApplicationDtoRequest(String title, String description, List<String> directions, int amount) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDirections() {
        return directions;
    }

    public int getAmount() {
        return amount;
    }
}

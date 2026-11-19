package academy.tochkavhoda.competition.dto.response;

import java.util.List;

/**
 * Номера поданных заявок в том же порядке, в каком они были в запросе.
 */
public class AddApplicationsDtoResponse {
    private List<Integer> ids;

    public AddApplicationsDtoResponse() {
    }

    public AddApplicationsDtoResponse(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }
}

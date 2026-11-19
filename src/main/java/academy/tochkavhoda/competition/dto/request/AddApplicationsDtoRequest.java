package academy.tochkavhoda.competition.dto.request;

import java.util.List;

/**
 * Подача заявок: {"applications": [{...}, {...}]}. Можно передать одну заявку или несколько.
 */
public class AddApplicationsDtoRequest {
    private List<ApplicationDtoRequest> applications;

    public AddApplicationsDtoRequest() {
    }

    public AddApplicationsDtoRequest(List<ApplicationDtoRequest> applications) {
        this.applications = applications;
    }

    public List<ApplicationDtoRequest> getApplications() {
        return applications;
    }
}

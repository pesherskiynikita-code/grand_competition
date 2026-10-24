package academy.tochkavhoda.competition.api.dto.responses;

public class ErrorDtoResponse {
    private String error;

    public ErrorDtoResponse() {
    }

    public ErrorDtoResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

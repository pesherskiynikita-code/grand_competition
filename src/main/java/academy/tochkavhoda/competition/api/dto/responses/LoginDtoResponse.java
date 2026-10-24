package academy.tochkavhoda.competition.api.dto.responses;

public class LoginDtoResponse {
    private String token;

    public LoginDtoResponse() {
    }

    public LoginDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

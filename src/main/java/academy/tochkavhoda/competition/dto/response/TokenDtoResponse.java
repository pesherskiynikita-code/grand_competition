package academy.tochkavhoda.competition.dto.response;

public class TokenDtoResponse {
    private String token;

    public TokenDtoResponse() {
    }

    public TokenDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

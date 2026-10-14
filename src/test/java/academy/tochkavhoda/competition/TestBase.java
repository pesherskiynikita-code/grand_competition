package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.dto.response.TokenDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.server.Server;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Общая основа тестов: работает только через Server и DTO.
 */
public abstract class TestBase {
    protected static final int SUCCESS = 200;
    protected static final int ERROR = 400;

    protected final Gson gson = new Gson();
    protected Server server;

    @BeforeEach
    public void setUp() {
        server = new Server();
        server.clearDatabase();
    }

    protected ServerResponse registerParticipant(String firstName, String lastName, String companyName,
                                                 String login, String password) {
        return server.registerParticipant(gson.toJson(
                new RegisterParticipantDtoRequest(firstName, lastName, companyName, login, password)));
    }

    protected ServerResponse registerParticipant(String login, String password) {
        return registerParticipant("Иван", "Петров", "ООО Ромашка", login, password);
    }

    protected ServerResponse registerExpert(String firstName, String lastName, List<String> directions,
                                            String login, String password) {
        return server.registerExpert(gson.toJson(
                new RegisterExpertDtoRequest(firstName, lastName, directions, login, password)));
    }

    protected ServerResponse registerExpert(String login, String password) {
        return registerExpert("Анна", "Сидорова", List.of("IT", "Экология"), login, password);
    }

    protected ServerResponse login(String login, String password) {
        return server.login(gson.toJson(new LoginDtoRequest(login, password)));
    }

    protected String loginAndGetToken(String login, String password) {
        ServerResponse response = login(login, password);
        assertSuccess(response);
        String token = gson.fromJson(response.getResponseData(), TokenDtoResponse.class).getToken();
        assertNotNull(token);
        return token;
    }

    protected void assertSuccess(ServerResponse response) {
        assertEquals(SUCCESS, response.getResponseCode(), response.getResponseData());
    }

    protected void assertEmptySuccess(ServerResponse response) {
        assertSuccess(response);
        assertEquals("{}", response.getResponseData());
    }

    protected void assertError(ServerResponse response, ServerErrorCode expected) {
        assertEquals(ERROR, response.getResponseCode(), response.getResponseData());
        ErrorDtoResponse error = gson.fromJson(response.getResponseData(), ErrorDtoResponse.class);
        assertEquals(expected.getErrorString(), error.getError());
    }
}

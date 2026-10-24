package academy.tochkavhoda.competition.api;

import academy.tochkavhoda.competition.api.dto.requests.LoginDtoRequest;
import academy.tochkavhoda.competition.api.dto.requests.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.api.dto.requests.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.api.dto.responses.ErrorDtoResponse;
import academy.tochkavhoda.competition.api.dto.responses.LoginDtoResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Общие вспомогательные методы для тестов сервера.
 * Тесты - это клиент сервера, поэтому работают только через Server и DTO.
 */
public abstract class TestBase {
    protected static final Gson GSON = new Gson();
    protected static final String PASSWORD = "password1";

    protected Server server;

    @BeforeEach
    public void setUp() {
        server = new Server();
        assertEquals(200, server.clearDatabase().getResponseCode());
    }

    protected static String json(Object dto) {
        return GSON.toJson(dto);
    }

    protected static <T> T assertOk(ServerResponse response, Class<T> responseClass) {
        assertEquals(200, response.getResponseCode(), response.getResponseData());
        return GSON.fromJson(response.getResponseData(), responseClass);
    }

    protected static void assertOk(ServerResponse response) {
        assertEquals(200, response.getResponseCode(), response.getResponseData());
    }

    protected static void assertError(ServerResponse response, String expectedErrorPart) {
        assertEquals(400, response.getResponseCode(), response.getResponseData());
        ErrorDtoResponse error = GSON.fromJson(response.getResponseData(), ErrorDtoResponse.class);
        assertNotNull(error.getError());
        assertTrue(error.getError().contains(expectedErrorPart),
                "Ожидалась ошибка, содержащая '" + expectedErrorPart + "', получено: " + error.getError());
    }

    protected String registerParticipantAndLogin(String login, String companyName) {
        assertOk(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", companyName, login, PASSWORD))));
        return login(login);
    }

    protected String registerExpertAndLogin(String login, String... directions) {
        assertOk(server.registerExpert(json(
                new RegisterExpertDtoRequest("Анна", "Смирнова", List.of(directions), login, PASSWORD))));
        return login(login);
    }

    protected String login(String login) {
        return assertOk(server.login(json(new LoginDtoRequest(login, PASSWORD))), LoginDtoResponse.class).getToken();
    }
}

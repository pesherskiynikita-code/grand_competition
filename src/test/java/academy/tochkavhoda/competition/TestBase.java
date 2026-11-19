package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RateApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationsDtoResponse;
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

    protected String registerParticipantAndLogin(String login) {
        assertEmptySuccess(registerParticipant(login, "secret123"));
        return loginAndGetToken(login, "secret123");
    }

    protected String registerExpertAndLogin(String login) {
        assertEmptySuccess(registerExpert(login, "secret123"));
        return loginAndGetToken(login, "secret123");
    }

    protected ApplicationDtoRequest application(String title, int amount) {
        return new ApplicationDtoRequest(title, "Описание заявки " + title, List.of("IT"), amount);
    }

    protected ServerResponse addApplications(String token, ApplicationDtoRequest... applications) {
        return server.addApplications(token, gson.toJson(new AddApplicationsDtoRequest(List.of(applications))));
    }

    /**
     * Подаёт одну заявку и возвращает её номер.
     */
    protected int addApplication(String token, String title, int amount) {
        ServerResponse response = addApplications(token, application(title, amount));
        assertSuccess(response);
        List<Integer> ids = gson.fromJson(response.getResponseData(), AddApplicationsDtoResponse.class).getIds();
        assertEquals(1, ids.size());
        return ids.get(0);
    }

    protected ServerResponse cancelApplication(String token, int applicationId) {
        return server.cancelApplication(token, gson.toJson(new ApplicationIdDtoRequest(applicationId)));
    }

    protected ServerResponse rateApplication(String token, int applicationId, int rating) {
        return server.rateApplication(token, gson.toJson(new RateApplicationDtoRequest(applicationId, rating)));
    }

    protected ServerResponse deleteRating(String token, int applicationId) {
        return server.deleteRating(token, gson.toJson(new ApplicationIdDtoRequest(applicationId)));
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

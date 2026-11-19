package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.ApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationsDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.server.ServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestApplications extends TestBase {

    @Test
    public void testAddOneApplication() {
        String token = registerParticipantAndLogin("ivan_petrov");
        int id = addApplication(token, "Умный дом", 100000);
        assertEquals(1, id);
    }

    @Test
    public void testAddSeveralApplicationsAtOnce() {
        String token = registerParticipantAndLogin("ivan_petrov");
        ServerResponse response = addApplications(token,
                application("Умный дом", 100000), application("Робот-пылесос", 50000));
        assertSuccess(response);
        List<Integer> ids = gson.fromJson(response.getResponseData(), AddApplicationsDtoResponse.class).getIds();
        assertEquals(List.of(1, 2), ids);
    }

    @Test
    public void testAddApplicationsOneByOne() {
        String ivan = registerParticipantAndLogin("ivan_petrov");
        String oleg = registerParticipantAndLogin("oleg_k");
        int first = addApplication(ivan, "Умный дом", 100000);
        int second = addApplication(oleg, "Умный дом", 100000);
        int third = addApplication(ivan, "Робот-пылесос", 50000);
        assertNotEquals(first, second);
        assertNotEquals(second, third);
        assertNotEquals(first, third);
    }

    @Test
    public void testAddApplicationsWrongToken() {
        assertError(addApplications("not-a-token", application("Умный дом", 100000)), ServerErrorCode.WRONG_TOKEN);
    }

    @Test
    public void testExpertCannotAddApplications() {
        String token = registerExpertAndLogin("anna_s");
        assertError(addApplications(token, application("Умный дом", 100000)), ServerErrorCode.NOT_PARTICIPANT);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "{не json", "{}", "{\"applications\": []}", "{\"applications\": [null]}"})
    public void testAddApplicationsWrongRequest(String json) {
        String token = registerParticipantAndLogin("ivan_petrov");
        ServerResponse response = server.addApplications(token, json);
        assertEquals(ERROR, response.getResponseCode(), response.getResponseData());
    }

    @Test
    public void testAddApplicationsEmptyList() {
        String token = registerParticipantAndLogin("ivan_petrov");
        assertError(addApplications(token), ServerErrorCode.EMPTY_APPLICATIONS);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    public void testAddApplicationEmptyTitle(String title) {
        String token = registerParticipantAndLogin("ivan_petrov");
        assertError(addApplications(token, new ApplicationDtoRequest(title, "Описание", List.of("IT"), 1000)),
                ServerErrorCode.EMPTY_TITLE);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    public void testAddApplicationEmptyDescription(String description) {
        String token = registerParticipantAndLogin("ivan_petrov");
        assertError(addApplications(token, new ApplicationDtoRequest("Умный дом", description, List.of("IT"), 1000)),
                ServerErrorCode.EMPTY_DESCRIPTION);
    }

    @Test
    public void testAddApplicationEmptyDirections() {
        String token = registerParticipantAndLogin("ivan_petrov");
        assertError(addApplications(token, new ApplicationDtoRequest("Умный дом", "Описание", null, 1000)),
                ServerErrorCode.EMPTY_DIRECTIONS);
        assertError(addApplications(token, new ApplicationDtoRequest("Умный дом", "Описание", List.of(), 1000)),
                ServerErrorCode.EMPTY_DIRECTIONS);
        assertError(addApplications(token,
                        new ApplicationDtoRequest("Умный дом", "Описание", Arrays.asList("IT", " "), 1000)),
                ServerErrorCode.EMPTY_DIRECTION);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100000})
    public void testAddApplicationWrongAmount(int amount) {
        String token = registerParticipantAndLogin("ivan_petrov");
        assertError(addApplications(token, application("Умный дом", amount)), ServerErrorCode.WRONG_AMOUNT);
    }

    @Test
    public void testNothingIsSavedIfOneApplicationIsWrong() {
        String token = registerParticipantAndLogin("ivan_petrov");
        assertError(addApplications(token, application("Умный дом", 1000), application("Плохая", 0)),
                ServerErrorCode.WRONG_AMOUNT);
        // первая заявка тоже не сохранилась, поэтому следующая получает номер 1
        assertEquals(1, addApplication(token, "Умный дом", 1000));
    }

    @Test
    public void testCancelApplication() {
        String token = registerParticipantAndLogin("ivan_petrov");
        int id = addApplication(token, "Умный дом", 100000);

        assertEmptySuccess(cancelApplication(token, id));
        assertError(cancelApplication(token, id), ServerErrorCode.APPLICATION_NOT_FOUND);
    }

    @Test
    public void testCancelledApplicationCannotBeRated() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);
        assertEmptySuccess(rateApplication(expert, id, 5));

        assertEmptySuccess(cancelApplication(participant, id));

        assertError(rateApplication(expert, id, 4), ServerErrorCode.APPLICATION_NOT_FOUND);
        assertError(deleteRating(expert, id), ServerErrorCode.APPLICATION_NOT_FOUND);
    }

    @Test
    public void testCannotCancelOtherParticipantApplication() {
        String ivan = registerParticipantAndLogin("ivan_petrov");
        String oleg = registerParticipantAndLogin("oleg_k");
        int id = addApplication(ivan, "Умный дом", 100000);

        assertError(cancelApplication(oleg, id), ServerErrorCode.APPLICATION_NOT_FOUND);
        // заявка осталась, владелец может её отменить
        assertEmptySuccess(cancelApplication(ivan, id));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100})
    public void testCancelUnknownApplication(int id) {
        String token = registerParticipantAndLogin("ivan_petrov");
        addApplication(token, "Умный дом", 100000);
        assertError(cancelApplication(token, id), ServerErrorCode.APPLICATION_NOT_FOUND);
    }

    @Test
    public void testExpertCannotCancelApplication() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);
        assertError(cancelApplication(expert, id), ServerErrorCode.NOT_PARTICIPANT);
    }

    @Test
    public void testCancelApplicationWrongToken() {
        String token = registerParticipantAndLogin("ivan_petrov");
        int id = addApplication(token, "Умный дом", 100000);
        assertEmptySuccess(server.logout(token));
        assertError(cancelApplication(token, id), ServerErrorCode.WRONG_TOKEN);
    }

    @Test
    public void testApplicationsSurviveLogout() {
        String token = registerParticipantAndLogin("ivan_petrov");
        int id = addApplication(token, "Умный дом", 100000);
        assertEmptySuccess(server.logout(token));

        String newToken = loginAndGetToken("ivan_petrov", "secret123");
        assertEmptySuccess(cancelApplication(newToken, id));
    }
}

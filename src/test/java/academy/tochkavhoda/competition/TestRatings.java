package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.server.ServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRatings extends TestBase {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    public void testRateApplication(int rating) {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);

        assertEmptySuccess(rateApplication(expert, id, rating));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1, 100})
    public void testWrongRating(int rating) {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);

        assertError(rateApplication(expert, id, rating), ServerErrorCode.WRONG_RATING);
    }

    @Test
    public void testChangeRating() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);

        assertEmptySuccess(rateApplication(expert, id, 3));
        assertEmptySuccess(rateApplication(expert, id, 5));
    }

    @Test
    public void testSeveralExpertsRateOneApplication() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String anna = registerExpertAndLogin("anna_s");
        String boris = registerExpertAndLogin("boris_m");
        int id = addApplication(participant, "Умный дом", 100000);

        assertEmptySuccess(rateApplication(anna, id, 5));
        assertEmptySuccess(rateApplication(boris, id, 2));

        // удаление оценки одного эксперта не трогает оценку другого
        assertEmptySuccess(deleteRating(anna, id));
        assertEmptySuccess(deleteRating(boris, id));
    }

    @Test
    public void testDeleteRating() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);
        assertEmptySuccess(rateApplication(expert, id, 4));

        assertEmptySuccess(deleteRating(expert, id));
        assertError(deleteRating(expert, id), ServerErrorCode.RATING_NOT_FOUND);
        // после удаления можно оценить заново
        assertEmptySuccess(rateApplication(expert, id, 2));
    }

    @Test
    public void testDeleteRatingThatWasNotGiven() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String expert = registerExpertAndLogin("anna_s");
        int id = addApplication(participant, "Умный дом", 100000);

        assertError(deleteRating(expert, id), ServerErrorCode.RATING_NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100})
    public void testRateUnknownApplication(int id) {
        String expert = registerExpertAndLogin("anna_s");
        assertError(rateApplication(expert, id, 5), ServerErrorCode.APPLICATION_NOT_FOUND);
        assertError(deleteRating(expert, id), ServerErrorCode.APPLICATION_NOT_FOUND);
    }

    @Test
    public void testParticipantCannotRate() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        int id = addApplication(participant, "Умный дом", 100000);

        assertError(rateApplication(participant, id, 5), ServerErrorCode.NOT_EXPERT);
        assertError(deleteRating(participant, id), ServerErrorCode.NOT_EXPERT);
    }

    @Test
    public void testRateWrongToken() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        int id = addApplication(participant, "Умный дом", 100000);

        assertError(rateApplication("not-a-token", id, 5), ServerErrorCode.WRONG_TOKEN);
        assertError(deleteRating(null, id), ServerErrorCode.WRONG_TOKEN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "{не json"})
    public void testRateWrongJson(String json) {
        String expert = registerExpertAndLogin("anna_s");
        ServerResponse response = server.rateApplication(expert, json);
        assertEquals(ERROR, response.getResponseCode(), response.getResponseData());
        assertError(server.deleteRating(expert, json), ServerErrorCode.WRONG_JSON);
    }
}

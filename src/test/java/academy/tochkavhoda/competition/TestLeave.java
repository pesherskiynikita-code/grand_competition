package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestLeave extends TestBase {

    @Test
    public void testParticipantLeave() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        String token = loginAndGetToken("ivan_petrov", "secret123");

        assertEmptySuccess(server.leave(token));

        assertError(server.logout(token), ServerErrorCode.WRONG_TOKEN);
        assertError(server.leave(token), ServerErrorCode.WRONG_TOKEN);
        assertError(login("ivan_petrov", "secret123"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }

    @Test
    public void testExpertLeave() {
        assertEmptySuccess(registerExpert("anna_s", "secret123"));
        String token = loginAndGetToken("anna_s", "secret123");

        assertEmptySuccess(server.leave(token));

        assertError(server.logout(token), ServerErrorCode.WRONG_TOKEN);
        assertError(login("anna_s", "secret123"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }

    @Test
    public void testLoginIsFreeAfterLeave() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertEmptySuccess(server.leave(loginAndGetToken("ivan_petrov", "secret123")));

        assertEmptySuccess(registerExpert("ivan_petrov", "newPass456"));
        assertError(login("ivan_petrov", "secret123"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        loginAndGetToken("ivan_petrov", "newPass456");
    }

    @Test
    public void testLeaveDoesNotAffectOtherUsers() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertEmptySuccess(registerExpert("anna_s", "secret123"));
        String ivanToken = loginAndGetToken("ivan_petrov", "secret123");
        String annaToken = loginAndGetToken("anna_s", "secret123");

        assertEmptySuccess(server.leave(ivanToken));

        assertEmptySuccess(server.logout(annaToken));
        loginAndGetToken("anna_s", "secret123");
    }

    @Test
    public void testLeaveAfterLogout() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        String token = loginAndGetToken("ivan_petrov", "secret123");
        assertEmptySuccess(server.logout(token));

        assertError(server.leave(token), ServerErrorCode.WRONG_TOKEN);
        // пользователь не удалён и может войти снова
        loginAndGetToken("ivan_petrov", "secret123");
    }

    @Test
    public void testParticipantLeaveDeletesApplications() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String other = registerParticipantAndLogin("oleg_k");
        String expert = registerExpertAndLogin("anna_s");
        int ivanApplication = addApplication(participant, "Умный дом", 100000);
        int olegApplication = addApplication(other, "Робот-пылесос", 50000);
        assertEmptySuccess(rateApplication(expert, ivanApplication, 5));

        assertEmptySuccess(server.leave(participant));

        assertError(rateApplication(expert, ivanApplication, 4), ServerErrorCode.APPLICATION_NOT_FOUND);
        assertError(deleteRating(expert, ivanApplication), ServerErrorCode.APPLICATION_NOT_FOUND);
        // заявки других участников остались
        assertEmptySuccess(rateApplication(expert, olegApplication, 4));
    }

    @Test
    public void testExpertLeaveDeletesRatings() {
        String participant = registerParticipantAndLogin("ivan_petrov");
        String anna = registerExpertAndLogin("anna_s");
        String boris = registerExpertAndLogin("boris_m");
        int id = addApplication(participant, "Умный дом", 100000);
        assertEmptySuccess(rateApplication(anna, id, 5));
        assertEmptySuccess(rateApplication(boris, id, 3));

        assertEmptySuccess(server.leave(anna));

        // новый эксперт с тем же логином не получает чужих оценок
        String newAnna = registerExpertAndLogin("anna_s");
        assertError(deleteRating(newAnna, id), ServerErrorCode.RATING_NOT_FOUND);
        // оценка другого эксперта и сама заявка остались
        assertEmptySuccess(deleteRating(boris, id));
        assertEmptySuccess(cancelApplication(participant, id));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-a-token", "123e4567-e89b-12d3-a456-426614174000"})
    public void testLeaveWrongToken(String token) {
        assertError(server.leave(token), ServerErrorCode.WRONG_TOKEN);
    }
}

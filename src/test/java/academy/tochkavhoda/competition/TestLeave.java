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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-a-token", "123e4567-e89b-12d3-a456-426614174000"})
    public void testLeaveWrongToken(String token) {
        assertError(server.leave(token), ServerErrorCode.WRONG_TOKEN);
    }
}

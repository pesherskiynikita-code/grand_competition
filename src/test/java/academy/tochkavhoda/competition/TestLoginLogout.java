package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestLoginLogout extends TestBase {

    @Test
    public void testLoginParticipant() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        String token = loginAndGetToken("ivan_petrov", "secret123");
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

    @Test
    public void testLoginExpert() {
        assertEmptySuccess(registerExpert("anna_s", "secret123"));
        String token = loginAndGetToken("anna_s", "secret123");
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

    @Test
    public void testLoginReturnsNewTokenEachTime() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        String first = loginAndGetToken("ivan_petrov", "secret123");
        String second = loginAndGetToken("ivan_petrov", "secret123");
        assertNotEquals(first, second);
    }

    @Test
    public void testRepeatedLoginInvalidatesPreviousToken() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        String first = loginAndGetToken("ivan_petrov", "secret123");
        String second = loginAndGetToken("ivan_petrov", "secret123");
        assertError(server.logout(first), ServerErrorCode.WRONG_TOKEN);
        assertEmptySuccess(server.logout(second));
    }

    @Test
    public void testLoginWrongPassword() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertError(login("ivan_petrov", "secret124"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }

    @Test
    public void testLoginUnknownUser() {
        assertError(login("nobody", "secret123"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }

    @Test
    public void testLoginIsCaseSensitive() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertError(login("IVAN_PETROV", "secret123"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testLoginEmptyFields(String value) {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertError(login(value, "secret123"), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        assertError(login("ivan_petrov", value), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"{bad json", "[]"})
    public void testLoginWrongJson(String json) {
        assertError(server.login(json), ServerErrorCode.WRONG_JSON);
    }

    @Test
    public void testLogout() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        String token = loginAndGetToken("ivan_petrov", "secret123");
        assertEmptySuccess(server.logout(token));
        assertError(server.logout(token), ServerErrorCode.WRONG_TOKEN);
        assertError(server.leave(token), ServerErrorCode.WRONG_TOKEN);
    }

    @Test
    public void testLoginAfterLogout() {
        assertEmptySuccess(registerExpert("anna_s", "secret123"));
        String token = loginAndGetToken("anna_s", "secret123");
        assertEmptySuccess(server.logout(token));
        String newToken = loginAndGetToken("anna_s", "secret123");
        assertNotEquals(token, newToken);
        assertEmptySuccess(server.logout(newToken));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-a-token", "123e4567-e89b-12d3-a456-426614174000"})
    public void testLogoutWrongToken(String token) {
        assertError(server.logout(token), ServerErrorCode.WRONG_TOKEN);
    }
}

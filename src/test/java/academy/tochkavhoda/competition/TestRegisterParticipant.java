package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestRegisterParticipant extends TestBase {

    @Test
    public void testRegisterParticipant() {
        assertEmptySuccess(registerParticipant("Иван", "Петров", "ООО Ромашка", "ivan_petrov", "secret123"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd", "Login_2024", "a_very_long_login_of_32_symbols_"})
    public void testRegisterParticipantValidLogins(String login) {
        assertEmptySuccess(registerParticipant(login, "secret123"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void testRegisterParticipantEmptyFirstName(String firstName) {
        assertError(registerParticipant(firstName, "Петров", "ООО Ромашка", "ivan_petrov", "secret123"),
                ServerErrorCode.EMPTY_FIRST_NAME);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void testRegisterParticipantEmptyLastName(String lastName) {
        assertError(registerParticipant("Иван", lastName, "ООО Ромашка", "ivan_petrov", "secret123"),
                ServerErrorCode.EMPTY_LAST_NAME);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void testRegisterParticipantEmptyCompanyName(String companyName) {
        assertError(registerParticipant("Иван", "Петров", companyName, "ivan_petrov", "secret123"),
                ServerErrorCode.EMPTY_COMPANY_NAME);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "a_very_long_login_of_33_symbols__", "логин", "ivan-petrov", "ivan petrov", "ivan@mail"})
    public void testRegisterParticipantWrongLogin(String login) {
        assertError(registerParticipant(login, "secret123"), ServerErrorCode.WRONG_LOGIN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc1234", "onlyletters", "1234567890", "        "})
    public void testRegisterParticipantWrongPassword(String password) {
        assertError(registerParticipant("ivan_petrov", password), ServerErrorCode.WRONG_PASSWORD);
    }

    @Test
    public void testRegisterParticipantDuplicateLogin() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertError(registerParticipant("ivan_petrov", "other4567"), ServerErrorCode.LOGIN_ALREADY_EXISTS);
    }

    @Test
    public void testRegisterParticipantLoginTakenByExpert() {
        assertEmptySuccess(registerExpert("anna_s", "secret123"));
        assertError(registerParticipant("anna_s", "secret123"), ServerErrorCode.LOGIN_ALREADY_EXISTS);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "{bad json", "[1, 2]", "null", "\"text\""})
    public void testRegisterParticipantWrongJson(String json) {
        assertError(server.registerParticipant(json), ServerErrorCode.WRONG_JSON);
    }
}

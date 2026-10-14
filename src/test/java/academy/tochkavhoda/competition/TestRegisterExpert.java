package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRegisterExpert extends TestBase {

    @Test
    public void testRegisterExpert() {
        assertEmptySuccess(registerExpert("Анна", "Сидорова", List.of("IT", "Экология"), "anna_s", "secret123"));
    }

    @Test
    public void testRegisterExpertOneDirection() {
        assertEmptySuccess(registerExpert("Анна", "Сидорова", List.of("Медицина"), "anna_s", "secret123"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void testRegisterExpertEmptyFirstName(String firstName) {
        assertError(registerExpert(firstName, "Сидорова", List.of("IT"), "anna_s", "secret123"),
                ServerErrorCode.EMPTY_FIRST_NAME);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void testRegisterExpertEmptyLastName(String lastName) {
        assertError(registerExpert("Анна", lastName, List.of("IT"), "anna_s", "secret123"),
                ServerErrorCode.EMPTY_LAST_NAME);
    }

    @Test
    public void testRegisterExpertNullDirections() {
        assertError(registerExpert("Анна", "Сидорова", null, "anna_s", "secret123"),
                ServerErrorCode.EMPTY_DIRECTIONS);
    }

    @Test
    public void testRegisterExpertEmptyDirections() {
        assertError(registerExpert("Анна", "Сидорова", new ArrayList<>(), "anna_s", "secret123"),
                ServerErrorCode.EMPTY_DIRECTIONS);
    }

    @Test
    public void testRegisterExpertBlankDirection() {
        assertError(registerExpert("Анна", "Сидорова", List.of("IT", "  "), "anna_s", "secret123"),
                ServerErrorCode.EMPTY_DIRECTION);
    }

    @Test
    public void testRegisterExpertNullDirection() {
        assertError(registerExpert("Анна", "Сидорова", Arrays.asList("IT", null), "anna_s", "secret123"),
                ServerErrorCode.EMPTY_DIRECTION);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "a_very_long_login_of_33_symbols__", "эксперт", "anna.s"})
    public void testRegisterExpertWrongLogin(String login) {
        assertError(registerExpert(login, "secret123"), ServerErrorCode.WRONG_LOGIN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc1234", "onlyletters", "1234567890"})
    public void testRegisterExpertWrongPassword(String password) {
        assertError(registerExpert("anna_s", password), ServerErrorCode.WRONG_PASSWORD);
    }

    @Test
    public void testRegisterExpertDuplicateLogin() {
        assertEmptySuccess(registerExpert("anna_s", "secret123"));
        assertError(registerExpert("anna_s", "secret123"), ServerErrorCode.LOGIN_ALREADY_EXISTS);
    }

    @Test
    public void testRegisterExpertLoginTakenByParticipant() {
        assertEmptySuccess(registerParticipant("ivan_petrov", "secret123"));
        assertError(registerExpert("ivan_petrov", "secret123"), ServerErrorCode.LOGIN_ALREADY_EXISTS);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"{bad json", "{\"directions\": \"IT\"}"})
    public void testRegisterExpertWrongJson(String json) {
        assertError(server.registerExpert(json), ServerErrorCode.WRONG_JSON);
    }
}

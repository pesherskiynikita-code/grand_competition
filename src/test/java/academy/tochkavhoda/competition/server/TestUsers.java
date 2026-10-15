package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUsers extends TestBase {

    @Test
    public void testRegisterParticipant() {
        ServerResponse response = server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "ООО Ромашка", "ivan", PASSWORD)));
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testRegisterExpert() {
        ServerResponse response = server.registerExpert(json(
                new RegisterExpertDtoRequest("Анна", "Смирнова", List.of("математика", "информатика"),
                        "anna", PASSWORD)));
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testRegisterParticipantWrongFields() {
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest(null, "Петров", "Ромашка", "ivan", PASSWORD))), "Имя");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "  ", "Ромашка", "ivan", PASSWORD))), "Фамилия");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "", "ivan", PASSWORD))), "фирмы");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "Ромашка", "iv", PASSWORD))), "Логин");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "Ромашка", "иван", PASSWORD))), "Логин");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "Ромашка", "ivan", "short1"))), "Пароль");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "Ромашка", "ivan", "onlyletters"))), "Пароль");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Петров", "Ромашка", "ivan", "12345678"))), "Пароль");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван".repeat(20), "Петров", "Ромашка", "ivan", PASSWORD))), "Имя");
    }

    @Test
    public void testRegisterExpertWrongDirections() {
        assertError(server.registerExpert(json(
                new RegisterExpertDtoRequest("Анна", "Смирнова", null, "anna", PASSWORD))), "направлений");
        assertError(server.registerExpert(json(
                new RegisterExpertDtoRequest("Анна", "Смирнова", Collections.emptyList(), "anna", PASSWORD))),
                "направлений");
        assertError(server.registerExpert(json(
                new RegisterExpertDtoRequest("Анна", "Смирнова", Arrays.asList("физика", " "), "anna", PASSWORD))),
                "направлений");
    }

    @Test
    public void testWrongJson() {
        assertError(server.registerParticipant("{login: "), "json");
        assertError(server.registerParticipant(""), "Пустой запрос");
        assertError(server.registerParticipant(null), "Пустой запрос");
        assertError(server.login("[1, 2]"), "json");
    }

    @Test
    public void testDuplicateLogin() {
        registerParticipantAndLogin("ivan", "Ромашка");
        assertError(server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Пётр", "Иванов", "Лютик", "ivan", PASSWORD))), "ivan");
        // логин уникален среди всех пользователей, в том числе экспертов
        assertError(server.registerExpert(json(
                new RegisterExpertDtoRequest("Анна", "Смирнова", List.of("физика"), "ivan", PASSWORD))), "ivan");
    }

    @Test
    public void testLogin() {
        String token1 = registerParticipantAndLogin("ivan", "Ромашка");
        String token2 = registerExpertAndLogin("anna", "физика");
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    public void testLoginWrongPassword() {
        registerParticipantAndLogin("ivan", "Ромашка");
        assertError(server.login(json(new LoginDtoRequest("ivan", "password2"))), "Неверный логин или пароль");
        assertError(server.login(json(new LoginDtoRequest("nobody", PASSWORD))), "Неверный логин или пароль");
        assertError(server.login(json(new LoginDtoRequest(null, null))), "Неверный логин или пароль");
    }

    @Test
    public void testLogoutAndLoginAgain() {
        String token = registerParticipantAndLogin("ivan", "Ромашка");
        assertOk(server.logout(token));
        assertError(server.logout(token), "токен");

        String newToken = login("ivan");
        assertNotEquals(token, newToken);
        assertOk(server.logout(newToken));
    }

    @Test
    public void testRepeatedLoginInvalidatesOldToken() {
        String token = registerParticipantAndLogin("ivan", "Ромашка");
        String newToken = assertOk(server.login(json(new LoginDtoRequest("ivan", PASSWORD))),
                LoginDtoResponse.class).getToken();
        assertNotEquals(token, newToken);
        assertError(server.logout(token), "токен");
        assertOk(server.logout(newToken));
    }

    @Test
    public void testLeave() {
        String token = registerParticipantAndLogin("ivan", "Ромашка");
        assertOk(server.leave(token));
        assertError(server.logout(token), "токен");
        assertError(server.login(json(new LoginDtoRequest("ivan", PASSWORD))), "Неверный логин или пароль");
        // после ухода с сервера логин освобождается
        registerParticipantAndLogin("ivan", "Лютик");
    }

    @Test
    public void testWrongToken() {
        assertError(server.logout(null), "токен");
        assertError(server.logout("not-a-token"), "токен");
        assertError(server.leave("not-a-token"), "токен");
    }
}

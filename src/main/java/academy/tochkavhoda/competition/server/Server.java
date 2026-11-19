package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.service.DebugService;
import academy.tochkavhoda.competition.service.ExpertService;
import academy.tochkavhoda.competition.service.ParticipantService;
import academy.tochkavhoda.competition.service.UserService;

public class Server {
    private final ParticipantService participantService = new ParticipantService();
    private final ExpertService expertService = new ExpertService();
    private final UserService userService = new UserService();
    private final DebugService debugService = new DebugService();

    public ServerResponse registerParticipant(String requestJson) {
        return participantService.registerParticipant(requestJson);
    }

    public ServerResponse registerExpert(String requestJson) {
        return expertService.registerExpert(requestJson);
    }

    public ServerResponse login(String requestJson) {
        return userService.login(requestJson);
    }

    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    public ServerResponse leave(String token) {
        return userService.leave(token);
    }

    /**
     * Подача одной или нескольких заявок. Возвращает номера созданных заявок.
     */
    public ServerResponse addApplications(String token, String requestJson) {
        return participantService.addApplications(token, requestJson);
    }

    /**
     * Отмена своей заявки. Оценки этой заявки тоже удаляются.
     */
    public ServerResponse cancelApplication(String token, String requestJson) {
        return participantService.cancelApplication(token, requestJson);
    }

    /**
     * Оценка заявки по шкале 1..5. Повторный вызов меняет прежнюю оценку.
     */
    public ServerResponse rateApplication(String token, String requestJson) {
        return expertService.rateApplication(token, requestJson);
    }

    public ServerResponse deleteRating(String token, String requestJson) {
        return expertService.deleteRating(token, requestJson);
    }

    /**
     * Удаляет все данные сервера. Используется в тестах.
     */
    public ServerResponse clearDatabase() {
        return debugService.clear();
    }
}

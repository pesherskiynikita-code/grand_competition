package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.service.DebugService;
import academy.tochkavhoda.competition.service.ExpertService;
import academy.tochkavhoda.competition.service.ParticipantService;
import academy.tochkavhoda.competition.service.UserService;
import com.google.gson.Gson;

/**
 * Принимает запросы и передаёт их сервисам.
 * Сервис возвращает DTO ответа или выбрасывает ServerException,
 * а Server превращает результат в ServerResponse.
 */
public class Server {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 400;

    private static final Gson GSON = new Gson();

    private final ParticipantService participantService = new ParticipantService();
    private final ExpertService expertService = new ExpertService();
    private final UserService userService = new UserService();
    private final DebugService debugService = new DebugService();

    /**
     * Вызов сервиса: возвращает DTO ответа или выбрасывает ServerException.
     */
    @FunctionalInterface
    private interface ServiceCall {
        Object call() throws ServerException;
    }

    private static ServerResponse execute(ServiceCall serviceCall) {
        try {
            return new ServerResponse(SUCCESS_CODE, GSON.toJson(serviceCall.call()));
        } catch (ServerException e) {
            return new ServerResponse(ERROR_CODE,
                    GSON.toJson(new ErrorDtoResponse(e.getErrorCode().getErrorString())));
        }
    }

    // ---------- регистрация, вход, выход ----------

    public ServerResponse registerParticipant(String requestJson) {
        return execute(() -> participantService.registerParticipant(requestJson));
    }

    public ServerResponse registerExpert(String requestJson) {
        return execute(() -> expertService.registerExpert(requestJson));
    }

    public ServerResponse login(String requestJson) {
        return execute(() -> userService.login(requestJson));
    }

    public ServerResponse logout(String token) {
        return execute(() -> userService.logout(token));
    }

    /**
     * Уход с сервера: пользователь удаляется вместе со своими заявками или оценками.
     */
    public ServerResponse leave(String token) {
        return execute(() -> userService.leave(token));
    }

    // ---------- заявки участника ----------

    /**
     * Подача одной или нескольких заявок. Возвращает номера созданных заявок.
     */
    public ServerResponse addApplications(String token, String requestJson) {
        return execute(() -> participantService.addApplications(token, requestJson));
    }

    /**
     * Отмена своей заявки. Оценки этой заявки тоже удаляются.
     */
    public ServerResponse cancelApplication(String token, String requestJson) {
        return execute(() -> participantService.cancelApplication(token, requestJson));
    }

    // ---------- оценки эксперта ----------

    /**
     * Оценка заявки по шкале 1..5. Повторный вызов меняет прежнюю оценку.
     */
    public ServerResponse rateApplication(String token, String requestJson) {
        return execute(() -> expertService.rateApplication(token, requestJson));
    }

    public ServerResponse deleteRating(String token, String requestJson) {
        return execute(() -> expertService.deleteRating(token, requestJson));
    }

    // ---------- служебное ----------

    /**
     * Удаляет все данные сервера. Используется в тестах.
     */
    public ServerResponse clearDatabase() {
        return execute(debugService::clear);
    }
}

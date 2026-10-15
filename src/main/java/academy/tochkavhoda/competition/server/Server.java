package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.daoimpl.DebugDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.service.DebugService;
import academy.tochkavhoda.competition.service.UserService;
import com.google.gson.Gson;

/**
 * Принимает запросы и передает их сервисам.
 * Сервис возвращает DTO ответа или выбрасывает ServerException,
 * а Server превращает результат в ServerResponse.
 */
public class Server {
    private static final Gson GSON = new Gson();

    private final UserService userService = new UserService(new UserDaoImpl());
    private final DebugService debugService = new DebugService(new DebugDaoImpl());

    @FunctionalInterface
    private interface ServiceCall {
        Object call() throws ServerException;
    }

    private static ServerResponse execute(ServiceCall serviceCall) {
        try {
            return new ServerResponse(ServerResponse.CODE_OK, GSON.toJson(serviceCall.call()));
        } catch (ServerException e) {
            return new ServerResponse(ServerResponse.CODE_ERROR, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
        }
    }

    // ---------- пользователи ----------

    public ServerResponse registerParticipant(String requestJsonString) {
        return execute(() -> userService.registerParticipant(requestJsonString));
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return execute(() -> userService.registerExpert(requestJsonString));
    }

    public ServerResponse login(String requestJsonString) {
        return execute(() -> userService.login(requestJsonString));
    }

    public ServerResponse logout(String token) {
        return execute(() -> userService.logout(token));
    }

    public ServerResponse leave(String token) {
        return execute(() -> userService.leave(token));
    }

    // ---------- служебное ----------

    /**
     * Очищает базу данных. Используется в тестах.
     */
    public ServerResponse clearDatabase() {
        return execute(debugService::clear);
    }
}

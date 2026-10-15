package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.daoimpl.DebugDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.service.DebugService;
import academy.tochkavhoda.competition.service.UserService;

public class Server {
    private final UserService userService = new UserService(new UserDaoImpl());
    private final DebugService debugService = new DebugService(new DebugDaoImpl());

    // ---------- пользователи ----------

    public ServerResponse registerParticipant(String requestJsonString) {
        return userService.registerParticipant(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return userService.registerExpert(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    public ServerResponse leave(String token) {
        return userService.leave(token);
    }

    // ---------- служебное ----------

    /**
     * Очищает базу данных. Используется в тестах.
     */
    public ServerResponse clearDatabase() {
        return debugService.clear();
    }
}

package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Хранилище всех данных сервера в оперативной памяти (синглтон).
 */
public final class Database {
    private static final Database INSTANCE = new Database();

    // логин -> пользователь
    private final Map<String, User> users = new HashMap<>();
    // токен <-> пользователь; у пользователя не больше одной активной сессии
    private final BidiMap<String, User> sessions = new DualHashBidiMap<>();

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public void insertUser(User user) throws ServerException {
        if (users.putIfAbsent(user.getLogin(), user) != null) {
            throw new ServerException(ServerErrorCode.LOGIN_ALREADY_EXISTS);
        }
    }

    public User getUserByLogin(String login) {
        return users.get(login);
    }

    public User getUserByToken(String token) {
        return token == null ? null : sessions.get(token);
    }

    public String login(User user) {
        String token = UUID.randomUUID().toString();
        // BidiMap сам удаляет прежний токен этого пользователя
        sessions.put(token, user);
        return token;
    }

    public void logout(String token) {
        sessions.remove(token);
    }

    public void deleteUser(User user) {
        sessions.removeValue(user);
        users.remove(user.getLogin());
    }

    public void clear() {
        sessions.clear();
        users.clear();
    }
}

package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, User> usersByLogin = new HashMap<>();
    // токен <-> пользователь, у каждого вошедшего пользователя ровно один действующий токен
    private final BidiMap<String, User> sessions = new DualHashBidiMap<>();

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public synchronized void insertUser(User user) throws ServerException {
        if (usersByLogin.putIfAbsent(user.getLogin(), user) != null) {
            throw new ServerException(ServerErrorCode.LOGIN_ALREADY_EXISTS, user.getLogin());
        }
    }

    public synchronized User getUserByLogin(String login) {
        return usersByLogin.get(login);
    }

    public synchronized String login(User user) {
        String token = UUID.randomUUID().toString();
        // повторный вход делает прежний токен недействительным
        sessions.removeValue(user);
        sessions.put(token, user);
        return token;
    }

    public synchronized void logout(String token) throws ServerException {
        if (sessions.remove(token) == null) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
    }

    public synchronized User getUserByToken(String token) throws ServerException {
        User user = token == null ? null : sessions.get(token);
        if (user == null) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
        return user;
    }

    public synchronized void deleteUser(User user) {
        sessions.removeValue(user);
        usersByLogin.remove(user.getLogin());
    }

    public synchronized void clear() {
        usersByLogin.clear();
        sessions.clear();
    }
}

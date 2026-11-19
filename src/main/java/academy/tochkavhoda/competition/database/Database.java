package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
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
    // номер заявки -> заявка
    private final Map<Integer, Application> applications = new HashMap<>();
    // номер заявки -> (логин эксперта -> оценка)
    private final Map<Integer, Map<String, Integer>> ratings = new HashMap<>();
    // номер, который получит следующая заявка
    private int nextApplicationId = 1;

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

    /**
     * Удаляет пользователя вместе с его данными:
     * у участника удаляются заявки (и их оценки), у эксперта - его оценки.
     */
    public void deleteUser(User user) {
        if (user instanceof Participant) {
            applications.values().removeIf(application -> {
                if (application.getParticipant().equals(user)) {
                    ratings.remove(application.getId());
                    return true;
                }
                return false;
            });
        }
        if (user instanceof Expert) {
            for (Map<String, Integer> applicationRatings : ratings.values()) {
                applicationRatings.remove(user.getLogin());
            }
        }
        sessions.removeValue(user);
        users.remove(user.getLogin());
    }

    public int insertApplication(Application application) {
        application.setId(nextApplicationId++);
        applications.put(application.getId(), application);
        ratings.put(application.getId(), new HashMap<>());
        return application.getId();
    }

    public Application getApplicationById(int id) {
        return applications.get(id);
    }

    public void deleteApplication(Application application) {
        applications.remove(application.getId());
        ratings.remove(application.getId());
    }

    /**
     * Ставит оценку или заменяет прежнюю оценку этого эксперта.
     */
    public void setRating(Application application, Expert expert, int rating) {
        ratings.get(application.getId()).put(expert.getLogin(), rating);
    }

    /**
     * Возвращает false, если эксперт эту заявку не оценивал.
     */
    public boolean deleteRating(Application application, Expert expert) {
        return ratings.get(application.getId()).remove(expert.getLogin()) != null;
    }

    public void clear() {
        sessions.clear();
        users.clear();
        applications.clear();
        ratings.clear();
        nextApplicationId = 1;
    }
}

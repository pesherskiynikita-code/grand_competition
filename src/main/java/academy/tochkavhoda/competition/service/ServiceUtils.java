package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Общие для всех сервисов проверки.
 */
public final class ServiceUtils {
    private static final Pattern LOGIN_PATTERN = Pattern.compile("[A-Za-z0-9_]{4,32}");
    private static final int MIN_PASSWORD_LENGTH = 8;

    private ServiceUtils() {
    }

    public static void checkNotBlank(String value, ServerErrorCode errorCode) throws ServerException {
        if (value == null || value.isBlank()) {
            throw new ServerException(errorCode);
        }
    }

    public static User getUserByToken(UserDao userDao, String token) throws ServerException {
        User user = userDao.getByToken(token);
        if (user == null) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
        return user;
    }

    /**
     * Список направлений не пустой и не содержит пустых строк.
     */
    public static void checkDirections(List<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ServerErrorCode.EMPTY_DIRECTIONS);
        }
        for (String direction : directions) {
            checkNotBlank(direction, ServerErrorCode.EMPTY_DIRECTION);
        }
    }

    public static void checkLogin(String login) throws ServerException {
        if (login == null || !LOGIN_PATTERN.matcher(login).matches()) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN);
        }
    }

    public static void checkPassword(String password) throws ServerException {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH
                || password.chars().noneMatch(Character::isLetter)
                || password.chars().noneMatch(Character::isDigit)) {
            throw new ServerException(ServerErrorCode.WRONG_PASSWORD);
        }
    }
}

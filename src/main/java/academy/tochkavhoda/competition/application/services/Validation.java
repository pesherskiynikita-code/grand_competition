package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.domain.exceptions.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Правила проверки входных данных, общие для нескольких сервисов.
 */
final class Validation {
    static final int MAX_NAME_LENGTH = 50;
    static final int MIN_LOGIN_LENGTH = 4;
    static final int MAX_LOGIN_LENGTH = 32;
    static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern LOGIN_PATTERN = Pattern.compile("[A-Za-z0-9_]+");
    private static final Pattern HAS_LETTER = Pattern.compile(".*\\p{L}.*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");

    private Validation() {
    }

    static boolean isValidName(String name) {
        return name != null && !name.isBlank() && name.length() <= MAX_NAME_LENGTH;
    }

    static void checkLogin(String login) throws ServerException {
        if (login == null || login.length() < MIN_LOGIN_LENGTH || login.length() > MAX_LOGIN_LENGTH
                || !LOGIN_PATTERN.matcher(login).matches()) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN, MIN_LOGIN_LENGTH, MAX_LOGIN_LENGTH);
        }
    }

    static void checkPassword(String password) throws ServerException {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH
                || !HAS_LETTER.matcher(password).matches() || !HAS_DIGIT.matcher(password).matches()) {
            throw new ServerException(ServerErrorCode.WRONG_PASSWORD, MIN_PASSWORD_LENGTH);
        }
    }

    /**
     * Проверяет список направлений и приводит их к единому виду (без пробелов по краям, в нижнем регистре).
     */
    static Set<String> normalizeDirections(Collection<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
        }
        Set<String> result = new LinkedHashSet<>();
        for (String direction : directions) {
            if (direction == null || direction.isBlank()) {
                throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
            }
            result.add(direction.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }
}

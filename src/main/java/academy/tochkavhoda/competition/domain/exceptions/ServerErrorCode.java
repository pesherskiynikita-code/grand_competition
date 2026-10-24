package academy.tochkavhoda.competition.domain.exceptions;

public enum ServerErrorCode {
    WRONG_JSON("Некорректный json запроса"),
    EMPTY_REQUEST("Пустой запрос"),
    WRONG_FIRST_NAME("Имя не должно быть пустым и длиннее %d символов"),
    WRONG_LAST_NAME("Фамилия не должна быть пустой и длиннее %d символов"),
    WRONG_COMPANY_NAME("Название фирмы не должно быть пустым и длиннее %d символов"),
    WRONG_LOGIN("Логин должен содержать от %d до %d латинских букв, цифр или знаков подчеркивания"),
    WRONG_PASSWORD("Пароль должен содержать не менее %d символов, хотя бы одну букву и одну цифру"),
    WRONG_DIRECTIONS("Список направлений не должен быть пустым и содержать пустые направления"),
    LOGIN_ALREADY_EXISTS("Логин %s уже используется"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    WRONG_TOKEN("Неверный или устаревший токен"),
    NOT_PARTICIPANT("Операция доступна только участникам конкурса"),
    NOT_EXPERT("Операция доступна только экспертам"),
    ;

    private final String messageTemplate;

    ServerErrorCode(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}

package academy.tochkavhoda.competition.exception;

public enum ServerErrorCode {
    WRONG_JSON("Некорректный JSON-запрос"),
    EMPTY_FIRST_NAME("Имя не может быть пустым"),
    EMPTY_LAST_NAME("Фамилия не может быть пустой"),
    EMPTY_COMPANY_NAME("Название фирмы не может быть пустым"),
    WRONG_LOGIN("Логин должен содержать от 4 до 32 символов: латинские буквы, цифры или _"),
    WRONG_PASSWORD("Пароль должен содержать не менее 8 символов, хотя бы одну букву и одну цифру"),
    EMPTY_DIRECTIONS("Список направлений не может быть пустым"),
    EMPTY_DIRECTION("Название направления не может быть пустым"),
    LOGIN_ALREADY_EXISTS("Пользователь с таким логином уже существует"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    WRONG_TOKEN("Недействительный токен"),
    NOT_PARTICIPANT("Операция доступна только участнику"),
    NOT_EXPERT("Операция доступна только эксперту"),
    EMPTY_APPLICATIONS("Список заявок не может быть пустым"),
    EMPTY_TITLE("Название заявки не может быть пустым"),
    EMPTY_DESCRIPTION("Описание заявки не может быть пустым"),
    WRONG_AMOUNT("Запрашиваемая сумма должна быть больше нуля"),
    APPLICATION_NOT_FOUND("Заявка не найдена"),
    WRONG_RATING("Оценка должна быть от 1 до 5"),
    RATING_NOT_FOUND("Вы не оценивали эту заявку");

    private final String errorString;

    ServerErrorCode(String errorString) {
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}

package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Общая часть сервисов: разбор json, проверка токена и формирование ServerResponse.
 */
abstract class BaseService {
    protected static final Gson GSON = new Gson();

    protected final UserDao userDao;

    protected BaseService(UserDao userDao) {
        this.userDao = userDao;
    }

    @FunctionalInterface
    protected interface Action {
        Object execute() throws ServerException;
    }

    /**
     * Выполняет действие и превращает его результат (DTO ответа) или исключение в ServerResponse.
     */
    protected static ServerResponse handle(Action action) {
        try {
            return new ServerResponse(ServerResponse.CODE_OK, GSON.toJson(action.execute()));
        } catch (ServerException e) {
            return new ServerResponse(ServerResponse.CODE_ERROR, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
        }
    }

    protected static <T> T parse(String json, Class<T> dtoClass) throws ServerException {
        if (json == null || json.isBlank()) {
            throw new ServerException(ServerErrorCode.EMPTY_REQUEST);
        }
        T dto;
        try {
            dto = GSON.fromJson(json, dtoClass);
        } catch (JsonParseException e) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        if (dto == null) {
            throw new ServerException(ServerErrorCode.EMPTY_REQUEST);
        }
        return dto;
    }

    protected User getUser(String token) throws ServerException {
        return userDao.getByToken(token);
    }

    protected Participant getParticipant(String token) throws ServerException {
        if (getUser(token) instanceof Participant participant) {
            return participant;
        }
        throw new ServerException(ServerErrorCode.NOT_PARTICIPANT);
    }

    protected Expert getExpert(String token) throws ServerException {
        if (getUser(token) instanceof Expert expert) {
            return expert;
        }
        throw new ServerException(ServerErrorCode.NOT_EXPERT);
    }
}

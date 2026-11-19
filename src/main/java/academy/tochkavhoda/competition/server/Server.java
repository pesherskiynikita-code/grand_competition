package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.SessionDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.DebugDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.SessionDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.AddApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RateApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.service.DebugService;
import academy.tochkavhoda.competition.service.ExpertService;
import academy.tochkavhoda.competition.service.ParticipantService;
import academy.tochkavhoda.competition.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Внешний слой приложения. Только он знает про JSON и ServerResponse:
 * разбирает JSON в DTO запроса, вызывает сервис и превращает результат сервиса в ServerResponse.
 * Сервисы ничего не знают ни про JSON, ни про коды ответа.
 */
public class Server {
    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 400;
    private static final Gson GSON = new Gson();

    private final ParticipantService participantService;
    private final ExpertService expertService;
    private final UserService userService;
    private final DebugService debugService;

    /**
     * Здесь создаются конкретные DAO и передаются в сервисы.
     * Сервисы получают только интерфейсы DAO и не знают, как устроено хранилище.
     */
    public Server() {
        UserDao userDao = new UserDaoImpl();
        SessionDao sessionDao = new SessionDaoImpl();
        ApplicationDao applicationDao = new ApplicationDaoImpl();
        RatingDao ratingDao = new RatingDaoImpl();
        participantService = new ParticipantService(userDao, applicationDao);
        expertService = new ExpertService(userDao, applicationDao, ratingDao);
        userService = new UserService(userDao, sessionDao);
        debugService = new DebugService(new DebugDaoImpl());
    }

    public ServerResponse registerParticipant(String requestJson) {
        return execute(() -> participantService.registerParticipant(
                fromJson(requestJson, RegisterParticipantDtoRequest.class)));
    }

    public ServerResponse registerExpert(String requestJson) {
        return execute(() -> expertService.registerExpert(
                fromJson(requestJson, RegisterExpertDtoRequest.class)));
    }

    public ServerResponse login(String requestJson) {
        return execute(() -> userService.login(fromJson(requestJson, LoginDtoRequest.class)));
    }

    public ServerResponse logout(String token) {
        return execute(() -> userService.logout(token));
    }

    public ServerResponse leave(String token) {
        return execute(() -> userService.leave(token));
    }

    /**
     * Подача одной или нескольких заявок. Возвращает номера созданных заявок.
     */
    public ServerResponse addApplications(String token, String requestJson) {
        return execute(() -> participantService.addApplications(token,
                fromJson(requestJson, AddApplicationsDtoRequest.class)));
    }

    /**
     * Отмена своей заявки. Оценки этой заявки тоже удаляются.
     */
    public ServerResponse cancelApplication(String token, String requestJson) {
        return execute(() -> participantService.cancelApplication(token,
                fromJson(requestJson, ApplicationIdDtoRequest.class)));
    }

    /**
     * Оценка заявки по шкале 1..5. Повторный вызов меняет прежнюю оценку.
     */
    public ServerResponse rateApplication(String token, String requestJson) {
        return execute(() -> expertService.rateApplication(token,
                fromJson(requestJson, RateApplicationDtoRequest.class)));
    }

    public ServerResponse deleteRating(String token, String requestJson) {
        return execute(() -> expertService.deleteRating(token,
                fromJson(requestJson, ApplicationIdDtoRequest.class)));
    }

    /**
     * Удаляет все данные сервера. Используется в тестах.
     */
    public ServerResponse clearDatabase() {
        return execute(debugService::clear);
    }

    /**
     * Вызов сервиса: код, который возвращает DTO ответа или бросает ServerException.
     */
    @FunctionalInterface
    private interface ServiceCall {
        Object call() throws ServerException;
    }

    /**
     * Единственное место, где результат сервиса превращается в ServerResponse.
     */
    private static ServerResponse execute(ServiceCall serviceCall) {
        try {
            return new ServerResponse(SUCCESS_CODE, GSON.toJson(serviceCall.call()));
        } catch (ServerException e) {
            return new ServerResponse(ERROR_CODE,
                    GSON.toJson(new ErrorDtoResponse(e.getErrorCode().getErrorString())));
        }
    }

    /**
     * Разбирает JSON в объект DTO. Пустой или неправильный JSON даёт ошибку WRONG_JSON.
     */
    private static <T> T fromJson(String json, Class<T> clazz) throws ServerException {
        if (json == null || json.isBlank()) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        T result;
        try {
            result = GSON.fromJson(json, clazz);
        } catch (JsonParseException e) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        if (result == null) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        return result;
    }
}

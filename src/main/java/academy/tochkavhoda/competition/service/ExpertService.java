package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.RateApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

/**
 * Операции эксперта: регистрация, оценка заявок.
 */
public class ExpertService {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();

    public EmptySuccessDtoResponse registerExpert(String requestJson) throws ServerException {
        RegisterExpertDtoRequest request =
                ServiceUtils.getClassFromJson(requestJson, RegisterExpertDtoRequest.class);
        validate(request);
        Expert expert = new Expert(request.getFirstName(), request.getLastName(),
                request.getDirections(), request.getLogin(), request.getPassword());
        userDao.insert(expert);
        return new EmptySuccessDtoResponse();
    }

    public EmptySuccessDtoResponse rateApplication(String token, String requestJson) throws ServerException {
        Expert expert = getExpert(token);
        RateApplicationDtoRequest request =
                ServiceUtils.getClassFromJson(requestJson, RateApplicationDtoRequest.class);
        if (request.getRating() < MIN_RATING || request.getRating() > MAX_RATING) {
            throw new ServerException(ServerErrorCode.WRONG_RATING);
        }
        ratingDao.setRating(getApplication(request.getApplicationId()), expert, request.getRating());
        return new EmptySuccessDtoResponse();
    }

    public EmptySuccessDtoResponse deleteRating(String token, String requestJson) throws ServerException {
        Expert expert = getExpert(token);
        ApplicationIdDtoRequest request =
                ServiceUtils.getClassFromJson(requestJson, ApplicationIdDtoRequest.class);
        if (!ratingDao.delete(getApplication(request.getApplicationId()), expert)) {
            throw new ServerException(ServerErrorCode.RATING_NOT_FOUND);
        }
        return new EmptySuccessDtoResponse();
    }

    private Expert getExpert(String token) throws ServerException {
        if (ServiceUtils.getUserByToken(userDao, token) instanceof Expert expert) {
            return expert;
        }
        throw new ServerException(ServerErrorCode.NOT_EXPERT);
    }

    private Application getApplication(int id) throws ServerException {
        Application application = applicationDao.getById(id);
        if (application == null) {
            throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND);
        }
        return application;
    }

    private static void validate(RegisterExpertDtoRequest request) throws ServerException {
        ServiceUtils.checkNotBlank(request.getFirstName(), ServerErrorCode.EMPTY_FIRST_NAME);
        ServiceUtils.checkNotBlank(request.getLastName(), ServerErrorCode.EMPTY_LAST_NAME);
        ServiceUtils.checkDirections(request.getDirections());
        ServiceUtils.checkLogin(request.getLogin());
        ServiceUtils.checkPassword(request.getPassword());
    }
}

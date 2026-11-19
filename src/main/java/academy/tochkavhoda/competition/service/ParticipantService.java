package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.AddApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Операции участника: регистрация, подача и отмена заявок.
 */
public class ParticipantService {
    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();

    public ServerResponse registerParticipant(String requestJson) {
        try {
            RegisterParticipantDtoRequest request =
                    ServiceUtils.getClassFromJson(requestJson, RegisterParticipantDtoRequest.class);
            validate(request);
            Participant participant = new Participant(request.getFirstName(), request.getLastName(),
                    request.getCompanyName(), request.getLogin(), request.getPassword());
            userDao.insert(participant);
            return ServiceUtils.success(new EmptySuccessDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    /**
     * Подача одной или нескольких заявок. Если хоть одна заявка неверна, не сохраняется ни одна.
     */
    public ServerResponse addApplications(String token, String requestJson) {
        try {
            Participant participant = getParticipant(token);
            AddApplicationsDtoRequest request =
                    ServiceUtils.getClassFromJson(requestJson, AddApplicationsDtoRequest.class);
            validate(request);
            List<Integer> ids = new ArrayList<>();
            for (ApplicationDtoRequest dto : request.getApplications()) {
                Application application = new Application(participant, dto.getTitle(), dto.getDescription(),
                        dto.getDirections(), dto.getAmount());
                ids.add(applicationDao.insert(application));
            }
            return ServiceUtils.success(new AddApplicationsDtoResponse(ids));
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    /**
     * Отмена своей заявки. Оценки этой заявки тоже удаляются.
     */
    public ServerResponse cancelApplication(String token, String requestJson) {
        try {
            Participant participant = getParticipant(token);
            ApplicationIdDtoRequest request =
                    ServiceUtils.getClassFromJson(requestJson, ApplicationIdDtoRequest.class);
            Application application = applicationDao.getById(request.getApplicationId());
            // чужую заявку отменить нельзя: для участника её как будто нет
            if (application == null || !application.getParticipant().equals(participant)) {
                throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND);
            }
            applicationDao.delete(application);
            return ServiceUtils.success(new EmptySuccessDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    private Participant getParticipant(String token) throws ServerException {
        if (ServiceUtils.getUserByToken(userDao, token) instanceof Participant participant) {
            return participant;
        }
        throw new ServerException(ServerErrorCode.NOT_PARTICIPANT);
    }

    private static void validate(AddApplicationsDtoRequest request) throws ServerException {
        if (request.getApplications() == null || request.getApplications().isEmpty()) {
            throw new ServerException(ServerErrorCode.EMPTY_APPLICATIONS);
        }
        for (ApplicationDtoRequest application : request.getApplications()) {
            if (application == null) {
                throw new ServerException(ServerErrorCode.WRONG_JSON);
            }
            ServiceUtils.checkNotBlank(application.getTitle(), ServerErrorCode.EMPTY_TITLE);
            ServiceUtils.checkNotBlank(application.getDescription(), ServerErrorCode.EMPTY_DESCRIPTION);
            ServiceUtils.checkDirections(application.getDirections());
            if (application.getAmount() <= 0) {
                throw new ServerException(ServerErrorCode.WRONG_AMOUNT);
            }
        }
    }

    private static void validate(RegisterParticipantDtoRequest request) throws ServerException {
        ServiceUtils.checkNotBlank(request.getFirstName(), ServerErrorCode.EMPTY_FIRST_NAME);
        ServiceUtils.checkNotBlank(request.getLastName(), ServerErrorCode.EMPTY_LAST_NAME);
        ServiceUtils.checkNotBlank(request.getCompanyName(), ServerErrorCode.EMPTY_COMPANY_NAME);
        ServiceUtils.checkLogin(request.getLogin());
        ServiceUtils.checkPassword(request.getPassword());
    }
}

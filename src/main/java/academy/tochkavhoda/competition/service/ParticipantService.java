package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.server.ServerResponse;

public class ParticipantService {
    private final UserDao userDao = new UserDaoImpl();

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

    private static void validate(RegisterParticipantDtoRequest request) throws ServerException {
        ServiceUtils.checkNotBlank(request.getFirstName(), ServerErrorCode.EMPTY_FIRST_NAME);
        ServiceUtils.checkNotBlank(request.getLastName(), ServerErrorCode.EMPTY_LAST_NAME);
        ServiceUtils.checkNotBlank(request.getCompanyName(), ServerErrorCode.EMPTY_COMPANY_NAME);
        ServiceUtils.checkLogin(request.getLogin());
        ServiceUtils.checkPassword(request.getPassword());
    }
}

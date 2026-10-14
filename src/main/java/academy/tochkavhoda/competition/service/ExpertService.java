package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.server.ServerResponse;

public class ExpertService {
    private final UserDao userDao = new UserDaoImpl();

    public ServerResponse registerExpert(String requestJson) {
        try {
            RegisterExpertDtoRequest request =
                    ServiceUtils.getClassFromJson(requestJson, RegisterExpertDtoRequest.class);
            validate(request);
            Expert expert = new Expert(request.getFirstName(), request.getLastName(),
                    request.getDirections(), request.getLogin(), request.getPassword());
            userDao.insert(expert);
            return ServiceUtils.success(new EmptySuccessDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    private static void validate(RegisterExpertDtoRequest request) throws ServerException {
        ServiceUtils.checkNotBlank(request.getFirstName(), ServerErrorCode.EMPTY_FIRST_NAME);
        ServiceUtils.checkNotBlank(request.getLastName(), ServerErrorCode.EMPTY_LAST_NAME);
        if (request.getDirections() == null || request.getDirections().isEmpty()) {
            throw new ServerException(ServerErrorCode.EMPTY_DIRECTIONS);
        }
        for (String direction : request.getDirections()) {
            ServiceUtils.checkNotBlank(direction, ServerErrorCode.EMPTY_DIRECTION);
        }
        ServiceUtils.checkLogin(request.getLogin());
        ServiceUtils.checkPassword(request.getPassword());
    }
}

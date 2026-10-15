package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;

import java.util.Set;

public class UserService extends BaseService {

    public UserService(UserDao userDao) {
        super(userDao);
    }

    public EmptyDtoResponse registerParticipant(String requestJsonString) throws ServerException {
        RegisterParticipantDtoRequest request = parse(requestJsonString, RegisterParticipantDtoRequest.class);
        validate(request);
        userDao.insert(new Participant(request.getFirstName().trim(), request.getLastName().trim(),
                request.getCompanyName().trim(), request.getLogin(), request.getPassword()));
        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse registerExpert(String requestJsonString) throws ServerException {
        RegisterExpertDtoRequest request = parse(requestJsonString, RegisterExpertDtoRequest.class);
        Set<String> directions = validate(request);
        userDao.insert(new Expert(request.getFirstName().trim(), request.getLastName().trim(),
                directions, request.getLogin(), request.getPassword()));
        return new EmptyDtoResponse();
    }

    public LoginDtoResponse login(String requestJsonString) throws ServerException {
        LoginDtoRequest request = parse(requestJsonString, LoginDtoRequest.class);
        User user = request.getLogin() == null ? null : userDao.getByLogin(request.getLogin());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }
        return new LoginDtoResponse(userDao.login(user));
    }

    public EmptyDtoResponse logout(String token) throws ServerException {
        userDao.logout(token);
        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse leave(String token) throws ServerException {
        userDao.delete(getUser(token));
        return new EmptyDtoResponse();
    }

    private static void validate(RegisterParticipantDtoRequest request) throws ServerException {
        validateNames(request.getFirstName(), request.getLastName());
        if (!Validation.isValidName(request.getCompanyName())) {
            throw new ServerException(ServerErrorCode.WRONG_COMPANY_NAME, Validation.MAX_NAME_LENGTH);
        }
        Validation.checkLogin(request.getLogin());
        Validation.checkPassword(request.getPassword());
    }

    private static Set<String> validate(RegisterExpertDtoRequest request) throws ServerException {
        validateNames(request.getFirstName(), request.getLastName());
        Set<String> directions = Validation.normalizeDirections(request.getDirections());
        Validation.checkLogin(request.getLogin());
        Validation.checkPassword(request.getPassword());
        return directions;
    }

    private static void validateNames(String firstName, String lastName) throws ServerException {
        if (!Validation.isValidName(firstName)) {
            throw new ServerException(ServerErrorCode.WRONG_FIRST_NAME, Validation.MAX_NAME_LENGTH);
        }
        if (!Validation.isValidName(lastName)) {
            throw new ServerException(ServerErrorCode.WRONG_LAST_NAME, Validation.MAX_NAME_LENGTH);
        }
    }
}

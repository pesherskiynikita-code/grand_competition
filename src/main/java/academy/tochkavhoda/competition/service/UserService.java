package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.SessionDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.SessionDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;
import academy.tochkavhoda.competition.dto.response.TokenDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

/**
 * Операции, общие для всех пользователей: вход, выход, уход с сервера.
 */
public class UserService {
    private final UserDao userDao = new UserDaoImpl();
    private final SessionDao sessionDao = new SessionDaoImpl();

    public TokenDtoResponse login(String requestJson) throws ServerException {
        LoginDtoRequest request = ServiceUtils.getClassFromJson(requestJson, LoginDtoRequest.class);
        validate(request);
        User user = userDao.getByLogin(request.getLogin());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }
        return new TokenDtoResponse(sessionDao.login(user));
    }

    public EmptySuccessDtoResponse logout(String token) throws ServerException {
        ServiceUtils.getUserByToken(userDao, token);
        sessionDao.logout(token);
        return new EmptySuccessDtoResponse();
    }

    public EmptySuccessDtoResponse leave(String token) throws ServerException {
        userDao.delete(ServiceUtils.getUserByToken(userDao, token));
        return new EmptySuccessDtoResponse();
    }

    private static void validate(LoginDtoRequest request) throws ServerException {
        ServiceUtils.checkNotBlank(request.getLogin(), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        ServiceUtils.checkNotBlank(request.getPassword(), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }
}

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
import academy.tochkavhoda.competition.server.ServerResponse;

/**
 * Операции, общие для всех пользователей: вход, выход, уход с сервера.
 */
public class UserService {
    private final UserDao userDao = new UserDaoImpl();
    private final SessionDao sessionDao = new SessionDaoImpl();

    public ServerResponse login(String requestJson) {
        try {
            LoginDtoRequest request = ServiceUtils.getClassFromJson(requestJson, LoginDtoRequest.class);
            validate(request);
            User user = userDao.getByLogin(request.getLogin());
            if (user == null || !user.getPassword().equals(request.getPassword())) {
                throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
            }
            return ServiceUtils.success(new TokenDtoResponse(sessionDao.login(user)));
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    public ServerResponse logout(String token) {
        try {
            getUserByToken(token);
            sessionDao.logout(token);
            return ServiceUtils.success(new EmptySuccessDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    public ServerResponse leave(String token) {
        try {
            userDao.delete(getUserByToken(token));
            return ServiceUtils.success(new EmptySuccessDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    private User getUserByToken(String token) throws ServerException {
        User user = userDao.getByToken(token);
        if (user == null) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
        return user;
    }

    private static void validate(LoginDtoRequest request) throws ServerException {
        ServiceUtils.checkNotBlank(request.getLogin(), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        ServiceUtils.checkNotBlank(request.getPassword(), ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
    }
}

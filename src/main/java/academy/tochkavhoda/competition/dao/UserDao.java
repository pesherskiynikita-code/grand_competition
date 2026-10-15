package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

public interface UserDao {
    void insert(User user) throws ServerException;

    User getByLogin(String login);

    String login(User user);

    void logout(String token) throws ServerException;

    User getByToken(String token) throws ServerException;

    void delete(User user);
}

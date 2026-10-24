package academy.tochkavhoda.competition.domain.dao;

import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.User;

public interface UserDao {
    void insert(User user) throws ServerException;

    User getByLogin(String login);

    String login(User user);

    void logout(String token) throws ServerException;

    User getByToken(String token) throws ServerException;

    void delete(User user);
}

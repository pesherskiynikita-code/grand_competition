package academy.tochkavhoda.competition.domain.daoimpl;

import academy.tochkavhoda.competition.domain.dao.UserDao;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.User;

public class UserDaoImpl implements UserDao {
    private final Database database = Database.getInstance();

    @Override
    public void insert(User user) throws ServerException {
        database.insertUser(user);
    }

    @Override
    public User getByLogin(String login) {
        return database.getUserByLogin(login);
    }

    @Override
    public String login(User user) {
        return database.login(user);
    }

    @Override
    public void logout(String token) throws ServerException {
        database.logout(token);
    }

    @Override
    public User getByToken(String token) throws ServerException {
        return database.getUserByToken(token);
    }

    @Override
    public void delete(User user) {
        database.deleteUser(user);
    }
}

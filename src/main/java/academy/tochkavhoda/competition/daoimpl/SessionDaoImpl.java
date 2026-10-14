package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.SessionDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.User;

public class SessionDaoImpl implements SessionDao {
    private final Database database = Database.getInstance();

    @Override
    public String login(User user) {
        return database.login(user);
    }

    @Override
    public void logout(String token) {
        database.logout(token);
    }
}

package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.User;

public interface SessionDao {
    String login(User user);

    void logout(String token);
}

package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

public class RatingDaoImpl implements RatingDao {
    private final Database database = Database.getInstance();

    @Override
    public void setRating(Application application, Expert expert, int rating) {
        database.setRating(application, expert, rating);
    }

    @Override
    public boolean delete(Application application, Expert expert) {
        return database.deleteRating(application, expert);
    }
}

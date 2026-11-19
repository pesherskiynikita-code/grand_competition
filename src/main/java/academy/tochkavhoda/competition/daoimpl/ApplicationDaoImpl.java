package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.Application;

public class ApplicationDaoImpl implements ApplicationDao {
    private final Database database = Database.getInstance();

    @Override
    public int insert(Application application) {
        return database.insertApplication(application);
    }

    @Override
    public Application getById(int id) {
        return database.getApplicationById(id);
    }

    @Override
    public void delete(Application application) {
        database.deleteApplication(application);
    }
}

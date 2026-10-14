package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.DebugDao;
import academy.tochkavhoda.competition.database.Database;

public class DebugDaoImpl implements DebugDao {
    @Override
    public void clear() {
        Database.getInstance().clear();
    }
}

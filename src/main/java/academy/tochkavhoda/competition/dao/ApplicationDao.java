package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.Application;

public interface ApplicationDao {
    /**
     * Сохраняет заявку и возвращает присвоенный ей номер.
     */
    int insert(Application application);

    Application getById(int id);

    void delete(Application application);
}

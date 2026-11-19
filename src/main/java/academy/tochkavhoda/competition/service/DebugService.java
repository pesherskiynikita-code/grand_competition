package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.DebugDao;
import academy.tochkavhoda.competition.daoimpl.DebugDaoImpl;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;

/**
 * Служебные операции для тестов.
 */
public class DebugService {
    private final DebugDao debugDao = new DebugDaoImpl();

    public EmptySuccessDtoResponse clear() {
        debugDao.clear();
        return new EmptySuccessDtoResponse();
    }
}

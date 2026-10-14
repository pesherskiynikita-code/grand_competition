package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.DebugDao;
import academy.tochkavhoda.competition.daoimpl.DebugDaoImpl;
import academy.tochkavhoda.competition.dto.response.EmptySuccessDtoResponse;
import academy.tochkavhoda.competition.server.ServerResponse;

/**
 * Служебные операции для тестов.
 */
public class DebugService {
    private final DebugDao debugDao = new DebugDaoImpl();

    public ServerResponse clear() {
        debugDao.clear();
        return ServiceUtils.success(new EmptySuccessDtoResponse());
    }
}

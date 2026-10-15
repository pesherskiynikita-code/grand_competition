package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.DebugDao;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;

/**
 * Служебные операции для тестов.
 */
public class DebugService {
    private final DebugDao debugDao;

    public DebugService(DebugDao debugDao) {
        this.debugDao = debugDao;
    }

    public EmptyDtoResponse clear() {
        debugDao.clear();
        return new EmptyDtoResponse();
    }
}

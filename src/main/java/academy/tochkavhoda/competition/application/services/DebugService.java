package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.domain.dao.DebugDao;
import academy.tochkavhoda.competition.api.dto.responses.EmptyDtoResponse;

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

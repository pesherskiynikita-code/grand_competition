package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.DebugDao;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;

/**
 * Служебные операции для тестов.
 */
public class DebugService {
    private static final Gson GSON = new Gson();

    private final DebugDao debugDao;

    public DebugService(DebugDao debugDao) {
        this.debugDao = debugDao;
    }

    public ServerResponse clear() {
        debugDao.clear();
        return new ServerResponse(ServerResponse.CODE_OK, GSON.toJson(new EmptyDtoResponse()));
    }
}

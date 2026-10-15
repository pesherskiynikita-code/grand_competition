package academy.tochkavhoda.competition.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestServerResponse {

    @Test
    public void testCreateResponse() {
        ServerResponse response = new ServerResponse(ServerResponse.CODE_OK, "{}");
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testEquals() {
        assertEquals(new ServerResponse(400, "{\"error\":\"x\"}"), new ServerResponse(400, "{\"error\":\"x\"}"));
        assertNotEquals(new ServerResponse(200, "{}"), new ServerResponse(400, "{}"));
    }
}

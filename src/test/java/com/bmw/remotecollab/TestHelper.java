package com.bmw.remotecollab;

import com.bmw.remotecollab.model.Room;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mockito.ArgumentMatcher;

public class TestHelper {
    public static final String URL_PREFIX_V1 = "/api/v1/";
    public static final String URL_PREFIX_V2 = "/api/v2/";
    public static final String VALID_ROOM_NAME = "validRoom";
    public static final String VALID_ROOM_UUID = "validRoomUUID";
    public static final String VALID_AV_TOKEN = "validAVToken";
    public static final String VALID_SCREEN_TOKEN = "validScreenToken";
    public static final String VALID_SESSION = "validSession";

    public static final String VALID_OPENVIDU_AV_TOKEN = "validOpenViduAVToken";
    public static final String VALID_OPENVIDU_SCREEN_TOKEN = "validOpenViduScreenToken";


    public static final Session VALID_OPENVIDU_SESSION = getValidOpenviduSession();

    public static ArgumentMatcher<String> isInvalid() {
        return argument -> !argument.startsWith("valid");
    }

    public static ArgumentMatcher<String> isValid() {
        return argument -> argument.startsWith("valid");
    }


    public static Room getValidRoom() {
        return new Room(VALID_ROOM_NAME);
    }

    @SuppressWarnings("unchecked")
    private static Session getValidOpenviduSession() {
        JSONObject json = new JSONObject();
        json.put("sessionId", VALID_ROOM_UUID);
        json.put("createdAt", System.currentTimeMillis());
        json.put("customSessionId",VALID_ROOM_UUID);
        json.put("recording", false);
        json.put("mediaMode", "RELAYED");
        json.put("recordingMode", "MANUAL");
        json.put("defaultOutputMode", "INDIVIDUAL");
        json.put("defaultRecordingLayout", "CUSTOM");
        json.put("defaultCustomLayout", "");
        JSONObject connections = new JSONObject();
        connections.put("numberOfElements", 0);
        JSONArray jsonArrayConnections = new JSONArray();
        connections.put("content", jsonArrayConnections);
        json.put("connections", connections);

        return new MockSession(null, json);
    }

    private static class MockSession extends Session {

        MockSession(OpenVidu openVidu, JSONObject json) {
            super(openVidu, json);
        }
    }

}

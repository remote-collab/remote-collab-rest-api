package com.bmw.remotecollab.rest.v2;

import com.bmw.remotecollab.dynamodb.RoomRepository;
import com.bmw.remotecollab.model.Room;
import com.bmw.remotecollab.rest.exception.OpenViduException;
import com.bmw.remotecollab.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.rest.v2.request.RequestInviteUser;
import com.bmw.remotecollab.rest.v2.request.RequestNewRoom;
import com.bmw.remotecollab.service.RoomService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static com.bmw.remotecollab.TestHelper.isInvalid;
import static com.bmw.remotecollab.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerV2Test {
    private static final Gson gson = new Gson();

    @Autowired
    RoomControllerV2 controller;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private RoomService roomService;


    @Before
    public void beforeTest() throws ResourceNotFoundException, OpenViduException {
        Mockito.when(roomService.doesRoomExist(VALID_ROOM_UUID)).thenReturn(true);
        Mockito.when(roomService.doesRoomExist(argThat(isInvalid()))).thenReturn(false);

        Mockito.when(roomService.joinRoom(VALID_ROOM_UUID))
                .thenReturn(new RoomService.JoinRoomTokens(VALID_ROOM_NAME, VALID_AV_TOKEN, VALID_SCREEN_TOKEN, VALID_SESSION));
        Mockito.when(roomService.joinRoom(argThat(isInvalid()))).thenThrow(new ResourceNotFoundException(""));

        Mockito.when(roomService.createNewRoom(argThat(isValid()), any())).thenReturn(new Room(VALID_ROOM_NAME));
    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    public void testStatus() throws Exception {
        get("status", status().isOk())
                .andExpect(content().string(containsString("up")));
    }

    @Test
    public void testNewRoom_validEmails() throws Exception {
        RequestNewRoom roomRequest = new RequestNewRoom(VALID_ROOM_NAME, null);


        // no mail is valid
        post("rooms", roomRequest, status().isOk())
                .andExpect(jsonPath("uuid").isNotEmpty());


        //any list of valid emails is valid
        roomRequest = new RequestNewRoom(VALID_ROOM_NAME, new ArrayList<>());
        //empty list
        post("rooms", roomRequest, status().isOk())
                .andExpect(jsonPath("uuid").isNotEmpty());

        roomRequest.getEmails().add("validMail@email.com");
        post("rooms", roomRequest, status().isOk())
                .andExpect(jsonPath("uuid").isNotEmpty());

        roomRequest.getEmails().add("validMail2@email.com");
        post("rooms", roomRequest, status().isOk())
                .andExpect(jsonPath("uuid").isNotEmpty());
    }

    @Test
    public void testNewRoom_invalidEmails() throws Exception {
        RequestNewRoom roomRequest = new RequestNewRoom("AnyName", new ArrayList<>());


        roomRequest.getEmails().add("INVALID");
        post("rooms", roomRequest, status().isBadRequest());

        roomRequest.getEmails().add("INVALID2");
        post("rooms", roomRequest, status().isBadRequest());

        roomRequest.getEmails().add("INVALID3");
        post("rooms", roomRequest, status().isBadRequest());
    }


    @Test
    public void testInviteUsersValidation_ValidRoom() throws Exception {
        RequestInviteUser invite = new RequestInviteUser(null);

        post("rooms/{roomUUID}/users", invite, status().isBadRequest(), VALID_ROOM_UUID);

        invite = new RequestInviteUser(new ArrayList<>());
        post("rooms/{roomUUID}/users", invite, status().isBadRequest(), VALID_ROOM_UUID);

        invite.getEmails().add("validMail@email.com");
        post("rooms/{roomUUID}/users", invite, status().isOk(), VALID_ROOM_UUID);

        invite.getEmails().add("validMail2@email.com");
        post("rooms/{roomUUID}/users", invite, status().isOk(), VALID_ROOM_UUID);

        invite.getEmails().add("INVALID");
        post("rooms/{roomUUID}/users", invite, status().isBadRequest(), VALID_ROOM_UUID);
    }

    @Test
    public void testInviteUsers_InvalidRoom() throws Exception {
        RequestInviteUser invite = new RequestInviteUser(null);

        //no emails are not ok
        post("rooms/{roomUUID}/users", invite, status().isBadRequest(), "invalidRoomUUID");

        //empty email list is not ok
        invite = new RequestInviteUser(new ArrayList<>());
        post("rooms/{roomUUID}/users", invite, status().isBadRequest(), "invalidRoomUUID");

        //correct email for unknown room is not found
        invite.getEmails().add("validMail@email.com");
        post("rooms/{roomUUID}/users", invite, status().isNotFound(), "invalidRoomUUID");
    }

    @Test
    public void testJoinRoom() throws Exception {
        post("rooms/{roomUUID}/join", status().isOk(), VALID_ROOM_UUID)
                .andExpect(ResultMatcher.matchAll(
                        jsonPath("roomName").value(VALID_ROOM_NAME),
                        jsonPath("audioVideoToken").value(VALID_AV_TOKEN),
                        jsonPath("screenShareToken").value(VALID_SCREEN_TOKEN),
                        jsonPath("sessionId").value(VALID_SESSION)
                ));

        post("rooms/{roomUUID}/join", status().isNotFound(), "invalidRoomUUID");
    }

    private ResultActions post(String path, ResultMatcher expectedResult, Object... pathParams) throws Exception {
        return post(path, null, expectedResult, pathParams);
    }

    private ResultActions post(String path, Object content, ResultMatcher expectedResult, Object... pathParams) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URL_PREFIX_V2 + path, pathParams);
        if (content != null) {
            request.contentType(MediaType.APPLICATION_JSON)
                    .content(gson.toJson(content));
        }
        return mockMvc.perform(request).andExpect(expectedResult);
    }

    private ResultActions get(String path, ResultMatcher expectedResult, Object... pathParams) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(URL_PREFIX_V2 + path, pathParams)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedResult);
    }
}
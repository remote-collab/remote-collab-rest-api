package com.bmw.remotecollab.rest.v1;

import com.bmw.remotecollab.dynamodb.RoomRepository;
import com.bmw.remotecollab.model.Room;
import com.bmw.remotecollab.rest.exception.OpenViduException;
import com.bmw.remotecollab.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.rest.v1.request.RequestInviteUser;
import com.bmw.remotecollab.rest.v1.request.RequestJoinRoom;
import com.bmw.remotecollab.rest.v1.request.RequestNewRoom;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static com.bmw.remotecollab.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerV1Test {
    private static final Gson gson = new Gson();

    @Autowired
    RoomControllerV1 controller;

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
            .thenReturn(new RoomService.JoinRoomToken(VALID_ROOM_NAME, VALID_AV_TOKEN, VALID_SESSION));
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
        RequestNewRoom roomRequest = new RequestNewRoom();
        roomRequest.setRoomName(VALID_ROOM_NAME);


        // no mail is valid
        post("rooms", roomRequest, status().isOk())
            .andExpect(jsonPath("uuid").isNotEmpty());


        //any list of valid emails is valid
        roomRequest.setEmails(new ArrayList<>());
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
        RequestInviteUser invite = new RequestInviteUser();
        invite.setRoomUUID(VALID_ROOM_UUID);

        post("rooms/users", invite, status().isBadRequest());

        invite.setEmails(new ArrayList<>());
        post("rooms/users", invite, status().isBadRequest());

        invite.getEmails().add("validMail@email.com");
        post("rooms/users", invite, status().isOk());

        invite.getEmails().add("validMail2@email.com");
        post("rooms/users", invite, status().isOk());

        invite.getEmails().add("INVALID");
        post("rooms/users", invite, status().isBadRequest());
    }

    @Test
    public void testInviteUsers_InvalidRoom() throws Exception {
        RequestInviteUser invite = new RequestInviteUser();
        invite.setRoomUUID("invalidRoomId");

        post("rooms/users", invite, status().isBadRequest());

        invite.setEmails(new ArrayList<>());
        post("rooms/users", invite, status().isBadRequest());

        invite.getEmails().add("validMail@email.com");
        post("rooms/users", invite, status().isNotFound());
    }

    @Test
    public void testJoinRoom() throws Exception {
        RequestJoinRoom request = new RequestJoinRoom();
        request.setRoomUUID(VALID_ROOM_UUID);


        post("rooms/join", request, status().isOk())
            .andExpect(ResultMatcher.matchAll(
                jsonPath("roomName").value(VALID_ROOM_NAME),
                jsonPath("token").value(VALID_AV_TOKEN),
                jsonPath("sessionId").value(VALID_SESSION)
            ));

        request.setRoomUUID("invalidRoomID");
        post("rooms/join", request, status().isNotFound());
    }


    private ResultActions post(String path, Object content, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(URL_PREFIX_V1 + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(content)))
            .andExpect(expectedResult);
    }

    private ResultActions get(String path, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(
            MockMvcRequestBuilders.get(URL_PREFIX_V1 + path)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(expectedResult);
    }
}

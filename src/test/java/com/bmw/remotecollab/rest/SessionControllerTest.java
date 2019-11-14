package com.bmw.remotecollab.rest;

import com.bmw.remotecollab.TestHelper;
import com.bmw.remotecollab.dynamodb.RoomRepository;
import com.bmw.remotecollab.rest.exception.OpenViduException;
import com.bmw.remotecollab.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.rest.requests.RequestInviteUser;
import com.bmw.remotecollab.rest.requests.RequestJoinRoom;
import com.bmw.remotecollab.rest.requests.RequestNewRoom;
import com.bmw.remotecollab.rest.response.ResponseJoinRoom;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {
    private static final Gson gson = new Gson();

    @Autowired
    SessionController controller;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private RoomService roomService;


    @Before
    public void beforeTest() throws ResourceNotFoundException, OpenViduException {
        Mockito.when(roomService.doesRoomExist(TestHelper.VALID_ROOM_UUID)).thenReturn(true);
        Mockito.when(roomService.doesRoomExist(argThat(TestHelper.isInvalid()))).thenReturn(false);

        Mockito.when(roomService.joinRoom(TestHelper.VALID_ROOM_UUID))
                .thenReturn(new ResponseJoinRoom(TestHelper.VALID_ROOM_NAME, TestHelper.VALID_AV_TOKEN, TestHelper.VALID_SCREEN_TOKEN, TestHelper.VALID_SESSION));
        Mockito.when(roomService.joinRoom(argThat(TestHelper.isInvalid()))).thenThrow(new ResourceNotFoundException(""));

        Mockito.when(roomService.createNewRoom(argThat(TestHelper.isValid()), any())).thenReturn(TestHelper.VALID_ROOM_UUID);
    }

    @Test
    public void contexLoads() {
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
        roomRequest.setRoomName(TestHelper.VALID_ROOM_NAME);


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
        RequestNewRoom roomRequest = new RequestNewRoom();
        roomRequest.setRoomName("AnyName");

        roomRequest.setEmails(new ArrayList<>());
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
        invite.setRoomUUID(TestHelper.VALID_ROOM_UUID);

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
        request.setRoomUUID(TestHelper.VALID_ROOM_UUID);


        post("rooms/join", request, status().isOk())
                .andExpect(ResultMatcher.matchAll(
                        jsonPath("roomName").value(TestHelper.VALID_ROOM_NAME),
                        jsonPath("token").value(TestHelper.VALID_AV_TOKEN),
                        jsonPath("secondToken").value(TestHelper.VALID_SCREEN_TOKEN),
                        jsonPath("sessionId").value(TestHelper.VALID_SESSION)
                ));

        request.setRoomUUID("invalidRoomID");
        post("rooms/join", request, status().isNotFound());
    }


    private ResultActions post(String path, Object content, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(TestHelper.URL_PREFIX + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(content)))
                .andExpect(expectedResult);
    }

    private ResultActions get(String path, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(TestHelper.URL_PREFIX + path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedResult);
    }
}
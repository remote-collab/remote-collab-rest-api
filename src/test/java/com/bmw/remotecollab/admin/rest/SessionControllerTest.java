package com.bmw.remotecollab.admin.rest;

import com.bmw.remotecollab.admin.dynamoDB.RoomRepository;
import com.bmw.remotecollab.admin.rest.requests.RequestInviteUser;
import com.bmw.remotecollab.admin.rest.requests.RequestNewRoom;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {


    private static final String URL_PREFIX = "/api/v1/";
    private static final Gson gson = new Gson();

    @Autowired
    SessionController controller;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository rooms;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }


    @Test
    public void testStatus() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX + "status")).andExpect(status().isOk())
                .andExpect(content().string(containsString("up")));
    }

    @Test
    public void testNewRoomEmailValidation() throws Exception {
        RequestNewRoom roomRequest = new RequestNewRoom();
        roomRequest.setRoomName("test123");

        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(roomRequest)))
                .andExpect(status().isOk());

        roomRequest.setEmails(new ArrayList<>());

        roomRequest.getEmails().add("validMail@email.com");
        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(roomRequest)))
                .andExpect(status().isOk());

        roomRequest.getEmails().add("validMail2@email.com");
        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(roomRequest)))
                .andExpect(status().isOk());

        roomRequest.getEmails().add("INVALID");
        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(roomRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInviteUsersValidation() throws Exception {
        RequestInviteUser invite = new RequestInviteUser();
        invite.setRoomUUID("validRoom");

        mockMvc.perform(
                post(URL_PREFIX + "rooms/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invite)))
                .andExpect(status().isBadRequest());

        invite.setEmails(new ArrayList<>());

        invite.getEmails().add("validMail@email.com");
        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invite)))
                .andExpect(status().isOk());

        invite.getEmails().add("validMail2@email.com");
        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invite)))
                .andExpect(status().isOk());

        invite.getEmails().add("INVALID");
        mockMvc.perform(
                post(URL_PREFIX + "rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invite)))
                .andExpect(status().isBadRequest());
    }

}
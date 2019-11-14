package com.bmw.remotecollab.service;

import com.bmw.remotecollab.TestHelper;
import com.bmw.remotecollab.dynamodb.RoomRepository;
import com.bmw.remotecollab.model.Room;
import com.bmw.remotecollab.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.rest.response.ResponseJoinRoom;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoomServiceTest {
    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private EmailService emailService;
    @MockBean
    private OpenViduService openViduService;

    @Autowired
    private RoomService roomService;


    @Before
    public void beforeTest() {
        when(roomRepository.findById(argThat(TestHelper.isValid()))).thenReturn(Optional.of(TestHelper.getValidRoom()));
        when(roomRepository.existsById(argThat(TestHelper.isValid()))).thenReturn(true);
    }

    @Test
    public void createNewRoom() {
        String uuid = roomService.createNewRoom(TestHelper.VALID_ROOM_NAME, null);
        assertThat(uuid).isNotBlank();

        List<String> emails = new ArrayList<>();
        String uuid2 = roomService.createNewRoom(TestHelper.VALID_ROOM_NAME, emails);
        assertThat(uuid2).isNotBlank();

        emails.add("any@email.com");
        String uuid3 = roomService.createNewRoom(TestHelper.VALID_ROOM_NAME, emails);
        assertThat(uuid3).isNotBlank();


        assertThat(uuid).isNotEqualTo(uuid2);
        assertThat(uuid2).isNotEqualTo(uuid3);
        assertThat(uuid3).isNotEqualTo(uuid);
    }

    @Test
    public void joinRoom() throws Exception {
        //- setup
        when(openViduService.createSession(anyString())).thenReturn(TestHelper.VALID_OPENVIDU_SESSION);
        when(openViduService.getTokenForSession(any())).thenReturn(UUID.randomUUID().toString());

        //- tests
        final ResponseJoinRoom response = roomService.joinRoom(TestHelper.VALID_ROOM_UUID);
        assertThat(response).isNotNull();
        assertThat(response.getRoomName()).isEqualTo(TestHelper.VALID_ROOM_NAME);
        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getSecondToken()).isNotEmpty();
    }


    @Test(expected = ResourceNotFoundException.class)
    public void joinRoom_invalidRoom_throwsNotFound() throws Exception {
        roomService.joinRoom("invalidRoomUUID");
    }


    @Test(expected = ResourceNotFoundException.class)
    public void joinRoom_createSessionFails_throwsNotFound() throws Exception {
        when(openViduService.createSession(anyString())).thenReturn(null);

        roomService.joinRoom(TestHelper.VALID_ROOM_UUID);
    }

    @Test
    public void doesRoomExist() {
        boolean exists = roomService.doesRoomExist(TestHelper.VALID_ROOM_UUID);
        assertThat(exists).isTrue();

        exists = roomService.doesRoomExist("invalidRoomUUID");
        assertThat(exists).isFalse();

        exists = roomService.doesRoomExist("");
        assertThat(exists).isFalse();
    }

    @Test(expected = NullPointerException.class)
    public void doesRoomExist_throwsException_whenNullParam() {
        boolean exists = roomService.doesRoomExist(null);
    }

    @Test
    public void sendUserInvitation_allValid() {
        final ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);

        List<String> emails = new ArrayList<>();
        emails.add("valid@email.com");
        emails.add("2ndValid@email.com");
        emails.add("email@same.com");
        emails.add("email@same.com");
        roomService.sendUserInvitation(TestHelper.VALID_ROOM_UUID, emails);

        verify(roomRepository).save(roomCaptor.capture());
        assertThat(roomCaptor.getValue().getMembers().size()).isEqualTo(3);

        verify(emailService).sendInvitationEmail(roomCaptor.capture());
        final Room usedRoom = roomCaptor.getValue();
        assertThat(usedRoom.getId()).isNotEmpty();
        //only three receivers because one mail is a duplicate
        assertThat(usedRoom.getMembers().size()).isEqualTo(3);
        assertThat(usedRoom.getName()).isEqualTo(TestHelper.VALID_ROOM_NAME);
    }

    @Test
    public void sendUserInvitation_invalidRoomDoesNotSendMails() {
        List<String> emails = new ArrayList<>();
        emails.add("valid@email.com");
        roomService.sendUserInvitation("invalidRoomUUID", emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_invalidEmail_throwException() {
        List<String> emails = new ArrayList<>();
        emails.add("INVALID EMAIL ADDRESS");
        roomService.sendUserInvitation(TestHelper.VALID_ROOM_UUID, emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_nullEmail_throwException() {
        //noinspection ConstantConditions
        roomService.sendUserInvitation(TestHelper.VALID_ROOM_UUID, null);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_emptyEmail_throwException() {
        List<String> emails = new ArrayList<>();
        roomService.sendUserInvitation(TestHelper.VALID_ROOM_UUID, emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_emptyEmail2_throwException() {
        List<String> emails = new ArrayList<>();
        emails.add("");
        roomService.sendUserInvitation(TestHelper.VALID_ROOM_UUID, emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }
}
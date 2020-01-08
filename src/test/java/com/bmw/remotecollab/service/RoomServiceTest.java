package com.bmw.remotecollab.service;

import com.bmw.remotecollab.TestHelper;
import com.bmw.remotecollab.dynamodb.RoomRepository;
import com.bmw.remotecollab.model.Member;
import com.bmw.remotecollab.model.Room;
import com.bmw.remotecollab.rest.exception.ResourceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.*;

import static com.bmw.remotecollab.TestHelper.*;
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
        when(roomRepository.findById(argThat(isValid()))).thenReturn(Optional.of(TestHelper.getValidRoom()));
        when(roomRepository.existsById(argThat(isValid()))).thenReturn(true);
    }

    @Test
    public void createNewRoom() {
        Room result1 = roomService.createNewRoom(VALID_ROOM_NAME, null);
        assertThat(result1.getId()).isNotBlank();
        assertThat(result1.getMembers()).isNull();

        List<String> emails = new ArrayList<>();
        Room result2 = roomService.createNewRoom(VALID_ROOM_NAME, emails);
        assertThat(result2.getId()).isNotBlank();
        assertThat(result1.getMembers()).isNull();

        emails.add("any@email.com");
        Room result3 = roomService.createNewRoom(VALID_ROOM_NAME, emails);
        assertThat(result3.getId()).isNotBlank();
        assertThat(result3.getMembers().size()).isEqualTo(1);


        assertThat(result1.getId()).isNotEqualTo(result2.getId());
        assertThat(result2.getId()).isNotEqualTo(result3.getId());
        assertThat(result3.getId()).isNotEqualTo(result1.getId());
    }

    @Test
    public void joinRoom() throws Exception {
        //- setup
        when(openViduService.createSession(anyString())).thenReturn(VALID_OPENVIDU_SESSION);
        when(openViduService.getTokenForSession(any())).thenReturn(UUID.randomUUID().toString());

        //- tests
        final RoomService.JoinRoomTokens response = roomService.joinRoom(VALID_ROOM_UUID);
        assertThat(response).isNotNull();
        assertThat(response.roomName).isEqualTo(VALID_ROOM_NAME);
        assertThat(response.audioVideoToken).isNotEmpty();
        assertThat(response.screenShareToken).isNotEmpty();
    }


    @Test(expected = ResourceNotFoundException.class)
    public void joinRoom_invalidRoom_throwsNotFound() throws Exception {
        roomService.joinRoom("invalidRoomUUID");
    }


    @Test(expected = ResourceNotFoundException.class)
    public void joinRoom_createSessionFails_throwsNotFound() throws Exception {
        when(openViduService.createSession(anyString())).thenReturn(null);

        roomService.joinRoom(VALID_ROOM_UUID);
    }

    @Test
    public void doesRoomExist() {
        boolean exists = roomService.doesRoomExist(VALID_ROOM_UUID);
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
        final ArgumentCaptor<Collection<Member>> membersCaptor = ArgumentCaptor.forClass(Collection.class);

        List<String> emails = new ArrayList<>();
        emails.add("valid@email.com");
        emails.add("2ndValid@email.com");
        emails.add("email@same.com");
        emails.add("email@same.com");
        roomService.sendUserInvitation(VALID_ROOM_UUID, emails);

        verify(roomRepository).save(roomCaptor.capture());
        assertThat(roomCaptor.getValue().getMembers().size()).isEqualTo(3);

        verify(emailService).sendInvitationEmail(roomCaptor.capture(), membersCaptor.capture());
        final Room usedRoom = roomCaptor.getValue();
        assertThat(usedRoom.getId()).isNotEmpty();
        //only three receivers because one mail is a duplicate
        assertThat(usedRoom.getMembers().size()).isEqualTo(3);
        assertThat(usedRoom.getName()).isEqualTo(VALID_ROOM_NAME);

        final Collection<Member> actualNewMembers = membersCaptor.getValue();
        assertThat(actualNewMembers.size()).isEqualTo(3);
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
        roomService.sendUserInvitation(VALID_ROOM_UUID, emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_nullEmail_throwException() {
        //noinspection ConstantConditions
        roomService.sendUserInvitation(VALID_ROOM_UUID, null);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_emptyEmail_throwException() {
        List<String> emails = new ArrayList<>();
        roomService.sendUserInvitation(VALID_ROOM_UUID, emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void sendUserInvitation_emptyEmail2_throwException() {
        List<String> emails = new ArrayList<>();
        emails.add("");
        roomService.sendUserInvitation(VALID_ROOM_UUID, emails);
        verify(emailService, never()).sendInvitationEmail(any());
    }
}

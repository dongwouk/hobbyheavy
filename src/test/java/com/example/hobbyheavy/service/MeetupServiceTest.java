package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.entity.Hobby;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.HobbyRepository;
import com.example.hobbyheavy.repository.MeetupsRepository;
import com.example.hobbyheavy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("모임 생성 테스트")
class MeetupServiceTest {

    @InjectMocks
    private MeetupsService meetupsService;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private MeetupsRepository meetupsRepository;

    @Mock
    private UserRepository userRepository;

    private MeetupCreateRequest request;

    @BeforeEach
    void setUp() {
        request = MeetupCreateRequest.builder()
                .meetupName("Sample Meetup")
                .description("This is a sample meetup.")
                .location("Seoul")
                .recurrenceRule("Weekly")
                .maxParticipants(10)
                .hostName("hostUser")
                .hobbyName("Hiking")
                .build();
    }

    @Test
    @DisplayName("이미 존재하는 모임 이름으로 호출할 때")
    void createMeetup_whenMeetupNameExists_thenThrowsException() {
        // given
        when(meetupsRepository.existsByMeetupName(request.getMeetupName())).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> meetupsService.createMeetup(request));
        verify(meetupsRepository, never()).save(any(Meetup.class));
    }

    @Test
    @DisplayName("객체 저장 확인")
    void createMeetup_whenHobbyNameIsValid_thenCreatesMeetup() {
        // given
        when(meetupsRepository.existsByMeetupName(request.getMeetupName())).thenReturn(false);
        when(userRepository.findByUserId(request.getHostName()))
                .thenReturn(User.builder().id(1L).user_id(request.getHostName()).build());
        when(hobbyRepository.findFirstByHobbyName(request.getHobbyName()))
                .thenReturn(Optional.of(Hobby.builder().hobbyId(1L).hobbyName(request.getHobbyName()).build()));

        // when
        meetupsService.createMeetup(request);

        // then
        verify(meetupsRepository, times(1)).save(any(Meetup.class));
    }

    @Test
    @DisplayName("유효하지 않은 취미 이름이 주어졌을 때")
    void createMeetup_whenHobbyNameIsInvalid_thenThrowsException() {
        // given
        when(meetupsRepository.existsByMeetupName(request.getMeetupName())).thenReturn(false);
        when(userRepository.findByUserId(request.getHostName()))
                .thenReturn(User.builder().id(1L).user_id(request.getHostName()).build());
        when(hobbyRepository.findFirstByHobbyName(request.getHobbyName())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> meetupsService.createMeetup(request));
        verify(meetupsRepository, never()).save(any(Meetup.class));
    }
}

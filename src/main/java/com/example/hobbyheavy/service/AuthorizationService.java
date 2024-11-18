package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.UnauthorizedException;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.util.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final ParticipantRepository participantRepository;

    // 호스트 또는 부호스트 권한 확인 메서드
    /**
     * 호스트 또는 부호스트 권한 확인 메서드
     *
     * @param userId 사용자 ID
     * @param meetupId 모임 ID
     * @return 검증된 Participant 객체
     * @throws UnauthorizedException 사용자가 권한이 없을 경우 예외 발생
     */
    public Participant verifyHostOrSubHostRole(String userId, Long meetupId) {
        return participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId)
                .filter(participant ->
                        ParticipantRole.HOST.name().equals(participant.getMeetupRole()) ||
                                ParticipantRole.SUB_HOST.name().equals(participant.getMeetupRole())
                )
                .orElseThrow(() -> new UnauthorizedException("권한이 없습니다."));
    }

    /**
     * 현재 사용자에 대해 모임의 호스트 또는 부호스트 권한을 확인하는 메서드
     *
     * @param meetupId 모임 ID
     */
    private void verifyAuthorization(Long meetupId) {
        String userId = UserContextUtil.getCurrentUserId();
        verifyHostOrSubHostRole(userId, meetupId);
    }
}

package com.example.hanghaefinal.service;

import com.example.hanghaefinal.Enum.AlarmType;
import com.example.hanghaefinal.dto.responseDto.AlarmResponseDto;
import com.example.hanghaefinal.dto.responseDto.UserInfoResponseDto;
import com.example.hanghaefinal.model.Alarm;
import com.example.hanghaefinal.model.Paragraph;
import com.example.hanghaefinal.model.Post;
import com.example.hanghaefinal.model.User;
import com.example.hanghaefinal.repository.AlarmRepository;
import com.example.hanghaefinal.repository.ParagraphRepository;
import com.example.hanghaefinal.repository.PostRepository;
import com.example.hanghaefinal.repository.UserRepository;
import com.example.hanghaefinal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ParagraphRepository paragraphRepository;


    /* 알림 목록 */
    public List<AlarmResponseDto> getAlamList(User user, int page, int size) {
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt")
                .descending());

        List<Alarm> alarmListPage = alarmRepository
                .findAllByUserIdOrderByIdDesc(userId, pageable).getContent();

        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmListPage) {
                /* 내가 참여한 게시글에 새로운 문단이 등록 됐을 때 */
            if (alarm.getType().equals(AlarmType.NEWPARAGRAPH)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId().toString())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId().toString())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            } /* 내가 참여한 게시글이 완성 됐을 때 */
            else if (alarm.getType().equals(AlarmType.COMPLETEPOST)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId().toString())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId().toString())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            } /* 내가 작성한 문단이 좋아요 받았을 때 */
            else if (alarm.getType().equals(AlarmType.LIKEPARAGRAPH)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId().toString())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId().toString())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            } /* 내가 참여한 게시글이 좋아요 받았을 때 */
            else if (alarm.getType().equals(AlarmType.LIKEPOST)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId().toString())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId().toString())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            }
        }
        return alarmResponseDtoList;
    }

    // 1. 내가 참여한 소설에 새로운 문단이 달렸을 경우
    // paragraphOwner 빼고 알림을 보내줌
    public void generateNewParagraphAlarm(User paragraphOwner, Post post) {
        log.info("---------------------- 111111bbbb ----------------------");
        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());
        List<Long> userIdList = new ArrayList<>();

        for(Paragraph paragraph : paragraphList){
            if( !userIdList.contains(paragraph.getUser().getId()) ) {
                userIdList.add(paragraph.getUser().getId());
            }
        }

        log.info("------------------- userIdList의 SIZE : " + userIdList.size());
        for(Long userid : userIdList){
            if (!Objects.equals(userid, paragraphOwner.getId())){ // 문단을 작성한 사람과 다르면
                Alarm alarm = Alarm.builder()
                        .userId(userid)
                        .type(AlarmType.NEWPARAGRAPH)
                        .postId(post.getId())
                        .isRead(false)
                        .alarmMessage("[알림] ["
                                + post.getTitle()
                                + "] 소설에 문단이 등록되었습니다. 확인해보세요! 11aa")
                        .build();

                //log.info("--------------- 터짐11 ---------------");
                // redis로 알림메시지 pub, alarmRepository에 저장
                // 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외 ?
                alarmRepository.save(alarm);

                /* 알림 메시지를 보낼 DTO 생성 */
                AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId().toString())
                        .type(alarm.getType().toString())
                        .message("[알림] ["
                                + post.getTitle()
                                + "] 소설에 문단이 등록되었습니다. 확인해보세요! 11bb")
                        .alarmTargetId(userid.toString())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId().toString())
                        .build();

                redisTemplate.convertAndSend(channelTopic.getTopic(),
                        alarmResponseDto);
            }
        }

    }

    // 2. 미완성 -> 완성 됐을 때 알림         LastParagraphOwner 만 빼고 알림을 보내줌
    public void generateCompletePostAlarm(User LastParagraphOwner, Post post) {
        log.info("---------------------- 222222bbbb ----------------------");
        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());
        List<Long> userIdList = new ArrayList<>();

        for(Paragraph paragraph : paragraphList){
            if( !userIdList.contains(paragraph.getUser().getId()) ) {
                userIdList.add(paragraph.getUser().getId());
            }
        }

        log.info("------------------- userIdList의 SIZE : " + userIdList.size());
        for(Long userid : userIdList){
            if (!Objects.equals(userid, LastParagraphOwner.getId())){
                Alarm alarm = Alarm.builder()
                        .userId(userid)
                        .type(AlarmType.COMPLETEPOST)
                        .postId(post.getId())
                        .isRead(false)
                        .alarmMessage("[알림] ["
                                + post.getTitle()
                                + "] 소설이 완성되었습니다. 확인해보세요! 22aa")
                        .build();

                alarmRepository.save(alarm);

                /* 알림 메시지를 보낼 DTO 생성 */
                AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId().toString())
                        .type(alarm.getType().toString())
                        .message("[알림] ["
                                + post.getTitle()
                                + "] 소설이 완성되었습니다.. 확인해보세요! 22bb")
                        .alarmTargetId(userid.toString())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId().toString())
                        .build();
                /*-
                 * redis로 알림메시지 pub, alarmRepository에 저장
                 * 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외
                 */
                redisTemplate.convertAndSend(channelTopic.getTopic(),
                        alarmResponseDto);
            }
        }

    }

    // 3. 내가 작성한 문단이 좋아요를 받았을 때 알림
    public void generateParagraphLikestAlarm(User ParagraphOwner, Post post ) {
        log.info("---------------------- 333333bbbb ----------------------");
        Alarm alarm = Alarm.builder()
                .userId(ParagraphOwner.getId())
                .type(AlarmType.LIKEPARAGRAPH)
                .postId(post.getId())
                .isRead(false)
                .alarmMessage("[알림] ["
                        + post.getTitle()
                        + "] 소설에 작성한 문단에 좋아요가 달렸습니다. 33aa")
                .build();

        // 조건문 없으니 밑에서 alarm.getId()를 찾기위해선 여기서 먼저 저장해야한다.
        alarmRepository.save(alarm);

        /* 알림 메시지를 보낼 DTO 생성 */
        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .alarmId(alarm.getId().toString())
                .type(alarm.getType().toString())
                .message("[알림] ["
                        + post.getTitle()
                        + "] 소설에 작성한 문단에 좋아요가 달렸습니다.! 33bb")
                .alarmTargetId(ParagraphOwner.getId().toString())
                .isRead(alarm.getIsRead())
                .postId(alarm.getPostId().toString())
                .build();
        /*-
         * redis로 알림메시지 pub, alarmRepository에 저장
         * 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외
         */
        redisTemplate.convertAndSend(channelTopic.getTopic(),
                alarmResponseDto);

    }

    // 4. 내가 참여한 게시글이 좋아요를 받았을 때
    public void generatePostLikesAlarm(Post post) {
        log.info("---------------------- 444444bbbb ----------------------");
        List<Paragraph> paragraphList = paragraphRepository.findAllByPostId(post.getId());
        List<Long> userIdList = new ArrayList<>();

        for(Paragraph paragraph : paragraphList){
            if( !userIdList.contains(paragraph.getUser().getId()) ) {
                userIdList.add(paragraph.getUser().getId());
            }
        }

        // 게시글에 참여한 모든 유저에게 알림을 보낸다
        log.info("------------------- userIdList의 SIZE : " + userIdList.size());
        for(Long userid : userIdList){
            log.info("------------------- 444userid : " + userid);
            Alarm alarm = Alarm.builder()
                    .userId(userid)
                    .type(AlarmType.LIKEPOST)
                    .postId(post.getId())
                    .isRead(false)
                    .alarmMessage("[알림] ["
                            + post.getTitle()
                            + "]에 좋아요가 등록되었습니다. 44aa")
                    .build();

            log.info("--------------- alarmRepository.save(alarm); 직전");
            // 조건문 없으니 밑에서 alarm.getId()를 찾기위해선 여기서 먼저 저장해야한다.
            alarmRepository.save(alarm);

            /* 알림 메시지를 보낼 DTO 생성 */
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId().toString())
                    .type(alarm.getType().toString())
                    .message("[알림] ["
                            + post.getTitle()
                            + "]에 좋아요가 등록되었습니다! 44bb")
                    .alarmTargetId(userid.toString())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId().toString())
                    .build();
            /*-
             * redis로 알림메시지 pub, alarmRepository에 저장
             * 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외
             */
            redisTemplate.convertAndSend(channelTopic.getTopic(),
                    alarmResponseDto);
        }

    }

    /* 알림 읽었을 경우 체크 */
    @Transactional
    public AlarmResponseDto alarmReadCheck(Long alarmId,
                                           UserDetailsImpl userDetails) {
        Alarm alarm = alarmRepository.getById(alarmId);
        User user = userDetails.getUser();
        alarm.setIsRead(true);
        alarmRepository.save(alarm);
        AlarmResponseDto alarmDto = new AlarmResponseDto();


        /*UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();

        userInfoResponseDto = UserInfoResponseDto.builder()
                .userKey(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickName())
                .isAlaramRead(true)
                .userProfileImage(user.getUserProfileImage())
                .introduction(user.getIntroduction())
                .build();*/

        user.updateUserAlaram(true);

            /* 내가 참여한 게시글에 새로운 문단이 달렸을 때 */
        if (alarm.getType().equals(AlarmType.NEWPARAGRAPH)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId().toString())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId().toString())
                    .build();
        } /* 내가 참여한 게시글이 완성 됐을 때 */
        else if(alarm.getType().equals(AlarmType.COMPLETEPOST)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId().toString())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId().toString())
                    .build();
        } /* 내가 작성한 문단이 좋아요를 받았을 때 */
        else if(alarm.getType().equals(AlarmType.LIKEPARAGRAPH)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId().toString())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId().toString())
                    .build();
        } /* 내가 참여한 게시글이 좋아요를 받았을 때 */
        else if(alarm.getType().equals(AlarmType.LIKEPOST)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId().toString())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId().toString())
                    .build();
        }

        return alarmDto;
    }
}
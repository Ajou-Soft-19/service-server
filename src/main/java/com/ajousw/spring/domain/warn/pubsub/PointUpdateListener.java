package com.ajousw.spring.domain.warn.pubsub;

import com.ajousw.spring.domain.navigation.EmergencyTrackService;
import com.ajousw.spring.web.controller.dto.navigation.CurrentPointUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PointUpdateListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    private EmergencyTrackService emergencyTrackService = null;

    public PointUpdateListener(ObjectMapper objectMapper, ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CurrentPointUpdateDto updateDto = objectMapper.readValue(message.getBody(), CurrentPointUpdateDto.class);
            getEmergencyTrackService().updateCurrentPathPoint(
                    updateDto.getNaviPathId(),
                    updateDto.getEmergencyEventId(),
                    updateDto.getCurrentPathPoint(),
                    updateDto.getCurrentLongitude(),
                    updateDto.getCurrentLatitude()
            );
        } catch (IOException e) {
            log.error("error while listening broadcast message", e);
        }
    }

    // 순환 참조로 applicationContext에서 꺼내서 사용
    private EmergencyTrackService getEmergencyTrackService() {
        if (emergencyTrackService == null) {
            emergencyTrackService = applicationContext.getBean(EmergencyTrackService.class);
        }

        return emergencyTrackService;
    }

}

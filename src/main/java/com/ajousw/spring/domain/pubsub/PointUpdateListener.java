package com.ajousw.spring.domain.pubsub;

import com.ajousw.spring.domain.navigation.EmergencyService;
import com.ajousw.spring.web.controller.dto.navigation.CurrentPointUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointUpdateListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CurrentPointUpdateDto updateDto = objectMapper.readValue(message.getBody(), CurrentPointUpdateDto.class);
            getEmergencyService().updateCurrentPathPoint(updateDto.getEmail(), updateDto.getNaviPathId(),
                    updateDto.getCurrentPoint());
        } catch (IOException e) {
            log.error("error while listening broadcast message");
            throw new IllegalStateException(e);
        }
    }

    // 순환참조로
    private EmergencyService getEmergencyService() {
        return applicationContext.getBean(EmergencyService.class);
    }

}

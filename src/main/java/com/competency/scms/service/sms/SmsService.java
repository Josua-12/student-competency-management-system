package com.competency.scms.service.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    /**
     * SMS 발송
     * TODO: 실제 SMS API 연동 필요 (네이버 SENS, CoolSMS 등)
     */
    public void sendSms(String phoneNumber, String message) {
        // 개발 단계에서는 로그로만 출력
        log.info("=== SMS 발송 ===");
        log.info("수신번호: {}", phoneNumber);
        log.info("메시지: {}", message);
        log.info("===============");

        // TODO: 실제 SMS API 호출
        // 예시:
        // smsApiClient.send(phoneNumber, message);
    }
}

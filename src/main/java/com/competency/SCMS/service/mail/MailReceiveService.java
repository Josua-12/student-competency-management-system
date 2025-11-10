package com.competency.SCMS.service.mail;

import com.competency.SCMS.domain.user.PhoneVerification;
import com.competency.SCMS.repository.verification.PhoneVerificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
@Transactional
public class MailReceiveService {

    private final PhoneVerificationRepo phoneVerificationRepo;

    private volatile boolean isPollingActive = false;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    @Value("${mail.receive.email}")
    private String receiverEmail;

    @Value("${mail.receive.password}")
    private String emailPassword;

    @Value("${mail.receive.imap-host}")
    private String imapHost;

    @Value("${mail.receive.imap-port}")
    private String imapPort;

    /**
     * 인증 시작 시 폴링 활성화
     */
    public void startPolling(String phone) {
        if (!isPollingActive) {
            isPollingActive = true;
            log.info("이메일 폴링 시작: {}", phone);

            // 5분 후 자동 중단
            scheduler.schedule(() -> {
                isPollingActive = false;
                log.info("이메일 폴링 자동 중단");
            }, 5, TimeUnit.MINUTES);
        }
    }

    /**
     * 10초마다 수신한 이메일을 확인 (폴링 활성화 시에만)
     */
    @Scheduled(fixedDelay = 10000)
    public void checkIncomingMails() {
        if (!isPollingActive) {
            return;
        }

        try {
            log.debug("이메일 확인 시작...");

            // IMAP 연결
            Session session = createSession();
            Store store = session.getStore("imaps");
            store.connect(imapHost, Integer.parseInt(imapPort), receiverEmail, emailPassword);

            // INBOX 폴더 오픈
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // 최근 이메일부터 확인 (최대 100개)
            int totalMessages = inbox.getMessageCount();
            int startIndex = Math.max(1, totalMessages - 100);

            Message[] messages = inbox.getMessages(startIndex, totalMessages);

            log.debug("총 {}개의 메일 확인 중...", messages.length);

            for (Message message : messages) {
                processMessage(message);
            }

            // 정리
            inbox.close(false);
            store.close();

            log.debug("이메일 확인 완료");

        } catch (Exception e) {
            log.error("메일 확인 중 오류 발생", e);
        }
    }

    /**
     * 개별 메시지 처리
     */
    private void processMessage(Message message) throws MessagingException, IOException {
        try {
            // 이메일에서 휴대폰 번호 추출
            String senderPhone = getSenderPhone(message);
            if (senderPhone == null) {
                log.debug("발신자 휴대폰 번호를 추출할 수 없습니다.");
                return;
            }

            // 메시지 본문 추출
            String body = getMessageBody(message);
            if (body == null) {
                log.debug("메시지 본문을 추출할 수 없습니다.");
                return;
            }

            // 본문에서 6자리 인증 번호 추출
            String verificationCode = extractVerificationCode(body);
            if (verificationCode == null) {
                log.debug("인증 번호를 추출할 수 없습니다.");
                return;
            }

            log.info("감지: phone={}, code={}", senderPhone, verificationCode);

            // DB에서 대응하는 인증 요청 조회
            PhoneVerification verification = phoneVerificationRepo
                    .findByPhoneAndCodeAndNotExpired(
                            senderPhone,
                            verificationCode,
                            LocalDateTime.now()
                    )
                    .orElse(null);

            if (verification != null && !verification.getIsVerified()) {
                // 인증 완료 처리
                verification.setIsVerified(true);
                verification.setVerifiedAt(LocalDateTime.now());
                phoneVerificationRepo.save(verification);

                log.info("✓ 휴대폰 인증 완료: phone={}, code={}, userId={}",
                        senderPhone, verificationCode, verification.getUser().getId());
            } else if (verification == null) {
                log.warn("⚠ 해당하는 인증 요청이 없습니다: phone={}, code={}", senderPhone, verificationCode);
            }

        } catch (Exception e) {
            log.error("메시지 처리 중 오류", e);
        }
    }

    /**
     * 메시지 발신자(휴대폰 번호) 추출
     * From: 01012345678@gmail.com → 01012345678 추출
     */
    private String getSenderPhone(Message message) throws MessagingException {
        try {
            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                String fromAddress = ((InternetAddress) fromAddresses[0]).getAddress();
                log.debug("From 주소: {}", fromAddress);

                // @ 기준으로 앞부분 추출
                String phoneNumber = fromAddress.split("@")[0];

                // 휴대폰 번호 형식 검증 (01로 시작, 10~11자리 숫자)
                if (phoneNumber.matches("^01[0-9]{8,9}$")) {
                    log.debug("휴대폰 번호 추출 성공: {}", phoneNumber);
                    return phoneNumber;
                } else {
                    log.warn("유효하지 않은 휴대폰 번호 형식: {}", phoneNumber);
                }
            }
        } catch (Exception e) {
            log.debug("발신자 추출 실패", e);
        }
        return null;
    }

    /**
     * 메시지 본문 추출
     */
    private String getMessageBody(Message message) throws MessagingException, IOException {
        try {
            Object content = message.getContent();

            // 단순 텍스트 메시지
            if (content instanceof String) {
                String body = (String) content;
                log.debug("메시지 본문: {}", body);
                return body;
            }
            // Multipart 메시지
            else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);

                    if (part.isMimeType("text/plain")) {
                        String body = (String) part.getContent();
                        log.debug("메시지 본문 (Multipart): {}", body);
                        return body;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("본문 추출 실패", e);
        }
        return null;
    }

    /**
     * 본문에서 6자리 인증 번호 추출
     * 예: "홍길동 인증번호: 123456" → 123456
     */
    private String extractVerificationCode(String body) {
        try {
            // 6자리 연속된 숫자를 찾음
            Pattern pattern = Pattern.compile("\\b(\\d{6})\\b");
            Matcher matcher = pattern.matcher(body);

            if (matcher.find()) {
                String code = matcher.group(1);
                log.debug("인증 번호 추출 성공: {}", code);
                return code;
            } else {
                log.warn("본문에서 6자리 번호를 찾을 수 없습니다: {}", body);
            }
        } catch (Exception e) {
            log.error("인증 번호 추출 중 오류", e);
        }
        return null;
    }

    /**
     * IMAP Session 생성
     */
    private Session createSession() {
        Properties props = new Properties();

        // IMAP 서버 설정
        props.put("mail.imap.host", imapHost);
        props.put("mail.imap.port", imapPort);

        // SSL 설정
        props.put("mail.imap.socketFactory.port", imapPort);
        props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imap.socketFactory.fallback", "false");

        // 인증 설정
        props.put("mail.imap.auth.mechanisms", "LOGIN");
        props.put("mail.imap.auth", "true");

        // 타임아웃 설정
        props.put("mail.imap.connectiontimeout", "5000");
        props.put("mail.imap.timeout", "5000");

        // 디버그 모드
        props.put("mail.debug", "false");

        return Session.getInstance(props);
    }
}

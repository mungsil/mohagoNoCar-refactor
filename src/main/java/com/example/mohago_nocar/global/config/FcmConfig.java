package com.example.mohago_nocar.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// todo Prod: 환경변수 GOOGLE_APPLICATION_CREDENTIALS 사용 --> 등록 필요
@Component
@Slf4j
@RequiredArgsConstructor
public class FcmConfig implements ApplicationRunner {

    @Value("${google.firebase.key.path}")
    private String firebaseKeyPath;

    private final Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());

        if (profiles.contains("test")) {
            log.warn("테스트 환경에서 FCM 테스트를 지원하지 않습니다.");
            return;
        }

        GoogleCredentials credentials = profiles.contains("dev")
                ? loadFromLocalFile()
                : loadFromApplicationDefault();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
    }

    private GoogleCredentials loadFromApplicationDefault() {
        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            log.error("GoogleCredentials 획득 중 문제가 발생했습니다.");
            log.error("에러: {}", e.getMessage());
            log.error("프로파일: {}", (Object) env.getActiveProfiles());
            throw new RuntimeException(e);
        }
    }

    private GoogleCredentials loadFromLocalFile() {
        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);
            return GoogleCredentials.fromStream(serviceAccount);
        } catch (IOException e) {
            log.error("GoogleCredentials 획득 중 문제가 발생했습니다.");
            log.error("에러: {}", e.getMessage());
            log.error("프로파일: {}", (Object) env.getActiveProfiles());
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

}

package com.example.mohago_nocar.global.notification.infrastructure.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.concurrent.Callable;

// todo Prod: 환경변수 GOOGLE_APPLICATION_CREDENTIALS 사용 --> 등록 필요
@Component
@Slf4j
@RequiredArgsConstructor
public class FcmConfig {

    @Value("${google.firebase.key.path}")
    private String firebaseKeyPath;

    private final Environment env;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        GoogleCredentials credentials = loadFromLocalFile();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setConnectTimeout(10_000)
                .setReadTimeout(30_000)
                .build();

        FirebaseApp.initializeApp(options);

        return FirebaseMessaging.getInstance();
    }

    private GoogleCredentials loadFromLocalFile() {
        return getCredentials(() -> {
            FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);
            return GoogleCredentials.fromStream(serviceAccount);
        });
    }

    private GoogleCredentials getCredentials(
            Callable<GoogleCredentials> callable
    ) {
        try {
            return callable.call();
        } catch (Exception e) {
            log.error("GoogleCredentials 획득 중 문제가 발생했습니다.");
            log.error("에러: {}", e.getMessage());
            log.error("프로파일: {}", (Object) env.getActiveProfiles());
            throw new RuntimeException(e);
        }
    }

}

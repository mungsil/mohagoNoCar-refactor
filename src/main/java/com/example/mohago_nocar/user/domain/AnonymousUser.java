package com.example.mohago_nocar.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnonymousUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fcmToken;

    public static AnonymousUser create(String fcmToken) {
       return AnonymousUser.builder()
               .fcmToken(fcmToken).build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private AnonymousUser(String uuid, String fcmToken) {
        this.fcmToken = fcmToken;
    }

}



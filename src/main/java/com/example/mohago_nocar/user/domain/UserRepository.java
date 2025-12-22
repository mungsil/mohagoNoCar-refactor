package com.example.mohago_nocar.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    AnonymousUser save(AnonymousUser identifier);

    Optional<AnonymousUser> findById(UUID userId);

    Optional<AnonymousUser> findByFcm(String fcmToken);
}

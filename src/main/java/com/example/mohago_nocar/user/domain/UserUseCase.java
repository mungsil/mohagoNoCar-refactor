package com.example.mohago_nocar.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

    AnonymousUser save(AnonymousUser user);

    String getFcmToken(UUID userId);

    AnonymousUser findByIdOrThrow(UUID userId);

    Optional<AnonymousUser> findById(UUID userId);

    AnonymousUser getOrCreate(String fcmToken);
}

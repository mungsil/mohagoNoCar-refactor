package com.example.mohago_nocar.user.domain;

import java.util.UUID;

public interface UserUseCase {

    AnonymousUser save(AnonymousUser user);

    AnonymousUser findById(UUID userId);

}

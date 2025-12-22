package com.example.mohago_nocar.user.application;

import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserRepository;
import com.example.mohago_nocar.user.domain.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    public AnonymousUser save(AnonymousUser user) {
        return userRepository.save(user);
    }

    @Override
    public String getFcmToken(UUID userId) {
        AnonymousUser user = findByIdOrThrow(userId);
        return user.getFcmToken();
    }

    @Override
    public AnonymousUser findByIdOrThrow(UUID userId) {
        return findById(userId).orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));
    }

    @Override
    public Optional<AnonymousUser> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public AnonymousUser getOrCreate(String fcmToken) {
        return userRepository.findByFcm(fcmToken)
                .orElseGet(()-> {
                    AnonymousUser created = AnonymousUser.create(fcmToken);
                    return save(created);
                });
    }

}

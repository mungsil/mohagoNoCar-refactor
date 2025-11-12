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
    public AnonymousUser findById(UUID userId) {
        Optional<AnonymousUser> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));
    }

}

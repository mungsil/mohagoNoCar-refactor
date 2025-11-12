package com.example.mohago_nocar.user.infrastructure;

import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final AnonymousUserJpaRepository anonymousUserJpaRepository;

    @Override
    public AnonymousUser save(AnonymousUser user) {
        return anonymousUserJpaRepository.save(user);
    }

    @Override
    public Optional<AnonymousUser> findById(UUID userId) {
        return anonymousUserJpaRepository.findById(userId);
    }

}

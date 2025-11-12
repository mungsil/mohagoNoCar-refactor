package com.example.mohago_nocar.user.infrastructure;

import com.example.mohago_nocar.user.domain.AnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnonymousUserJpaRepository extends JpaRepository<AnonymousUser, UUID> {
}

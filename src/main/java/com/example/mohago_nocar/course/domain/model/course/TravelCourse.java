package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TravelCourse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private UUID anonymousUserId;

    public static TravelCourse from() {
        return new TravelCourse();
    }

}

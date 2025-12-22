package com.example.mohago_nocar.festival.domain.model;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@Table(name = "festival_image")
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class FestivalImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private Long festivalId;

    @NotNull
    private String imageUrl;

    public static FestivalImage from(Long festivalId, String imageUrl) {
        return FestivalImage.builder()
                .festivalId(festivalId)
                .imageUrl(imageUrl)
                .build();
    }

    @Builder
    private FestivalImage(Long festivalId, String imageUrl) {
        this.festivalId = festivalId;
        this.imageUrl = imageUrl;
    }
}

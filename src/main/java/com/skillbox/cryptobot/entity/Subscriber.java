package com.skillbox.cryptobot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "subscribers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subscriber {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "user_telegram_id", nullable = false)
    private Long userTelegramId;

    @Column(name = "price")
    private Double price;
}

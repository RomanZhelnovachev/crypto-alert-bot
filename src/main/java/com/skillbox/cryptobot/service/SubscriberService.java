package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

    private final SubscriberRepository repository;

    @Transactional
    public void createUser(Long userTelegramId) {
        Optional<Subscriber> subscriber = repository.findByUserTelegramId(userTelegramId);
        if (subscriber.isEmpty()) {
            Subscriber newSubscriber = new Subscriber(UUID.randomUUID(),
                    userTelegramId,
                    null);
            log.info("User with ID {} successfully created",
                    userTelegramId);
            repository.save(newSubscriber);
        }
    }

    @Transactional
    public void updateSubscribe(Long userTelegramId,
                                Double price) {
        Subscriber subscriber = repository.findByUserTelegramId(userTelegramId)
                .orElseGet(() -> {
                    Subscriber newSubscriber = new Subscriber(UUID.randomUUID(),
                            userTelegramId,
                            price);
                    repository.save(newSubscriber);
                    return newSubscriber;
                });
        subscriber.setPrice(price);
        log.info("Subscribe from user by ID {} successfully updated",
                userTelegramId);
        repository.save(subscriber);
    }

    @Transactional(readOnly = true)
    public Double findSubscribeByUserId(Long userTelegramId){
        return repository.findByUserTelegramId(userTelegramId)
                .map(Subscriber::getPrice)
                .orElse(null);
    }

    @Transactional
    public void deleteSubscribe(Long userTelegramId){
        repository.findByUserTelegramId(userTelegramId)
                .ifPresent(subscriber -> subscriber.setPrice(null));
    }
}

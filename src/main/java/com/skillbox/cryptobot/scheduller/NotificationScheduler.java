package com.skillbox.cryptobot.scheduller;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final SubscriberRepository repository;
    private final CryptoCurrencyService service;
    private final CryptoBot bot;
    private final Map<Long, Instant> lastNotificationTime = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.MINUTES)
    public void checkAndNotificationSubscriber()
            throws
            IOException {
        double currentPrice = service.getBitcoinPrice();
        List<Subscriber> subscribers = repository.findAllByPriceIsNotNull();
        for(Subscriber subscriber:subscribers){
            if(currentPrice <= subscriber.getPrice()){
                Long userId = subscriber.getUserTelegramId();
                Instant now = Instant.now();
                Instant lastNotification = lastNotificationTime.get(userId);
                if(lastNotification == null || Duration.between(lastNotification, now).toMinutes() >= 10){
                    String text = "Пора покупать, стоимость биткоина " +
                            TextUtil.toString(currentPrice) + " USD";
                    try {
                        log.info("Notification sent to user {}", userId);
                        bot.execute(new SendMessage(userId.toString(), text));
                        lastNotificationTime.put(userId, now);
                    } catch (TelegramApiException e) {
                        log.error("Error occurred in send notification", e);
                    }
                }
            }
        }
    }

}

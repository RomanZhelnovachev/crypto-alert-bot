package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.service.SubscriberService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final CryptoCurrencyService cryptoService;
    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender,
                               Message message,
                               String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        Long userTelegramId = message.getFrom()
                .getId();
        if (arguments.length != 1) {
            answer.setText("Укажите ровно одну цену");
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки сообщения",
                        e);
            }
            return;
        }
        String priceStr = arguments[0];
        if (!priceStr.matches("\\d+(\\.\\d{1,8})?")) {
            answer.setText("Цена должна быть числом (до 8 знаков после точки)");
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки сообщения",
                        e);
            }
            return;
        }
        double currentPrice;
        try {
            currentPrice = cryptoService.getBitcoinPrice();
        } catch (IOException e) {
            log.error("Ошибка при получении цены биткоина",
                    e);
            try {
                absSender.execute(answer);
            } catch (TelegramApiException ex) {
                log.error("Ошибка отправки сообщения",
                        ex);
            }
            return;
        }
        Double price = Double.parseDouble(priceStr);
        subscriberService.updateSubscribe(userTelegramId,
                price);
        String text;
        if (currentPrice <= price) {
            text = "Текущая цена биткоина: " + TextUtil.toString(currentPrice) + " USD\n" +
                    "Новая подписка создана на стоимость: " + priceStr + " USD\n" +
                    "Цена уже достигла или ниже указанного уровня — уведомление может прийти в ближайшие минуты.";
        } else {
            text = "Текущая цена биткоина: " + TextUtil.toString(currentPrice) + " USD\n" +
                    "Новая подписка создана на стоимость: " + priceStr + " USD";
        }
        answer.setText(text);
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /subscribe command",
                    e);
        }
    }
}
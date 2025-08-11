package com.reminderapp.reminder.config;

import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
//@ConditionalOnProperty(name = "app.notification.telegram.enabled", havingValue = "true")
@Profile("!test")
public class TelegramBotConfig {

    @SneakyThrows
    @Bean
    public TelegramBotsApi telegramBotsApi(LongPollingBot telegramBot) {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);
        return api;
    }
}

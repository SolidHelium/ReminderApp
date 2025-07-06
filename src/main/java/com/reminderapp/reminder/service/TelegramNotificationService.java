package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class TelegramNotificationService extends TelegramLongPollingBot implements NotificationService {
    private boolean isEnabled;
    private String botUserName;

    public TelegramNotificationService(
            @Value("${app.notification.telegram.bot-token}")
            String botToken,
            @Value("${app.notification.telegram.bot-username}")
            String botUserName,
            @Value("${app.notification.telegram.enabled}")
            boolean isEnabled
    ) {
        super(botToken);
        this.botUserName = botUserName;
        this.isEnabled = isEnabled;
    }

    @Override
    public void sendNotification(Reminder reminder) {
        log.info("Trying to send Telegram notification");
        if (!isEnabled) {
            log.info("Telegram notification is disabled");
            return;
        }
        String telegramId = reminder.getUser().getTelegram();
        if (telegramId == null || telegramId.trim().isEmpty()) {
            log.info("User did not enter their telegram");
            return;
        }
        try {
            SendMessage message = new SendMessage();
            message.setChatId(telegramId);
            message.setText(constructMessage(reminder));
            execute(message);
            log.info("Telegram notification sent");
        } catch (TelegramApiException e) {
            throw new RuntimeException("Could not send a message " + e);
        }

    }

    private String constructMessage(Reminder reminder) {
        String message = "";
        if (reminder.getTitle() != null || !reminder.getTitle().isEmpty()) {
            message += reminder.getTitle();
        }
        if (reminder.getDescription() != null || !reminder.getDescription().isEmpty()) {
            message += "\n" + reminder.getDescription();
        }
        return message;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatID = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatID);

        if (update.getMessage().getText().equals("/start")) {
            message.setText("Welcome to ReminderApp telegram bot \n Your chat ID is "
                    + chatID
                    + "\n Paste it in the app to receive notifications from this bot");
        } else {
            message.setText("This bot is used only for receiving notifications. You can not use commands here");
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Could not send a message " + e);
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}

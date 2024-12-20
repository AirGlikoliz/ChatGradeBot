package ru.loylabs.chatgradebot.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.loylabs.chatgradebot.config.BotConfig;
import ru.loylabs.chatgradebot.service.MessageService;

@Component
@RequiredArgsConstructor
public class Receiver extends TelegramLongPollingBot {

    private final MessageService messageService;
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        var response = messageService.mapperResponse(update);
        sendResponse(response);
    }

    private void sendResponse(BotApiMethodMessage method) {
        try {
            if (method != null) execute(method);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}

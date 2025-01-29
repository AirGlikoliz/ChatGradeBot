package ru.loylabs.chatgradebot.message;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.loylabs.chatgradebot.config.BotConfig;
import ru.loylabs.chatgradebot.service.MessageService;

import java.util.Random;

import static ru.loylabs.chatgradebot.consts.Stringi.PARNI;

@Component
@RequiredArgsConstructor
public class Receiver extends TelegramLongPollingBot {

    private final MessageService messageService;
    private final BotConfig botConfig;
    private long chatId;


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
        chatId = update.getMessage().getChatId();
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

    @Scheduled(cron = "0 0 10 * * ?")
    private void gayOfDay() {
        int randomIndex = new Random().nextInt(PARNI.size());
        String pp = PARNI.get(randomIndex) + " пидор дня";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(pp);
        sendResponse(message);
    }


}

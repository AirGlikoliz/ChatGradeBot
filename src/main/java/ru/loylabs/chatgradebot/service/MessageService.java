package ru.loylabs.chatgradebot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.loylabs.chatgradebot.config.BotConfig;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.loylabs.chatgradebot.consts.Stringi.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final BotConfig botConfig;
    private int countRetry = 0;
    private int countRetrySviat = 0;
    private long startTime;

    public BotApiMethodMessage mapperResponse(Update update) {
        if (!update.hasMessage()) {
            log.info("Prishlo ne soobshenie");
            return null;
        }
        long messageTime = update.getMessage().getDate();
        long chatId = update.getMessage().getChatId();

        if (messageTime < startTime) {
            return null;
        }

        if(update.getMessage().hasPhoto() && update.getMessage().getFrom().getId().equals(Long.parseLong(botConfig.getLovelyUserSecond()))){
            return sendMessage(chatId, "милый");
        }

        if (update.getMessage().hasText() && !update.getMessage().hasVideo()) {
            String messageText = update.getMessage().getText();

            if (containsLink(messageText)) {
                return sendPoll(chatId, false);
            }

            if (messageText.equalsIgnoreCase("Саня")) {
                int randomIndex = new Random().nextInt(SANYA_IS.size());
                String sanus = SANYA_IS.get(randomIndex);
                return sendMessage(chatId, sanus);
            }

            if(messageText.equalsIgnoreCase("Свят")){
                int randomIndex = new Random().nextInt(SVIAT_IS.size());
                String sviat = SVIAT_IS.get(randomIndex);
                return sendMessage(chatId, sviat);
            }

            if (messageText.equalsIgnoreCase("Опрос")) {
                return sendPoll(chatId, false);
            }

            if (update.getMessage().getFrom().getId().equals(Long.parseLong(botConfig.getLovelyUserSecond()))) {
                var answer = sviatAnswer(chatId, messageText, update);
                if (answer != null) return answer;
            }

            if (messageText.toLowerCase().contains("никит")) {
                return sendMessage(chatId, "Легенда упомянут");
            }

            if (checkQuestion(messageText)) {
                return sendMessage(chatId, "А тебя это ебать не должно");
            }

            if (update.getMessage().getFrom().getId().equals(Long.parseLong(botConfig.getLovelyUser()))) {
                if (countRetry == 5) {
                    countRetry = 0;
                    return sendMessage(chatId, "Бля Никита заебал флудить");
                }
                countRetry++;
            }

        } else if (update.getMessage().hasVideo()) {
            if (update.getMessage().getFrom().getId().equals(Long.parseLong(botConfig.getLovelyUserSecond()))) {
                return sendPoll(chatId, true);
            } else {
                return sendPoll(chatId, false);
            }
        }
        return null;
    }

    private BotApiMethodMessage sviatAnswer(long chatId, String message, Update update) {

        if(message.equalsIgnoreCase("нет")){
            return sendMessage(chatId, "мама не разрешает");
        }

        if(message.equals("Политех") || message.equalsIgnoreCase("гуап")){
            return sendMessage(chatId, "Политех forever (ГУАП сосать)");
        }

        if(message.toLowerCase().contains("политех")){
            return sendMessage(chatId, "Бабкин, хочу в основу");
        }

        if (countRetrySviat == 4) {
            countRetrySviat++;
            return sendMessage(chatId, "похуй");
        }

        if (countRetrySviat == 5) {
            countRetrySviat++;
            return sendMessage(chatId, "ваще похую");
        }

        if (countRetrySviat == 6) {
            countRetrySviat++;
            return sendMessage(chatId, "да всем похуй");
        }

        if (countRetrySviat == 7) {
            countRetrySviat = 0;
            return sendMessage(chatId, "БОЖЕ, ДА ЗАВАЛИ ЕБАЛО, НИКОМУ НЕ ИНТЕРЕСНО, ХВАТИТ ФЛУДИТЬ, ЛЮБЛЮ ТЕБЯ");
        }

        countRetrySviat++;

        return null;
    }

    private boolean containsLink(String text) {
        String urlPattern = "https?://[\\w-]+(\\.[\\w-]+)+(/[\\S]*)?";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public SendMessage sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        return message;
    }

    public SendPoll sendPoll(long chatId, boolean isSviat) {
        SendPoll poll = new SendPoll();
        poll.setChatId(String.valueOf(chatId));
        String dopString = isSviat ? "милый" : "";
        poll.setQuestion( dopString + "\nСколько Мелиханов?");
        poll.setOptions(List.of(
                "0 - заезженный или не смешной мем",
                "1-3 - может вызвать улыбку",
                "4-6 - плотный середнячок",
                "7-8 - шакалий хихик",
                "9-10 - ебать какой крик чайки"
        ));
        poll.setIsAnonymous(false);
        return poll;
    }

    private boolean checkQuestion(String text) {
        return QUESTIONS.stream().anyMatch(s -> text.toLowerCase().startsWith(s.toLowerCase())
                || text.toLowerCase().endsWith(s.toLowerCase() + "?")
                || text.toLowerCase().endsWith(s.toLowerCase()));
    }

    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    private void updateCount() {
        countRetry = 0;
        countRetrySviat = 0;
    }

    @PostConstruct
    public void startTime() {
        this.startTime = Instant.now().getEpochSecond();
    }
}

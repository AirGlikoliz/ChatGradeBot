package ru.loylabs.chatgradebot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;
    @Value("${user.userid}")
    String lovelyUser;
    @Value("${user.userid.second}")
    String lovelyUserSecond;

}

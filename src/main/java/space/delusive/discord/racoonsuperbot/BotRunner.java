package space.delusive.discord.racoonsuperbot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotRunner implements Runnable {

    @Override
    @Scheduled(fixedDelay = 5000L)
    public void run() {

    }
}

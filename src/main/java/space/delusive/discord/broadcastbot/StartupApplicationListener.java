package space.delusive.discord.broadcastbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private boolean wasInitialized = false;
    private final String botPrefix;
    private final String emojiSuccess;
    private final String emojiWarning;
    private final String emojiError;
    private final String ownerId;
    private final JDA jda;
    private final List<Command> commands;
    private final EventWaiter eventWaiter;

    public StartupApplicationListener(@Value("${discord.bot.prefix}") String botPrefix,
                                      @Value("${discord.bot.emoji.success}") String emojiSuccess,
                                      @Value("${discord.bot.emoji.warning}") String emojiWarning,
                                      @Value("${discord.bot.emoji.error}") String emojiError,
                                      @Value("${discord.bot.owner.id}") String ownerId,
                                      JDA jda,
                                      List<Command> commands,
                                      EventWaiter eventWaiter) {
        this.botPrefix = botPrefix;
        this.emojiSuccess = emojiSuccess;
        this.emojiWarning = emojiWarning;
        this.emojiError = emojiError;
        this.ownerId = ownerId;
        this.jda = jda;
        this.commands = commands;
        this.eventWaiter = eventWaiter;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (wasInitialized) {
            return;
        }
        CommandClientBuilder commandClientBuilder = new CommandClientBuilder()
                .setPrefix(botPrefix)
                .setOwnerId(ownerId)
                .setEmojis(emojiSuccess, emojiWarning, emojiError)
                .addCommands(commands.toArray(new Command[0]));
        jda.addEventListener(eventWaiter, commandClientBuilder.build());
        wasInitialized = true;
    }
}

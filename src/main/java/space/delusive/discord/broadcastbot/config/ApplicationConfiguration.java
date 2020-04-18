package space.delusive.discord.broadcastbot.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.discord.impl.DiscordManagerImpl;
import space.delusive.discord.broadcastbot.integration.MixerIntegration;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.integration.YoutubeIntegration;
import space.delusive.discord.broadcastbot.integration.impl.MixerIntegrationImpl;
import space.delusive.discord.broadcastbot.integration.impl.TwitchIntegrationImpl;
import space.delusive.discord.broadcastbot.integration.impl.YoutubeIntegrationImpl;

import java.util.List;

@Configuration
@ComponentScan
@EnableScheduling
public class ApplicationConfiguration {

    @Bean
    DiscordManager getDiscordManager(
            @Value("${discord.message.pattern.youtube.video}") String youtubeVideoMessagePattern,
            @Value("${discord.message.pattern.twitch.stream}") String twitchStreamMessagePattern,
            @Value("${discord.message.pattern.mixer.stream}") String mixerStreamMessagePattern,
            @Value("${discord.channel.id}") String messageChannelId,
            @Autowired JDA jda) {
        return new DiscordManagerImpl(youtubeVideoMessagePattern, twitchStreamMessagePattern, mixerStreamMessagePattern,
                messageChannelId, jda);
    }

    @Bean
    YoutubeIntegration getYoutubeVideoRepository(
            @Value("${youtube.api.token}") String apiToken,
            @Value("${youtube.api.search.url}") String searchUrl,
            @Value("${youtube.api.videos.from.playlist.url}") String videosFromPlaylistUrl) {
        return new YoutubeIntegrationImpl(searchUrl, videosFromPlaylistUrl, apiToken);
    }

    @Bean
    TwitchIntegration getTwitchStreamRepository(
            @Value("${twitch.api.client.id}") String clientId,
            @Value("${twitch.api.streams.url}") String url) {
        return new TwitchIntegrationImpl(url, clientId);
    }

    @Bean
    MixerIntegration getMixerStreamRepository(@Value("${mixer.api.last.stream.url}") String lastStreamUrl) {
        return new MixerIntegrationImpl(lastStreamUrl);
    }

    @Bean
    @SneakyThrows
    JDA getJda(@Value("${discord.bot.token}") String botToken,
               @Value("${discord.bot.status}") String status,
               @Value("${discord.bot.prefix}") String botPrefix,
               @Value("${discord.bot.emoji.success}") String emojiSuccess,
               @Value("${discord.bot.emoji.warning}") String emojiWarning,
               @Value("${discord.bot.emoji.error}") String emojiError,
               @Value("${discord.bot.owner.id}") String ownerId,
               List<Command> commandList) {
        CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
        EventWaiter eventWaiter = new EventWaiter();
        commandClientBuilder.useDefaultGame()
                .setPrefix(botPrefix)
                .setOwnerId(ownerId)
                .setEmojis(emojiSuccess, emojiWarning, emojiError)
                .addCommands(commandList.toArray(new Command[0]));
        return new JDABuilder(botToken)
                .setActivity(Activity.playing(status))
                .addEventListeners(commandClientBuilder.build(), eventWaiter)
                .build();
    }

}

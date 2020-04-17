package space.delusive.discord.broadcastbot.discord.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.domain.*;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class DiscordManagerImpl implements DiscordManager {
    private static final String youtubeVideoBaseLink = "https://youtu.be/";
    private static final String twitchStreamBaseLink = "https://twitch.tv/";
    private static final String mixerStreamBaseLink = "https://mixer.com/";
    private final String youtubeVideoMessagePattern;
    private final String twitchStreamMessagePattern;
    private final String mixerStreamMessagePattern;
    private final String messageChannelId;
    private final JDA jda;

    @Override
    public void informAboutNewVideoOnYoutube(YoutubeChannel youtubeChannel, YoutubeVideo youtubeVideo) {
        val optionalTextChannel = getChannelIfGood();
        val optionalRoleAsMention = getRoleAsMentionIfGood(youtubeChannel.getMentionRoleId());
        if (optionalTextChannel.isEmpty() || optionalRoleAsMention.isEmpty()) return;
        val message = youtubeVideoMessagePattern
                .replaceAll("\\{mention\\}", optionalRoleAsMention.get())
                .replaceAll("\\{channelTitle\\}", youtubeChannel.getChannelName())
                .replaceAll("\\{videoLink\\}", youtubeVideoBaseLink + youtubeVideo.getYoutubeId());
        optionalTextChannel.get().sendMessage(message).queue();
    }

    @Override
    public void informAboutBeginningOfStreamOnTwitch(TwitchChannel twitchChannel, TwitchStream twitchStream) {
        val optionalTextChannel = getChannelIfGood();
        val optionalRoleAsMention = getRoleAsMentionIfGood(twitchChannel.getMentionRoleId());
        if (optionalTextChannel.isEmpty() || optionalRoleAsMention.isEmpty()) return;
        val message = twitchStreamMessagePattern
                .replaceAll("\\{mention\\}", optionalRoleAsMention.get())
                .replaceAll("\\{channelName\\}", twitchChannel.getChannelName())
                .replaceAll("\\{streamTitle\\}", twitchStream.getTitle())
                .replaceAll("\\{streamLink\\}", twitchStreamBaseLink + twitchChannel.getChannelName());
        optionalTextChannel.get().sendMessage(message).queue();
    }

    @Override
    public void informAboutBeginningOfStreamOnMixer(MixerChannel mixerChannel, MixerStream mixerStream) {
        val optionalTextChannel = getChannelIfGood();
        val optionalRoleAsMention = getRoleAsMentionIfGood(mixerChannel.getMentionRoleId());
        if (optionalTextChannel.isEmpty() || optionalRoleAsMention.isEmpty()) return;
        val message = mixerStreamMessagePattern
                .replaceAll("\\{mention\\}", optionalRoleAsMention.get())
                .replaceAll("\\{channelName\\}", mixerChannel.getChannelName())
                .replaceAll("\\{streamLink\\}", mixerStreamBaseLink + mixerChannel.getChannelName());
        optionalTextChannel.get().sendMessage(message).queue();
    }

    private Optional<TextChannel> getChannelIfGood() {
        TextChannel textChannel = jda.getTextChannelById(messageChannelId);
        if (textChannel == null || canNotTalkTo(textChannel)) {
            log.error("No text channel found or bot can't write to this channel. ID: " + messageChannelId);
            return Optional.empty();
        }
        return Optional.of(textChannel);
    }

    private Optional<String> getRoleAsMentionIfGood(String roleId) {
        if (roleId.equalsIgnoreCase("everyone")) return Optional.of("@everyone");
        if (roleId.equalsIgnoreCase("noone")) return Optional.of("");
        Role mentionRole = jda.getRoleById(roleId);
        if (mentionRole == null || isNotMentionable(mentionRole)) {
            log.error("No mention role found or that role is not mentionable. ID: " + roleId);
            return Optional.empty();
        }
        return Optional.of(mentionRole.getAsMention());
    }

    private boolean canNotTalkTo(TextChannel textChannel) {
        return !textChannel.canTalk();
    }

    private boolean isNotMentionable(Role role) {
        return !role.isMentionable();
    }
}

package space.delusive.discord.racoonsuperbot.discord.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import space.delusive.discord.racoonsuperbot.discord.DiscordManager;
import space.delusive.discord.racoonsuperbot.domain.YoutubeChannel;
import space.delusive.discord.racoonsuperbot.domain.YoutubeVideo;

@Log4j2
@RequiredArgsConstructor
public class DiscordManagerImpl implements DiscordManager {
    private static final String youtubeVideoBaseLink = "https://youtu.be/";
    private final String youtubeVideoMessagePattern;
    private final String messageChannelId;
    private final JDA jda;

    @Override
    public void informAboutNewVideoOnYoutube(YoutubeChannel youtubeChannel, YoutubeVideo youtubeVideo) {
        TextChannel textChannel = jda.getTextChannelById(messageChannelId);
        if (textChannel == null || canNotTalkTo(textChannel)) {
            log.error("No text channel found or bot can't write to this channel. ID: " + messageChannelId);
            return;
        }
        Role mentionRole = jda.getRoleById(youtubeChannel.getMentionRoleId());
        if (mentionRole == null || isNotMentionable(mentionRole)) {
            log.error("No mention role found or that role is not mentionable. ID: " + youtubeChannel.getMentionRoleId());
            return;
        }
        val message = youtubeVideoMessagePattern
                .replaceAll("\\{mention\\}", mentionRole.getAsMention())
                .replaceAll("\\{channelTitle\\}", youtubeChannel.getChannelName())
                .replaceAll("\\{videoLink\\}", youtubeVideoBaseLink + youtubeVideo.getYoutubeId());
        textChannel.sendMessage(message).queue();
    }

    private boolean canNotTalkTo(TextChannel textChannel) {
        return !textChannel.canTalk();
    }

    private boolean isNotMentionable(Role role) {
        return !role.isMentionable();
    }
}

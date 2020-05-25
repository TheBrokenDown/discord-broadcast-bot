package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.CommandUtils;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.exception.MoreThanOneMentionedRoleFoundException;
import space.delusive.discord.broadcastbot.exception.NoMentionedRolesFoundException;
import space.delusive.discord.broadcastbot.exception.YoutubeVideoNotFoundException;
import space.delusive.discord.broadcastbot.service.YoutubeService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Log4j2
@Component
public class AddYoutubeChannelCommand extends Command {
    private static final String VIDEO_URL_REGEXP = "^(https://www\\.youtube\\.com/watch\\?v=.+)|(https://youtu\\.be/.+)$";
    private static final String FULL_URL_VIDEO_ID_PREFIX = "^https://www\\.youtube\\.com/watch\\?v=";
    private static final String SHORT_URL_VIDEO_ID_PREFIX = "^https://youtu\\.be/";
    private static final String CORRECT_CHANNEL_FOUND_REACTION = "\u2705";
    private static final String INCORRECT_CHANNEL_FOUND_REACTION = "\uD83D\uDEAB";

    private final YoutubeService youtubeService;
    private final EventWaiter eventWaiter;

    private Map<String, String> messages;

    public AddYoutubeChannelCommand(YoutubeService youtubeService,
                                    EventWaiter eventWaiter,
                                    @Value("${discord.bot.command.add.youtube.channel.name}") String name,
                                    @Value("${discord.bot.command.add.youtube.channel.help}") String help,
                                    @Value("${discord.bot.command.add.youtube.channel.aliases}") String[] aliases) {
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.youtubeService = youtubeService;
        this.eventWaiter = eventWaiter;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(messages.get("add.youtube.channel.enter.video.url"));
        Runnable timeoutAction = () -> event.replyWarning(messages.get("add.youtube.channel.timeout"));
        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(event),
                getChannelNameReceivedConsumer(event), 1, TimeUnit.MINUTES, timeoutAction);
    }

    private Consumer<GuildMessageReceivedEvent> getChannelNameReceivedConsumer(CommandEvent event) {
        return channelNameMessageEvent -> {
            String messageText = channelNameMessageEvent.getMessage().getContentRaw();
            if (!messageText.matches(VIDEO_URL_REGEXP)) {
                event.replyError(messages.get("add.youtube.channel.invalid.video.url"));
                return;
            }
            YoutubeChannel channel;
            try {
                channel = youtubeService.getYoutubeChannelInfoByVideoId(getVideoIdFromUrl(messageText));
            } catch (ChannelNotFoundException e) { // this shouldn't happen
                log.debug(e);
                event.replyError(messages.get("unexpected.error"));
                return;
            } catch (YoutubeVideoNotFoundException e) {
                log.debug(e);
                event.replyError(messages.get("add.youtube.channel.video.not.found"));
                return;
            }
            boolean isChannelAlreadyRegistered = youtubeService.getChannelByName(channel.getChannelName()).isPresent();
            if (isChannelAlreadyRegistered) {
                event.replyError(messages.get("add.youtube.channel.already.exist"));
                return;
            }
            event.reply(messages.get("add.youtube.channel.confirm.channel.name")
                            .replaceAll("%channelName%", channel.getChannelName()),
                    message -> {
                        message.addReaction(CORRECT_CHANNEL_FOUND_REACTION).queue();
                        message.addReaction(INCORRECT_CHANNEL_FOUND_REACTION).queue();
                        waitForReactionEvent(channel, message.getIdLong(), event);
                    });
        };
    }

    private Consumer<GuildMessageReactionAddEvent> getReactionToConfirmationReceivedConsumer(
            YoutubeChannel youtubeChannel, long confirmationMessageId, CommandEvent commandEvent) {
        return reactionEvent -> {
            Runnable timeoutAction = () -> commandEvent.replyWarning(messages.get("add.youtube.channel.timeout"));
            String reaction = "";
            try {
                reaction = reactionEvent.getReactionEmote().getEmoji();
            } catch (IllegalStateException e) {
                log.debug(e);
            }
            switch (reaction) {
                case CORRECT_CHANNEL_FOUND_REACTION -> {
                    commandEvent.reply(messages.get("add.youtube.channel.enter.role"));
                    eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                            CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(commandEvent),
                            getMentionRoleReceivedConsumer(commandEvent, youtubeChannel),
                            1, TimeUnit.MINUTES, timeoutAction);
                }
                case INCORRECT_CHANNEL_FOUND_REACTION ->
                        commandEvent.reply(messages.get("add.youtube.channel.confirm.invalid"));
                default -> waitForReactionEvent(youtubeChannel, confirmationMessageId, commandEvent); // wait for required reaction!
            }
        };
    }

    private void waitForReactionEvent(YoutubeChannel youtubeChannel, long confirmationMessageId,
                                      CommandEvent commandEvent) {
        Runnable timeoutAction = () -> commandEvent.replyWarning(messages.get("add.youtube.channel.timeout"));
        eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, // wait for another reaction
                getSameMessageSameAuthorPredicate(confirmationMessageId, commandEvent),
                getReactionToConfirmationReceivedConsumer(youtubeChannel, confirmationMessageId, commandEvent),
                1, TimeUnit.MINUTES, timeoutAction);
    }

    private Consumer<GuildMessageReceivedEvent> getMentionRoleReceivedConsumer(CommandEvent event,
                                                                               YoutubeChannel channel) {
        return roleMentionMessageEvent -> {
            String mentionedRoleId = null;
            try {
                mentionedRoleId = CommandUtils.getMentionedRoleIdFrom(roleMentionMessageEvent.getMessage());
            } catch (NoMentionedRolesFoundException e) {
                log.debug(e);
                event.replyError(messages.get("add.youtube.channel.enter.role.not.specified"));
            } catch (MoreThanOneMentionedRoleFoundException e) {
                log.debug(e);
                event.replyError(messages.get("add.youtube.channel.enter.role.more.than.one"));
            }
            if (mentionedRoleId != null) {
                channel.setMentionRoleId(mentionedRoleId);
                youtubeService.addChannel(channel);
                event.replySuccess(messages.get("add.youtube.channel.success"));
            }
        };
    }

    private Predicate<GuildMessageReactionAddEvent> getSameMessageSameAuthorPredicate(
            long messageId, CommandEvent event) {
        return reactionEvent ->
                reactionEvent.getMessageIdLong() == messageId &&
                        reactionEvent.getUser().equals(event.getAuthor());
    }

    private String getVideoIdFromUrl(String videoUrl) {
        val stringBuilder = new StringBuilder(
                videoUrl.replaceAll(FULL_URL_VIDEO_ID_PREFIX, "")
                        .replaceAll(SHORT_URL_VIDEO_ID_PREFIX, ""));
        if (stringBuilder.indexOf("?") != -1) {
            stringBuilder.delete(stringBuilder.indexOf("?"), stringBuilder.length());
        }
        if (stringBuilder.indexOf("&") != -1) {
            stringBuilder.delete(stringBuilder.indexOf("&"), stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    @Resource(name = "messages")
    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}

package space.delusive.discord.broadcastbot.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
public class YoutubeChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    private String channelId;

    @NonNull
    private String channelName;

    @NonNull
    private String mentionRoleId;

    @NonNull
    private String uploadsPlaylistId;
}

package space.delusive.discord.broadcastbot.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
public class MixerChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String channelName;
    private String channelId;
    private String mentionRoleId;
}

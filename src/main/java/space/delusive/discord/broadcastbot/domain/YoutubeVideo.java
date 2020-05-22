package space.delusive.discord.broadcastbot.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class YoutubeVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String youtubeId;

    public YoutubeVideo(String youtubeId) {
        this.youtubeId = youtubeId;
    }
}

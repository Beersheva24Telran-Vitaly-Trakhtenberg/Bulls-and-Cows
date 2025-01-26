package telran.game.bulls_cows.models;

import jakarta.persistence.*;

@Entity
@Table(name = "gamers_to_games",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"game_id", "gamer_name"})})
public class GamerGame
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gamer_id")
    private String gamerName;
    @Column(name = "game_id")
    private Long gameId;
    @Column(name = "is_winner")
    private boolean isWinner;

    public GamerGame() {}
    public GamerGame(String gamerName, Long gameId) {
        this.gamerName = gamerName;
        this.gameId = gameId;
    }
    public GamerGame(String gamerName, Long gameId, boolean isWinner) {
        this.gamerName = gamerName;
        this.gameId = gameId;
        this.isWinner = isWinner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGamerName() {
        return gamerName;
    }

    public void setGamerName(String gamerName) {
        this.gamerName = gamerName;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public boolean isWinner() {
        return isWinner;
    }
    public void setWinner() {
        this.isWinner = true;
    }
}

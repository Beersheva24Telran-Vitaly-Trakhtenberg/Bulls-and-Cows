package telran.game.bulls_cows;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import telran.game.bulls_cows.common.CsvConvertible;
import telran.game.bulls_cows.common.Tools;

import java.time.LocalDateTime;

@Entity
public class Game implements CsvConvertible
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long gameId;

    @Column(name="time_start")
    private LocalDateTime startDateTime;

    @Column(name="time_finish")
    private LocalDateTime finishDateTime;

    private String sequence;

    public Game() {    }

    public void startGame() {
        this.sequence = Tools.generateSequence();
        this.startDateTime = LocalDateTime.now();
    }

    public String getTimeStart() {
        return startDateTime.toString();
    }

    public boolean isStarted() {
        return startDateTime != null && startDateTime.isBefore(LocalDateTime.now());
    }

    public boolean isFinished() {
        return finishDateTime != null;
    }

    public void finishGame() {
        if (finishDateTime == null) {
            this.finishDateTime = LocalDateTime.now();
        }
    }

    public String getTimeFinish() {
        if (finishDateTime == null) {
            throw new RuntimeException("The game isn't finished yet");
        }
        return finishDateTime.toString();
    }

    public String getSequence() {
        return this.sequence;
    }

    public Long getGameID() {
        return this.gameId;
    }

    @Override
    public String[] toStringArray()
    {
        return new String[] {
                Long.toString(gameId),
                isStarted() ? startDateTime.toString() : null,
                Boolean.toString(isStarted()),
                Boolean.toString(isFinished()),
                sequence,
                isFinished() ? finishDateTime.toString() : null
        };
    }
}

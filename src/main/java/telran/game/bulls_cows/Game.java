package telran.game.bulls_cows;

import telran.game.bulls_cows.common.CsvConvertible;
import telran.game.bulls_cows.common.Tools;

import java.time.LocalDateTime;

public class Game implements CsvConvertible
{
    private final int game_id;
    private String sequence;
    private LocalDateTime date_starting;
    private LocalDateTime date_finish = null;

    public Game(int game_id) {
        this.game_id = game_id;
    }

    public void startGame() {
        this.sequence = Tools.generateSequence();
        this.date_starting = LocalDateTime.now();
    }

    public String getTimeStart() {
        return date_starting.toString();
    }

    public boolean isFinished() {
        return date_finish != null;
    }

    public void finishGame() {
        if (date_finish == null) {
            this.date_finish = LocalDateTime.now();
        }
    }

    public String getTimeFinish() {
        if (date_finish == null) {
            throw new RuntimeException("The game isn't finished yet");
        }
        return date_finish.toString();
    }

    public String getSequence() {
        return this.sequence;
    }

    public int getGameID() {
        return this.game_id;
    }

    @Override
    public String[] toStringArray()
    {
        return new String[] {
                Integer.toString(game_id),
                date_starting.toString(),
                Boolean.toString(isFinished()),
                sequence,
                isFinished() ? date_finish.toString() : null
        };
    }
}

package telran.game.bulls_cows;

import telran.game.bulls_cows.common.Tools;

import java.time.LocalDateTime;

public class Game
{
    private final int game_id;
    private String sequence;
    private LocalDateTime date_starting;
    private LocalDateTime date_finishing = null;

    public Game(int game_id)
    {
        this.game_id = game_id;
    }

    public void startGame()
    {
        this.sequence = Tools.generateSequence();
        this.date_starting = LocalDateTime.now();
    }

    public String getTimeStarting()
    {
        return date_starting.toString();
    }

    public boolean isFinished()
    {
        return date_finishing != null;
    }

    public void finishGame()
    {
        if (date_finishing == null) {
            this.date_finishing = LocalDateTime.now();
        }
    }

    public String getTimeFinishing()
    {
        if (date_finishing == null) {
            throw new RuntimeException("The game isn't finished yet");
        }
        return date_finishing.toString();
    }

    public String getSequence()
    {
        return this.sequence;
    }

    public int getGameID()
    {
        return this.game_id;
    }
}

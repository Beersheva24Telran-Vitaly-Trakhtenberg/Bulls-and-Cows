package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class GameNotStartedException extends BaseGameCowsException
{
    public GameNotStartedException(Long gameId)
    {
        super("Game [%s] is not started yet.", Map.of("gameId", gameId));
    }
}

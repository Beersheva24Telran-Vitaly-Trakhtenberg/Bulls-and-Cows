package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class GameAlreadyStartedException extends BaseGameCowsException
{
    public GameAlreadyStartedException(Long gameId)
    {
        super("Game [%s] already started.", Map.of("gameId", gameId));
    }
}

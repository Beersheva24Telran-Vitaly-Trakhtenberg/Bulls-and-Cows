package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class GameAlreadyFinishedException extends BaseGameCowsException
{
    public GameAlreadyFinishedException(Long gameId)
    {
        super("Game [%s] already finished. Additional moves are not allowed.", Map.of("gameId", gameId));
    }
}

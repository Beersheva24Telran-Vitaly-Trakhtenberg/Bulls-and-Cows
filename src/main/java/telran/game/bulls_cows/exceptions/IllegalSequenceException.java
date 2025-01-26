package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class IllegalSequenceException extends BaseGameCowsException
{
    public IllegalSequenceException(String sequence)
    {
        super("Sequence [%s] is not valid.", Map.of("sequence", sequence));
    }
}

package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.UsernameNotFoundException;

public interface BullsCowsRepository
{
    boolean isUserExists(String gamerName);
    boolean isGameExists(int gameId);
    boolean isGamerInGame(SessionToken gamerToken, int gameId) throws UsernameNotFoundException;
    boolean isGameStarted(int gameId);
    boolean isGameFinished(int gameId);
    boolean isGameHasGamers(int gameId);
}

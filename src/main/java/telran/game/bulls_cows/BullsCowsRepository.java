package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import java.util.List;

public interface BullsCowsRepository
{
    boolean isUserExists(String gamerName);
    boolean isGameExists(int gameId);
    boolean isGamerInGame(SessionToken gamerToken, int gameId) throws UserNotFoundException;
    boolean isGameStarted(int gameId);
    boolean isGameFinished(int gameId);
    boolean isGameHasGamers(int gameId);

    Gamer getGamer(SessionToken gamerToken) throws UserNotFoundException;
    List<Gamer> getAllGamers();
    List<Game> findGamesOfGamer(SessionToken gamerToken) throws UserNotFoundException;
    Game getGame(int gameId) throws GameNotFoundException;
    List<Game> getAllGames();
    List<Game> findAllStartedGames();
    List<Game> findAllFinishedGames();
    List<Gamer> findAllGamersOfGame(int gameId) throws GameNotFoundException;
    int CreateGame();
    List<Gamer> addGamersToGame(int gameId, List<Gamer> gamers) throws GameNotFoundException, UserNotFoundException;
    boolean removeGamerFromGame(SessionToken gamerToken, int gameId) throws GameNotFoundException, UserNotFoundException;
}

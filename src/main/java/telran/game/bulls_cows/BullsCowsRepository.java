package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import java.time.LocalDate;
import java.util.List;

public interface BullsCowsRepository
{
    boolean isUserExists(String gamerName);
    boolean isGamerInGame(SessionToken gamerToken, int gameId) throws UserNotFoundException;
    boolean isGameStarted(int gameId);
    boolean isGameFinished(int gameId);
    boolean isGameHasGamers(int gameId);

    Gamer createGamer(String gamerName, LocalDate birthday) throws UserAlreadyExistsException;
    List<Gamer> getAllGamers();
    List<Gamer> findAllGamersOfGame(int gameId) throws GameNotFoundException;
    List<Gamer> addGamersToGame(int gameId, List<Gamer> gamers) throws GameNotFoundException, UserNotFoundException;
    boolean removeGamerFromGame(SessionToken gamerToken, int gameId) throws GameNotFoundException, UserNotFoundException;

    List<Game> findGamesOfGamer(SessionToken gamerToken) throws UserNotFoundException;
    List<Game> getAllGames();
    List<Game> findAllStartedGames();
    List<Game> findAllFinishedGames();
    int CreateGame(String secuence);
    Game findGameById(Long gameId) throws GameNotFoundException;
}

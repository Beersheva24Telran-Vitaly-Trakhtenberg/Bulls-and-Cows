package telran.game.bulls_cows.repository;

import telran.game.bulls_cows.Game;
import telran.game.bulls_cows.Gamer;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import java.time.LocalDate;
import java.util.List;

public interface BullsCowsRepository
{
    boolean isUserExists(String gamerID);
    boolean isGamerInGame(String gamerID, Long gameId) throws UserNotFoundException, GameNotFoundException;
    boolean isGameStarted(Long gameId) throws GameNotFoundException;
    boolean isGameFinished(Long gameId) throws GameNotFoundException;
    boolean isGameHasGamers(Long gameId) throws GameNotFoundException;

    Gamer createGamer(String gamerName, LocalDate birthday) throws UserAlreadyExistsException;
    List<Gamer> findAllGamersOfGame(Long gameId) throws GameNotFoundException;
    List<Gamer> addGamersToGame(Long gameId, List<Gamer> gamers) throws GameNotFoundException, UserNotFoundException, GameAlreadyStartedException;
    boolean removeGamerFromGame(String gamerID, Long gameId) throws GameNotFoundException, UserNotFoundException;

    List<Game> findGamesOfGamer(String gamerID) throws UserNotFoundException;
    List<Game> getAllGames();
    List<Game> findAllStartedGames();
    List<Game> findAllStartedGames(String gamerID);
    List<Game> findAllNonStartedGames(String gamerID);
    List<Game> findAllJoinabledGames(String gamerID) throws UserNotFoundException;
    List<Game> findAllFinishedGames();
    Long createGame(String secuence);
    void startGame(Long gameId, String gamerID) throws GameNotFoundException, UserNotFoundException;
    Game findGameById(Long gameId) throws GameNotFoundException;
}

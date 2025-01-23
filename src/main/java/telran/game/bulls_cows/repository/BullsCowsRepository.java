package telran.game.bulls_cows.repository;

import telran.game.bulls_cows.models.Game;
import telran.game.bulls_cows.models.Gamer;
import telran.game.bulls_cows.exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    List<Gamer> addGamersToGame(Long gameId, List<String> gamersIDs) throws GameNotFoundException, UserNotFoundException, GameAlreadyStartedException;
    boolean removeGamerFromGame(String gamerID, Long gameId) throws GameNotFoundException, UserNotFoundException;

    List<Game> findGamesOfGamer(String gamerID) throws UserNotFoundException;
    List<Game> getAllGames();
    List<Game> findAllStartedGames();
    List<Game> findAllStartedGames(String gamerID);
    List<Game> findAllNonStartedGames(String gamerID);
    List<Game> findAllStartableGames(String userId) throws UserNotAuthorizedException, UserNotFoundException;
    List<Game> findAllJoinableGames(String gamerID) throws UserNotAuthorizedException, UserNotFoundException;
    List<Game> findAllFinishedGames();
    Long createGame(String secuence);
    void startGame(Long gameId, LocalDateTime startDateTime) throws GameNotFoundException, GameAlreadyStartedException;
    Game findGameById(Long gameId) throws GameNotFoundException;
}

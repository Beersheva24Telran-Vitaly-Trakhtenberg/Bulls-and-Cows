package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import javax.naming.AuthenticationException;
import java.util.List;

/**
 * Service layer for Bulls and Cows game
 */
public interface BullsCowsService
{
    SessionToken logIn(String gamerName) throws UserNotFoundException;
    void logOut(SessionToken gamerToken) throws AuthenticationException;
    void isSessionTokenValid(SessionToken gamerToken) throws AuthenticationException;

    /**
     * Registers new user
     * @param gamerName
     * @param birthday
     * @return SessionToken
     * @throws UserAlreadyExistsException
     * @throws IllegalArgumentException
     */
    SessionToken signUp(String gamerName, String birthday) throws UserAlreadyExistsException,IllegalArgumentException;

    Long createGame(SessionToken gamerToken) throws AuthenticationException;
    void joinGame(SessionToken gamerToken, Long game_id) throws AuthenticationException;
    void startGame(SessionToken gamerToken, Long game_id) throws AuthenticationException;
    void startGame(SessionToken gamerToken, Long game_id, String dateTimeStart) throws AuthenticationException;
    boolean isGameStarted(Long game_id) throws GameNotFoundException;

    List<Long> getAvailabledGamesForStarting(SessionToken gamerToken) throws AuthenticationException;
    List<Long> getAvailabledGamesForJoining(SessionToken gamerToken) throws AuthenticationException;
    List<Long> getGamerStartedGames(SessionToken gamerToken) throws AuthenticationException;
    List<Long> getGamerFinishedGames(SessionToken gamerToken) throws AuthenticationException;
    List<Moves> getGamerMoves(SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException;
    List<Moves> addGamerNewMove(String sequence, SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException;
}

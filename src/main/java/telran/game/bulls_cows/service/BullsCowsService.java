package telran.game.bulls_cows.service;

import telran.game.bulls_cows.Moves;
import telran.game.bulls_cows.exceptions.*;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

/**
 * Service layer for Bulls and Cows game
 */
public interface BullsCowsService
{
    String logIn(Map<String, Object> params) throws UserNotFoundException;
    void logOut(String gamerToken) throws AuthenticationException, UserNotAuthorizedException;
    void isSessionTokenValid(String gamerToken) throws AuthenticationException, UserNotAuthorizedException;

    /**
     * Registers a new user.
     *
     * @param params A map containing the following keys:
     *               - "username" (String): The username of the gamer.
     *               - "birthdate" (String): The birthdate of the gamer (in any of predefined formats).
     * @return SessionToken for the newly registered user.
     * @throws UserAlreadyExistsException if the username already exists.
     * @throws IllegalArgumentException if any of the parameters are missing or invalid.
     */
    public String signUp(Map<String, Object> params) throws UserAlreadyExistsException, IllegalArgumentException;

    Long createGame(Map<String, Object> params) throws AuthenticationException, UserNotAuthorizedException;
    Long joinGame(Map<String, Object> params) throws AuthenticationException, GameAlreadyStartedException, UserNotFoundException, UserNotAuthorizedException;
    Long startGame(Map<String, Object> params) throws AuthenticationException, UserNotAuthorizedException, GameAlreadyStartedException;
    //void startGame(String gamerToken, Long game_id, String dateTimeStart) throws AuthenticationException;
    boolean isGameStarted(Long game_id) throws GameNotFoundException;

    List<Long> getAvailableGamesForStarting(Map<String, Object> params) throws AuthenticationException, UserNotFoundException, UserNotAuthorizedException;
    List<Long> getAvailableGamesForJoining(Map<String, Object> params) throws AuthenticationException, UserNotFoundException, UserNotAuthorizedException;
    List<Long> getGamerGamingGames(Map<String, Object> params) throws AuthenticationException;
    List<Long> getGamerFinishedGames(Map<String, Object> params) throws AuthenticationException;
    List<Moves> getGamerMoves(String gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException, UserNotAuthorizedException;
    List<Moves> addGamerNewMove(String sequence, String gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException, UserNotAuthorizedException;
}

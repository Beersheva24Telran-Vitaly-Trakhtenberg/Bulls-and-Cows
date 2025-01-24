package telran.game.bulls_cows.service;

import org.json.JSONObject;
import telran.game.bulls_cows.common.settings.Settings;
import telran.game.bulls_cows.models.Game;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.common.Tools;
import telran.game.bulls_cows.exceptions.*;
import telran.game.bulls_cows.repository.BullsCowsRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BullsCowsServiceImpl implements BullsCowsService
{
    private final BullsCowsRepository repository;

    public BullsCowsServiceImpl(BullsCowsRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public String logIn(Map<String, Object> params) throws UserNotFoundException
    {
        if (!params.containsKey("username") || !(params.get("username") instanceof String gamerName)) {
            throw new IllegalArgumentException("Missing or invalid parameter: username");
        }
        try {
            if (!repository.isUserExists(gamerName))
                throw new UserNotFoundException(gamerName);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(gamerName);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return SessionToken.getSessionToken(gamerName);
    }

    @Override
    public void logOut(String gamerToken) throws UserNotAuthorizedException {
        isSessionTokenValid(gamerToken);
    }

    @Override
    public void isSessionTokenValid(String gamerToken) throws UserNotAuthorizedException {
        if (gamerToken == null || !SessionToken.isTokenValid(gamerToken)) {
            throw new UserNotAuthorizedException(SessionToken.getUserIdFromToken(gamerToken));
        }
    }

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
    @Override
    public String signUp(Map<String, Object> params) throws UserAlreadyExistsException, IllegalArgumentException
    {
        if (!params.containsKey("username") || !(params.get("username") instanceof String gamerName)) {
            throw new IllegalArgumentException("Missing or invalid parameter: username");
        }

        if (!params.containsKey("birthdate") || !(params.get("birthdate") instanceof String birthdateStr)) {
            throw new IllegalArgumentException("Missing or invalid parameter: birthdate");
        }

        if (repository.isUserExists(gamerName)) {
            throw new UserAlreadyExistsException(gamerName);
        }

        List<DateTimeFormatter> formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalDate birthdate = null;
        for (DateTimeFormatter formatter : formatters) {
            try {
                birthdate = LocalDate.parse(birthdateStr, formatter);
                break;
            } catch (DateTimeParseException e) {}
        }

        if (birthdate == null) {
            throw new IllegalArgumentException("Invalid date format for birthdate. Supported formats: dd-MM-yyyy, dd.MM.yyyy, yyyy-MM-dd, yyyy.MM.dd");
        }

        repository.createGamer(gamerName, birthdate);
        return SessionToken.getSessionToken(gamerName);
    }

    public void ping() {}

    @Override
    public Long createGame(Map<String, Object> params) throws UserNotAuthorizedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        return repository.createGame(Tools.generateSequence());
    }

    @Override
    public Long joinGame(Map<String, Object> params) throws
            GameAlreadyStartedException,
            UserNotFoundException,
            UserNotAuthorizedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        Long gameId = null;
        String gamerId = "";
        try {
            if (!params.containsKey("gameId") || !(params.get("gameId") instanceof String)) {
                throw new IllegalArgumentException("Missing or invalid parameter: 'gameId'");
            }
            gameId = Long.valueOf(params.get("gameId").toString());
            gamerId = SessionToken.getUserIdFromToken(gamerToken);
            List<String> gamers = new ArrayList<String>();
            gamers.add(gamerId);
            repository.addGamersToGame(gameId, gamers);

            return gameId;
        } catch (GameNotFoundException e) {
            throw new GameNotFoundException(gameId);
        } catch (GameAlreadyStartedException e) {
            throw new GameAlreadyStartedException(gameId);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(gamerId);
        }
    }

    @Override
    public Long startGame(Map<String, Object> params) throws UserNotAuthorizedException, GameAlreadyStartedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        Long gameId = null;
        try {
            if (!params.containsKey("gameId") || !(params.get("gameId") instanceof String)) {
                throw new IllegalArgumentException("Missing or invalid parameter: 'gameId'");
            }
            gameId = Long.valueOf(params.get("gameId").toString());
            LocalDateTime startDateTime = null;
            if (
                !params.containsKey("startDateTime") ||
                !(params.get("startDateTime") instanceof String) ||
                ((String) params.get("startDateTime")).isEmpty())
            {} else {
                startDateTime = params.get("startDateTime") instanceof String ? Tools.parse(params.get("startDateTime").toString()) : null;
            }
            repository.startGame(gameId, startDateTime);

            return gameId;
        } catch (GameNotFoundException e) {
            throw new GameNotFoundException(gameId);
        } catch (GameAlreadyStartedException a) {
            throw new GameAlreadyStartedException(gameId);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for startDateTime. Supported formats: dd-MM-yyyy HH:mm or HH:mm");
        }
    }

    @Override
    public boolean isGameStarted(
            Map<String,
            Object> params
        ) throws
            GameNotFoundException,
            UserNotAuthorizedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        if (!params.containsKey("gameId") || !(params.get("gameId") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: 'gameId'");
        }
        Long gameId = Long.valueOf(params.get("gameId").toString());

        return repository.isGameStarted(gameId);
    }

    @Override
    public boolean isGameFinished(
            Map<String,
            Object> params
        ) throws
            GameNotFoundException,
            UserNotAuthorizedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        if (!params.containsKey("gameId") || !(params.get("gameId") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: 'gameId'");
        }
        Long gameId = Long.valueOf(params.get("gameId").toString());

        return repository.isGameFinished(gameId);
    }

    @Override
    public List<Long> getAvailableGamesForStarting(
            Map<String,
            Object> params
        ) throws
            UserNotFoundException,
            UserNotAuthorizedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");
        isSessionTokenValid(gamerToken);
        List<Game> games = repository.findAllStartableGames(SessionToken.getUserIdFromToken(gamerToken));
        return games.stream().map(Game::getGameID).toList();
    }

    @Override
    public List<Long> getAvailableGamesForJoining(
            Map<String,
            Object> params
        ) throws
            UserNotFoundException,
            UserNotAuthorizedException
    {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");
        isSessionTokenValid(gamerToken);
        List<Game> games = repository.findAllJoinableGames(SessionToken.getUserIdFromToken(gamerToken));
        return games.stream().map(Game::getGameID).toList();
    }

    @Override
    public List<Long> getGamerGamingGames(Map<String, Object> params) throws UserNotAuthorizedException {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        try {
            List<Game> games = repository.findGamesOfGamer(SessionToken.getUserIdFromToken(gamerToken));
            return games.stream().map(Game::getGameID).toList();
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> getGamerFinishedGames(Map<String, Object> params)
    {
        //isSessionTokenValid(gamerToken);
        return List.of();
    }

    @Override
    public List<Map<String, String>> getGamerMoves(Map<String, Object> params)
            throws
            GameNotFoundException,
            UserNotAuthorizedException, GameNotStartedException, GameAlreadyFinishedException {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        String gamerId = SessionToken.getUserIdFromToken(gamerToken);
        if (!params.containsKey("gameId") || !(params.get("gameId") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: 'gameId'");
        }
        Long gameId = Long.valueOf(params.get("gameId").toString());
        if (!repository.isGameStarted(gameId)) {
            throw new GameNotStartedException(gameId);
        }
        if (repository.isGameFinished(gameId)) {
            throw new GameAlreadyFinishedException(gameId);
        }
        List<Map<String, String>> result = new ArrayList<>();
        try {
            result = repository.getGamerMoves(gameId, gamerId);
        } catch (Exception e) {
            var error = 1;  // FixMe remove after tests
        }
        return result;
    }

    @Override
    public Map<String, String> addGamerNewMove(
            Map<String, Object> params
        ) throws
            GameNotFoundException,
            UserNotAuthorizedException,
            GameAlreadyFinishedException, IllegalSequenceException {
        checksIsTokenPresented(params);
        String gamerToken = (String) params.get("userSessionToken");

        isSessionTokenValid(gamerToken);
        String gamerId = SessionToken.getUserIdFromToken(gamerToken);
        Long gameId = null;
        String sequence = "";
        try {
            if (!params.containsKey("gameId") || !(params.get("gameId") instanceof String)) {
                throw new IllegalArgumentException("Missing or invalid parameter: 'gameId'");
            }
            gameId = Long.valueOf(params.get("gameId").toString());
            if (repository.isGameFinished(gameId)) {
                throw new GameAlreadyFinishedException(gameId);
            }
            if (!params.containsKey("sequence") || !(params.get("sequence") instanceof String)) {
                throw new IllegalArgumentException("Missing or invalid parameter: 'sequence'");
            }
            sequence = params.get("sequence").toString();
            HashMap<String, String> currentMovie = new HashMap<>();
            try {
                currentMovie = Tools.calculateSequenceResults(repository.findGameById(gameId).getSequence(), sequence);
            } catch (IllegalSequenceException e) {
                throw new IllegalSequenceException(sequence);
            }
            currentMovie.put("sequence", sequence);
            repository.addMovieToGame(gameId, gamerId, currentMovie);
            if (Integer.parseInt(currentMovie.get("numberBulls")) == Settings.LENGTH_OF_SEQUENCE) {
                repository.finishGame(gameId, gamerId);
                currentMovie.put("numberBulls", "WINNER");
            }

            return currentMovie;
        } catch (GameNotFoundException e) {
            throw new GameNotFoundException(gameId);
        } catch (GameNotStartedException e) {
            throw new RuntimeException(e);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (GameAlreadyFinishedException e) {
            throw new GameAlreadyFinishedException(gameId);
        } catch (IllegalSequenceException e) {
            throw new IllegalSequenceException(sequence);
        }
    }

    private String extractInput(String key, String data) throws IllegalArgumentException {
        if (isJson(data)) {
            return parseJsonForKey(key, data);
        } else {
            return data;
        }
    }

    private boolean isJson(String input) {
        return input.trim().startsWith("{") && input.trim().endsWith("}");
    }

    private String parseJsonForKey(String key, String data) throws IllegalArgumentException {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            } else {
                throw new IllegalArgumentException("Missing '" + key + "' field in JSON input");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }

    private void checksIsTokenPresented(Map<String, Object> params)
    {
        if (!params.containsKey("userSessionToken") || !(params.get("userSessionToken") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: userSessionToken");
        }
    }
}

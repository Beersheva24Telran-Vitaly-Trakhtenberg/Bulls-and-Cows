package telran.game.bulls_cows.repository;

import jakarta.persistence.*;

import telran.game.bulls_cows.dto.GamerMovesDTO;
import telran.game.bulls_cows.models.*;
import telran.game.bulls_cows.exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BullsCowsRepositoryImpl implements BullsCowsRepository
{
    @PersistenceContext
    private EntityManagerFactory emf;

    public BullsCowsRepositoryImpl(EntityManagerFactory emf)
    {
        this.emf = emf;
    }

    private EntityManager getEntityManager()
    {
        return emf.createEntityManager();
    }

    @Override
    public boolean isUserExists(String gamerName)
    {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(g) FROM Gamer g WHERE g.gamer_name = :gamerName", Long.class);
            query.setParameter("gamerName", gamerName);
            long count = query.getSingleResult();
            return count > 0;
        }
    }

    @Override
    public boolean isGamerInGame(String gamerId, Long gameId) throws UserNotFoundException, GameNotFoundException
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }
        if (!isGamerExists(gamerId)) {
            throw new UserNotFoundException(gamerId);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g) FROM GamerGame g WHERE g.gamerName = :gamerName AND g.gameId = :gameId",
                    Long.class);
            query.setParameter("gamerName", gamerId);
            query.setParameter("gameId", gameId);
            long count = query.getSingleResult();
            return count > 0;
        }
    }

    @Override
    public boolean isGamerExists(String gamerID)
    {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g) FROM Gamer g WHERE g.id = :gamerId", Long.class);
            query.setParameter("gamerId", gamerID);
            long count = query.getSingleResult();
            return count > 0;
        }
    }

    @Override
    public boolean isGameStarted(Long gameId) throws GameNotFoundException
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Boolean> query = em.createQuery(
                    "SELECT CASE WHEN g.startDateTime IS NOT NULL " +
                            "AND g.startDateTime < :currentDateTime THEN TRUE ELSE FALSE END " +
                            "FROM Game g WHERE g.id = :gameId",
                    Boolean.class);
            query.setParameter("currentDateTime", LocalDateTime.now());
            query.setParameter("gameId", gameId);
            return query.getSingleResult();
        }
    }

    @Override
    public boolean isGameFinished(Long gameId)
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Boolean> query = em.createQuery(
                    "SELECT CASE WHEN g.finishDateTime IS NOT NULL " +
                            "THEN TRUE ELSE FALSE END " +
                            "FROM Game g WHERE g.id = :gameId",
                    Boolean.class);
            query.setParameter("gameId", gameId);
            return query.getSingleResult();
        }
    }

    @Override
    public boolean isGameHasGamers(Long gameId) throws GameNotFoundException
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g) FROM GamerGame g WHERE g.id = :gameId",
                    Long.class);
            long count = query.getSingleResult();
            return count > 0;
        }
    }

    @Override
    public Gamer createGamer(String gamerName, LocalDate birthday) throws UserAlreadyExistsException
    {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();

            Gamer gamer = new Gamer(gamerName, birthday);
            em.persist(gamer);

            em.getTransaction().commit();
            return gamer;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new UserAlreadyExistsException(gamerName);
        } finally {
            em.close();
        }
    }

    public boolean isGameExists(Long gameId)
    {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g) FROM Game g WHERE g.id = :gameId", Long.class);
            query.setParameter("gameId", gameId);
            long count = query.getSingleResult();
            return count > 0;
        }
    }

    @Override
    public List<Gamer> findAllGamersOfGame(Long gameId) throws GameNotFoundException
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Gamer> query = em.createQuery(
                    "SELECT g FROM Gamer g JOIN GamerGame gg ON g.gamer_name = gg.gamerName " +
                            "WHERE gg.gameId = :gameId",
                    Gamer.class);
            query.setParameter("gameId", gameId);
            return query.getResultList();
        }
    }

    @Override
    public List<Gamer> addGamersToGame(Long gameId, List<String> gamersIDs)
            throws GameNotFoundException,
            UserNotFoundException,
            GameAlreadyStartedException
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }
        if (isGameStarted(gameId)) {
            throw new GameAlreadyStartedException(gameId);
        }

        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            for (String gamerId : gamersIDs) {
                if (!isUserExists(gamerId)) {
                    throw new UserNotFoundException(gamerId);
                }

                GamerGame gamerGame = new GamerGame(gamerId, gameId);
                em.persist(gamerGame);
            }

            em.getTransaction().commit();
        } catch (EntityExistsException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

        return findAllGamersOfGame(gameId);
    }

    @Override
    public boolean removeGamerFromGame(String gamerID, Long gameId) throws GameNotFoundException, UserNotFoundException {
        // TODO Implement this method
        throw new UnsupportedOperationException("Method BullsCowsRepositoryImpl.removeGamerFromGame() not implemented yet");
    }

    @Override
    public List<Game> findGamesOfGamer(String gamerID) throws UserNotFoundException
    {
        if (!isUserExists(gamerID)) {
            throw new UserNotFoundException(gamerID);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g JOIN GamerGame gg ON g.gameId = gg.gameId " +
                            "WHERE gg.gamerName = :gamerName AND g.finishDateTime IS NULL",
                    Game.class);
            query.setParameter("gamerName", gamerID);
            return query.getResultList();
        }
    }

    @Override
    public List<Game> getAllGames() {
        // TODO Implement this method
        throw new UnsupportedOperationException("Method BullsCowsRepositoryImpl.getAllGames() not implemented yet");
    }

    @Override
    public List<Game> findAllStartedGames() {
        // TODO Implement this method
        throw new UnsupportedOperationException("Method BullsCowsRepositoryImpl.findAllStartedGames() not implemented yet");
    }

    @Override
    public List<Game> findAllStartedGames(String gamerID) //throws UserNotFoundException
    {
/*
        if (!isUserExists(gamerToken.toString())) {
            throw new UserNotFoundException(gamerToken.toString());
        }

        try (EntityManager em = getEntityManager()){
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g JOIN GamerGame gg ON g.id = gg.gameId " +
                            "WHERE gg.gamerName = :gamerName AND g.startDateTime IS NOT NULL " +
                            "AND g.startDateTime < :currentDateTime AND g.finishDateTime IS NULL",
                    Game.class);
            query.setParameter("gamerName", gamerToken.getUsername());
            query.setParameter("currentDateTime", LocalDateTime.now());
            return query.getResultList();
        }
*/
        return List.of();
    }

    @Override
    public List<Game> findAllNonStartedGames(String gamerID)  //throws UserNotFoundException
    {
/*
        if (!isUserExists(gamerToken.toString())) {
            throw new UserNotFoundException(gamerToken.toString());
        }

        try (EntityManager em = getEntityManager()){
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g " +
                            "WHERE (g.startDateTime IS NULL OR g.startDateTime > :currentDateTime) " +
                            "AND g.id NOT IN (SELECT gg.gameId FROM GamerGame gg WHERE gg.gamerName = :gamerName)",
                    Game.class);
            query.setParameter("currentDateTime", LocalDateTime.now());
            query.setParameter("gamerName", gamerToken.getUsername());
            return query.getResultList();
        }
*/
        return List.of();
    }

    @Override
    public List<Game> findAllStartableGames(String gamerID) throws UserNotAuthorizedException, UserNotFoundException {
        if (!isUserExists(gamerID)) {
            throw new UserNotFoundException(gamerID);
        }

        try (EntityManager em = getEntityManager()){
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g " +
                            "WHERE (g.startDateTime IS NULL OR g.startDateTime > :currentDateTime) " +
                            "AND EXISTS (SELECT gg.gameId FROM GamerGame gg WHERE gg.gameId = g.gameId)",
                    Game.class);
            query.setParameter("currentDateTime", LocalDateTime.now());
            return query.getResultList();
        }
    }

    @Override
    public List<Game> findAllJoinableGames(String gamerID) throws UserNotAuthorizedException, UserNotFoundException {
        if (!isUserExists(gamerID)) {
            throw new UserNotFoundException(gamerID);
        }

        try (EntityManager em = getEntityManager()){
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g " +
                            "WHERE (g.startDateTime IS NULL OR g.startDateTime > :currentDateTime) " +
                            "AND g.id NOT IN (SELECT gg.gameId FROM GamerGame gg WHERE gg.gamerName = :gamerName)",
                    Game.class);
            query.setParameter("currentDateTime", LocalDateTime.now());
            query.setParameter("gamerName", gamerID);
            return query.getResultList();
        }
    }

    @Override
    public List<Game> findAllFinishedGames() {
        // TODO Implement this method
        throw new UnsupportedOperationException("Method BullsCowsRepositoryImpl.findAllFinishedGames() not implemented yet");
    }

    @Override
    public Long createGame(String sequence) {
        EntityManager em = getEntityManager();

        try {
            Game game = new Game();
            em.getTransaction().begin();
            {
                game.setSequence(sequence);
                em.persist(game);
            }
            em.getTransaction().commit();
            return game.getGameID();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void startGame(Long gameId, LocalDateTime startDateTime) throws GameNotFoundException, GameAlreadyStartedException
    {
        EntityManager em = getEntityManager();

        try {
            Game game = em.find(Game.class, gameId);
            if (game == null) {
                throw new GameNotFoundException(gameId);
            }

            if (startDateTime == null) {
                ZoneId zoneId = ZoneId.systemDefault();
                startDateTime = ZonedDateTime.now(zoneId).toLocalDateTime();
            }

            em.getTransaction().begin();
            {
                game.setStartDateTime(startDateTime);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public Game findGameById(Long gameId) throws GameNotFoundException
    {
        try (EntityManager em = getEntityManager()) {
            return em.find(Game.class, gameId);
        }
    }

    @Override
    public void addMovieToGame(
            Long gameId,
            String userId,
            Object movie
        ) throws
            UserNotFoundException,
            GameNotFoundException,
            GameNotStartedException
    {
        EntityManager em = getEntityManager();

        if (!isGamerInGame(userId, gameId)) {
            throw new UserNotFoundException(userId);
        }
        if (!isGameStarted(gameId)) {
            throw new GameNotStartedException(gameId);
        }

        TypedQuery<Long> query = em.createQuery(
                "SELECT g.id FROM GamerGame g WHERE g.gamerName = :gamerName AND g.gameId = :gameId",
                Long.class);
        query.setParameter("gamerName", userId);
        query.setParameter("gameId", gameId);

        try {
            em.getTransaction().begin();
            Long gamerGameId = query.getSingleResult();
            {
                GamerMoves gamerMovies;
                if (movie instanceof String) {
                    gamerMovies = new GamerMoves(gamerGameId, (String) movie);
                } else if (movie instanceof HashMap || movie instanceof ConcurrentHashMap) {
                    String sequence = ((ConcurrentMap<?, ?>) movie).get("sequence").toString();
                    String bulls = ((ConcurrentMap<?, ?>) movie).get("numberBulls").toString();
                    String cows = ((ConcurrentMap<?, ?>) movie).get("numberCows").toString();
                    Integer numberBulls = Integer.parseInt(bulls);
                    int numberCows = Integer.parseInt(cows);
                    gamerMovies = new GamerMoves(
                            gamerGameId,
                            sequence,
                            numberBulls,
                            numberCows
                    );
                } else {
                    throw new IllegalArgumentException("Invalid movie type");
                }
                em.persist(gamerMovies);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }

    }

    @Override
    public List<Map<String, String>> getGamerMoves(Long gameId, String userId)
    {
        EntityManager em = getEntityManager();
        TypedQuery<Long> query = em.createQuery(
                "SELECT id FROM GamerGame g WHERE g.gamerName = :gamerName AND g.gameId = :gameId",
                Long.class);
        query.setParameter("gamerName", userId);
        query.setParameter("gameId", gameId);
        List<Map<String, String>> result = new ArrayList<>();
        try {
            Long gamerGameId = query.getSingleResult();
            TypedQuery<GamerMovesDTO> queryMoves = em.createQuery(
                    "SELECT new telran.game.bulls_cows.dto.GamerMovesDTO(g.sequence, g.resultBulls, g.resultCows) FROM GamerMoves g WHERE g.keyGamerGameId = :gamerGameId",
                    GamerMovesDTO.class);
            queryMoves.setParameter("gamerGameId", gamerGameId);

            List<GamerMovesDTO> gamerMovesDTOList = queryMoves.getResultList();

            for (GamerMovesDTO gamerMove : gamerMovesDTOList) {
                if (gamerMove != null) {
                    Map<String, String> entity = new ConcurrentHashMap<>();
                    entity.put("sequence", gamerMove.getSequence());
                    entity.put("numberCows", String.valueOf(gamerMove.getResultCows()));
                    entity.put("numberBulls", String.valueOf(gamerMove.getResultBulls()));
                    result.add(entity);
                }
            }
        } catch (NoResultException e) {
        }

        return result;
    }

    @Override
    public void finishGame(Long gameId, String userId) throws GameNotFoundException, GameNotStartedException
    {
        EntityManager em = getEntityManager();

        TypedQuery<Long> query = em.createQuery(
                "SELECT id FROM GamerGame g WHERE g.gamerName = :gamerName AND g.gameId = :gameId",
                Long.class);
        query.setParameter("gamerName", userId);
        query.setParameter("gameId", gameId);

        try {
            Long gamerGameId = query.getSingleResult();
            ZoneId zoneId = ZoneId.systemDefault();
            em.getTransaction().begin();
            {
                GamerGame gamerGame = em.find(GamerGame.class, gamerGameId);
                gamerGame.setWinner();

                Game game = em.find(Game.class, gameId);
                game.setFinishDateTime(ZonedDateTime.now(zoneId).toLocalDateTime());
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}

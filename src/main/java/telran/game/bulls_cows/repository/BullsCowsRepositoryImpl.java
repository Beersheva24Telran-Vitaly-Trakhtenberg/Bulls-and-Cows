package telran.game.bulls_cows.repository;

import jakarta.persistence.*;

import telran.game.bulls_cows.Game;
import telran.game.bulls_cows.Gamer;
import telran.game.bulls_cows.GamerGame;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.GameAlreadyStartedException;
import telran.game.bulls_cows.exceprions.GameNotFoundException;
import telran.game.bulls_cows.exceprions.UserAlreadyExistsException;
import telran.game.bulls_cows.exceprions.UserNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    public boolean isGamerInGame(SessionToken gamerToken, Long gameId) throws UserNotFoundException, GameNotFoundException
    {
        if (!isGameExists(gameId)) {
            throw new GameNotFoundException(gameId);
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g) FROM GamerGame g WHERE g.gamerName = :gamerName AND g.gameId = :gameId",
                    Long.class);
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
    public List<Gamer> addGamersToGame(Long gameId, List<Gamer> gamers)
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

            for (Gamer gamer : gamers) {
                if (!isUserExists(gamer.getGamerName())) {
                    throw new UserNotFoundException(gamer.getGamerName());
                }

                GamerGame gamerGame = new GamerGame(gamer.getGamerName(), gameId);
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
    public boolean removeGamerFromGame(SessionToken gamerToken, Long gameId) throws GameNotFoundException, UserNotFoundException {
        // TODO Implement this method
        throw new UnsupportedOperationException("Method BullsCowsRepositoryImpl.removeGamerFromGame() not implemented yet");
    }

    @Override
    public List<Game> findGamesOfGamer(SessionToken gamerToken) throws UserNotFoundException
    {
        if (!isUserExists(gamerToken.toString())) {
            throw new UserNotFoundException(gamerToken.toString());
        }

        try (EntityManager em = getEntityManager()) {
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g JOIN GamerGame gg ON g.gameId = gg.gameId " +
                            "WHERE gg.gamerName = :gamerName",
                    Game.class);
            query.setParameter("gamerName", gamerToken.getUsername());
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
    public List<Game> findAllStartedGames(SessionToken gamerToken) throws UserNotFoundException
    {
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
    }

    @Override
    public List<Game> findAllNonStartedGames(SessionToken gamerToken)  throws UserNotFoundException
    {
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
    }

    @Override
    public List<Game> findAllJoinabledGames(SessionToken gamerToken) throws UserNotFoundException
    {
        if (!isUserExists(gamerToken.toString())) {
            throw new UserNotFoundException(gamerToken.toString());
        }

        try (EntityManager em = getEntityManager()){
            TypedQuery<Game> query = em.createQuery(
                    "SELECT g FROM Game g " +
                            "WHERE (g.startDateTime IS NULL OR g.startDateTime > :currentDateTime) " +
                            "AND g.id NOT IN (SELECT gg.gameId FROM GamerGame gg WHERE gg.gamerName != :gamerName)",
                    Game.class);
            query.setParameter("currentDateTime", LocalDateTime.now());
            query.setParameter("gamerName", gamerToken.getUsername());
            return query.getResultList();
        }
    }

    @Override
    public List<Game> findAllFinishedGames() {
        // TODO Implement this method
        throw new UnsupportedOperationException("Method BullsCowsRepositoryImpl.findAllFinishedGames() not implemented yet");
    }

    @Override
    public Long CreateGame(String secuence) {
        return 0L;
    }

    @Override
    public void startGame(Long gameId, SessionToken gamerToken) throws GameNotFoundException, UserNotFoundException {

    }

    @Override
    public Game findGameById(Long gameId) throws GameNotFoundException
    {
        try (EntityManager em = getEntityManager()) {
            return em.find(Game.class, gameId);
        }
    }
}

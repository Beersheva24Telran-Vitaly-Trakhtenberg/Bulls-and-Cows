package telran.game.bulls_cows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.TypedQuery;
import telran.game.bulls_cows.common.SessionToken;
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
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(g) FROM Gamer g WHERE g.gamer_name = :gamerName", Long.class);
            query.setParameter("gamerName", gamerName);
            long count = query.getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean isGamerInGame(SessionToken gamerToken, int gameId) throws UserNotFoundException
    {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g) FROM GamerGame g WHERE g.gamerName = :gamerName AND g.gameId = :gameId",
                    Long.class);
            long count = query.getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean isGameStarted(int gameId)
    {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Boolean> query = em.createQuery(
                    "SELECT CASE WHEN g.startDateTime IS NOT NULL AND g.startDateTime < :currentDateTime THEN TRUE ELSE FALSE END FROM Game g WHERE g.id = :gameId",
                    Boolean.class);
            query.setParameter("currentDateTime", LocalDateTime.now());
            query.setParameter("gameId", gameId);
            return query.getSingleResult();
        } finally {
            em.close();
        }

    }

    @Override
    public boolean isGameFinished(int gameId) {
        return false;
    }

    @Override
    public boolean isGameHasGamers(int gameId) {
        return false;
    }

    @Override
    public Gamer createGamer(String gamerName, LocalDate birthday) throws UserAlreadyExistsException {
        return null;
    }

    @Override
    public List<Gamer> getAllGamers() {
        return List.of();
    }

    @Override
    public List<Gamer> findAllGamersOfGame(int gameId) throws GameNotFoundException {
        return List.of();
    }

    @Override
    public List<Gamer> addGamersToGame(int gameId, List<Gamer> gamers) throws GameNotFoundException, UserNotFoundException {
        return List.of();
    }

    @Override
    public boolean removeGamerFromGame(SessionToken gamerToken, int gameId) throws GameNotFoundException, UserNotFoundException {
        return false;
    }

    @Override
    public List<Game> findGamesOfGamer(SessionToken gamerToken) throws UserNotFoundException {
        return List.of();
    }

    @Override
    public List<Game> getAllGames() {
        return List.of();
    }

    @Override
    public List<Game> findAllStartedGames() {
        return List.of();
    }

    @Override
    public List<Game> findAllFinishedGames() {
        return List.of();
    }

    @Override
    public int CreateGame(String secuence) {
        return 0;
    }

    @Override
    public Game findGameById(Long gameId) throws GameNotFoundException {
        EntityManager em = getEntityManager();
        try {
            return em.find(Game.class, gameId);
        } finally {
            em.close();
        }
    }

    public List<Game> findAllGames() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g", Game.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

package telran.game.bulls_cows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import telran.game.bulls_cows.common.settings.BullsCowsPersistenceUnitInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;
import telran.net.*;
import telran.view.InputOutput;
import telran.view.StandardInputOutput;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GameServer implements Runnable, Protocol
{
    //private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    static InputOutput io = new StandardInputOutput();
    private static EntityManager em;
    private static EntityManagerFactory emf;

    private static final int PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;
    private volatile BullsCowsServiceImpl service;

    private SessionToken user = null;

    public static void main(String[] args) throws IOException
    {
/*
        HashMap<String, Object> hibernateProperties = new HashMap<>();
        hibernateProperties.put("hibernate.hbm2ddl.auto","update");
        PersistenceUnitInfo persistenceUnit = new BullsCowsPersistenceUnitInfo();
        HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
        EntityManagerFactory emf =
                hibernatePersistenceProvider.createContainerEntityManagerFactory(persistenceUnit, hibernateProperties);
        em = emf.createEntityManager();
*/

        BullsCowsPersistenceUnitInfo persistenceUnitInfo = new BullsCowsPersistenceUnitInfo();
        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
        emf = provider.createContainerEntityManagerFactory(persistenceUnitInfo, null);

        //PersistenceProvider provider = new org.hibernate.jpa.HibernatePersistenceProvider();
        testConnection(emf);
        GameServer server = new GameServer();
        server.startServer();
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static int getPort()
    {
        return PORT;
    }

    private static void testConnection(EntityManagerFactory emf) {
        try {
            emf.createEntityManager().close();
            System.out.println("DB Connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DB Connection failed");
        }
    }

    private void testServer(String method) throws IOException
    {
        switch (method) {
            case "signUp":
                try {
                    SessionToken newUser = service.signUp("Vasya", "16-08-1971");
                    System.out.println(newUser.getToken());
                    user = newUser;
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (UserAlreadyExistsException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "logIn":
                try {
                    SessionToken existedUser = service.logIn("ViT");
                    System.out.println(existedUser);
                    user = existedUser;
                } catch (UserNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "createGame":
                try {
                    Long game_id = service.createGame(user);
                    System.out.println(game_id);
                } catch (AuthenticationException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("Wrong method");
        }
    }

    public void startServer() throws IOException
    {
        BullsCowsRepository repository = new BullsCowsRepositoryImpl(emf);
        service = new BullsCowsServiceImpl(repository);
        run();
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        try {
            TCPServer tcp_server = new TCPServer(this, this.getPort());
            tcp_server.run();
        } catch (Exception e) {
            System.out.println("Client closed connection abnormally");
            System.err.println(e.getMessage() + " \n " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public Response getResponse(Request request) {
        String requestType = request.requestType();
        String requestData = request.requestData();
        io.writeLine(requestType + " " + requestData + "\n");
        Response response = null;

        try {
            Method method = BullsCowsServiceImpl.class.getDeclaredMethod(requestType, String.class);
            method.setAccessible(true);
            var responseData = method.invoke(service, requestData);
            response = new Response(ResponseCode.SUCCESS, responseData.toString());
        } catch (NoSuchMethodException e) {
            response = new Response(ResponseCode.WRONG_REQUEST, requestType + " - Wrong type for Bulls&Cows Service");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UserNotFoundException) {
                response = new Response(ResponseCode.WRONG_DATA, cause.getMessage()); // Note: ResponseCode.UNAUTHORIZED is 401
            } else if (cause instanceof IllegalArgumentException) {
                response = new Response(ResponseCode.WRONG_DATA, cause.getMessage());
            } else {
                response = new Response(ResponseCode.WRONG_DATA, e.getMessage());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @Override
    public String getResponseWithJSON(String requestJSON) {
        return Protocol.super.getResponseWithJSON(requestJSON);
    }
}
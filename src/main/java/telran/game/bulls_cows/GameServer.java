package telran.game.bulls_cows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.json.JSONObject;
import telran.game.bulls_cows.common.settings.BullsCowsPersistenceUnitInfo;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceptions.*;
import telran.game.bulls_cows.repository.BullsCowsRepository;
import telran.game.bulls_cows.repository.BullsCowsRepositoryImpl;
import telran.game.bulls_cows.service.BullsCowsServiceImpl;
import telran.net.*;
import telran.view.InputOutput;
import telran.view.StandardInputOutput;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GameServer implements Runnable, Protocol
{
    //private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    protected static InputOutput io = new StandardInputOutput();
    private static EntityManager em;
    private static EntityManagerFactory emf;

    private static final int PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;
    private volatile BullsCowsServiceImpl service;

    private SessionToken user = null;

    public static void main(String[] args) throws IOException
    {
        BullsCowsPersistenceUnitInfo persistenceUnitInfo = new BullsCowsPersistenceUnitInfo();
        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
        emf = provider.createContainerEntityManagerFactory(persistenceUnitInfo, null);

        testDBConnection(emf);
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

    private static void testDBConnection(EntityManagerFactory emf)
    {
        try {
            emf.createEntityManager().close();
            io.writeLine("DB Connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            io.writeLine("DB Connection failed");
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
    public Response getResponse(Request request)
    {
        String requestType = request.requestType();
        String requestData = request.requestData();
        io.writeLine(requestType + " " + requestData + "\n");
        Response response = null;

        try {
            Method[] methods = BullsCowsServiceImpl.class.getDeclaredMethods();
            Method targetMethod = null;

            for (Method method : methods) {
                if (method.getName().equals(requestType)) {
                    targetMethod = method;
                    break;
                }
            }

            if (targetMethod == null) {
                throw new NoSuchMethodException(requestType);
            }

            JSONObject paramMap = new JSONObject(requestData);
            Object[] methodParams = new Object[]{paramMap.toMap()};

            targetMethod.setAccessible(true);
            Object responseData = targetMethod.invoke(service, methodParams);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("responseCode", ResponseCode.SUCCESS);
            jsonResponse.put("responseData", responseData.toString());

            response = new Response(ResponseCode.SUCCESS, jsonResponse.toString());
        } catch (NoSuchMethodException e) {
            response = new Response(ResponseCode.WRONG_REQUEST, requestType + " - Wrong type for Bulls&Cows Service");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            ResponseCode code;
            if (cause instanceof UserNotFoundException) {
                code = ResponseCode.UNAUTHORIZED;
            } else if (cause instanceof UserAlreadyExistsException) {
                code = ResponseCode.CONFLICT;
            } else if (cause instanceof IllegalArgumentException) {
                code = ResponseCode.WRONG_DATA;
            } else if (cause instanceof GameNotFoundException) {
                code = ResponseCode.WRONG_DATA;
            } else if (cause instanceof GameNotStartedException) {
                code = ResponseCode.WRONG_DATA;
            } else if (cause instanceof GameAlreadyStartedException) {
                code = ResponseCode.WRONG_DATA;
            } else if (cause instanceof GameAlreadyFinishedException) {
                code = ResponseCode.WRONG_DATA;
            }
            else {
                code = ResponseCode.INTERNAL_ERROR;
            }
            response = createErrorResponse(code, cause.getClass().getSimpleName(), cause.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            response = new Response(ResponseCode.WRONG_REQUEST, e.getMessage());
        } catch (Exception e) {
            response = new Response(ResponseCode.INTERNAL_ERROR, e.getMessage());
        }

        return response;
    }

    @Override
    public String getResponseWithJSON(String requestJSON) {
        return Protocol.super.getResponseWithJSON(requestJSON);
    }

    private Response createErrorResponse(ResponseCode code, String targetError, String targetMessage)
    {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("responseCode", targetError);
        jsonResponse.put("responseData", targetMessage);
        return new Response(code, jsonResponse.toString());
    }
}
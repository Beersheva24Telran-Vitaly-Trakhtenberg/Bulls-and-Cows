package telran.game.bulls_cows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer
{
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    static InputOutput io = new StandardInputOutput();
    static EntityManager em;

    private static final int PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;
    private static BullsCowsServiceImpl server = new BullsCowsServiceImpl();

    public static void main(String[] args) throws IOException
    {
        new GameServer().testServer("signUp");
    }

    private void testServer(String method) throws IOException
    {
        switch (method) {
            case "signUp":
                try {
                    SessionToken newUser = server.signUp("Vasya", "16-08-1971");
                    System.out.println(newUser.getToken());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (UserAlreadyExistsException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "logIn":
                try {
                    SessionToken existedUser = server.logIn("ViT");
                    System.out.println(existedUser);
                } catch (UserNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("Wrong method");
        }
    }

    public void startServer()
    {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Bulls & Cows Server started and waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread
    {
        private Socket clientSocket;

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
//
        }
    }
}
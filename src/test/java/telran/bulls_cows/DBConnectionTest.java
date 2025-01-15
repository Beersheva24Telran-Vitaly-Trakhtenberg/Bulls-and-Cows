package telran.bulls_cows;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceProvider;
import telran.game.bulls_cows.common.settings.BullsCowsPersistenceUnitInfo;

import java.util.HashMap;
import java.util.Map;

public class DBConnectionTest {
    public static void main(String[] args) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://%s:5432/postgres", System.getenv("POSTGRES_HOST")));
        properties.put("jakarta.persistence.jdbc.user", "postgres");
        properties.put("jakarta.persistence.jdbc.password", System.getenv("POSTGRES_PASSWORD"));
        properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        BullsCowsPersistenceUnitInfo persistenceUnitInfo = new BullsCowsPersistenceUnitInfo();
        PersistenceProvider provider = new org.hibernate.jpa.HibernatePersistenceProvider();

        EntityManagerFactory emf = provider.createContainerEntityManagerFactory(persistenceUnitInfo, null);

        testConnection(emf);
    }

    private static void testConnection(EntityManagerFactory emf) {
        try {
            emf.createEntityManager().close();
            System.out.println("Connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection failed");
        }
    }
}

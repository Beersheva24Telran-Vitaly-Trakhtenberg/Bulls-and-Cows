package telran.game.bulls_cows.common.settings;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

import telran.game.bulls_cows.models.Game;
import telran.game.bulls_cows.models.Gamer;
import telran.game.bulls_cows.models.GamerGame;
import telran.game.bulls_cows.models.GamerMoves;

public class BullsCowsPersistenceUnitInfo implements PersistenceUnitInfo{

    @Override
    public String getPersistenceUnitName() {
        return "bulls-cows-unit";
    }

    @Override
    public String getPersistenceProviderClassName() {
        return "org.hibernate.jpa.HibernatePersistenceProvider";
    }

    @Override
    public String getScopeAnnotationName() {
        return null;
    }

    @Override
    public List<String> getQualifierAnnotationNames() {
        return null;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getJtaDataSource() {
        HikariDataSource ds = new HikariDataSource();
        //PORT=4500;POSTGRES_HOST=44.201.172.20;POSTGRES_PASSWORD=12345.com
        String dbUrl = System.getenv("POSTGRES_HOST");
        String dbUsername = System.getenv("POSTGRES_USERNAME");
        String dbPassword = System.getenv("POSTGRES_PASSWORD");
        if (dbUrl == null || dbUrl.isEmpty()) {
            dbUrl = "jdbc:postgresql://localhost:5432/postgres";
        }
        if (dbUsername == null || dbUsername.isEmpty()) {
            dbUsername = "postgres";
        }
        if (dbPassword == null || dbPassword.isEmpty()) {
            dbPassword = "password";
        }

        ds.setJdbcUrl(String.format("jdbc:postgresql://%s:5432/postgres", dbUrl));
        ds.setPassword(dbPassword);
        ds.setUsername(dbUsername);

        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return null;
    }

    @Override
    public List<String> getMappingFileNames() {
        return null;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return null;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return List.of(Game.class.getName(), GamerGame.class.getName(),
                Gamer.class.getName(), GamerMoves.class.getName());

    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return null;
    }

    @Override
    public ValidationMode getValidationMode() {
        return null;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {

    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }

}
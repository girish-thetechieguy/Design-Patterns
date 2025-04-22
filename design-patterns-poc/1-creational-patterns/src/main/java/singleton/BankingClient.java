package singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
class DatabaseConnectionPool {
    private static Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);
    // Private static instance
    private static DatabaseConnectionPool instance;

    // Pool of connections
    private List<Connection> connections;
    private final int POOL_SIZE = 5;

    // Private constructor
    private DatabaseConnectionPool() {
        logger.info("Initializing database connection pool...");
        connections = new ArrayList<>();

        // Initialize connections
        for (int i = 0; i < POOL_SIZE; i++) {
            connections.add(createNewConnection());
        }
    }

    // Public method to get instance
    public static synchronized DatabaseConnectionPool getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionPool();
        }
        return instance;
    }

    // Create a new connection (mock implementation)
    private Connection createNewConnection() {
        return new Connection();
    }

    // Get a connection from the pool
    public synchronized Connection getConnection() {
        if (connections.isEmpty()) {
            logger.info("All connections in use, creating new one...");
            return createNewConnection();
        }
        return connections.remove(connections.size() - 1);
    }

    // Return a connection to the pool
    public synchronized void releaseConnection(Connection connection) {
        if (connections.size() < POOL_SIZE) {
            connections.add(connection);
        } else {
            logger.info("Closing extra connection...");
            connection.close();
        }
    }

    // Mock Connection class
    public class Connection {
        public void close() {
            logger.info("Closing database connection...");
        }

        public void executeQuery(String query) {
            logger.info("Executing query: {}", query);
        }
    }
}
public class BankingClient {
    private static Logger logger = LoggerFactory.getLogger(BankingClient.class);
    public static void main(String[] args) {
        // Get the singleton instance
        DatabaseConnectionPool pool = DatabaseConnectionPool.getInstance();

        // Get connections from the pool
        DatabaseConnectionPool.Connection conn1 = pool.getConnection();
        DatabaseConnectionPool.Connection conn2 = pool.getConnection();

        // Use connections
        conn1.executeQuery("SELECT * FROM accounts WHERE balance > 1000");
        conn2.executeQuery("UPDATE transactions SET status = 'processed'");

        // Return connections to pool
        pool.releaseConnection(conn1);
        pool.releaseConnection(conn2);

        // Verify it's the same instance
        DatabaseConnectionPool anotherPool = DatabaseConnectionPool.getInstance();
        logger.info("Same instance? {}", pool == anotherPool);
    }
}

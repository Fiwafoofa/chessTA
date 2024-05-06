package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
  private static final String DATABASE_NAME;
  private static final String USER;
  private static final String PASSWORD;
  private static final String CONNECTION_URL;

  private static final String AUTH_TABLE_CREATE_STATEMENT = """
      CREATE TABLE IF NOT EXISTS auth (
        `token` VARCHAR(36) PRIMARY KEY,
        `username` VARCHAR(20) NOT NULL
      );
      """;

  private static final String GAME_TABLE_CREATE_STATEMENT = """
      CREATE TABLE IF NOT EXISTS game (
        `gameID` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
        `gameName` VARCHAR(20) NOT NULL,
        `whiteUsername` VARCHAR(20),
        `blackUsername` VARCHAR(20),
        `chessGame` TEXT NOT NULL
      );
      """; // could also use JSON instead of TEXT

  private static final String USER_TABLE_CREATE_STATEMENT = """
      CREATE TABLE IF NOT EXISTS user (
        `username` VARCHAR(20) PRIMARY KEY,
        `password` VARCHAR(64) NOT NULL,
        `email` VARCHAR(20) NOT NULL
      );
      """;

  private static final String[] TABLE_CREATE_STATEMENTS = {
    AUTH_TABLE_CREATE_STATEMENT,
    GAME_TABLE_CREATE_STATEMENT,
    USER_TABLE_CREATE_STATEMENT
  };

  /*
   * Load the database information for the db.properties file.
   */
  static {
    try {
      try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
        if (propStream == null) throw new Exception("Unable to load db.properties");
        Properties props = new Properties();
        props.load(propStream);
        DATABASE_NAME = props.getProperty("db.name");
        USER = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
      }
    } catch (Exception ex) {
      throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
    }
  }

  /**
   * Creates the database if it does not already exist.
   */
  public static void createDatabase() throws DataAccessException {
    try {
      var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
      var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
      try (var preparedStatement = conn.prepareStatement(statement)) {
        preparedStatement.executeUpdate();
      }
      conn.close(); // added
      DatabaseManager.createTables(); // added
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  static void createTables() throws DataAccessException {
    Connection conn = DatabaseManager.getConnection();
    for (String tableCreateStatement: TABLE_CREATE_STATEMENTS) {
      try (PreparedStatement preparedStatement = conn.prepareStatement(tableCreateStatement)) {
        preparedStatement.executeUpdate();
      } catch (SQLException e) {
        throw new DataAccessException(e.getMessage());
      }
    }
  }

  /**
   * Create a connection to the database and sets the catalog based upon the
   * properties specified in db.properties. Connections to the database should
   * be short-lived, and you must close the connection when you are done with it.
   * The easiest way to do that is with a try-with-resource block.
   * <br/>
   * <code>
   * try (var conn = DbInfo.getConnection(databaseName)) {
   * // execute SQL statements.
   * }
   * </code>
   */
  public static Connection getConnection() throws DataAccessException {
    try {
      var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
      conn.setCatalog(DATABASE_NAME);
      return conn;
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }
}

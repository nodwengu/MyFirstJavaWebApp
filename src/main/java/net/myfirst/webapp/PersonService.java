package net.myfirst.webapp;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class PersonService {
   private final String JDBC_DRIVER = "org.postgresql";
   private final String DB_URL = "jdbc:postgresql:greet_db";
   //private final String DB_URL = "jdbc:postgresql://localhost:5432/greet_db";
   private final String USER = "coder";
   private final String PASS = "pg123";

   Connection conn = null;
   Statement stmt = null;

  
//   public void connectDb(String USER, String PASS, String dbUrl) {
//      jdbi = Jdbi.create(dbUrl, USER, PASS);
//      handle = jdbi.open();
//   }



   public Connection getDatabaseConnection(String jdbcDefaultUrl) throws URISyntaxException, SQLException {
      ProcessBuilder processBuilder = new ProcessBuilder();
      String database_url = processBuilder.environment().get("DATABASE_URL");
      System.out.println("\u001B[32m" + database_url + "\u001B[0m");
      if (database_url != null) {

         URI uri = new URI(database_url);
         String[] hostParts = uri.getUserInfo().split(":");
         String username = hostParts[0];
         String password = hostParts[1];
         String host = uri.getHost();
         int port = uri.getPort();
         String path = uri.getPath();
         String url = String.format("jdbc:postgresql://%s:%s%s", host, port, path);

         //personService.connectDb(url, username, password);
         connectToDatabase(url, username, password);
         return DriverManager.getConnection(url, username, password);
      }

      return DriverManager.getConnection(jdbcDefaultUrl);

   }

   public void connectToDatabase(String DB_URL, String USER, String PASS) {
      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         System.out.println("\u001B[32m" + "Connection established Successful! " + "\u001B[0m");
         System.out.println();

      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void closeConnectionAndStatement(Statement statement, Connection connection) {
      try {
         if(statement != null)
            statement.close();
         if(connection != null)
            connection.close();

      } catch (SQLException e) {
         System.out.println(e.getMessage());
         e.printStackTrace();
      }
   }





   public void add(Person person) {
      PreparedStatement preparedStatement = null;
      String sql = "INSERT INTO person (name, count) VALUES (?, ?)";

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         preparedStatement = conn.prepareStatement(sql);

         preparedStatement.setString(1, person.getName());
         preparedStatement.setInt(2, 1);
         preparedStatement.execute();

      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         closeConnectionAndStatement(stmt, conn);
      }
   }
//   public int getId(Person person) {
//      return handle.select("select id from person where name = :name")
//              .bind("name", person.getName())
//              .mapTo(int.class)
//              .findOnly();
//   }
   
   public Person getByName(String name) {
      PreparedStatement preparedStatement = null;
      Person person = null;

      String sql = "SELECT id, name, count FROM person WHERE name=?";

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);

         preparedStatement = conn.prepareStatement(sql);
         preparedStatement.setString(1, name);
         ResultSet resultSet = preparedStatement.executeQuery();

         while (resultSet.next()) {
            person = new Person();

            person.setName(resultSet.getString("name"));
            person.setCount(resultSet.getInt("count"));
            person.setId(resultSet.getInt("id"));
         }
      } catch (SQLException e ) {
         e.printStackTrace();
      } finally {
         closeConnectionAndStatement(stmt, conn);
      }

      return person;
   }

//   public Person getById(int id) {
//      return handle.select("select id, name, count from person where id = :id")
//              .bind("id", id)
//              .mapToBean(Person.class)
//              .findOnly();
//   }

   public List<Person> usersList() {
      List<Person> personList = new ArrayList<>();
      String sql = "SELECT id, name, count FROM person";

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         ResultSet resultSet = stmt.executeQuery(sql);

         while (resultSet.next()) {
            Person person = new Person();
            person.setName(resultSet.getString("name"));
            person.setCount(resultSet.getInt("count"));
            person.setId(resultSet.getInt("id"));
            personList.add(person);
         }
      } catch (SQLException e ) {
         e.printStackTrace();
      } finally {
         closeConnectionAndStatement(stmt, conn);
      }

      return personList;
   }
   
   public List<String> getUserNames() {
      List<String> namesList = new ArrayList<>();
      String sql = "SELECT id, name, count FROM person";

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         ResultSet resultSet = stmt.executeQuery(sql);

         while (resultSet.next()) {
            Person person = new Person();
            person.setName(resultSet.getString("name"));
            person.setCount(resultSet.getInt("count"));
            person.setId(resultSet.getInt("id"));
            namesList.add(person.getName());
         }
      } catch (SQLException e ) {
         e.printStackTrace();
      } finally {
         closeConnectionAndStatement(stmt, conn);
      }

      return namesList;
   }
//
//   public void updateCount(Person person) {
//      person.increaseCount();
//      handle.execute("UPDATE person SET count=? WHERE name=?", person.getCount(), person.getName());
//   }
//

   public void updateCount(Person person) {
      out.println("WORKING");
      PreparedStatement preparedStatement = null;

      String sql = "UPDATE person SET count=? WHERE name=?";

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         preparedStatement = conn.prepareStatement(sql);

         preparedStatement.setInt(1, person.getCount());
         preparedStatement.setString(2, person.getName());
         preparedStatement.execute();

      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         closeConnectionAndStatement(stmt, conn);
      }
   }

   public void clearDb() {
      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         String selectSql = "DELETE FROM person";
         stmt = conn.createStatement();
         stmt.execute(selectSql);

      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         closeConnectionAndStatement(stmt, conn);
      }
   }


}

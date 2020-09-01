package net.myfirst.webapp;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class PersonService {
   private final String JDBC_DRIVER = "org.postgresql";
   private final String DB_URL = "jdbc:postgresql://localhost:5432/greet_db";
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





//   public void closeConnection() {
//      handle.close();
//   }
//
//   public void add(Person person) {
//      handle.createUpdate("INSERT INTO person(name, count) VALUES(:name, :count)")
//           .bind("name", person.getName())
//           .bind("count", person.getCount())
//           .execute();
//   }
//
//   public int getId(Person person) {
//      return handle.select("select id from person where name = :name")
//              .bind("name", person.getName())
//              .mapTo(int.class)
//              .findOnly();
//   }
   
//   public Person getByName(String name) {
//      return handle.select("select id, name, count from person where name = :name")
//              .bind("name", name)
//              .mapToBean(Person.class)
//              .findOnly();
//   }
//
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
   
//   public List<String> getUserNames() {
//      return handle.createQuery("select name from person")
//              .mapTo(String.class)
//              .list();
//   }
//
//   public void updateCount(Person person) {
//      person.increaseCount();
//      handle.execute("UPDATE person SET count=? WHERE name=?", person.getCount(), person.getName());
//   }
//
//   public void clearDb() {
//      handle.createUpdate("DELETE FROM person").execute();
//   }
   

}

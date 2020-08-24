package net.myfirst.webapp;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static java.lang.System.out;



public class PersonService {
//   static int getHerokuAssignedPort() {
//      ProcessBuilder processBuilder = new ProcessBuilder();
//      if (processBuilder.environment().get("PORT") != null) {
//         return Integer.parseInt(processBuilder.environment().get("PORT"));
//      }
//      return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
//   }
//
   static Connection getDatabaseConnection(String defualtJdbcUrl) throws URISyntaxException, SQLException {
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

         return DriverManager.getConnection(url, username, password);

      }
      return DriverManager.getConnection(defualtJdbcUrl);
   }


   private final String dbDiskURL = "jdbc:postgresql://localhost:5432/greet_db";
   private final String USER = "coder";
   private final String PASS = "pg123";
           private Jdbi jdbi = null;
   private Handle handle = null;
  
   public void connectDb() {
      jdbi = Jdbi.create(dbDiskURL, USER, PASS);
      handle = jdbi.open();
   }
   
   public void closeConnection() {
      handle.close();
   }
   
   public void add(Person person) {
      handle.createUpdate("INSERT INTO person(name, count) VALUES(:name, :count)")
           .bind("name", person.getName())
           .bind("count", person.getCount())
           .execute();
   }
   
   public int getId(Person person) {
      return handle.select("select id from person where name = :name")
              .bind("name", person.getName())
              .mapTo(int.class)
              .findOnly();
   }
   
   public Person getByName(String name) {
      return handle.select("select id, name, count from person where name = :name")
              .bind("name", name)
              .mapToBean(Person.class)
              .findOnly();
   }
   
   public Person getById(int id) {
      return handle.select("select id, name, count from person where id = :id")
              .bind("id", id)
              .mapToBean(Person.class)
              .findOnly();
   }
   
   public List<Person> usersList() {
      return handle.createQuery("SELECT id, name, count FROM person")
           .mapToBean(Person.class)
           .list();
   }
   
   public List<String> getUserNames() {
      return handle.createQuery("select name from person")
              .mapTo(String.class)
              .list();
   }
   
   public void updateCount(Person person) {
      person.increaseCount();
      handle.execute("UPDATE person SET count=? WHERE name=?", person.getCount(), person.getName());
   }

   public void clearDb() {
      handle.createUpdate("DELETE FROM person").execute();
   }
   
   
}

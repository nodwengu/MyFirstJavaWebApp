package net.myfirst.webapp;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class PersonService {
   private final String JDBC_DRIVER = "org.postgresql";
//   private final String DB_URL = "jdbc:postgresql:greet_db";
//   //private final String DB_URL = "jdbc:postgresql://localhost:5432/greet_db";
//   private final String USER = "coder";
//   private final String PASS = "pg123";

   Connection connection = null;
   Statement stmt = null;

   public PersonService(Connection connection) {
      this.connection = connection;
   }

  
//   public void connectDb(String USER, String PASS, String dbUrl) {
//      jdbi = Jdbi.create(dbUrl, USER, PASS);
//      handle = jdbi.open();
//   }







//   public void connectToDatabase(String DB_URL, String USER, String PASS) {
//      try {
//         connection = DriverManager.getConnection(DB_URL, USER, PASS);
//         System.out.println("\u001B[32m" + "Connection established Successful! " + "\u001B[0m");
//         System.out.println();
//
//      } catch (SQLException e) {
//         e.printStackTrace();
//      }
//   }
//
//   public void closeConnectionAndStatement(Statement statement, Connection connection) {
//      try {
//         if(statement != null)
//            statement.close();
//         if(connection != null)
//            connection.close();
//
//      } catch (SQLException e) {
//         System.out.println(e.getMessage());
//         e.printStackTrace();
//      }
//   }





   public void add(Person person) {
      String sql = "INSERT INTO person (name, count) VALUES (?, ?)";

      try {
         PreparedStatement preparedStatement = connection.prepareStatement(sql);

         preparedStatement.setString(1, person.getName());
         preparedStatement.setInt(2, 1);
         preparedStatement.execute();

      } catch (SQLException e) {
         e.printStackTrace();
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

         preparedStatement = connection.prepareStatement(sql);
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
         stmt = connection.createStatement();
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
      }

      return personList;
   }
   
   public List<String> getUserNames() {
      List<String> namesList = new ArrayList<>();
      String sql = "SELECT id, name, count FROM person";

      try {
         stmt = connection.createStatement();
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
//         connection = DriverManager.getConnection(DB_URL, USER, PASS);
         preparedStatement = connection.prepareStatement(sql);

         preparedStatement.setInt(1, person.getCount());
         preparedStatement.setString(2, person.getName());
         preparedStatement.execute();

      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void clearDb() {
      try {
 //        connection = DriverManager.getConnection(DB_URL, USER, PASS);
         String selectSql = "DELETE FROM person";
         stmt = connection.createStatement();
         stmt.execute(selectSql);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }


}

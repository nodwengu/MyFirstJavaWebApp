package net.myfirst.webapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.out;

public class PersonService {
   Connection connection = null;
   Statement stmt = null;

   public PersonService(Connection connection) {
      this.connection = connection;
   }

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
   
   public Person getByName(String name) {
      Person person = null;
      String sql = "SELECT id, name, count FROM person WHERE name=?";

      try {
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
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

   public void updateCount(Person person) {
      String sql = "UPDATE person SET count=? WHERE name=?";

      try {
         person.increaseCount();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setInt(1, person.getCount());
         preparedStatement.setString(2, person.getName());
         preparedStatement.execute();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void updateName(Person person, String newName) {
      String sql = "UPDATE person SET name=? WHERE name=?";

      try {
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setString(1, newName);
         preparedStatement.setString(2, person.getName());
         preparedStatement.execute();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void delete(String name) {
      String sql = "DELETE FROM person WHERE name = ?";

      try {
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setString(1, name);
         preparedStatement.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void clearDb() {
      try {
         String selectSql = "DELETE FROM person";
         stmt = connection.createStatement();
         stmt.execute(selectSql);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

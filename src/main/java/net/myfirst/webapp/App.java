package net.myfirst.webapp;

import net.myfirst.webapp.exceptions.InputRequiredException;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

import static java.lang.System.out;

public class App {
   private static String errorMsg = null;
   private static String showError = "hide";
   private static PersonService personService;

   static int getHerokuAssignedPort() {
      ProcessBuilder processBuilder = new ProcessBuilder();
      if (processBuilder.environment().get("PORT") != null) {
         return Integer.parseInt(processBuilder.environment().get("PORT"));
      }
      return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
   }

   public static Connection getDatabaseConnection(String jdbcDefaultUrl) throws URISyntaxException, SQLException {
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

      return DriverManager.getConnection(jdbcDefaultUrl);
   }

   public static void main(String[] args) {
      staticFiles.location("/public");
      List<String> users = new ArrayList<>();
      port(getHerokuAssignedPort());

      try {
         Connection connection = getDatabaseConnection("jdbc:postgresql://localhost:5432/greet_db?user=thando&password=thando123");
         personService = new PersonService(connection);

      }  catch (SQLException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }

      get("/", (req, res) -> {
         res.redirect("/hello");
         return null;
      });

      get("/hello", (req, res) -> {
         Map<String, Object> map = new HashMap<>();
         List<Person> usersList = new ArrayList<>();
         usersList = personService.usersList();

         map.put("usersList", usersList);
         map.put("greetedCounter", usersList.size());

         return new ModelAndView(map, "hello.handlebars");
      }, new HandlebarsTemplateEngine());

      get("/greeted/:name", (req, res) -> {
         Map<String, Object> map = new HashMap<>();
         Person user = personService.getByName(req.params("name"));
         int count = user.getCount();

         map.put("name", req.params("name"));
         map.put("count", count);

         return new ModelAndView(map, "greeted.handlebars");
      }, new HandlebarsTemplateEngine());

      post("/hello", (req, res) -> {
         Map<String, Object> map = new HashMap<>();
         String greeting = null;

         try {
            String name = req.queryParams("name");
            String language = req.queryParams("language");

            if (language == null || name.isEmpty()) {
               throw new InputRequiredException("Name and Language are required");
            } else {
               name = name.substring(0, 1).toUpperCase() + name.substring(1);
               greeting = App.greeting(language, name);
               List<String> names = personService.getUserNames();
               Person user = new Person();
               user.setName(name);
               user.setCount(1);

               if (!names.contains(name)) {
                  personService.add(user);
               } else {
                  Person person = personService.getByName(name);
                  personService.updateCount(person);
               }
               showError = "hide";
            }
         } catch (InputRequiredException e) {
            out.println("\u001B[32m" + e.getMessage() + "\u001B[0m");
            errorMsg = e.getMessage();
            showError = "show";
         }

         List<Person> usersList = personService.usersList();

         map.put("greeting", greeting);
         map.put("usersList", usersList);
         map.put("greetedCounter", usersList.size());
         map.put("errorMsg", errorMsg);
         map.put("showError", showError);

         return new ModelAndView(map, "hello.handlebars");

      }, new HandlebarsTemplateEngine());

      get("/reset", (req, res) -> {
         personService.clearDb();
         res.redirect("/hello");
         return null;
      });

      get("/delete/:name", (req, res) -> {
         personService.delete(req.params("name"));
         out.println("User has been removed...");
         res.redirect("/hello");
         return null;
      });

      post("/update/:name", (req, res) -> {
         Person person = personService.getByName(req.params("name"));
         personService.updateName( person, req.queryParams("theName") );
         out.println("User has been updated...");
         res.redirect("/hello");
         return null;
      });
   }


   static String greeting(String language, String name){
      String greeting = null;
      String newName = name.toLowerCase();
      newName = name.substring(0, 1).toUpperCase() + name.substring(1);

      switch (language) {
         case "English":
            greeting = "Hello, " + newName;
            break;
         case "Xhosa":
            greeting = "Mholo, " + newName;
            break;
         case "Zulu":
            greeting = "Sawubona, " + newName;
            break;
         default:
            greeting = "Select Radio button";
      }

      return greeting;
   }
}

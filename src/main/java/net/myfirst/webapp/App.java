package net.myfirst.webapp;

import net.myfirst.webapp.exceptions.InputRequiredException;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

import static java.lang.System.out;

public class App {
   private static Person user = null;
   private static String errorMsg = null;
   private static String showError = "hide";
   
   static int getHerokuAssignedPort() {
      ProcessBuilder processBuilder = new ProcessBuilder();
      if (processBuilder.environment().get("PORT") != null) {
         return Integer.parseInt(processBuilder.environment().get("PORT"));
      }
      return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
   }
   
   public static void main(String[] args) {
      port(getHerokuAssignedPort());
      staticFiles.location("/public");
      
      List<String> users = new ArrayList<>();

      PersonService personService = new PersonService();
      personService.connectDb();
      
      get("/", (req, res) -> {
         res.redirect("/hello");
         return null;
      });
   
      get("/hello", (req, res) -> {
         personService.connectDb();
         List<Person> usersList = new ArrayList<>();
         usersList = personService.usersList();
        
         Map<String, Object> map = new HashMap<>();
         map.put("usersList", usersList);
         map.put("greetedCounter", usersList.size());
         
         personService.closeConnection();
         return new ModelAndView(map, "hello.handlebars");
      }, new HandlebarsTemplateEngine());
   
      get("/greeted/:name", (req, res) -> {
         Person user = null;
         personService.connectDb();
         Map<String, Object> map = new HashMap<>();
         map.put("name", req.params("name"));
         
         user = personService.getByName(req.params("name"));
         int count = user.getCount();
         map.put("count", count);
   
         personService.closeConnection();
         return new ModelAndView(map, "greeted.handlebars");
      }, new HandlebarsTemplateEngine());
   
      post("/hello", (req, res) -> {
         personService.connectDb();
         Map<String, Object> map = new HashMap<>();
         String language = req.queryParams("language");
         String name = req.queryParams("name");

         Person person = null;
         String greeting = null;

         try {
            greeting = App.greeting(language, name);
            List<String> names = personService.getUserNames();

            if (!names.contains(name)) {
               personService.add(user);
            } else {
               person = personService.getByName(name);
               personService.updateCount(person);
            }
            showError = "hide";

         } catch (InputRequiredException e) {
            errorMsg = e.getMessage();
            showError = "show";
         }

         List<Person> usersList = personService.usersList();

         map.put("greeting", greeting);
         map.put("usersList", usersList);
         map.put("greetedCounter", usersList.size());
         map.put("errorMsg", errorMsg);
         map.put("showError", showError);

         personService.closeConnection();

         return new ModelAndView(map, "hello.handlebars");
      
      }, new HandlebarsTemplateEngine());
   
      get("/reset", (req, res) -> {
         personService.connectDb();
         personService.clearDb();
         res.redirect("/hello");
         personService.closeConnection();
         return null;
      });
   }
   
   static String greeting(String language, String name) throws InputRequiredException{
      String greeting = null;

      if ( language == null || name.isEmpty() ) {
         throw new InputRequiredException("Name and Language are required");

      } else {
         String newName = name.toLowerCase();
         newName = name.substring(0, 1).toUpperCase() + name.substring(1);

         user = new Person();
         user.setName(newName);
         user.setCount(1);

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
      }
      return greeting;
   }
}

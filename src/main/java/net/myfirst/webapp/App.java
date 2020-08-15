package net.myfirst.webapp;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

import static java.lang.System.out;

public class App {
   static  String name = null;
   
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
         Person user = null;
         String language = req.queryParams("language");
         name = req.queryParams("name").toLowerCase();
         name = name.substring(0, 1).toUpperCase() + name.substring(1);
   
         user = new Person();
         user.setName(name);
         user.setCount(1);
         
         String greeting = App.greeting(language, user.getName());
         
         List<String> names = personService.getUserNames();
         
         if (!names.contains(name)) {
            personService.add(user);

         } else {
            Person person = null;
            person = personService.getByName(name);
            personService.updateCount(person);
         }
         List<Person> usersList = personService.usersList();
         
         map.put("greeting", greeting);
         map.put("usersList", usersList);
         map.put("greetedCounter", usersList.size());
   
         personService.closeConnection();
         //res.redirect("/hello");
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
   
   static String greeting(String language, String name) {
      String greeting = null;
  
      switch (language) {
         case "English":
            greeting = "Hello, " + name;
            break;
         case "Xhosa":
            greeting = "Mholo, " + name;
            break;
         case "Zulu":
            greeting = "Sawubona, " + name;
            break;
         default:
            greeting = "Select Radio button";
      }
     
      return greeting;
   }
}

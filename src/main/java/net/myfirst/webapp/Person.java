package net.myfirst.webapp;

public class Person {
   private String name;
   private int count;
   private int id;
   
   public void setCount(int greetCount) {
      this.count = greetCount;
   }
   
   public void setId(int id) {
      this.id = id;
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public int getCount() {
      return count;
   }
   
   public int getId() {
      return id;
   }
   
   public void increaseCount() {
      count++;
   }
   
   @Override
   public String toString() {
      return "Person{" +
              "first_name='" + name + '\'' +
              ", count=" + count +
              ", id=" + id +
              '}';
   }
}

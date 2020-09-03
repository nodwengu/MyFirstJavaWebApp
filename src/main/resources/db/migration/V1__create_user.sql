create table person(
    id serial not null primary key,
    name text,
    count int
);

  try {
         Class.forName(JDBC_DRIVER);
      } catch (ClassNotFoundException e) {
         System.out.println("H2 JDBC Driver not found !!");
      }
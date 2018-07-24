# Book My Flight-System
Book My Flight (BMF) is a flight reservation service where customers can book domestic flights within India, according to their needs.The aim of this case study is to design and develop a database management system for flight booking.

## Getting Started
In order to better understand the problem, go through the problem statement which can be found here [Probem Statement for this project](docs/Probem_Statement.docx) and ER-diagram [ER-diagram for this project](docs/ER-diagram)


### Prerequisites
Following are the softwares use in this project along with their respective version (**Strictly use the versions mentioned below to run the project successfully**)

1. MySQl Server version: **5.7.18 MySQL Community Server (GPL)**
2. Java Version : **javac 1.8.0_131**
3. Eclipse IDE : **Luna and above (my version: Eclipse Oxygen.3a Release (4.7.3a))**

### Installing the project
In the source folder, there are 4 files namely
1. **SQL files**: tables.sql, triggers.sql, test_queries.sql
2. **Java files**: frontend.java, mysql-connector-java-5.1.29-bin.jar

**Step-1**: Set up MySQL on your computer. You can refer to tutorials on youtube for this purpose.
**Step-2**: Clone this repository on computer.
**Step-3**: Open MySQL command line in terminal (mac/linux users) / command propmt (windows users), use the following snippet to set up the database:
```
create database bmf;
use bmf;
source "path to the sql file"
```
 "path to the sql file" with actual path to your files (without quotes)
 **Example (mac /linux users)**
```
source ~/Desktop/Book-My-Flight-System/src/tables.sql
```
Follow this order strictly
1. tables.sql
2. triggers.sql
3. testing_queries.sql

**Step-4**: Open Eclipse IDE and create a new JAVA project and name it "Book-My-Flight-System" (without quotes).

**Step-5**: Create a new package inside source class and name it "frontend" (without quotes).

**Step-6**: Create a new class inside "frontend" package and name it "BMF" (without quotes).

**Step-7**: Copy and paste the entire code in frontend.java, of this repository onto the BMF.java file and change this snippet
```
mycon = DriverManager.getConnection("jdbc:mysql://localhost:3306/bmf?autoReconnect=true&useSSL=false",
					"root", "yourpassword");
```
replace ***yourpassword*** with ***your actual password***, **change root only if you are using some othe user with all privileges**.

**Step-8**: Create new folder "lib" inside the project directory, same level as "src" (without quotes).

**Step-9**: Copy and paste mysql-connector-java-5.1.29-bin.jar file in lib folder.

**Step-10**: Right click on project folder, select
**properties->java build path->libraries->Add Jars->Book-My-Flight-System->lib->mysql-connector-java-5.1.29-bin.jar**
        , apply and close.

**Step-11**: Now the project is all set up and can be run tested.



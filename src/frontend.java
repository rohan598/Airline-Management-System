package frontend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BMF {
	
	static Scanner s = new Scanner(System.in);
	static Connection mycon;
	static Statement mystat;
	static String userId = null;
	static PreparedStatement myStmt;
	public static void main(String[] args) {

		try {
			mycon = DriverManager.getConnection("jdbc:mysql://localhost:3306/bmf?autoReconnect=true&useSSL=false",
					"root", "yourpassword");
			mystat = mycon.createStatement();
		} catch (Exception e) {
			System.out.println("Server can't connect. Please check your authorizations.");
		}

		System.out.println("Welcome to Book My Trip");
		userenter();

		char ans = 'y';
		int ch = 0;
		do {

			System.out.println(
					"Menu:\n1.Search and Book flight\n2.Search for my previous bookings\n3.Search for my previous cancellations\n4.Log Out");
			System.out.print("Enter Choice : ");
			ch = s.nextInt();

			switch (ch) {
			case 1:
				flightinfo();// booking of tickets within this function
				break;
			case 2:
				searchbook();
				break;
			case 3:
				searchCancel();
				break;
			case 4:
				userId = null;
				System.out.println("Logged Out.");
				userenter();
				break;
			default:
				System.out.println("Wrong Choice. Please try again");
			}

			System.out.print("Press y to continue :");
			ans = s.next().charAt(0);
		} while (ans == 'Y' || ans == 'y');

	}
private static void searchbook() {
		
		ResultSet myRs;
		try {
			
			String q = "select ticket.id as tid,book_date,cost,departure_date,departure_time from booked,ticket,flight,ticket_of where user_id='"+userId+"' and booked.ticket_id=ticket.id and ticket_of.ticket_id=ticket.id and ticket_of.flight_id=flight.id;";
			myRs = mystat.executeQuery(q);
			System.out.println("All the booked tickets are:");
			while (myRs.next()) {
				System.out.println("ticketId:" + myRs.getString("tid") + " Book_Date: "+ myRs.getString("book_date") + " Cost: " + myRs.getString("cost")+ "departure date: " + myRs.getString("departure_date")+ " departure time: " + myRs.getString("departure_time"));
			}

		} catch (SQLException e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}
		
		System.out.print("Do you wanna cancel some ticket yet scheduled?(y/n): ");
		char ch=s.next().charAt(0);
		if(ch=='n'||ch=='N')return;
		
		System.out.print("Enter Ticket ID you wanna cancel: ");
		int can=s.nextInt();
		
		try {
			mystat.executeUpdate("delete from booked where ticket_id="+can+";");
			myRs=mystat.executeQuery("select refund from cancelled where ticket_id="+can+";");
		    String refund="0";
			while(myRs.next()){
				refund=myRs.getString("refund");
			}
			System.out.println("Your refund is: "+refund);
			
		} catch (SQLException e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}
		
	}

	private static void searchCancel() {
		
		ResultSet myRs;
		try {
			
			String q = "select ticket.id as tid,refund,book_date,departure_date,departure_time,cancelled_at from cancelled,ticket,flight,ticket_of where user_id='"+userId+"' and cancelled.ticket_id=ticket.id and ticket_of.ticket_id=ticket.id and ticket_of.flight_id=flight.id;";
			myRs = mystat.executeQuery(q);
			System.out.println("All the cancelled tickets are:");
			while (myRs.next()) {
				System.out.println("ticketId:" + myRs.getString("tid") + " Book_Date: "+ myRs.getString("book_date") + "departure date: " + myRs.getString("departure_date")+ " departure time: " + myRs.getString("departure_time")+ " cancellation time: " + myRs.getString("cancelled_at")+ " refund: " + myRs.getString("refund"));
			}

		} catch (SQLException e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}
	}

	private static int infantFn() {
		System.out.println("Enter the number of infants(max allowed 2)");
		int noOfInfants = s.nextInt();
		if (noOfInfants > 2) {
			System.out.println("Sorry but max 2 infants are allowed.");
			noOfInfants = infantFn();
		}
		return noOfInfants;
	}

	private static int seatsFn(String FID,char type) {
		
		
		System.out.println("Enter the number of passengers (does not include infants < 2years): ");
		int noOfPassengers = s.nextInt();
        int seats_available=0;
		if (type == 'e'|| type=='E') {
			
			try {
				String q = "select e_seats from flight where id='"+FID+"';";// this will fetch number of economy seats
				ResultSet myRs = mystat.executeQuery(q);
				while(myRs.next()){
				seats_available = myRs.getInt("e_seats");
				}
				if (seats_available < noOfPassengers) {
					System.out.println("Not enough seats are availble");
					System.out.println("Please re-enter the seats:");
					return seatsFn(FID,type);
				}
			} catch (Exception e) {
				System.out.println("Cannot execute query");
				e.printStackTrace();
			}
		} else if (type == 'b'||type=='B') {
			
			try {
				String q = "select b_seats from flight where id='"+FID+"';";// this will fetch number of business seats
				ResultSet myRs = mystat.executeQuery(q);
				while(myRs.next()){
					seats_available = myRs.getInt("b_seats");
					}
				if (seats_available < noOfPassengers) {
					System.out.println("Those much seats are not availble");
					System.out.println("Please re-enter the seats");
					return seatsFn(FID,type);
				}
			} catch (Exception e) {
				System.out.println("Cannot execute query");
				e.printStackTrace();
			}
		} else if (type == 'f'||type=='F') {
			
			try {
				String q = "select f_seats from flight where id='"+FID+"';";// this will fetch number of first seats
				ResultSet myRs = mystat.executeQuery(q);
				while(myRs.next()){
					seats_available = myRs.getInt("f_seats");
					}
				if (seats_available < noOfPassengers) {
					System.out.println("Those much seats are not availble");
					System.out.println("Please re-enter the seats");
					return seatsFn(FID,type);
				}
			} catch (Exception e) {
				System.out.println("Cannot execute query");
				e.printStackTrace();
			}
		}
		return noOfPassengers;
	}

	private static void bookTickets(String flightId, char type) {
		
		
		int noOfPassengers = seatsFn(flightId,type);

        
		
		int ticketPrice = 0;
		ResultSet myRs;
		
		for (int i = 0; i < noOfPassengers; i++) {
			
			System.out.println("Details for passenger "+ (i+1)+":");

			System.out.print("Enter the gender[M/F/O]: ");
			char gender = s.next().charAt(0);

			System.out.print("Enter the firstname: ");
			String fname = s.next();

			System.out.print("Enter the lastname");
			String lname = s.next();
			
			System.out.print("Enter the date of birth");
			String dob = s.next();

			System.out.print("Enter the food preference veg or non-veg [V/N]");
			char food = s.next().charAt(0);
			int infant = infantFn();
			
			try {
				mystat.executeUpdate("insert into ticket(user_id) values('"+userId+"');");
				myRs=mystat.executeQuery("select max(id) as num from ticket;");
				int id=0;
				while(myRs.next()){
					id = myRs.getInt("num");
				}
				mystat.executeUpdate("insert into ticket_of(ticket_id,flight_id,seat_type) values("+id+",'"+flightId+"','"+type+"');");
				mystat.executeUpdate("insert into booked(ticket_id) values("+id+");");
				mystat.executeUpdate("insert into passenger values("+id+",'"+fname+"','"+lname+"','"+gender+"','"+dob+"','"+food+"');");
			} catch (SQLException e) {
				System.out.println("Cannot execute query");
				e.printStackTrace();
				return;
			}
			
			System.out.println("Successful Booking.");

		}
		
	}

	private static void flightinfo() {
		
          destdisp();
          
        System.out.println("Enter the source of flight");
  		String src = s.next();
  		System.out.println("Enter the destination of flight");
  		String dst = s.next();
  		System.out.println("Enter the departure date of flight");
  		String date = s.next();
  		
  		
  		ResultSet myRs;
		System.out.println("Available flights are:");
		try {
			String query = "select flight.id as fid,airline.name,departure_time,arrival_date,arrival_time,e_cost,b_cost,f_cost,e_seats,b_seats,f_seats from flight,destination,airplane,airline where flight.airplane_id=airplane.id and airplane.airline_id=airline.id and concat(flight.departure_date,' ',flight.departure_time)>current_timestamp";
			myRs = mystat.executeQuery(query);
			int ctr=1;
			while (myRs.next()) {
				System.out.println(ctr+". Flight ID:"+myRs.getString("fid") + " Airline:" + myRs.getString("name")+" Dep Time:"+ myRs.getString("departure_time")+" Arrival Time:"+ myRs.getString("arrival_time")+" Arrival Date:" +myRs.getString("arrival_date")+" E-seats-rem: "+myRs.getString("e_seats")+" E-cost: "+myRs.getString("e_cost")+" B-seats-rem: "+myRs.getString("b_seats")+" B-cost: "+myRs.getString("b_cost")+" F-seats-rem: "+myRs.getString("f_seats")+" F-cost: "+myRs.getString("f_cost"));
			    ctr++;
			}
		} catch (Exception e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}
  		
  	    System.out.print("Do you want to book a flight?(y/n): ");
  	    char ch=s.next().charAt(0);
  	    if(ch!='y'&&ch!='Y')return;
  	    
  	    System.out.print("Enter flight ID you wanna book : ");
  	    String FID=s.next();
  	    
  	    System.out.print("Please enter the type of ticket[E/B/F]");
		char type = s.next().charAt(0);
		
		bookTickets(FID,type);
          
	}

	private static void destdisp() {
		ResultSet myRs;
		System.out.println("Available Destinations and codes are:");
		try {
			String query = "select * from destination";
			myRs = mystat.executeQuery(query);
			while (myRs.next()) {
				System.out.println(myRs.getString("dest_name") + " " + myRs.getString("dest_code"));
			}
		} catch (Exception e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}
		
	}

	public static void userenter() {
		userId = null;
		do {
			System.out.println("Menu:\n1.User Login\n2.User Signup\n3.Exit");

			System.out.print("Enter Choice : ");
			int ch = s.nextInt();

			if (ch == 1)
				userId=userlogin();
			else if (ch == 2)
				userId=usersignup();
			else if (ch == 3) {
				System.out.println("Thankyou for stopping by!");
				System.exit(0);
			} else {
				System.out.println("Wrong choice. Please retry.");
			}

		} while (userId == null);
	}

	private static String usersignup() {
		System.out.print("Please enter new userID (max length 15) : ");
		String userId = s.next();
		ResultSet myRs;
		try {
			String sql = "select count(*) as present from user where id=?";
			myStmt = mycon.prepareStatement(sql);
			myStmt.setString(1, userId);

			myRs = myStmt.executeQuery();
			// System.out.println(myRs.getString("present"));

			while (myRs.next()) {
				if (Integer.parseInt(myRs.getString("present")) != 0) {
					System.out.println("Sorry! User ID already exists. Please retry for signup.");
					return null;
				}
			}

			System.out.print("Please enter password (min length 10) : ");
			String password = s.next();

			System.out.print("Please enter first name ");
			String firstName = s.next();

			System.out.print("Please enter last name ");
			String lastName = s.next();

			System.out.print("Please enter date of birth (format: yyyy-mm-dd) together ");
			String date = s.next();
			
			System.out.print("Please enter gender ('M'/'F'/'O') in capital ");
			String gender = s.next();
			
			String sqln = "insert into user " + "(id,fname,lname,gender,dob,password) " + "values(?,?,?,?,?,?)";
			myStmt = mycon.prepareStatement(sqln);
			myStmt.setString(1, userId);
			myStmt.setString(2, firstName);
			myStmt.setString(3, lastName);
			myStmt.setString(4, gender);
			myStmt.setString(5, date);
			myStmt.setString(6, password);
			myStmt.execute();
            System.out.println("User successfully created.");
		} catch (SQLException e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}

		return userId;
	}

	private static String userlogin() {
		System.out.print("Please enter your user ID : ");
		String userId = s.next();
		System.out.println("Please enter password (min length 10)");
		String password = s.next();

		ResultSet myRs;
		try {
			String sql = "select password from user where id =?";
			myStmt = mycon.prepareStatement(sql);
			myStmt.setString(1, userId);
			myRs = myStmt.executeQuery();
			while (myRs.next()) {
				if (!myRs.getString("password").equals(password)) {
					System.out.println("Sorry! Incorrect User Id or password. Please retry for login.");
					return null;
				}
			}

		} catch (SQLException e) {
			System.out.println("Cannot execute query");
			e.printStackTrace();
		}
		System.out.println("signed in");
		
		return userId;

	}

	
}

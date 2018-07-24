-- DATA QUERIES --

insert into user(id,fname,lname,gender,dob,password)
values('rohan3','rohan','wadhawan','M','1985-08-11','password');

insert into airline
values('VI','Vistara');

insert into airplane
values('BOEING737','VI',50,20,5);

insert into destination
values('BOM','Mumbai','Chhatrapati Shivaji International Airport');
insert into destination
values('NWD','New Delhi','Indira Gandhi International Airport, Terminal-3');

insert into flight(id,airplane_id,from_dest_code,to_dest_code,departure_date,departure_time,arrival_date,arrival_time,e_cost,b_cost,f_cost)
values('VI779','BOEING737','BOM','NWD','2018-10-23','17:45:00','2018-10-23','21:15:00','20000','55000','100000');

 insert into ticket(id,user_id) values('1','rohan3');
  insert into ticket_of values('VI779','1','E');
  insert into booked(ticket_id) values('1');
  insert into passenger (ticket_id,fname,lname,gender,dob,food_preferences)
values('1','rohan','wadhawan','M','1985-08-11','V');

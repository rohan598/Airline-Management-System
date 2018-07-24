-- Tables --

create table user( 
id varchar(100) primary key,
fname varchar(20) not null,
lname varchar(20) not null,
gender char(1) not null,
dob date not null,
password varchar(100) not null 
);


create table airline( 
id char(2) primary key,
name varchar(20) not null
);

create table airplane( 
id varchar(15) primary key,
airline_id char(2) not null,
e_capacity int(11) not null,
b_capacity int(11) not null,
f_capacity int(11) not null,
foreign key(airline_id) references airline(id)
);


create table destination( 
dest_code char(4) primary key,
dest_name varchar(50) not null,
airport_name varchar(50) not null
);


create table flight(
id char(10) primary key,
airplane_id varchar(15) not null,
from_dest_code char(4) not null,
to_dest_code char(4) not null,
departure_date date not null,
departure_time time not null,
arrival_date date not null,
arrival_time time not null,
e_seats int(11) DEFAULT 0, 
b_seats int(11) DEFAULT 0, 
f_seats int(11) DEFAULT 0, 
e_cost numeric(9,2) not null,
b_cost numeric(9,2) not null,
f_cost numeric(9,2) not null,
foreign key (airplane_id) references airplane(id),
foreign key(from_dest_code) references destination(dest_code),
foreign key(to_dest_code) references destination(dest_code)
);

create table ticket( 
id varchar(100) primary key, 
book_date timestamp not null default current_timestamp,
user_id varchar(100),
foreign key(user_id) references user(id)
);

create table ticket_of(
flight_id char(5),
ticket_id varchar(100), 
seat_type char(1) not null, 
primary key(flight_id,ticket_id),
foreign key(flight_id) references flight(id),
foreign key(ticket_id) references ticket(id)
);



create table booked( 
ticket_id varchar(100) primary key, 
status tinyint(1) NOT NULL DEFAULT '1',
cost numeric(9,2) default 0.00, 
foreign key(ticket_id) references ticket(id) ON DELETE CASCADE
);



create table cancelled(
ticket_id varchar(100) primary key, 
cancelled_at timestamp not null default CURRENT_TIMESTAMP(),
refund numeric(9,2) not null default 0.00, 
foreign key(ticket_id) references ticket(id) ON DELETE CASCADE
);


create table passenger( 
ticket_id varchar(100) not null unique,
fname varchar(20) not null,
lname varchar(20) not null,
gender char(1) not null,
dob date not null,
food_preferences char,
primary key(ticket_id,fname,lname,dob),
foreign key(ticket_id) references ticket(id)
);

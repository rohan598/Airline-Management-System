-- Triggers --

-- trigger for checking user's info -- checked

delimiter $$
create trigger check_info_user before insert on user -- checked
for each row begin 
    if (new.gender !='M' and new.gender !='F' and new.gender !='O') then
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "insertion violation in user due to gender mismatch";
    elseif(new.dob > curdate()) then
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = "insertion violation in user due to dob mismatch";
    end if;
end;
$$
delimiter ;

-- trigger for setting initial value for flight seats -- checked

delimiter $$
create trigger check_flight_seats before insert on flight
    for each row begin
    
        set new.e_seats = ( select e_capacity
                            from airplane
                            where airplane.id = new.airplane_id );
        set new.b_seats = ( select b_capacity
                            from airplane
                            where airplane.id = new.airplane_id );
        set new.f_seats = ( select f_capacity
                            from airplane
                            where airplane.id = new.airplane_id );

        end;
$$
delimiter ;

-- trigger for airplane information -- checked

delimiter $$
create trigger set_airplane_id before insert on airplane
    for each row begin
    
        if (new.e_capacity > 150  or new.b_capacity > 20 or new.f_capacity > 8) then
                    SIGNAL SQLSTATE '45000' 
                    SET MESSAGE_TEXT = "insertion violation in passenger due to capacity mismatch";
                                                          
        end if;
         
    end;
$$
delimiter ;

-- trigger to reduce count of seat capacity in flight 

delimiter $$
create trigger set_seats after insert on ticket_of
    for each row begin
    
        if(new.seat_type = 'E') then
            update flight
            set flight.e_seats = flight.e_seats  - 1
            where flight.id = new.flight_id;
        elseif(new.seat_type = 'B') then 
            update flight
            set flight.b_seats  = flight.b_seats   - 1
            where flight.id = new.flight_id;
        else
            update flight
            set flight.f_seats  = flight.f_seats   - 1
            where flight.id = new.flight_id;
        end if;
        
    end;
    $$
delimiter ;


-- trigger to formulate cost of ticket
delimiter $$
create trigger add_cost_booked before insert on booked
    for each row begin 
    declare seatType char;
    declare seat_cost numeric(9,2);
    declare date_diff int;
    declare flight_date date;
    declare flight_time time;
    declare flight_timestamp timestamp;

    set flight_date = (select flight.departure_date
                    from flight,ticket_of
                        where new.ticket_id = ticket_of.ticket_id 
                    and
                  ticket_of.flight_id = flight.id
                  );
    if(CURDATE() < flight_date) then 
        set seatType  = (select ticket_of.seat_type 
                        from ticket_of
                        where new.ticket_id = ticket_of.ticket_id); 

                              
                set flight_time = (select flight.departure_time
                                from flight,ticket_of
                                    where new.ticket_id = ticket_of.ticket_id 
                                and
                              ticket_of.flight_id = flight.id
                              );
            set flight_timestamp = concat(flight_date,' ',flight_time);

    set date_diff = DATEDIFF(
                                (
                                 flight_timestamp
                                ),
                                (
                                select book_date
                                from ticket
                                where new.ticket_id = ticket.id
                                )
                              );
        if(seatType = 'E') then
            set seat_cost =  (select e_cost
                     from flight,ticket_of
                     where new.ticket_id = ticket_of.ticket_id   
                          and
                          ticket_of.flight_id = flight.id);
                          
        elseif(seatType = 'B') then
            set seat_cost =  (select b_cost
                     from flight,ticket_of
                     where new.ticket_id = ticket_of.ticket_id   
                          and
                          ticket_of.flight_id = flight.id);  
                          
        else
        set seat_cost =  (select f_cost
                 from flight,ticket_of
                 where new.ticket_id = ticket_of.ticket_id   
                      and
                      ticket_of.flight_id = flight.id);
                      
        end if;
        
        if(date_diff = 1) then
            set seat_cost = seat_cost*(2);
            
        elseif(date_diff <= 7 and date_diff > 1) then
            set seat_cost = seat_cost*(1.75);
            
        elseif(date_diff <= 15 and date_diff > 7) then
            set seat_cost = seat_cost*(1.50);
            
        elseif(date_diff <= 30 and date_diff > 15) then
            set seat_cost = seat_cost*(1.25);
            
        elseif(date_diff <= 60 and date_diff > 30) then
            set seat_cost = seat_cost*(1.1); 
            
        else
            set seat_cost = seat_cost;
            
        end if;
        
    set new.cost = seat_cost;
    
    else
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "Booking date unavailable";

    end if;

    end;
    $$
delimiter ;

-- trigger to shift cancelled ticket from booked to cancelled table

delimiter $$
create trigger delete_cost before delete on booked
    for each row begin
    
        declare seatType char;
        declare flightId char(5);

            insert into cancelled
            values(old.ticket_id,current_timestamp,old.cost);
            
            set seatType  = (select seat_type 
                                from ticket_of
                                where old.ticket_id = ticket_of.ticket_id); 
            set flightId = (select flight_id
                                from ticket_of
                                where old.ticket_id = ticket_of.ticket_id);
            
            if(seatType = 'E') then
                update flight
                set flight.e_seats = flight.e_seats  + 1
                where flight.id = flightId;
                
            elseif(seatType = 'B') then 
                update flight
                set flight.b_seats = flight.b_seats  + 1
                where flight.id = flightId;
        
            else
                update flight
                set flight.f_seats =flight.f_seats  + 1
                where flight.id = flightId;
        
            end if;

    end;
    $$
delimiter ;

-- trigger to add refund cost

delimiter $$
create trigger refund_cost before insert on cancelled
    for each row begin 
        
        declare seatType char;
        declare refund numeric(9,2);
        declare date_diff int;
        declare flight_date date;
        declare flight_time time;
        declare flight_timestamp timestamp;
        
        set flight_date = (select flight.departure_date
                        from flight,ticket_of
                            where new.ticket_id = ticket_of.ticket_id 
                        and
                      ticket_of.flight_id = flight.id
                      );
        if(NOW() < flight_date) then 
                                  
                    set flight_time = (select flight.departure_time
                                    from flight,ticket_of
                                        where new.ticket_id = ticket_of.ticket_id 
                                    and
                                  ticket_of.flight_id = flight.id
                                  );
                set flight_timestamp = concat(flight_date,' ',flight_time);
                
                if(flight_timestamp > new.cancelled_at) then
                    set date_diff = DATEDIFF(
                                                (
                                                flight_timestamp
                                                ),
                                                new.cancelled_at
                                              );
                    
                    if(date_diff = 1) then
                        set new.refund = 0.00;
                        
                    elseif(date_diff > 1) then
                        set new.refund = new.refund - new.refund*(0.75);
                        
                    elseif(date_diff > 3) then
                        set new.refund = new.refund - new.refund*(0.50);
             
                    elseif(date_diff > 7 and date_diff <= 15) then
                        set new.refund = new.refund - new.refund*(0.25);
                    
                    else 
                        set new.refund = new.refund;
                                        
                    end if;
                    
            else
                SIGNAL SQLSTATE '45000' 
                SET MESSAGE_TEXT = "invalid ticket";
                
            end if;

        else
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "Cancellation date unavailable";

        end if;
    end;
$$
delimiter ;

-- trigger to check passenger information before insert 

delimiter $$
create trigger check_info_passenger before insert on passenger 
    for each row begin
    
        if (new.gender !='M' and new.gender !='F' and new.gender !='O') then
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = "insertion violation in passenger due to gender mismatch";
            
        elseif(new.dob > curdate()) then
                SIGNAL SQLSTATE '45000' 
                SET MESSAGE_TEXT = "insertion violation in passenger due to dob mismatch";
                
        end if;
    end;
$$
delimiter ;
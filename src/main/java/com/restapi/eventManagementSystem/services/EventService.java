package com.restapi.eventManagementSystem.services;

import java.util.List;
import java.util.Optional;

import com.restapi.eventManagementSystem.entites.EventRegister;
import com.restapi.eventManagementSystem.entites.Events;

public interface EventService {
	
	List<Events> getEvents();
	
	Optional<Events>  getEventById(long id);
	
	boolean insertEvent(Events events);

	boolean updateEvent(Events events);

	boolean deleteEvent(long id);
	
	
	boolean checkEventStudent(long eid,long sid);
	
	boolean checkTid(String string);
	
	boolean registerEvent(EventRegister eventRegister);

}

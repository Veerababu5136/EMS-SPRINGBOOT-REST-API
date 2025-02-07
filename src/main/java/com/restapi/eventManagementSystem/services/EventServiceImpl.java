package com.restapi.eventManagementSystem.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restapi.eventManagementSystem.entites.EventRegister;
import com.restapi.eventManagementSystem.entites.Events;
import com.restapi.eventManagementSystem.repositories.EventRegisterRepository;
import com.restapi.eventManagementSystem.repositories.EventRepository;

@Service
public class EventServiceImpl implements EventService {

	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private EventRegisterRepository eventRegisterRepository;
	
	@Override
	public List<Events> getEvents() 
	{
		return eventRepository.findAll();
	}

	@Override
	public Optional<Events> getEventById(long id) {
		
		return eventRepository.findById(id);
	}

	@Override
	public boolean insertEvent(Events events) {
		
		try
		{
		eventRepository.save(events);
		
		return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@Override
	public boolean updateEvent(Events events) {
		
		Events updated=eventRepository.save(events);
		
		if(updated==null)
		{
			return false;
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean deleteEvent(long id) {
		
		Optional<Events> event = eventRepository.findById(id);

        if (event.isPresent()) {
            eventRepository.delete(event.get());
            return true; // Event deleted successfully
        }

        return false; // Event not found
	}

	@Override
	public boolean checkEventStudent(long eventId, long studentId) 
	{
		Optional<EventRegister> register=eventRegisterRepository.findByEventIdAndStudentId(eventId,studentId);
		
		if(register.isPresent())
		{
			return true;
		}
		
		
		return false;
	}

	@Override
	public boolean registerEvent(EventRegister eventRegister) 
	{
//		System.out.println(eventRegister);

		EventRegister status=eventRegisterRepository.save(eventRegister);
		
		
		if(status==null)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTid(String id) {
	    Optional<EventRegister> status = eventRegisterRepository.findByTransactionId(id);
	    
	    System.out.println("Transaction ID exists: " + status.isPresent());

	    return status.isEmpty(); // If transaction ID exists, return false; otherwise, return true
	}

	
	

}

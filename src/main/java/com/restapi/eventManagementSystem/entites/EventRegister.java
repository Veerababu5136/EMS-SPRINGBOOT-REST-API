package com.restapi.eventManagementSystem.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_register")
public class EventRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long eventId;
    private long studentId;
    
    
    private String transactionId;
    
    private int status;

    public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setId(long id) {
		this.id = id;
	}

	// Constructors
    public EventRegister() {}

    public EventRegister(long eventId, long studentId) {
        this.eventId = eventId;
        this.studentId = studentId;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

	@Override
	public String toString() {
		return "EventRegister [id=" + id + ", eventId=" + eventId + ", studentId=" + studentId + ", transactionId="
				+ transactionId + ", status=" + status + "]";
	}

    
}

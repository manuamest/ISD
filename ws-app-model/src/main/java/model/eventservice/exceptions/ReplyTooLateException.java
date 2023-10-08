package model.eventservice.exceptions;

import java.time.LocalDateTime;

public class ReplyTooLateException extends Exception{

    private Long eventID;

    private LocalDateTime eventDate;

    public ReplyTooLateException(Long eventID, LocalDateTime eventDate) {
        super("Reply for event with id=\"" + eventID + "\" must be " +
                " at least 24 hours before \"" + eventDate + "\"");
        this.eventID = eventID;
        this.eventDate = eventDate;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}

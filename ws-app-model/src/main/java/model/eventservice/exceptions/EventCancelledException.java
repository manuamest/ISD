package model.eventservice.exceptions;

public class EventCancelledException extends Exception {

    private Long eventID;

    public EventCancelledException(Long eventID) {
        super("Event with id=\"" + eventID + "\n cannot be replied because it has been canceled");
        this.eventID = eventID;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }
}

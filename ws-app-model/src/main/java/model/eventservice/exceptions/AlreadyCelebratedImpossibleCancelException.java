package model.eventservice.exceptions;

public class AlreadyCelebratedImpossibleCancelException extends Exception{
    private Long eventID;

    public AlreadyCelebratedImpossibleCancelException(Long eventID) {
        super("Event with id=\"" + eventID + "\n cannot be cancelled because it already took place.");
        this.eventID = eventID;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }
}

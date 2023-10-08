package model.eventservice.exceptions;

public class AlreadyAnswerException  extends Exception{

    private Long eventID;
    private String userEmail;

    public AlreadyAnswerException(Long eventID, String userEmail) {
        super("Event with id=\"" + eventID + "\n cannot be replied because user \"" + userEmail + "\n has already replied it");
        this.eventID = eventID;
        this.userEmail = userEmail;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}

package es.udc.ws.app.client.service.exceptions;

public class ClientEventCancelledException extends Exception{
    private Long eventID;

    public ClientEventCancelledException(Long eventID) {
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

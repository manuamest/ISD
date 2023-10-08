package es.udc.ws.app.client.service.dto;

import java.time.format.DateTimeFormatter;

public class ClientReplyDto {

    private Long replyID;
    private String userEmail;
    private Long eventID;
    private boolean state;


    public ClientReplyDto(String userEmail, Long eventID, boolean state) {
        this.state = state;
        this.userEmail = userEmail;
        this.eventID = eventID;
    }
    public ClientReplyDto(Long replyId, String userEmail, Long eventID, boolean state) {
        this(userEmail, eventID, state);
        this.replyID = replyId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public boolean getState() {
        return state;
    }

    public Long getReplyID() {
        return replyID;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ReplyID: " + replyID + " userEmail: " + userEmail + " EventID: " + eventID + " State: " + state;
    }
}

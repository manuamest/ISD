package es.udc.ws.app.restservice.dto;

public class RestReplyDto {

    private Long replyID;
    private String userEmail;
    private Long eventID;
    private boolean state;


    public RestReplyDto(String userEmail, Long eventID, boolean state) {
        this.state = state;
        this.userEmail = userEmail;
        this.eventID = eventID;
    }
    public RestReplyDto(Long replyId, String userEmail, Long eventID, boolean state) {
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

    public Long getReplyID(){return replyID;}

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }


}

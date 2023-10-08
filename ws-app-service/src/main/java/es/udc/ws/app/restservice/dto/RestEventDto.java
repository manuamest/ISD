package es.udc.ws.app.restservice.dto;

public class RestEventDto {

    private Long eventID;
    private String eventName;
    private String description;
    private String eventDate;
    private int duration;
    private boolean state;

    private int acceptedReplies;
    private int repliesNum;


    public RestEventDto(String eventName, String description, String eventDate, int duration, boolean state, int acceptedReplies,  int repliesNum) {
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.duration = duration;
        this.state = state;
        this.acceptedReplies = acceptedReplies;
        this.repliesNum = repliesNum;
    }

    public RestEventDto(Long eventID, String eventName, String description, String eventDate, int duration, boolean state, int acceptedReplies, int repliesNum) {
        this(eventName, description, eventDate, duration, state, acceptedReplies, repliesNum);
        this.eventID = eventID;
    }

    public Long getEventID() {


        if(eventID == null){
            return null;
        }
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setRepliesNum(int repliesNum) {this.repliesNum = repliesNum;}
    public int getRepliesNum() {
        return repliesNum;
    }

    public void increaseRepliesNum (RestEventDto event){
        this.repliesNum = event.getRepliesNum() + 1;
    }

    public int getAcceptedReplies() {
        return acceptedReplies;
    }

    public void setAcceptedReplies(int acceptedReplies) {
        this.acceptedReplies = acceptedReplies;
    }
}

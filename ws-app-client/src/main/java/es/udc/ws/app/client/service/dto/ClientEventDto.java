package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientEventDto {
    private Long eventID;
    private String eventName;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime eventEndDate;
    private boolean state;
    private int acceptedReplies;
    private int repliesNum;


    public ClientEventDto(String eventName, String description, LocalDateTime eventDate, LocalDateTime eventEndDate, boolean state, int acceptedReplies,  int repliesNum) {
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.eventEndDate = eventEndDate;
        this.state = state;
        this.acceptedReplies = acceptedReplies;
        this.repliesNum = repliesNum;
    }

    public ClientEventDto(Long eventID, String eventName, String description, LocalDateTime eventDate, LocalDateTime eventEndDate, boolean state, int acceptedReplies, int repliesNum) {
        this(eventName, description, eventDate, eventEndDate, state, acceptedReplies, repliesNum);
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

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
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
    public void setAcceptedReplies(int acceptedReplies) {this.acceptedReplies = acceptedReplies;}
    public int getAcceptedReplies() {
        return acceptedReplies;
    }

    public void increaseRepliesNum (ClientEventDto event){
        this.repliesNum = event.getRepliesNum() + 1;
    }

    @Override
    public String toString() {
        return "ClientEventDto{" +
                "eventID=" + eventID +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", eventEndDate=" + eventEndDate +
                ", state=" + state +
                ", acceptedReplies=" + acceptedReplies +
                ", repliesNum=" + repliesNum +
                '}';
    }

    public String getEventDateAsString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return eventDate.format(formatter);
    }
}

package model.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {

    private Long eventID;

    private String eventName;

    private String description;

    private LocalDateTime eventDate;

    private int duration;


    private boolean state; // True->Active	False->Cancelled Cambiar Cancelado

    private int acceptedReplies;

    private int deniedReplies;

    private LocalDateTime creationDate;

    public Event(String eventName, String description, LocalDateTime eventDate, int duration, boolean state,  int acceptedReplies, int deniedReplies) {
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.duration = duration;
        this.state = state;
        this.acceptedReplies = acceptedReplies;
        this.deniedReplies = deniedReplies;
    }

    public Event(Long eventID, String eventName, String description, LocalDateTime eventDate, int duration, boolean state, int acceptedReplies, int deniedReplies) {
        this(eventName, description, eventDate, duration, state, acceptedReplies, deniedReplies);
        this.eventID = eventID;
    }

    public Event(Long eventID, String eventName, String description, LocalDateTime eventDate, int duration, boolean state, int acceptedReplies, int deniedReplies, LocalDateTime creationDate) {
        this(eventID, eventName, description, eventDate, duration, state, acceptedReplies, deniedReplies);
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setAcceptedReplies(int acceptedReplies) {
        this.acceptedReplies = acceptedReplies;
    }

    public void increaseAcceptedReplies (Event event){
        this.acceptedReplies = event.getAcceptedReplies() + 1;
    }

    public void increaseDeniedReplies (Event event){
        this.acceptedReplies = event.getAcceptedReplies() + 1;
    }

    public void setDeniedReplies(int deniedReplies) {
        this.deniedReplies = deniedReplies;
    }

    public int getAcceptedReplies() {
        return acceptedReplies;
    }

    public int getDeniedReplies() {
        return deniedReplies;
    }

    public String getEventDateAsString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return eventDate.format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;

        if (duration != event.duration) return false;
        if (!eventID.equals(event.eventID)) return false;
        if (!eventName.equals(event.eventName)) return false;
        if (!description.equals(event.description)) return false;
        if (state != event.state) return false;
        return eventDate.equals(event.eventDate);
    }

    @Override
    public int hashCode() {
        int result = eventID.hashCode();
        result = 31 * result + eventName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + eventDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventID=" + eventID +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", duration=" + duration +
                ", state=" + state +
                ", acceptedReplies=" + acceptedReplies +
                ", deniedReplies=" + deniedReplies +
                ", creationDate=" + creationDate +
                '}';
    }
}

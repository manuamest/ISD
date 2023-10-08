package model.reply;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Reply {

    private Long replyID;
    private String userEmail;
    private Long eventID;
    private LocalDateTime replyDate;
    private boolean state;

    public String getReplyDateAsString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return replyDate.format(formatter);
    }

    public Reply(String userEmail, Long eventID, LocalDateTime replyDate, boolean state) {
        this.replyDate = replyDate;
        this.state = state;
        this.userEmail = userEmail;
        this.eventID = eventID;
    }
    public Reply(Long replyId, String userEmail, Long eventID, LocalDateTime replyDate, boolean state) {
        this(userEmail, eventID, replyDate, state);
        this.replyID = replyId;
    }

    public Reply(Long replyId, String userEmail, Long eventID, boolean state) {
        this.state = state;
        this.userEmail = userEmail;
        this.eventID = eventID;
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

    public LocalDateTime getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(LocalDateTime replyDate) {
        this.replyDate = replyDate;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reply reply = (Reply) o;

        if (state != reply.state) return false;
        if (!Objects.equals(userEmail, reply.userEmail)) return false;
        if (!Objects.equals(eventID, reply.eventID)) return false;
        return Objects.equals(replyDate, reply.replyDate);
    }

    @Override
    public int hashCode() {
        int result = userEmail != null ? userEmail.hashCode() : 0;
        int prime = 31;
        result = prime * result + (eventID != null ? eventID.hashCode() : 0);
        result = prime * result + (replyDate != null ? replyDate.hashCode() : 0);
        result = prime * result + (state? 1 : 0);
        return result;
    }

    public Long getReplyID() {
        if(replyID == null){
            return null;
        }
        return replyID;
    }
}

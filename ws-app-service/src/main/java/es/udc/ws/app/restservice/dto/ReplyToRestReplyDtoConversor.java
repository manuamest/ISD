package es.udc.ws.app.restservice.dto;

import model.event.Event;
import model.reply.Reply;

import java.util.ArrayList;
import java.util.List;

public class ReplyToRestReplyDtoConversor {

    public static RestReplyDto toRestReplyDto(Reply reply) {
        return new RestReplyDto(reply.getReplyID(), reply.getUserEmail(), reply.getEventID(), reply.getState());
    }

    public static List<RestReplyDto> toRestReplyDtos(List<Reply> replies) {
        List<RestReplyDto> replyDtos = new ArrayList<>(replies.size());
        for (int i = 0; i < replies.size(); i++) {
            Reply reply = replies.get(i);
            replyDtos.add(toRestReplyDto(reply));
        }
        return replyDtos;
    }
}

package es.udc.ws.app.thriftservice;
import es.udc.ws.app.thrift.ThriftReplyDto;
import model.reply.Reply;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReplyToThriftReplyDtoConversor {
        public static Reply toReply(ThriftReplyDto reply) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return new Reply(reply.getReplyID(), reply.getUserEmail(), reply.getEventID(), reply.isState());
        }

        public static ThriftReplyDto toThriftReplyDto(Reply reply) {
            return new ThriftReplyDto(reply.getReplyID(), reply.getUserEmail(), reply.getEventID(), reply.getState());
        }

        public static List<ThriftReplyDto> toThriftReplyDtos(List<Reply> replies) {

            List<ThriftReplyDto> dtos = new ArrayList<>(replies.size());

            for (Reply reply : replies) {
                dtos.add(toThriftReplyDto(reply));
            }
            return dtos;

    }
}

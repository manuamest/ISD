package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.thrift.ThriftReplyDto;

import java.util.ArrayList;
import java.util.List;

import static es.udc.ws.app.client.service.rest.json.JsonToClientReplyDtoConversor.toClientReplyDto;

public class ClientReplyDtoToThriftReplyDtoConversor {
    public static ThriftReplyDto toThriftReplyDto(ClientReplyDto clientReplyDto) {

        Long replyId = clientReplyDto.getEventID();

        return new ThriftReplyDto(replyId == null ? -1 : replyId, clientReplyDto.getUserEmail(),
                clientReplyDto.getEventID(), clientReplyDto.getState());

    }

    public static List<ClientReplyDto> toClientReplyDtos(List<ThriftReplyDto> repliesList) {

        List<ClientReplyDto> dtos = new ArrayList<>(repliesList.size());

        for (ThriftReplyDto reply : repliesList) {
            dtos.add(toClientReplyDto(reply));
        }
        return dtos;

    }

    private static ClientReplyDto toClientReplyDto(ThriftReplyDto reply) {
        return new ClientReplyDto(reply.getReplyID(), reply.getUserEmail(), reply.getEventID(), reply.isState());
    }
}

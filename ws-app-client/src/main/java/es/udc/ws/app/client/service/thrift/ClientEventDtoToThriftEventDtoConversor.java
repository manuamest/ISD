package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.thrift.ThriftEventDto;
import model.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ClientEventDtoToThriftEventDtoConversor {

    public static ThriftEventDto toThriftEventDto(ClientEventDto clientEventDto) {

        Long eventId = clientEventDto.getEventID();
        long duration = ChronoUnit.HOURS.between(clientEventDto.getEventDate(), clientEventDto.getEventEndDate());

        return new ThriftEventDto(eventId == null ? -1 : eventId, clientEventDto.getEventName(),
                clientEventDto.getDescription(), clientEventDto.getEventDateAsString(), (short) duration , clientEventDto.isState(),
                (short) clientEventDto.getAcceptedReplies(), Integer.valueOf( clientEventDto.getRepliesNum() - clientEventDto.getAcceptedReplies()).shortValue() );

    }

    public static List<ClientEventDto> toClientEventDtos(List<ThriftEventDto> events) {

        List<ClientEventDto> clientEventDtos = new ArrayList<>(events.size());

        for (ThriftEventDto event : events) {
            clientEventDtos.add(toClientEventDto(event));
        }
        return clientEventDtos;

    }

    public static ClientEventDto toClientEventDto(ThriftEventDto event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new ClientEventDto(
                event.getEventID(),
                event.getEventName(),
                event.getDescription(),
                LocalDateTime.parse(event.getEventDate(), formatter),
                LocalDateTime.parse(event.eventDate, formatter).plusHours(event.duration), event.isState(),
                event.getAcceptedReplies(), event.acceptedReplies + event.getDeniedReplies());

    }

}

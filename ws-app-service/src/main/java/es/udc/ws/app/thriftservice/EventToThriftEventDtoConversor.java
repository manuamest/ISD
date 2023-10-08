package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.ThriftEventDto;
import model.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

public class EventToThriftEventDtoConversor {

    public static Event toEvent(ThriftEventDto event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new Event( event.getEventID(), event.getEventName(), event.getDescription(), LocalDateTime.parse( event.getEventDate(), formatter),
                event.getDuration(), event.isState(), event.getAcceptedReplies(), event.getDeniedReplies());
    }

    public static List<ThriftEventDto> toThriftEventDtos(List<Event> events) {

        List<ThriftEventDto> dtos = new ArrayList<>(events.size());

        for (Event event : events) {
            dtos.add(toThriftEventDto(event));
        }
        return dtos;

    }

    public static ThriftEventDto toThriftEventDto(Event event) {

        return new ThriftEventDto(event.getEventID(), event.getEventName(),
                event.getDescription(), event.getEventDateAsString(), Integer.valueOf(event.getDuration()).shortValue() , event.isState(),
                (short) event.getAcceptedReplies(), Integer.valueOf( event.getDeniedReplies()).shortValue() );

    }
}

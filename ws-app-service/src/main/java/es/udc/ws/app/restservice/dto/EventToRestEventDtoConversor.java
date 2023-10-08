package es.udc.ws.app.restservice.dto;

import model.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventToRestEventDtoConversor {
    public static List<RestEventDto> toRestEventDtos(List<Event> events) {
        List<RestEventDto> eventDtos = new ArrayList<>(events.size());
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            eventDtos.add(toRestEventDto(event));
        }
        return eventDtos;
    }

    public static RestEventDto toRestEventDto(Event event) {
        return new RestEventDto(event.getEventID(), event.getEventName(), event.getDescription(), event.getEventDate().toString(),
                event.getDuration(), event.isState(), event.getAcceptedReplies(), event.getAcceptedReplies());
    }
    //REVISAR PORQUE TENGO QUE PONER DENIED REPLIES A 0 Y ME ESTRAÑA. ¿CREAR OTRO CONSTRUCTOR?
    public static Event toEvent(RestEventDto event) {


        return new Event(event.getEventID(), event.getEventName(), event.getDescription(), LocalDateTime.parse(event.getEventDate()) ,
                event.getDuration(), event.isState(), event.getRepliesNum(), 0);
    }
}

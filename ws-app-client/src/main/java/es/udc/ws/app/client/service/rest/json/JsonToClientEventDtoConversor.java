package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientEventDtoConversor {
    public static ObjectNode toObjectNode(ClientEventDto event) throws IOException {

        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

        if (event.getEventID() != null) {
            eventObject.put("eventID", event.getEventID());
        }

        eventObject.
                put("eventName", event.getEventName()).
                put("description", event.getDescription()).
                put("eventDate", event.getEventDate().toString()).
                put("eventEndDate", event.getEventEndDate().toString());


        return eventObject;
    }

    public static ClientEventDto toClientEventDto(InputStream jsonEvent) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientEventDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientEventDto> toClientEventDtos(InputStream jsonEvents) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvents);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode eventsArray = (ArrayNode) rootNode;
                List<ClientEventDto> eventDtos = new ArrayList<>();
                for (JsonNode eventNode : eventsArray) {
                    eventDtos.add(toClientEventDto(eventNode));
                }

                return eventDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientEventDto toClientEventDto(JsonNode eventNode) throws ParsingException {
        if (eventNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {

            ObjectNode eventObject = (ObjectNode) eventNode;

            JsonNode movieIdNode = eventObject.get("eventID");
            Long eventId = (movieIdNode != null) ? movieIdNode.longValue() : null;

            String eventName = eventObject.get("eventName").textValue().trim();
            String description = eventObject.get("eventDescription").textValue().trim();
            LocalDateTime eventDate = LocalDateTime.parse(eventObject.get("eventDate").textValue());

            boolean state = eventObject.get("state").booleanValue();
            int acceptedReplies = eventObject.get("acceptedReplies").intValue();
            int repliesNum = eventObject.get("repliesNum").intValue();


            try {
                LocalDateTime eventEndDate = LocalDateTime.parse(eventObject.get("eventEndDate").textValue());
                return new ClientEventDto(eventId, eventName, description, eventDate, eventEndDate, state, acceptedReplies, repliesNum);
            }catch (ParsingException | NullPointerException e){
                int duration = eventObject.get("duration").intValue();
                LocalDateTime eventEndDate = eventDate.plusDays(duration);
                return new ClientEventDto(eventId, eventName, description, eventDate, eventEndDate, state, acceptedReplies, repliesNum);
            }



        }
    }

}

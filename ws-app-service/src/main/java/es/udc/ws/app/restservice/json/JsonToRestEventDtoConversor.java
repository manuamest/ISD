package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class JsonToRestEventDtoConversor {

    public static ObjectNode toObjectNode(RestEventDto event) {

        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

        if (event.getEventID() != null) {
            eventObject.put("eventID", event.getEventID());
        }
        eventObject.put("eventName", event.getEventName()).
                put("eventDescription", event.getDescription()).
                put("eventDate", event.getEventDate()).
                put("duration", event.getDuration()).
                put("state", event.isState()).
                put("repliesNum", event.getRepliesNum()).
                put("acceptedReplies", event.getAcceptedReplies());


        return eventObject;
    }

    public static ArrayNode toArrayNode(List<RestEventDto> events) {

        ArrayNode eventNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < events.size(); i++) {
            RestEventDto eventDto = events.get(i);
            ObjectNode eventObject = toObjectNode(eventDto);
            eventNode.add(eventObject);
        }

        return eventNode;
    }

    public static RestEventDto toRestEventDto(InputStream jsonEvent) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode eventIdNode = eventObject.get("eventID");
                Long eventID = (eventIdNode != null) ? eventIdNode.longValue() : null;

                String eventName = eventObject.get("eventName").textValue().trim();
                String description = eventObject.get("description").textValue().trim();
                String eventDate =  eventObject.get("eventDate").textValue();
                String eventEndDate = eventObject.get("eventEndDate").textValue();

                long duration = ChronoUnit.HOURS.between(LocalDateTime.parse(eventDate), LocalDateTime.parse(eventEndDate));

                return new RestEventDto(eventID, eventName, description, eventDate, (int) duration, true, 0, 0);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}

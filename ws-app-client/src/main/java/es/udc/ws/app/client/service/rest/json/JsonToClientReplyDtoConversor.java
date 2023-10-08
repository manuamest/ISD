package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientReplyDtoConversor {

    public static ClientReplyDto toClientReplyDto(InputStream jsonReply) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonReply);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode replyObject = (ObjectNode) rootNode;

                JsonNode replyIDNode = replyObject.get("replyID");
                Long replyID = (replyIDNode != null) ? replyIDNode.longValue() : null;

                Long eventID = replyObject.get("eventID").longValue();
                String userEmail = replyObject.get("userEmail").textValue().trim();
                boolean state = replyObject.get("state").booleanValue();

                return new ClientReplyDto(replyID, userEmail, eventID, state);

            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientReplyDto> toClientReplyDtos(InputStream jsonReplies) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonReplies);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode repliesArray = (ArrayNode) rootNode;
                List<ClientReplyDto> replies = new ArrayList<>(repliesArray.size());
                for (JsonNode replyNode : repliesArray) {
                    replies.add(toClientReplyDto(replyNode));
                }
                return replies;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static ClientReplyDto toClientReplyDto(JsonNode replyNode) throws ParsingException {
        try {

            ObjectNode replyObject = (ObjectNode) replyNode;

            JsonNode replyIDNode = replyObject.get("replyID");
            Long replyID = (replyIDNode != null) ? replyIDNode.longValue() : null;

            Long eventID = replyObject.get("eventID").longValue();
            String userEmail = replyObject.get("userEmail").textValue().trim();
            boolean state = replyObject.get("state").booleanValue();

            return new ClientReplyDto(replyID, userEmail, eventID, state);

        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}

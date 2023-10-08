package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.dto.RestReplyDto;

import java.util.List;

public class JsonToRestReplyDtoConversor {

    public static ObjectNode toObjectNode(RestReplyDto sale) {

        ObjectNode saleNode = JsonNodeFactory.instance.objectNode();

        if (sale.getReplyID() != null) {
            saleNode.put("replyID", sale.getReplyID());
        }
        saleNode.put("userEmail", sale.getUserEmail()).
                put("eventID", sale.getEventID()).
                put("state", sale.getState());

        return saleNode;
    }

    public static ArrayNode toArrayNode(List<RestReplyDto> replies) {

        ArrayNode replyNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < replies.size(); i++) {
            RestReplyDto replyDto = replies.get(i);
            ObjectNode replyObject = toObjectNode(replyDto);
            replyNode.add(replyObject);
        }

        return replyNode;
    }
}

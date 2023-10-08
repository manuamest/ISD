package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.AlreadyCelebratedImpossibleCancelException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;

public class AppExceptionToJsonConversor {

    public static ObjectNode toReplyToLateException(ReplyTooLateException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "ReplyTooLateException");
        exceptionObject.put("eventID", (ex.getEventID() != null) ? ex.getEventID() : null);
        if (ex.getEventDate() != null) {
            exceptionObject.put("eventDate", ex.getEventDate().toString());
        } else {
            exceptionObject.set("eventDate", null);
        }

        return exceptionObject;
    }

    public static ObjectNode toAlreadyAnswerException(AlreadyAnswerException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "ReplyTooLateException");
        exceptionObject.put("eventID", (ex.getEventID() != null) ? ex.getEventID() : null);
        if (ex.getUserEmail() != null) {
            exceptionObject.put("userEmail", ex.getUserEmail().toString());
        } else {
            exceptionObject.set("userEmail", null);
        }

        return exceptionObject;
    }

    public static ObjectNode toEventCancelledException(EventCancelledException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "EventCancelledException");
        exceptionObject.put("eventID", (ex.getEventID() != null) ? ex.getEventID() : null);

        return exceptionObject;
    }

    public static ObjectNode toAlreadyCelebratedImpossibleCancelException(AlreadyCelebratedImpossibleCancelException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AlreadyCelebratedImpossibleCancelException");
        exceptionObject.put("eventID", (ex.getEventID() != null) ? ex.getEventID() : null);

        return exceptionObject;
    }
}

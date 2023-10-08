package model.eventservice;

import es.udc.ws.util.exceptions.InputValidationException;
import model.event.Event;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.AlreadyCelebratedImpossibleCancelException;
import model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.eventservice.exceptions.ReplyTooLateException;
import model.reply.Reply;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    // Input Exception:
    // 	- event.date < día actual
    // 	- event.eventName == null || event.eventName.length() == 0
    // 	- event.description == null || event.description .length() == 0
    // 	- event.duration <= 0
    public Event addEvent(Event event) throws InputValidationException;

    public Event findEvent(Long eventID) throws InstanceNotFoundException;

    public List<Event> findEvents(LocalDate date1, LocalDate date2, String keyword);

    // Already Replied Exception:
    //	- Se xa existe un reply.userEmail == userEmail
    // Input Exception:
    //	- event.date < día actual (FACELO PARA 24H ANTES DA DATA)
    // Already Cancelled Exception:
    //	- event.state == False
    public Reply newReply (Reply reply)
            throws AlreadyAnswerException, InstanceNotFoundException, ReplyTooLateException, EventCancelledException;

    // Already Cancelled Exception:
    //	- event.state == False
    // Input Exception:
    //	- event.date < día actual
    public void cancelEvent (long id) throws InputValidationException, EventCancelledException, InstanceNotFoundException, AlreadyCelebratedImpossibleCancelException;

    // Incorrect User Exception:
    //	- userEmail non ten ningunha resposta
    public List<Reply> repliesList (String userEmail, boolean replyType);
    // (replyType == True) -> Só mostra respostas de aceptación)
    // (replyType == False) -> Mostra tanto respostas de aceptación como de negación

}

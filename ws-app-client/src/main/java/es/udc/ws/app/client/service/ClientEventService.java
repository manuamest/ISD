package es.udc.ws.app.client.service;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;

import java.time.LocalDate;
import java.util.List;

public interface ClientEventService {

    public Long addEvent(ClientEventDto event) throws InputValidationException;

    public ClientEventDto findEvent(Long eventID) throws InstanceNotFoundException;

    public List<ClientEventDto> findEvents(LocalDate date, String keyword);

    public Long newReply (ClientReplyDto reply)
            throws AlreadyAnswerException, InstanceNotFoundException, ReplyTooLateException, EventCancelledException;

    public void cancelEvent (long id) throws InputValidationException, EventCancelledException, InstanceNotFoundException;

    public List<ClientReplyDto> repliesList (String userEmail, boolean replyType);



}

package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.event.Event;
import model.eventservice.EventServiceFactory;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.AlreadyCelebratedImpossibleCancelException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import model.reply.Reply;
import org.apache.thrift.TException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThriftEventServiceImpl implements ThriftEventService.Iface {


    @Override
    public ThriftEventDto addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException, TException {


        Event event = EventToThriftEventDtoConversor.toEvent(eventDto);

        try{
            Event addedEvent = EventServiceFactory.getService().addEvent(event);
            return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }

    @Override
    public ThriftEventDto findEvent(long eventID) throws ThriftInstanceNotFoundException, TException {
        try {

            Event event = EventServiceFactory.getService().findEvent(eventID);
            return EventToThriftEventDtoConversor.toThriftEventDto(event);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }

    @Override
    public List<ThriftEventDto> findEvents(String endDate, String keywords) throws TException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Event> events = EventServiceFactory.getService().findEvents(LocalDate.now(), LocalDate.parse(endDate, formatter), keywords );
        return EventToThriftEventDtoConversor.toThriftEventDtos(events);

    }

    @Override
    public ThriftReplyDto newReply(ThriftReplyDto reply) throws ThriftInstanceNotFoundException, ThriftAlreadyAnsweredException, ThriftReplyTooLateException, ThriftEventCancelledException, TException {
        try {
            Reply reply1 = EventServiceFactory.getService().newReply(ReplyToThriftReplyDtoConversor.toReply(reply));
            return ReplyToThriftReplyDtoConversor.toThriftReplyDto(reply1);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (AlreadyAnswerException e) {
            throw new RuntimeException(e);
        } catch (EventCancelledException e) {
            throw new RuntimeException(e);
        } catch (ReplyTooLateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ThriftReplyDto> repliesList(String userEmail, boolean replyType) throws TException {

        List<Reply> replies = EventServiceFactory.getService().repliesList(userEmail, replyType);
        return ReplyToThriftReplyDtoConversor.toThriftReplyDtos(replies);


    }

    @Override
    public void cancelEvent(long eventID) throws ThriftEventCancelledException, ThriftAlreadyCelebratedImpossibleCancelException, TException {

        try {
            EventServiceFactory.getService().cancelEvent(eventID);
        } catch (EventCancelledException e) {
            throw new ThriftEventCancelledException(Long.parseLong(e.getMessage()));
        } catch (AlreadyCelebratedImpossibleCancelException e) {
            throw new ThriftAlreadyCelebratedImpossibleCancelException(Long.parseLong(e.getMessage()));
        } catch (InstanceNotFoundException | InputValidationException e) {
            throw new RuntimeException(e);
        }
    }
}

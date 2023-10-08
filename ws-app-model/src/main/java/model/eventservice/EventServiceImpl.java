package model.eventservice;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.event.Event;
import model.event.SqlEventDao;
import model.event.SqlEventDaoFactory;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.AlreadyCelebratedImpossibleCancelException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import model.reply.Reply;
import model.reply.SqlReplyDao;
import model.reply.SqlReplyDaoFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static model.util.ModelConstants.APP_DATA_SOURCE;


public class EventServiceImpl implements EventService {

    private final DataSource dataSource;

    private SqlEventDao eventDao = null;
    private SqlReplyDao replyDao = null;

    public EventServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        eventDao = SqlEventDaoFactory.getDao();
        replyDao = SqlReplyDaoFactory.getDao();
    }

    private void validateEvent(Event event) throws InputValidationException {

        PropertyValidator.validateMandatoryString("eventName", event.getEventName());
        PropertyValidator.validateMandatoryString("description", event.getDescription());
        if (event.getDuration() <= 0) {
            throw new InputValidationException("Invalid Duration: Duration of the event cannot be less than 1");
        }
        if (event.getEventDate().isBefore(event.getCreationDate())) {
            throw new InputValidationException("Invalid Date: Creation date cannot be before the event date");
        }
    }

    @Override
    public Event addEvent(Event event) throws InputValidationException {

        event.setCreationDate(LocalDateTime.now().withNano(0));
        validateEvent(event);
        event.setState(true);
        event.setAcceptedReplies(0);
        event.setDeniedReplies(0);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Event createdEvent = eventDao.create(connection, event);

                /* Commit. */
                connection.commit();

                return createdEvent;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Event findEvent(Long eventID) throws InstanceNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            return eventDao.find(connection, eventID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Event> findEvents(LocalDate date1, LocalDate date2, String keyword) {
        try (Connection connection = dataSource.getConnection()) {
            return eventDao.findByDateKeyword(connection, date1, date2, keyword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Reply newReply(Reply reply) throws AlreadyAnswerException, InstanceNotFoundException, ReplyTooLateException, EventCancelledException {

        try (Connection connection = dataSource.getConnection()) {
            try {

                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Event event = eventDao.find(connection, reply.getEventID());
                LocalDateTime maxReplyTime = event.getEventDate().minusDays(1);
                LocalDateTime replyDate = LocalDateTime.now();

                if (replyDate.isAfter(maxReplyTime)) {
                    throw new ReplyTooLateException(reply.getEventID(), event.getEventDate()); // Non poder responder -24h antes
                }

                if (!event.isState()) {
                    throw new EventCancelledException(reply.getEventID()); // Non poder responder a eventos xa cancelados
                }

                if (replyDao.existsReplyForEvent(connection, reply.getUserEmail(), reply.getEventID()))
                    throw new AlreadyAnswerException(reply.getEventID(), reply.getUserEmail()); //Non poder responder ao mesmo evento duas veces

                // Incrementar contador de respostas do evento
                if (reply.getState())
                    event.increaseAcceptedReplies(event);
                else
                    event.increaseDeniedReplies(event);

                eventDao.update(connection, event);

                Reply reply1 = replyDao.create(connection, new Reply(reply.getUserEmail(), reply.getEventID(), replyDate, reply.getState()));

                connection.commit();

                return reply1;

            } catch (AlreadyAnswerException | ReplyTooLateException | EventCancelledException | InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEvent(long id) throws EventCancelledException, AlreadyCelebratedImpossibleCancelException {
        try {


            Event event = findEvent(id);

            if (event.isState()) {
                if (event.getEventDate().isAfter(LocalDateTime.now())) {
                    try (Connection connection = dataSource.getConnection()) {

                        try {

                            /* Prepare connection. */
                            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                            connection.setAutoCommit(false);
                            event.setState(false);

                            /* Do work. */
                            eventDao.update(connection, event);

                            /* Commit. */
                            connection.commit();

                        } catch (InstanceNotFoundException e) {
                            connection.commit();
                            throw e;
                        } catch (SQLException e) {
                            connection.rollback();
                            throw new RuntimeException(e);
                        } catch (RuntimeException | Error e) {
                            connection.rollback();
                            throw e;
                        }


                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new AlreadyCelebratedImpossibleCancelException(id);
                }
            } else {
                throw new EventCancelledException(id);
            }
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Reply> repliesList(String userEmail, boolean replyType) {
        try (Connection connection = dataSource.getConnection()) {

            return replyDao.find_Replies(connection, userEmail, replyType);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}

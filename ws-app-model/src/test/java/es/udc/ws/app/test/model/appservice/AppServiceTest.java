package es.udc.ws.app.test.model.appservice;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

import model.event.Event;
import model.event.SqlEventDao;
import model.event.SqlEventDaoFactory;
import model.eventservice.EventService;
import model.eventservice.EventServiceFactory;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.AlreadyCelebratedImpossibleCancelException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import model.reply.Reply;
import model.reply.SqlReplyDao;
import model.reply.SqlReplyDaoFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {

    private final long NON_EXISTENT_EVENT_ID = -1;

    private static EventService eventService = null;

    private static SqlReplyDao replyDao = null;

    @BeforeAll
    public static void init() {

        /*
         * Create a simple data source and add it to "DataSourceLocator" (this)1
         * is needed to test "es.udc.ws.movies.model.movieservice.MovieService"
         */
        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        eventService = EventServiceFactory.getService();

        replyDao = SqlReplyDaoFactory.getDao();

    }


    private Event getValidEvent(String eventName, String description, LocalDateTime eventDate) {
        return new Event(eventName, description, eventDate, 10, true, 0, 0);
    }

    private Event getValidEvent(String eventName, LocalDateTime eventDate) {
        return new Event(eventName, "Event description", eventDate, 10, true, 0, 0);
    }

    private Event getValidEvent(String eventName) {
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1).withNano(0);
        return new Event(eventName, "Event description", eventDate, 10, true, 0, 0);
    }

    private Event getValidEvent() {
        return getValidEvent("New Event");
    }
    
    private void removeReply(Reply reply) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        SqlReplyDao replyDao = SqlReplyDaoFactory.getDao();
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                replyDao.remove(connection, reply.getReplyID());
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
            } catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void removeEvent(Event event){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        SqlEventDao eventDao = SqlEventDaoFactory.getDao();
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            eventDao.remove(connection, event.getEventID());
            connection.commit();

        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Reply getValidReply(Event event1){
        return new Reply("luis@gmail.com", event1.getEventID(),LocalDateTime.now(),true);
    }

    private Event createEvent(Event event) {
        Event addedEvent = null;
        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;
    }



    private Reply createReply(Reply reply){
        Reply addedReply = null;
        try {
            addedReply = eventService.newReply(reply);
        } catch (InstanceNotFoundException | ReplyTooLateException | EventCancelledException |
                 AlreadyAnswerException e) {
            throw new RuntimeException(e);
        }

        return addedReply;
    }

    private Event createEventWithoutValidation(Event evento){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        SqlEventDao eventDao = SqlEventDaoFactory.getDao();
        evento.setEventDate(LocalDateTime.now().minusDays(1).withNano(0));
        evento.setCreationDate(LocalDateTime.now().withNano(0));
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            evento = eventDao.create(connection, evento);
            connection.commit();
            return evento;

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

    }

    //------------------------------------------------------------------------------------------
    //FUNCIONALIDAD 1
    @Test
    public void testCreateEvent() {

        Event evento = getValidEvent();
        Event resultado = createEvent(evento);
        removeEvent(resultado);
    }

    @Test
    public void testInvalidEvent() {
        //Check event name not null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setEventName(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent);
        });
        //Check event name not empty
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setEventName("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent);
        });
        //Check event duration > 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration(-1);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent);
        });
        //Check event description is not null
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent);
        });
        //Check event description is not empty
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent);
        });
        //Check event date is after creation date
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setEventDate(LocalDateTime.now().minusDays(1).withNano(0));
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent);
        });
    }

    //------------------------------------------------------------------------------------------
    //FUNCIONALIDAD 2
    @Test
    public void testFindEventsWithKeywordAndDates() {
        List<Event> events = new LinkedList<Event>();
        LocalDateTime evenDate1 = LocalDateTime.of(2023, 12, 3, 10, 30);
        LocalDateTime evenDate2 = LocalDateTime.of(2023, 12, 4, 10, 30);
        LocalDateTime evenDate3 = LocalDateTime.of(2023, 11, 5, 10, 30);

        Event event1 = createEvent(getValidEvent("event1", "descripcion 1", evenDate1));
        events.add(event1);
        Event event2 = createEvent(getValidEvent("event2", "descripcion 2", evenDate2));
        events.add(event2);
        Event event3 = createEvent(getValidEvent("event3", "descripcion 3", evenDate3));
        events.add(event3);

        try {

            LocalDateTime date1 = LocalDateTime.of(2023, 10, 3, 10, 30);
            LocalDateTime date2 = LocalDateTime.of(2023, 12, 12, 10, 30);

            List<Event> foundEvents = eventService.findEvents(date1.toLocalDate(), date2.toLocalDate(), null);
            assertEquals(events, foundEvents);

            foundEvents = eventService.findEvents(date1.toLocalDate(), date2.toLocalDate(), "Descripcion");
            assertEquals(foundEvents, events);
            assertEquals(3, foundEvents.size());

            foundEvents = eventService.findEvents(date1.toLocalDate(), date2.toLocalDate(), "3");
            assertEquals(foundEvents.get(0), events.get(2));
            assertEquals(1, foundEvents.size());

            date1 = LocalDateTime.of(2024, 10, 3, 10, 30);
            date2 = LocalDateTime.of(2024, 12, 12, 10, 30);
            foundEvents = eventService.findEvents(date1.toLocalDate(), date2.toLocalDate(), null);
            assertEquals(0, foundEvents.size());

        } finally {
            // Clear Database
            for (Event event : events) {
                removeEvent(event);
            }
        }
    }

    //------------------------------------------------------------------------------------------
    //FUNCIONALIDAD 3
    @Test
    public void testFindEventWithID() throws InputValidationException, InstanceNotFoundException {
        Event expectedEvent = getValidEvent();
        Event addedEvent = null;

        try {
            // Create Event
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedEvent = eventService.addEvent(expectedEvent);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            // Find Event
            Event actualEvent = eventService.findEvent(addedEvent.getEventID());

            assertEquals(addedEvent, actualEvent);
            assertEquals(actualEvent.getEventName(), expectedEvent.getEventName());
            assertEquals(actualEvent.getDuration(), expectedEvent.getDuration());
            assertEquals(actualEvent.getDescription(), expectedEvent.getDescription());
            assertEquals(actualEvent.isState(), expectedEvent.isState());
            assertTrue((actualEvent.getCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (actualEvent.getCreationDate().compareTo(afterCreationDate) <= 0));
        } finally {
            // Clear Database
            if (addedEvent != null) {
                removeEvent(addedEvent);
            }
        }
    }



    @Test
    public void testFindNonExistentEvent() {
        assertThrows(InstanceNotFoundException.class, () -> eventService.findEvent(NON_EXISTENT_EVENT_ID));
    }

    //------------------------------------------------------------------------------------------
    //FUNCIONALIDAD 4
    @Test
    public void testNewReplyWorks() throws InstanceNotFoundException {
        Reply reply1 = null;
        Event event1 = null;
        try {
            event1 = createEvent(getValidEvent("Coachella", LocalDateTime.now().plusDays(2)));
            reply1 = createReply(getValidReply(event1));

            event1 = eventService.findEvent(event1.getEventID());

            assertEquals("luis@gmail.com",reply1.getUserEmail());
            assertEquals(event1.getEventID(),reply1.getEventID());
            assertEquals(true,reply1.getState());
            // FALTA O REPLY_ID E REPLY_DATE


            assertEquals(1, event1.getAcceptedReplies());
            assertEquals(0, event1.getDeniedReplies());
        }finally {
            if (reply1 != null){
                removeReply(reply1);
            }
            if (event1 != null){
                removeEvent(event1);
            }

        }
    }

    @Test
    public void testNewReplyTooLateForReplying(){


        Event evento = getValidEvent();
        evento = createEventWithoutValidation(evento);
        Reply reply = getValidReply(evento);
        assertThrows(ReplyTooLateException.class, () -> eventService.newReply(reply));

    }

    @Test
    public void testNewReplyAlreadyReplied(){
        Reply reply1 = null;


        try {
            Event evento = createEvent(getValidEvent("Coachella", LocalDateTime.now().plusDays(2)));
            reply1 = createReply(getValidReply(evento));

            Reply reply2 = getValidReply(evento);
            assertThrows(AlreadyAnswerException.class, () -> eventService.newReply(reply2));
        }finally {
            if(reply1 != null){
                removeReply(reply1);
            }

        }
    }

    @Test
    public void testNewReplyFailsDueToAlreadyCancelled(){
        try {
            Event evento = createEvent(getValidEvent("Coachella", LocalDateTime.now().plusDays(2).withNano(0)));
            eventService.cancelEvent(evento.getEventID());
            evento = eventService.findEvent(evento.getEventID());
            Reply reply = getValidReply(evento);

            assertThrows(EventCancelledException.class, ( )-> eventService.newReply(reply));

        } catch (EventCancelledException | InputValidationException | InstanceNotFoundException |
                 AlreadyCelebratedImpossibleCancelException e) {
            throw new RuntimeException(e);
        }
    }

    //------------------------------------------------------------------------------------------
    //FUNCIONALIDAD 5

    @Test
    public void testCancelWorks(){
        /*EL EVENTO A CANCELAR CUMPLE CON LAS CONDICIONES, ASÍ QUE COMPROBAMOS QUE SU ESTADO CAMBIA*/
        try{
            Event evento = createEvent(getValidEvent());
            eventService.cancelEvent(evento.getEventID());
            Event updatedEvent = eventService.findEvent(evento.getEventID());
            assertNotEquals(updatedEvent.isState(), evento.isState());

        } catch (InputValidationException | InstanceNotFoundException | EventCancelledException |
                 AlreadyCelebratedImpossibleCancelException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    public void testCancelFailsDueToDate(){
        /*CREAMOS UN EVENTO CUYA FECHA DE CELEBRACIÓN YA HA PASADO. NO SE PUEDE CANCELAR*/
        Event evento = getValidEvent();
        evento = createEventWithoutValidation(evento);
        Event finalEvento = evento;
        assertThrows(AlreadyCelebratedImpossibleCancelException.class, () -> eventService.cancelEvent(finalEvento.getEventID()));


    }

    @Test
    public void testCancelFailsDueToAlreadyCancelled(){
        /*CREAMOS UN VENTO, LO CANCELAMOS E INTENTAMOS VOLVERLO A CANCELAR.*/
        try{
            Event evento = createEvent(getValidEvent());
            eventService.cancelEvent(evento.getEventID());

            assertThrows(EventCancelledException.class, ( )-> eventService.cancelEvent(evento.getEventID()) );


        } catch (InputValidationException | EventCancelledException | InstanceNotFoundException |
                 AlreadyCelebratedImpossibleCancelException e) {
            throw new RuntimeException(e);
        }
    }

    //------------------------------------------------------------------------------------------
    //FUNCIONALIDAD 6

    @Test
    public void findAllReplies() throws InstanceNotFoundException {
        Reply reply1 = null;
        Reply reply2 = null;
        Reply reply3 = null;
        try {
            List<Reply> lista = new ArrayList<Reply>();
            

            Event evento1 = createEvent(getValidEvent("Coachella", LocalDateTime.now().plusDays(2).withNano(0)));
            Event evento2 = createEvent(getValidEvent("O Son do Camiño", LocalDateTime.now().plusDays(2).withNano(0)));
            Event evento3 = createEvent(getValidEvent("Ortigueira", LocalDateTime.now().plusDays(2).withNano(0)));
            reply1 = createReply(getValidReply(evento1));

            reply2 = getValidReply(evento2);
            reply2.setState(false);
            reply2 = createReply(reply2);
            reply3 = createReply(getValidReply(evento3));
            

            lista = eventService.repliesList("luis@gmail.com", false);
            assertEquals(reply1.getReplyID(), lista.get(0).getReplyID());
            assertEquals(reply2.getReplyID(), lista.get(1).getReplyID());
            assertEquals(reply3.getReplyID(), lista.get(2).getReplyID());
        }finally {
            if(reply1 != null){
                removeReply(reply1);
            }
            if(reply2 != null){
                removeReply(reply2);
            }
            if(reply3 != null){
                removeReply(reply3);
            }
        }
    }

    @Test
    public void findAcceptedReplies() throws InstanceNotFoundException {
        Reply reply1 = null;
        Reply reply2 = null;
        Reply reply3 = null;
        try {
            List<Reply> lista = new ArrayList<Reply>();


            Event evento1 = createEvent(getValidEvent("Coachella", LocalDateTime.now().plusDays(2).withNano(0)));
            Event evento2 = createEvent(getValidEvent("O Son do Camiño", LocalDateTime.now().plusDays(2).withNano(0)));
            Event evento3 = createEvent(getValidEvent("Ortigueira", LocalDateTime.now().plusDays(2).withNano(0)));
            reply1 = createReply(getValidReply(evento1));

            reply2 = getValidReply(evento2);
            reply2.setState(false);
            reply2 = createReply(reply2);
            reply3 = createReply(getValidReply(evento3));


            lista = eventService.repliesList("luis@gmail.com", true);
            assertEquals(reply1.getReplyID(), lista.get(0).getReplyID());

            assertEquals(reply3.getReplyID(), lista.get(1).getReplyID());
        }finally {
            if(reply1 != null){
                removeReply(reply1);
            }
            if(reply2 != null){
                removeReply(reply2);
            }
            if(reply3 != null){
                removeReply(reply3);
            }
        }
    }

    @Test
    public void testFindRepliesReturnsEmptyList() throws InstanceNotFoundException {
        List<Reply> lista = new ArrayList<Reply>();

        lista = eventService.repliesList("luis@gmail.com", true);
        assertEquals(true, lista.isEmpty());
    }


}

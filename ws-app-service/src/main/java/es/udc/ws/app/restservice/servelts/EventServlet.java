package es.udc.ws.app.restservice.servelts;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.event.Event;
import model.eventservice.EventServiceFactory;
import model.eventservice.exceptions.AlreadyCelebratedImpossibleCancelException;
import model.eventservice.exceptions.EventCancelledException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventServlet extends RestHttpServletTemplate {


    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, InstanceNotFoundException, InputValidationException {

        String pathInfo = req.getPathInfo();

        if(pathInfo == null | pathInfo.equals("/")){

            String endDate = ServletUtils.getMandatoryParameter(req, "endDate");
            String keyword = req.getParameter("keyword");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                List<Event> events = EventServiceFactory.getService().findEvents(LocalDate.now(), LocalDate.parse(endDate, formatter), keyword);
                List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                        JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
            }catch (DateTimeParseException e){
                throw new InputValidationException(e.getMessage());
            }

        }else{

            long eventID = ServletUtils.getIdFromPath(req, "event");
            Event event = EventServiceFactory.getService().findEvent(eventID);
            RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
        }
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {

        String pathInfo = ServletUtils.normalizePath(req.getPathInfo());
        if(pathInfo == null || pathInfo.equals("/")){

            RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(req.getInputStream());
            Event event = EventToRestEventDtoConversor.toEvent(eventDto);
            event = EventServiceFactory.getService().addEvent(event);
            eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            String eventURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + event.getEventID();
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", eventURL);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
        }else{

            String[] parameters = pathInfo.split("/");
            if(parameters[2].equals("cancel")){
                try{
                    long eventID = Long.parseLong(parameters[1]);
                    EventServiceFactory.getService().cancelEvent(eventID);
                } catch (AlreadyCelebratedImpossibleCancelException | InstanceNotFoundException |
                         EventCancelledException | RuntimeException e) {
                    throw new InputValidationException(e.getMessage());
                }
            }
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK, null, null);

        }
    }


}

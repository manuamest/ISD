package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.http.client.fluent.Form;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientReplyDtoConversor;
import es.udc.ws.util.json.ObjectMapperFactory;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import es.udc.ws.app.client.service.dto.ClientEventDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

public class RestClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";
    private String endpointAddress;
    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "event").
                    bodyStream(toInputStream(event), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventID();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ClientEventDto findEvent(Long eventID) throws InstanceNotFoundException {
        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "event/" +eventID).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent());

        }catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> findEvents(LocalDate date, String keyword) {
        try {
            HttpResponse response;
            if(keyword != null) {
                response = Request.Get(getEndpointAddress() + "event/"+"?endDate=" +  URLEncoder.encode(date.toString(), "UTF-8") + "&keyword="+URLEncoder.encode(keyword, "UTF-8")).
                        execute().returnResponse();
            }else{
                response = Request.Get(getEndpointAddress() + "event/"+"?endDate=" +  URLEncoder.encode(date.toString(), "UTF-8") ).
                        execute().returnResponse();

            }

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long newReply(ClientReplyDto reply) throws AlreadyAnswerException, InstanceNotFoundException, ReplyTooLateException, EventCancelledException {
        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "reply").
                    bodyForm(
                            Form.form().
                                    add("eventID", Long.toString(reply.getEventID())).
                                    add("userEmail", reply.getUserEmail()).
                                    add("state", Boolean.toString(reply.getState())).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientReplyDtoConversor.toClientReplyDto(
                    response.getEntity().getContent()).getReplyID();

        } catch (InstanceNotFoundException | AlreadyAnswerException  | ReplyTooLateException | EventCancelledException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEvent(long id) throws InputValidationException, EventCancelledException, InstanceNotFoundException {
        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "event/" + id +"/cancel").
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

        } catch (InstanceNotFoundException | EventCancelledException | InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientReplyDto> repliesList(String userEmail, boolean replyType) {
        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "reply/"+"?userEmail=" + userEmail + "&state=" + replyType).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientReplyDtoConversor.toClientReplyDtos(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    private InputStream toInputStream(ClientEventDto event) {
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception{
        try {

            int statusCode = response.getStatusLine().getStatusCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {

                case HttpStatus.SC_NOT_FOUND:
                    throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_BAD_REQUEST:
                    throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_FORBIDDEN:
                    throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_GONE:
                    throw JsonToClientExceptionConversor.fromGoneErrorCode(
                            response.getEntity().getContent());

                default:
                    throw new RuntimeException("HTTP error; status code = "
                            + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

}

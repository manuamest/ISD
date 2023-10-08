package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.thrift.ThriftEventService;
import es.udc.ws.app.thrift.ThriftInputValidationException;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDate;
import java.util.List;

public class ThriftClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientEventService.endpointAddress";
    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(event)).getEventID();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventID) throws InstanceNotFoundException {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientEventDtoToThriftEventDtoConversor.toClientEventDto(client.findEvent(eventID));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientEventDto> findEvents(LocalDate date, String keyword) {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(client.findEvents(date.toString(), keyword));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public Long newReply(ClientReplyDto reply) throws AlreadyAnswerException, InstanceNotFoundException, ReplyTooLateException, EventCancelledException {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {

            transport.open();

            return client.newReply(ClientReplyDtoToThriftReplyDtoConversor.toThriftReplyDto(reply)).getReplyID();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public void cancelEvent(long id) throws InputValidationException, EventCancelledException, InstanceNotFoundException {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {

            transport.open();

            client.cancelEvent(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientReplyDto> repliesList(String userEmail, boolean replyType) {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {

            transport.open();

            return ClientReplyDtoToThriftReplyDtoConversor.toClientReplyDtos(client.repliesList(userEmail, replyType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    private ThriftEventService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftEventService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }
}

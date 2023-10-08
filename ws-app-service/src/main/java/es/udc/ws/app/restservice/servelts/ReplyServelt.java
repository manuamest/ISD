package es.udc.ws.app.restservice.servelts;

import es.udc.ws.app.restservice.dto.ReplyToRestReplyDtoConversor;
import es.udc.ws.app.restservice.dto.RestReplyDto;
import es.udc.ws.app.restservice.json.JsonToRestReplyDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.eventservice.EventServiceFactory;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import model.reply.Reply;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplyServelt extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        ServletUtils.checkEmptyPath(req);
        Long eventId = ServletUtils.getMandatoryParameterAsLong(req,"eventID");
        String userEmail = ServletUtils.getMandatoryParameter(req,"userEmail");
        String state = ServletUtils.getMandatoryParameter(req,"state");
        Reply reply = new Reply(userEmail, eventId, LocalDateTime.now().withNano(0), Boolean.parseBoolean(state));

        try {
            reply = EventServiceFactory.getService().newReply(reply);
        } catch (AlreadyAnswerException | ReplyTooLateException | EventCancelledException e) {
            throw new InputValidationException(e.getMessage());
        }

        RestReplyDto replyDto = ReplyToRestReplyDtoConversor.toRestReplyDto(reply);
        String saleURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + reply.getReplyID().toString();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", saleURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestReplyDtoConversor.toObjectNode(replyDto), headers);
    }


    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String userEmail = req.getParameter("userEmail");
        String state = req.getParameter("state");


        List<Reply> replies;

        replies = EventServiceFactory.getService().repliesList(userEmail, Boolean.parseBoolean(state));



        List<RestReplyDto> replyDtos = ReplyToRestReplyDtoConversor.toRestReplyDtos(replies);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestReplyDtoConversor.toArrayNode(replyDtos), null);
    }
}

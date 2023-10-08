package es.udc.ws.app.client.ui;

// import model.controllers.ConnectionManager;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.eventservice.exceptions.AlreadyAnswerException;
import model.eventservice.exceptions.EventCancelledException;
import model.eventservice.exceptions.ReplyTooLateException;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class AppServiceClient {
    public static void main(String[] args) {

        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientEventService clientEventService = ClientEventServiceFactory.getService();
        Long replyID;

        //------------------------------------------------------------------------------------------------
        if ("-addEvent".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[]{});

            // [addEvent] AppServiceClient -addEvent <eventName> <description> <eventDate> <eventEndDate>
            try {

                Long eventID = clientEventService.addEvent(new ClientEventDto(args[1], args[2], LocalDateTime.parse(args[3]), LocalDateTime.parse(args[4]) ,true,0,0));

                System.out.println("Event " + eventID + " created sucessfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

            //------------------------------------------------------------------------------------------------

        } else if ("-respond".equalsIgnoreCase(args[0])) {
            validateArgs(args, 4, new int[]{2});

            // [newReply] AppServiceClient -respond <userEmail> <eventID> <state>

            try {
                replyID = clientEventService.newReply(new ClientReplyDto(args[1], Long.parseLong(args[2]), Boolean.parseBoolean(args[3])));
                System.out.println("Reply by " + args[1] + " done sucessfully with replyID " + replyID);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }

            //------------------------------------------------------------------------------------------------
        } else if ("-findEvent".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[] {});

            // [find] AppServiceClient -findEvent <eventId>

            try {
                ClientEventDto event = clientEventService.findEvent(Long.parseLong(args[1]));

                System.out.println("Event with id " + args[1] + " found sucessfully");
                System.out.println(event);

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            //------------------------------------------------------------------------------------------------
        } else if ("-findEvents".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[]{});

            // [findEvents] AppServiceClient -findEvents <finalDate> <keyword>

            try {
                List<ClientEventDto> events;
                if(args.length ==3) {
                    events = clientEventService.findEvents(LocalDate.parse(args[1]), args[2]);
                    System.out.println("Found:" + events.size() + " event(s) with keyword " + args[2] + " and date between today and " + args[1]);
                    for (ClientEventDto event : events) {
                        System.out.println(event);
                    }
                }else{
                    events = clientEventService.findEvents(LocalDate.parse(args[1]), null);
                    System.out.println("Found:" + events.size() + " with date between today and " + args[1]);
                    for (ClientEventDto event : events) {
                        System.out.println(event);
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            //------------------------------------------------------------------------------------------------
        } else if ("-cancel".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[] {1});

            // [cancelEvent] AppServiceClient -cancel <eventId>

            try {
                clientEventService.cancelEvent(Long.parseLong(args[1]));
                System.out.println("Event " + args[1] + " cancelled sucessfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            //------------------------------------------------------------------------------------------------
        }else if("-findResponses".equalsIgnoreCase(args[0])){
            validateArgs(args, 3, new int[]{});
            List<ClientReplyDto> replies;

            // [repliesList] AppServiceClient -findResponses <userEmail> <eventID>

            try{
                replies = clientEventService.repliesList(args[1], Boolean.parseBoolean(args[2]));
                System.out.println("Found " + replies.size() + " replies for e-mail " + args[1] + " and state " + args[2]);
                for (ClientReplyDto reply : replies) {
                    System.out.println(reply);
                }
            }catch (Exception ex){
                ex.printStackTrace(System.err);
            }



        }else {
            printUsageAndExit();
        }
    }

    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if(!args[0].equals("-findEvents")) {
            if (expectedArgs != args.length) {
                printUsageAndExit();
            }
            for (int i = 0; i < numericArguments.length; i++) {
                int position = numericArguments[i];
                try {
                    Double.parseDouble(args[position]);
                } catch (NumberFormatException n) {
                    printUsageAndExit();
                }
            }
        }else{
            if(expectedArgs < args.length){
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [addEvent]    AppServiceClient -addEvent <eventName> <description> <eventDate> <Duration>\n" +
                "    [findEvent] AppServiceClient -findEvent <eventID>\n" +
                "    [findEvents] AppServiceClient -findEvents <finalDate> <keyword>\n" +
                "    [newReply]   AppServiceClient -respond <eventID> <userEmail> <state>\n" +
                "    [cancelEvent]    AppServiceClient -cancel <eventID>\n" +
                "    [repliesList]    AppServiceClient -findResponses <userEmail>\n");
    }



}
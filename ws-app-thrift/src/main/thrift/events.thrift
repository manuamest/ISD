namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
    1: i64 eventID
    2: string eventName
    3: string description
    4: string eventDate
    5: i16 duration
    6: bool state
    7: i16 acceptedReplies
    8: i16 deniedReplies
}

struct ThriftReplyDto {
    1: i64 replyID
    2: string userEmail
    3: i64 eventID
    5: bool state
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftAlreadyAnsweredException {
    1: i64 eventID
    2: string userEmail
}

exception ThriftAlreadyCelebratedImpossibleCancelException {
    1: i64 eventID
}

exception ThriftEventCancelledException {
    1: i64 eventID
}

exception ThriftReplyTooLateException {
    1: i64 eventID
    2: string eventDate
}

service ThriftEventService {

   ThriftEventDto addEvent(1: ThriftEventDto eventDto) throws (1: ThriftInputValidationException e)

   ThriftEventDto findEvent(1: i64 eventID) throws (1: ThriftInstanceNotFoundException e)

   list<ThriftEventDto> findEvents(1: string endDate 2: string keywords)

   ThriftReplyDto newReply(1: ThriftReplyDto reply) throws (1: ThriftInstanceNotFoundException e, 2: ThriftAlreadyAnsweredException ee, 3: ThriftReplyTooLateException eee, 4: ThriftEventCancelledException eeee)

   list<ThriftReplyDto> repliesList(1: string userEmail, 2: bool replyType)

   void cancelEvent(1: i64 eventID) throws (1: ThriftEventCancelledException e, 2: ThriftAlreadyCelebratedImpossibleCancelException ee)
}
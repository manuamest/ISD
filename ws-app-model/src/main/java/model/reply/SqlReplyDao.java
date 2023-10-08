package model.reply;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.Connection;
import java.util.List;

public interface SqlReplyDao {
    // CREAR RESPOSTA
    public Reply create(Connection connection, Reply reply);

    // BUSCAR RESPOSTAS DUN USER
    public List<Reply> find_Replies(Connection connection, String userEmail, boolean replyType);

    // BORRAR REPOSTA
    public void remove(Connection connection, Long replyID)
            throws InstanceNotFoundException;

    public boolean existsReplyForEvent(Connection connection, String userEmail, Long eventID);

}

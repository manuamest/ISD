package model.reply;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import model.event.Event;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlReplyDao implements SqlReplyDao{

    protected AbstractSqlReplyDao(){
    }
    @Override
    public boolean existsReplyForEvent(Connection connection, String userEmail, Long eventID)  {
        String queryString = "SELECT COUNT(*) FROM Reply WHERE eventID = ? AND userEmail = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            preparedStatement.setLong(1, eventID);
            preparedStatement.setString(2, userEmail);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()){
                throw new SQLException("Error checking the number of replies made by the email " + userEmail + "for the event" + eventID);
            }
            Long numberOfReplies = resultSet.getLong(1);
            return numberOfReplies >0;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    @Override
    public List<Reply> find_Replies(Connection connection, String userEmail, boolean replyType) {

        String queryString = "SELECT replyID, eventID, replyDate, state FROM Reply WHERE" + " userEmail = ?";

        if (replyType) {
            queryString += " AND state = ?";
        }
        queryString += " ORDER BY replyID";



        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setString(1, userEmail);
            if(replyType){
                preparedStatement.setBoolean(2, replyType);

            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Reply> replyList = new ArrayList<>();

            while (resultSet.next()) {

                int i = 1;
                Long replyID = resultSet.getLong(i++);
                Long eventID = resultSet.getLong(i++);
                Timestamp replyDateAsTimeStamp = resultSet.getTimestamp(i++);
                LocalDateTime replyDate = replyDateAsTimeStamp.toLocalDateTime();
                boolean state = resultSet.getBoolean(i++);

                //Revisar si pasarle userEmail o obtenerlo de la base de datos y pasarselo (es los mismo vaya)
                replyList.add(new Reply(replyID, userEmail, eventID, replyDate, state));
            }

            return replyList; //SI NO HAY RESPUESTAS DEVUELVE UNA LISTA VACÍA.

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long replyID){

        String queryString = "DELETE FROM Reply WHERE" + " replyID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, replyID);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new es.udc.ws.util.exceptions.InstanceNotFoundException(replyID,
                        Event.class.getName());
            }

        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

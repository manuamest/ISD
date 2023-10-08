package model.event;

import java.sql.*;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao{

    @Override
    public Event create(Connection connection, Event event) {
        String queryString = "INSERT INTO Event" +
                "(eventName, eventDescription, eventDate, duration, state, acceptedReplies, deniedReplies, creationDate)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)){


            int i =1;
            preparedStatement.setString(i++, event.getEventName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getEventDate()));
            preparedStatement.setLong(i++, event.getDuration());
            preparedStatement.setBoolean(i++, event.isState());
            preparedStatement.setInt(i++, event.getAcceptedReplies());
            preparedStatement.setInt(i++, event.getDeniedReplies());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key.");
            }
            Long eventId = resultSet.getLong(1);


            return new Event(eventId, event.getEventName(), event.getDescription(), event.getEventDate(),
                    event.getDuration(), event.isState(), event.getAcceptedReplies(),event.getDeniedReplies(), event.getCreationDate());


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }






}

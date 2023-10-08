package model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public abstract class AbstractSqlEventDao implements SqlEventDao {

    protected AbstractSqlEventDao() {

    }



    @Override
    public Event find(Connection connection, Long eventID) throws InstanceNotFoundException {

        String queryString = "SELECT eventName, "
                + " eventDescription, eventDate, duration, state, acceptedReplies, deniedReplies, creationDate FROM Event WHERE eventID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, eventID.longValue());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(eventID, Event.class.getName());
            }

            i = 1;
            String eventName = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            Timestamp dateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime date = dateAsTimestamp.toLocalDateTime();
            int duration = resultSet.getInt(i++);
            boolean state = resultSet.getBoolean(i++);
            int acceptedReplies = resultSet.getInt(i++);
            int deniedReplies = resultSet.getInt(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();

            return new Event(eventID, eventName, description, date, duration, state, acceptedReplies, deniedReplies, creationDate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Event> findByDateKeyword(Connection connection, LocalDate date1, LocalDate date2, String keyword) {

        String queryString = "SELECT eventID, eventName, eventDescription, eventDate, duration, state, acceptedReplies, deniedReplies, creationDate FROM Event";

        queryString += " WHERE (eventDate BETWEEN ? AND ?)";

        if (keyword != null && keyword.length()>0)
            queryString += " AND (LOWER(eventDescription) LIKE LOWER(?))";


        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            LocalDateTime dateTime1 = date1.atStartOfDay();
            LocalDateTime dateTime2 = date2.atTime(23,59,59);


            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTime1));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateTime2));
            if (keyword != null)
                preparedStatement.setString(3, "%"+keyword+"%");


            ResultSet resultSet = preparedStatement.executeQuery();

            List<Event> events = new ArrayList<Event>();
            while (resultSet.next()) {

                int i = 1;
                Long eventID = resultSet.getLong(i++);
                String eventName = resultSet.getString(i++);
                String description = resultSet.getString(i++);
                Timestamp dateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime date = dateAsTimestamp.toLocalDateTime();
                int duration = resultSet.getInt(i++);
                boolean state = resultSet.getBoolean(i++);
                int acceptedReplies = resultSet.getInt(i++);
                int deniedReplies = resultSet.getInt(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();

                events.add(new Event(eventID,eventName,description,date, duration,state,acceptedReplies,deniedReplies,creationDate));

            }

            return events;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Event event) throws InstanceNotFoundException {
        String queryString = "UPDATE Event"
                + " SET eventName = ?, eventDescription = ?, eventDate = ?, duration = ?,  state = ?, acceptedReplies = ?, deniedReplies = ? , creationDate = ?  WHERE eventID = ?" ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setString(i++, event.getEventName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getEventDate()));
            preparedStatement.setLong(i++, event.getDuration());
            preparedStatement.setBoolean(i++, event.isState());
            preparedStatement.setInt(i++, event.getAcceptedReplies());
            preparedStatement.setInt(i++, event.getDeniedReplies());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.setLong(i++, event.getEventID());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(event.getEventID(),
                        Event.class.getName());
            }

        }catch (SQLException e){
            throw new RuntimeException(e);

        }


    }

    @Override
    public void remove(Connection connection, Long eventID) throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Event WHERE" + " eventID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, eventID);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(eventID,
                        Event.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}

package model.reply;

import java.sql.*;

public class Jdbc3CcSqlReplyDao extends AbstractSqlReplyDao{

    @Override
    public Reply create(Connection connection, Reply reply) {
        String queryString = "INSERT INTO Reply" +
                "(userEmail, eventID, replyDate, state)" +
                "VALUES (?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)){


            int i =1;
            preparedStatement.setString(i++, reply.getUserEmail());
            preparedStatement.setLong(i++, reply.getEventID());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(reply.getReplyDate()));
            preparedStatement.setBoolean(i++, reply.getState());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key.");
            }
            Long replyId = resultSet.getLong(1);


            return new Reply(replyId, reply.getUserEmail(), reply.getEventID(), reply.getReplyDate(),
                    reply.getState());


        }catch (SQLException e){
            throw new RuntimeException(e);
        }


    }
}

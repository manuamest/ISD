package model.event;


import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface SqlEventDao {
    // CREAR EVENTO
    public Event create(Connection connection, Event event);

    // BUSCAR EVENTO POLO ID
    public Event find(Connection connection, Long eventID)
            throws InstanceNotFoundException;

    // BUSCAR EVENTOS ENTRE DÚAS DATAS
    public List<Event> findByDateKeyword(Connection connection, LocalDate date1, LocalDate date2, String keyword);

    // ACTUALIZAR EVENTO
    public void update(Connection connection, Event event)
            throws InstanceNotFoundException;

    // BORRAR EVENTO
    public void remove(Connection connection, Long eventID)
            throws InstanceNotFoundException;
}

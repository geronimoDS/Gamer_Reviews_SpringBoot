package com.gamer.api.service;
// 4 Servicio que llama a los Stored Procedures (JdbcTemplate)


 import com.gamer.api.model.Juego;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class JuegoService {

    private final JdbcTemplate jdbcTemplate;

    public JuegoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Helper: ejecutar SP que devuelve RETURN int
    private int callStoredProcedureReturn(String callSql, List<Object> params) {
        return jdbcTemplate.execute((Connection con) -> {
            CallableStatement cs = con.prepareCall(callSql);
            cs.registerOutParameter(1, Types.INTEGER); // posición 1 para RETURN_VALUE
            int index = 2;
            for (Object p : params) {
                cs.setObject(index++, p);
            }
            return cs;
        }, (CallableStatementCallback<Integer>) cs -> {
            cs.execute();
            return cs.getInt(1); // valor del RETURN
        });
    }

    // Agregar nuevo juego (usa sp_AddNewGame)
    public int addNewGame(String nombre, String descripcion, LocalDate fechaCreacion,
                          String desarrollador, String editor, String plataforma, String imagenUrl) {
        // SQL Server: {? = call sp_AddNewGame(?,?,?,?,?,?,?)}
        String call = "{? = call sp_AddNewGame(?, ?, ?, ?, ?, ?, ?)}";
        List<Object> params = Arrays.asList(nombre, descripcion, Date.valueOf(fechaCreacion), desarrollador, editor, plataforma, imagenUrl);
        return callStoredProcedureReturn(call, params);
    }

    // Obtener todos los juegos -> sp_GetGames (devuelve rows)
    public List<Juego> getAllGames() {
        String sql = "EXEC sp_GetGames";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Juego j = new Juego();
            j.setId(rs.getInt("Id"));
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));
            Date d = rs.getDate("FechaPublicacion");
            if (d != null) j.setFechaPublicacion(d.toLocalDate());
            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));
            j.setImagenURL(rs.getString("imagenURL"));
            return j;
        });
    }

    // Obtener 1 juego -> sp_GetOneGame (@game_id)
    public Optional<Juego> getOneGame(int gameId) {
        String sql = "EXEC sp_GetOneGame @game_id = ?";
        List<Juego> lista = jdbcTemplate.query(sql, (rs, rowNum) -> {
   //otra opcion mejor-> List<Juego> lista = jdbcTemplate.query(sql, new Object[]{gameId}, new BeanPropertyRowMapper<>(Juego.class));
        	// mapeo de resultados
            Juego j = new Juego();
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));
            Date d = rs.getDate("FechaPublicacion");
            if (d != null) j.setFechaPublicacion(d.toLocalDate());
            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));
            j.setImagenURL(rs.getString("imagenURL"));
            return j;
        }, new Object[]{gameId});
        if (lista.isEmpty()) return Optional.empty();
        return Optional.of(lista.get(0));
    }

    // Lazy (sp_GetGamesLazy @PageNumber,@PageSize)
    public List<Map<String,Object>> getGamesLazy(int pageNumber, int pageSize) {
        String sql = "EXEC sp_GetGamesLazy @PageNumber = ?, @PageSize = ?";
        return jdbcTemplate.queryForList(sql, pageNumber, pageSize);
    }

    // Editar juego (sp_EditeGameById) - recibe imagenURL nullable
    public int editGameById(int juegoId, String nombre, String descripcion, LocalDate fechaCreacion,
                            String desarrollador, String editor, String plataforma, String imagenURL) {
        String call = "{? = call sp_EditeGameById(?, ?, ?, ?, ?, ?, ?, ?)}";
        List<Object> params = Arrays.asList(juegoId, nombre, descripcion, Date.valueOf(fechaCreacion), desarrollador, editor, plataforma, imagenURL);
        return callStoredProcedureReturn(call, params);
    }

    // Dar de baja juego (SP_Update_Juego_Baja) -> devuelve SELECT con Success/Message rather than RETURN, llamamos con queryForMap
    public Map<String,Object> darDeBajaJuego(int juegoId) {
        String sql = "EXEC SP_Update_Juego_Baja @JuegoId = ?";
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, juegoId);
        if (rows.isEmpty()) return Map.of("success", 0, "message", "No data");
        return rows.get(0);
    }

    // Obtener mis juegos por usuario -> sp_GetMyGames @usuario_id
    public List<Juego> getMyGames(int usuarioId) {
        String sql = "EXEC sp_GetMyGames @usuario_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Juego j = new Juego();
            j.setId(rs.getInt("Id"));
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));
            j.setImagenURL(rs.getString("imagenURL"));
            Date d = rs.getDate("fecha_publicacion");
            if (d != null) j.setFechaPublicacion(d.toLocalDate());
            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));
            return j;
        }, new Object[]{usuarioId});
    }

    // Método para sp_GetTop10JuegosPorCalificacion (ranking)
    public List<Map<String,Object>> getTopRanking() {
        String sql = "EXEC sp_GetTop10JuegosPorCalificacion";
        return jdbcTemplate.queryForList(sql);
    }
}

package com.gamer.api.service;

import com.gamer.api.model.Respuesta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

@Service
public class RespuestaService {

    private final JdbcTemplate jdbcTemplate;

    public RespuestaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🔹 POST: sp_AddRespuesta
    public int addRespuesta(String comentario, int comentarioId, int usuarioId) {
        String call = "{? = call sp_AddRespuesta(?, ?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, comentario);
                cs.setInt(3, comentarioId);
                cs.setInt(4, usuarioId);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // 🔹 GET: sp_GetRespuestasByComentario
    public List<Respuesta> getRespuestasByComentario(int comentarioId, String baseUrl) {
        String sql = "EXEC sp_GetRespuestasByComentario @comentario_id = ?";
        return jdbcTemplate.query(sql, new Object[]{comentarioId}, (ResultSet rs, int rowNum) -> {
            Respuesta r = new Respuesta();
            r.setRespuestaId(rs.getInt("repuesta_id"));
            r.setTexto(rs.getString("respuestaTexto"));
            r.setComentarioId(rs.getInt("comentario_id"));
            r.setUsuarioId(rs.getInt("usuario_id"));
            r.setUsuarioNombre(rs.getString("UsuarioNombre"));

            String fotoArchivo = rs.getString("perfilURL");
            if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
                r.setPerfilUrl(baseUrl + "/users/" + fotoArchivo);
            } else {
                r.setPerfilUrl(null);
            }
            return r;
        });
    }

    // 🔹 DELETE: sp_DeleteRespuesta
    public int deleteRespuesta(int respuestaId) {
        String call = "{? = call sp_DeleteRespuesta(?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setInt(2, respuestaId);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // 🔹 PATCH: sp_UpdateRespuesta
    public int updateRespuesta(int respuestaId, String nuevoTexto) {
        String call = "{? = call sp_UpdateRespuesta(?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setInt(2, respuestaId);
                cs.setString(3, nuevoTexto);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }
}

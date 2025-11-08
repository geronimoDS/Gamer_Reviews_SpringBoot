package com.gamer.api.service;

import com.gamer.api.model.Usuario;
import com.gamer.api.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.time.LocalDate;

import com.gamer.api.model.Review;

import java.util.stream.Collectors;
import java.sql.ResultSet;
import java.sql.SQLException;





@Service
public class UsuarioService {

    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;

    @Autowired
    public UsuarioService(JdbcTemplate jdbcTemplate, JwtUtil jwtUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
    }

    // sp_AddNewUserLogin
    public int createUser(String correo, String contrasena, String nombre, LocalDate fechaInscripcion) {
        String call = "{? = call sp_AddNewUserLogin(?, ?, ?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, correo);
                cs.setString(3, contrasena);
                cs.setString(4, nombre);
                cs.setDate(5, Date.valueOf(fechaInscripcion));
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // Login (SELECT directo como en C#)
    public Optional<String> login(String correo, String contrasena) {
        String sql = "SELECT usuario_id, nombre, rol FROM Usuarios WHERE correo = ? AND contrasena = ? AND baja = 0";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, correo.trim().toLowerCase(), contrasena);

        if (list.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> u = list.get(0);
        Number idNum = (Number) u.get("usuario_id");
        Number rolNum = (Number) u.get("rol");

        int id = idNum.intValue();
        int rol = rolNum.intValue();
        String nombre = (String) u.get("nombre");

        String token = jwtUtil.generateToken(id, nombre, rol);
        return Optional.of(token);
    }


    // sp_GetUserById
    public Optional<Usuario> getUserById(int usuarioId) {
        String sql = "EXEC sp_GetUserById @usuario_id = ?";
        List<Usuario> lista = jdbcTemplate.query(sql, new Object[]{usuarioId}, (rs, rowNum) -> {
            Usuario u = new Usuario();
            u.setUsuarioId(rs.getInt("usuario_id"));
            u.setCorreo(rs.getString("correo"));
            u.setUsuario(rs.getString("nombre"));
            u.setRol(rs.getByte("rol"));
            u.setPerfilURL(rs.getString("perfilURL"));
            u.setBaja(rs.getByte("baja"));
            return u;
        });
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    // sp_UpdateUser
    //Usá la versión que acepta dos lambdas: una para crear el CallableStatement, y otra para ejecutarlo. Así:
    public int updateUser(int usuarioId, String correo, String contrasena, String nombre, String perfilURL) {
        String call = "{? = call sp_UpdateUser(?, ?, ?, ?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, java.sql.Types.INTEGER);
                cs.setInt(2, usuarioId);
                cs.setString(3, correo);
                cs.setString(4, contrasena);
                cs.setString(5, nombre);
                cs.setString(6, perfilURL);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }
      
    // sp_GetIconUSer
    public Optional<String> getIconUser(int usuarioId) {
        String sql = "EXEC sp_GetIconUSer @usuario_id = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, usuarioId);

        if (result.isEmpty()) return Optional.empty();

        Object val = result.get(0).get("perfilURL");
        return Optional.ofNullable(val != null ? val.toString() : null);
    }

    // SELECT all users
    public List<Usuario> getAllUsers(String baseUrl) {
        String sql = "SELECT usuario_id, correo, nombre, perfilURL, rol, baja FROM Usuarios";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs, baseUrl));
    }

    // sp_GetAllReviewsXUser
    public List<Review> getReviewsByUser(int usuarioId) {
        String sql = "EXEC sp_GetAllReviewsXUser @usuario_id = ?";
        return jdbcTemplate.query(sql, new Object[]{usuarioId}, (rs, rowNum) -> {
            Review r = new Review();
            r.setComentarioId(rs.getInt("comentario_id"));
            r.setNombreJuego(rs.getString("nombreJuego"));
            r.setComentario(rs.getString("comentario"));
            r.setPuntuacion(rs.getInt("puntuacion"));
            return r;
        });
    }

    // Helper privado
    private Usuario mapUser(ResultSet rs, String baseUrl) throws SQLException {
        Usuario u = new Usuario();
        u.setUsuarioId(rs.getInt("usuario_id"));
        u.setCorreo(rs.getString("correo"));
        u.setUsuario(rs.getString("nombre"));
        String foto = rs.getString("perfilURL");
        u.setPerfilURL(foto != null ? baseUrl + "/users/" + foto : null);
        u.setRol(rs.getByte("rol"));
        u.setBaja(rs.getByte("baja"));
        return u;
    }
}

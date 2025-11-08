package com.gamer.api.controller;

import com.gamer.api.dto.*;
import com.gamer.api.model.Usuario;
import com.gamer.api.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

import com.gamer.api.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.gamer.api.model.Review;


@RestController
@RequestMapping("/api/login")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/login/create-user
    @PostMapping("/create-user")
    public ResponseEntity<BaseResponse> createUser(@RequestBody AddUserRequest req) {
        try {
            int res = usuarioService.createUser(req.getCorreo(), req.getContrasena(), req.getNombre(), req.getFechaInscripcion());
            if (res == 0)
                return ResponseEntity.ok(new BaseResponse(true, 200, "Usuario creado correctamente"));
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "El usuario ya existe"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error: " + ex.getMessage()));
        }
    }

    // POST /api/login/login
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest req) {
        try {
            Optional<String> token = usuarioService.login(req.getCorreo(), req.getContrasena());
            if (token.isPresent()) {
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Login exitoso", token.get()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponse(false, 401, "Usuario o contraseña incorrectos"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en login: " + ex.getMessage()));
        }
    }

    // GET /api/login/get-user
    @GetMapping("/get-user")
    public ResponseEntity<BaseResponse> getUser(@RequestParam("usuario_id") int usuarioId) {
        try {
            Optional<Usuario> user = usuarioService.getUserById(usuarioId);
            if (user.isPresent())
                return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", user.get()));
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "Usuario no encontrado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, ex.getMessage()));
        }
    }
    
    
    //despues probar, con el FileStorageService de package com.gamer.api.service;
    //Metodo: PATCH... para actualizar la foto del usuario URL: http://localhost:8080/api/login/update-user
        @Autowired
        @Qualifier("userFileStorageService")
        private FileStorageService fileStorageService;

        @PatchMapping("/update-user")
        public ResponseEntity<BaseResponse> updateUser(
                @RequestParam int usuarioId,
                @RequestParam(required = false) String correo,
                @RequestParam(required = false) String contrasena,
                @RequestParam(required = false) String nombre,
                @RequestParam(required = false) MultipartFile imagen,
                @RequestParam(required = false) String urlVieja) {

            try {
                String nombreImagen = null;

                if (imagen != null && !imagen.isEmpty()) {
                    String contentType = imagen.getContentType();
                    if (!contentType.matches("image/(jpeg|png|webp|jpg)")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new BaseResponse(false, 400, "Formato de imagen no permitido."));
                    }

                    if (urlVieja != null && !urlVieja.isEmpty()) {
                        fileStorageService.deleteFile(urlVieja);
                    }

                    nombreImagen = fileStorageService.saveFile(imagen);
                }

                int result = usuarioService.updateUser(usuarioId, correo, contrasena, nombre, nombreImagen);

                if (result == 0)
                    return ResponseEntity.ok(new BaseResponse(true, 200, "Usuario actualizado correctamente"));
                else if (result == 1)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new BaseResponse(false, 404, "Usuario no encontrado"));
                else
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new BaseResponse(false, 500, "Error desconocido"));

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error al guardar imagen: " + e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en update-user: " + e.getMessage()));
            }
        }
        
        // GET /api/login/getIconXUser
        @GetMapping("/getIconXUser")
        public ResponseEntity<BaseResponse> getIconXUser(@RequestParam("usuario_id") int usuarioId,
                                                         HttpServletRequest request) {
            try {
                Optional<String> icon = usuarioService.getIconUser(usuarioId);
                if (icon.isEmpty())
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new BaseResponse(false, 404, "Usuario no encontrado"));

                String fullUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/users/" + icon.get();
                return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", fullUrl));

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en getIconXUser: " + e.getMessage()));
            }
        }

        // GET /api/login/get-all-users
        @GetMapping("/get-all-users")
        public ResponseEntity<BaseResponse> getAllUsers(HttpServletRequest request) {
            try {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                var users = usuarioService.getAllUsers(baseUrl);
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Usuarios obtenidos con éxito", users));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en get-all-users: " + e.getMessage()));
            }
        }

        // GET /api/login/getMisReviews
        @GetMapping("/getMisReviews")
        public ResponseEntity<BaseResponse> getMisReviews(@RequestParam("usuario_id") int usuarioId) {
            try {
                var reviews = usuarioService.getReviewsByUser(usuarioId);
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Reviews obtenidos con éxito", reviews));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en getMisReviews: " + e.getMessage()));
            }
        }

}

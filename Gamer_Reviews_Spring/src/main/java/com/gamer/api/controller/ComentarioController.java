package com.gamer.api.controller;

import com.gamer.api.dto.AddComentarioRequest;

import com.gamer.api.dto.UpdateComentarioRequest;
import com.gamer.api.model.Comentario;
import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;
import com.gamer.api.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import com.gamer.api.dto.ToggleLikeRequest;
import com.gamer.api.model.LikeResult;
import com.gamer.api.model.LikeStatus;
import com.gamer.api.service.LikeService;
//funciona
@RestController
@RequestMapping("/api/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    // 🔹 POST /add-comentario
    @PostMapping("/add-comentario")
    public ResponseEntity<BaseResponse> addComentario(@RequestBody AddComentarioRequest req) {
        try {
            int result = comentarioService.addComentario(req.getComentario(), req.getPuntuacion(), req.getUsuarioId(), req.getJuegoId());

            switch (result) {
                case 0:
                    return ResponseEntity.ok(new BaseResponse(true, 200, "Comentario agregado correctamente"));
                case 1:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, 404, "Usuario no existe"));
                case 2:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, 404, "Juego no existe"));
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "Error al agregar comentario"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en add-comentario: " + e.getMessage()));
        }
    }

    // 🔹 GET /get-comentarios/{juegoId}
    @GetMapping("/get-comentarios/{juegoId}")
    public ResponseEntity<BaseResponse> getComentarios(@PathVariable int juegoId, HttpServletRequest request) {
        try {
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            List<Comentario> list = comentarioService.getComentariosByJuego(juegoId, baseUrl);
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Comentarios obtenidos", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en get-comentarios: " + e.getMessage()));
        }
    }

    // 🔹 PATCH /update-comentario
    @PatchMapping("/update-comentario")
    public ResponseEntity<BaseResponse> updateComentario(@RequestBody UpdateComentarioRequest req) {
        try {
            int result = comentarioService.updateComentario(req.getComentarioId(), req.getNuevoTexto(), req.getNuevaPuntuacion());

            if (result == 0)
                return ResponseEntity.ok(new BaseResponse(true, 200, "Comentario actualizado correctamente"));
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "Comentario no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en update-comentario: " + e.getMessage()));
        }
    }

    // 🔹 DELETE /delete-comentario/{comentarioId}
    @DeleteMapping("/delete-comentario/{comentarioId}")
    public ResponseEntity<BaseResponse> deleteComentario(@PathVariable int comentarioId) {
        try {
            int result = comentarioService.deleteComentario(comentarioId);

            if (result == 0)
                return ResponseEntity.ok(new BaseResponse(true, 200, "Comentario eliminado correctamente"));
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "Comentario no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en delete-comentario: " + e.getMessage()));
        }
    }
    
    @Autowired
    private LikeService likeService;

    // 🔹 POST /api/comentario/toggle-like
    @PostMapping("/toggle-like")
    public ResponseEntity<BaseResponse> toggleLike(@RequestBody ToggleLikeRequest req) {
        try {
            LikeResult result = likeService.toggleLike(req.getComentarioId(), req.getUsuarioId());
            if (result == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse(false, 400, "No se pudo actualizar el like"));
            }
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Like actualizado", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en toggle-like: " + e.getMessage()));
        }
    }

    // 🔹 GET /api/comentario/get-like
    @GetMapping("/get-like")
    public ResponseEntity<BaseResponse> getLike(@RequestParam int comentarioId, @RequestParam int usuarioId) {
        try {
            LikeStatus result = likeService.getLike(comentarioId, usuarioId);
            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "No se encontraron likes"));
            }
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Likes obtenidos", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en get-like: " + e.getMessage()));
        }
    }
}

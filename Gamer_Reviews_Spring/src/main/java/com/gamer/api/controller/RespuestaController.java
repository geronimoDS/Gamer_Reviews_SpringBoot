package com.gamer.api.controller;

import com.gamer.api.dto.AddRespuestaRequest;
import com.gamer.api.dto.UpdateRespuestaRequest;
import com.gamer.api.model.Respuesta;
import com.gamer.api.service.RespuestaService;
import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/respuesta")
public class RespuestaController {

    private final RespuestaService respuestaService;

    @Autowired
    public RespuestaController(RespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    // 🔹 POST /api/respuesta/add-respuesta
    @PostMapping("/add-respuesta")
    public ResponseEntity<BaseResponse> addRespuesta(@RequestBody AddRespuestaRequest req) {
        try {
            int res = respuestaService.addRespuesta(req.getComentario(), req.getComentarioId(), req.getUsuarioId());
            if (res == 1)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "El comentario no existe"));
            if (res == 2)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "El usuario no existe"));

            return ResponseEntity.ok(new BaseResponse(true, 200, "Respuesta agregada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en add-respuesta: " + e.getMessage()));
        }
    }

    // 🔹 GET /api/respuesta/get-respuestas/{comentarioId}
    @GetMapping("/get-respuestas/{comentarioId}")
    public ResponseEntity<BaseResponse> getRespuestas(@PathVariable int comentarioId, HttpServletRequest request) {
        try {
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            List<Respuesta> list = respuestaService.getRespuestasByComentario(comentarioId, baseUrl);
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Respuestas obtenidas", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en get-respuestas: " + e.getMessage()));
        }
    }

    // 🔹 DELETE /api/respuesta/delete-respuesta/{respuestaId}
    @DeleteMapping("/delete-respuesta/{respuestaId}")
    public ResponseEntity<BaseResponse> deleteRespuesta(@PathVariable int respuestaId) {
        try {
            int res = respuestaService.deleteRespuesta(respuestaId);
            if (res == 1)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, 404, "Respuesta no encontrada"));
            return ResponseEntity.ok(new BaseResponse(true, 200, "Respuesta eliminada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en delete-respuesta: " + e.getMessage()));
        }
    }

    // 🔹 PATCH /api/respuesta/update-respuesta
    @PatchMapping("/update-respuesta")
    public ResponseEntity<BaseResponse> updateRespuesta(@RequestBody UpdateRespuestaRequest req) {
        try {
            int res = respuestaService.updateRespuesta(req.getRespuestaId(), req.getNuevoTexto());
            if (res == 1)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, 404, "Respuesta no encontrada"));
            return ResponseEntity.ok(new BaseResponse(true, 200, "Respuesta actualizada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en update-respuesta: " + e.getMessage()));
        }
    }
}

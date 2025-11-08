package com.gamer.api.controller;
// 5) Controller (endpoints idénticos a C#)

/* Observaciones:

En create-juego y edit-game acepto parámetros @RequestParam y @RequestPart MultipartFile para emular [FromForm] de C#.

Ajustá los nombres de los parámetros si tu front los envía con otros keys.
*/

import com.gamer.api.dto.*;
import com.gamer.api.model.Juego;
import com.gamer.api.service.JuegoService;
import com.gamer.api.storage.FileStorageService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/juego")
public class JuegoController {

    private final JuegoService juegoService;
    private final FileStorageService fileStorage;

    public JuegoController(JuegoService juegoService, @Qualifier("imageFileStorageService") FileStorageService fileStorage) {
        this.juegoService = juegoService;
        this.fileStorage = fileStorage;
    }

    @PostMapping("/create-juego")
    public ResponseEntity<BaseResponse> createJuego(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam(required = false) String fechaCreacion, // ISO YYYY-MM-DD
            @RequestParam String desarrollador,
            @RequestParam String editor,
            @RequestParam String plataforma,
            @RequestPart(required = false) MultipartFile imagen
    ) {
        try {
            LocalDate fecha = (fechaCreacion == null || fechaCreacion.isBlank()) ? LocalDate.now() : LocalDate.parse(fechaCreacion);

            String imagenUrl = null;
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = fileStorage.saveImage(imagen, "games");
            }

            int result = juegoService.addNewGame(nombre, descripcion, fecha, desarrollador, editor, plataforma, imagenUrl);

            if (result == 0) return ResponseEntity.ok(new BaseResponse(true, 200, "Juego agregado correctamente"));
            else if (result == 1) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "El juego ya existe"));
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, "Error desconocido"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, ex.getMessage()));
        }
    }

    @GetMapping("/get-all-games")
    public ResponseEntity<DataResponse<List<Juego>>> getAllGames() {
        List<Juego> list = juegoService.getAllGames();
        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", list));
    }

    @GetMapping("/get-one-game")
    public ResponseEntity<?> getOneGame(@RequestParam("game_id") int gameId) {
        Optional<Juego> maybe = juegoService.getOneGame(gameId);
        if (maybe.isPresent()) return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", maybe.get()));
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, 404, "Juego no encontrado"));
    }

    @GetMapping("/get-all-games-lazy")
    public ResponseEntity<DataResponse<List<Map<String,Object>>>> getAllGamesLazy(@RequestParam(defaultValue = "1") int page,
                                                                                 @RequestParam(defaultValue = "20") int limit) {
        List<Map<String,Object>> rows = juegoService.getGamesLazy(page, limit);
        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", rows));
    }

    @PatchMapping("/edit-game")
    public ResponseEntity<BaseResponse> editGame(
            @RequestParam Integer juegoId,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam(required = false) String fechaPublicacion,
            @RequestParam String desarrollador,
            @RequestParam String editor,
            @RequestParam String plataforma,
            @RequestPart(required = false) MultipartFile imagen,
            @RequestParam(required = false) String imagenVieja
    ) {
        try {
            LocalDate fecha = (fechaPublicacion == null || fechaPublicacion.isBlank()) ? LocalDate.now() : LocalDate.parse(fechaPublicacion);
            String imagenUrl = null;
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = fileStorage.saveImage(imagen, "games");
                // opcional: borrar imagenVieja si querés
                if (imagenVieja != null && !imagenVieja.isBlank()) {
                    fileStorage.deleteImageByUrl(imagenVieja);
                }
            }

            int res = juegoService.editGameById(juegoId, nombre, descripcion, fecha, desarrollador, editor, plataforma, imagenUrl);
            if (res == 0) return ResponseEntity.ok(new BaseResponse(true, 200, "Juego editado correctamente"));
            else if (res == 1) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "Juego no encontrado o dado de baja"));
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, "Error desconocido"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, ex.getMessage()));
        }
    }

    @DeleteMapping("/delete-juego")
    public ResponseEntity<?> darDeBajaJuego(@RequestParam int id) {
        Map<String,Object> result = juegoService.darDeBajaJuego(id);
        // result contiene keys: Success, Message (según SP)
        Object success = result.getOrDefault("Success", result.getOrDefault("success", 0));
        if (success != null && Integer.parseInt(success.toString()) == 1) {
            return ResponseEntity.ok(new BaseResponse(true, 200, "Juego dado de baja correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, result.getOrDefault("Message", "No encontrado").toString()));
        }
    }

    @GetMapping("/get-my-games/{usuarioId}")
    public ResponseEntity<DataResponse<List<Juego>>> getMyGames(@PathVariable int usuarioId) {
        List<Juego> list = juegoService.getMyGames(usuarioId);
        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", list));
    }

    @GetMapping("/get-ranking")
    public ResponseEntity<DataResponse<List<Map<String,Object>>>> getRanking() {
        List<Map<String,Object>> ranking = juegoService.getTopRanking();
        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", ranking));
    }
}

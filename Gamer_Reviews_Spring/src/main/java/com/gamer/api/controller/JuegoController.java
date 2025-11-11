package com.gamer.api.controller;
// 5) Controller (endpoints identicos a C#)

/* Observaciones:

En create-juego y edit-game acepto parametros @RequestParam y @RequestPart MultipartFile para emular [FromForm] de C#.

Ajusta los nombres de los parametros si tu front los envía con otros keys.
*/

import com.gamer.api.dto.*;
import com.gamer.api.model.FavoriteGame;
import com.gamer.api.model.IsFavoriteResult;
import com.gamer.api.model.Juego;
import com.gamer.api.service.JuegoService;
import com.gamer.api.storage.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import com.gamer.api.service.CarouselJuegoService;  // 👈 usa tu nuevo servicio

@RestController
@RequestMapping("/api/Juego")
public class JuegoController {

    private final JuegoService juegoService;
    private final FileStorageService fileStorage;
    private final CarouselJuegoService carouselStorage;  // 👈 cambia el tipo

    public JuegoController(JuegoService juegoService, @Qualifier("imageFileStorageService") FileStorageService fileStorage, 
    						CarouselJuegoService carouselStorage) {
        this.juegoService = juegoService;
        this.fileStorage = fileStorage;
        this.carouselStorage = carouselStorage;
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
    
 // ================= FAVORITOS =================
    @PostMapping("/add-favorite-game")
    public ResponseEntity<BaseResponse> addFavoriteGame(@RequestBody FavoriteGame request) {
        try {
            int result = carouselStorage.addOrRemoveFavorite(
                    request.getUsuarioId(),
                    request.getJuegoId(),
                    request.isBotonCheck()
            );

            return switch (result) {
                case 0 -> ResponseEntity.ok(new BaseResponse(true, 200, "Operación de favorito ejecutada correctamente"));
                case 1 -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "Usuario o juego no encontrado"));
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse(false, 400, "Error desconocido"));
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/is-favorite")
    public ResponseEntity<BaseResponse> isFavorite(@RequestParam int usuarioId, @RequestParam int juegoId) {
        try {
            Optional<Boolean> result = carouselStorage.isFavorite(usuarioId, juegoId);
            if (result.isPresent()) {
                IsFavoriteResult dto = new IsFavoriteResult();
                dto.setIsFavorite(result.get());
                return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", dto));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "No encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en isFavorite: " + e.getMessage()));
        }
    }

    // ================= PRÓXIMOS LANZAMIENTOS =================

    @PostMapping(value = "/create-proxjuego", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> createProxJuego(
            @RequestParam("nombre") String nombre,
            @RequestParam("imagen") MultipartFile imagen
    ) {
        try {
            BaseResponse res = carouselStorage.createProxJuego(nombre, imagen);
            return ResponseEntity.status(res.getCode()).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error al crear el próximo juego: " + e.getMessage()));
        }
    }

    @GetMapping("/get-proxjuegos")
    public ResponseEntity<BaseResponse> getProxJuegos(HttpServletRequest request) {
        try {
            var lista = carouselStorage.getAllProxJuegos(request);
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Lista obtenida con éxito", lista));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-proxjuego")
    public ResponseEntity<BaseResponse> deleteProxJuego(
            @RequestParam int id,
            @RequestParam String imagenVieja
    ) {
        try {
            BaseResponse res = carouselStorage.deleteProxJuego(id, imagenVieja);
            return ResponseEntity.status(res.getCode()).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error al eliminar el próximo juego: " + e.getMessage()));
        }
    }

}

package com.gamer.api.model;

public class Review {
    private int comentarioId;
    private String nombreJuego;
    private String comentario;
    private int puntuacion;

    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public String getNombreJuego() { return nombreJuego; }
    public void setNombreJuego(String nombreJuego) { this.nombreJuego = nombreJuego; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
}

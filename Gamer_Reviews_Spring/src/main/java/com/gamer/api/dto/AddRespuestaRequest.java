package com.gamer.api.dto;

public class AddRespuestaRequest {
    private String comentario;
    private int comentarioId;
    private int usuarioId;

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}

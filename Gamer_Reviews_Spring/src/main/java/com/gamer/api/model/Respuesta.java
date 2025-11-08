package com.gamer.api.model;

public class Respuesta {
    private int respuestaId;
    private String texto;
    private int comentarioId;
    private int usuarioId;
    private String usuarioNombre;
    private String perfilUrl;

    public int getRespuestaId() { return respuestaId; }
    public void setRespuestaId(int respuestaId) { this.respuestaId = respuestaId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getPerfilUrl() { return perfilUrl; }
    public void setPerfilUrl(String perfilUrl) { this.perfilUrl = perfilUrl; }
}

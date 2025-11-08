package com.gamer.api.dto;

public class UpdateRespuestaRequest {
    private int respuestaId;
    private String nuevoTexto;

    public int getRespuestaId() { return respuestaId; }
    public void setRespuestaId(int respuestaId) { this.respuestaId = respuestaId; }

    public String getNuevoTexto() { return nuevoTexto; }
    public void setNuevoTexto(String nuevoTexto) { this.nuevoTexto = nuevoTexto; }
}

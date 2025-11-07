package com.gamer.api.dto;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class UpdateGameRequest {
	private String nombre;
    private String descripcion;
    private LocalDate fechaPublicacion;
    private String desarrollador;
    private String editor;
    private String plataforma;
    private MultipartFile imagen;        // opcional
    private String imagenVieja;         // nombre/ruta anterior
	
	private Integer juegoId;
    public Integer getJuegoId() {
		return juegoId;
	}
	public void setJuegoId(Integer juegoId) {
		this.juegoId = juegoId;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public LocalDate getFechaPublicacion() {
		return fechaPublicacion;
	}
	public void setFechaPublicacion(LocalDate fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}
	public String getDesarrollador() {
		return desarrollador;
	}
	public void setDesarrollador(String desarrollador) {
		this.desarrollador = desarrollador;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getPlataforma() {
		return plataforma;
	}
	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}
	public MultipartFile getImagen() {
		return imagen;
	}
	public void setImagen(MultipartFile imagen) {
		this.imagen = imagen;
	}
	public String getImagenVieja() {
		return imagenVieja;
	}
	public void setImagenVieja(String imagenVieja) {
		this.imagenVieja = imagenVieja;
	}
	

}

package com.gamer.api.dto;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class AddGameRequest {
	
	private String descripcion;
    private LocalDate fechaCreacion;
    private String desarrollador;
    private String editor;
    private String plataforma;
    private MultipartFile imagen;
	
	private String nombre;
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
	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
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
	
}

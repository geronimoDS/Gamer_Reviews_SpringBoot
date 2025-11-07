package com.gamer.api.model;
import java.time.LocalDate;
 // 1) Modelo y DTOs
public class Juego {

	private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaPublicacion;
    private String desarrollador;
    private String editor;
    private String plataforma;
    private String imagenURL;
    private Byte baja;
    
	  public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getImagenURL() {
		return imagenURL;
	}
	public void setImagenURL(String imagenURL) {
		this.imagenURL = imagenURL;
	}
	public Byte getBaja() {
		return baja;
	}
	public void setBaja(Byte baja) {
		this.baja = baja;
	}
	  
}

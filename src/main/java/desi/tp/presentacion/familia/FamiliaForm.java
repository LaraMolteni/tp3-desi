package desi.tp.presentacion.familia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import desi.tp.entidades.Asistido;
import desi.tp.entidades.Familia;
import desi.tp.presentacion.asistido.AsistidoForm;

public class FamiliaForm {

	private String nombre;
	private LocalDate fechaRegistro;
	private List<AsistidoForm> asistidos = new ArrayList<>();

	public FamiliaForm() {
		super();
	}

	public Familia toEntidad() {
		Familia f = new Familia();
		f.setNombre(this.nombre);
		f.setFechaRegistro(this.fechaRegistro);
		f.setActivo(true);
		List<Asistido> listaAsistidos = this.asistidos.stream().map(AsistidoForm::toEntidad).toList();
		f.setAsistidos(listaAsistidos);
		return f;
	}

	public static FamiliaForm desdeEntidad(Familia f) {
		FamiliaForm form = new FamiliaForm();
		form.setNombre(f.getNombre());
		form.setFechaRegistro(f.getFechaRegistro());
		form.setAsistidos(f.getAsistidos().stream().map(AsistidoForm::desdeEntidad).toList());
		return form;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public List<AsistidoForm> getAsistidos() {
		return asistidos;
	}

	public void setAsistidos(List<AsistidoForm> asistidos) {
		this.asistidos = asistidos;
	}

}

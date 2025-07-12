package desi.tp.presentacion.asistido;

import java.time.LocalDate;

import desi.tp.entidades.Asistido;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

public class AsistidoForm {

	private Integer id;
	@NotNull(message = "El dni no puede ser nulo")
	@Min(value = 1000000, message = "El DNI debe tener al menos 7 dígitos")
	@Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
	@Positive(message = "El DNI debe ser un número positivo")
	private Long dni;

	@NotBlank(message = "El nombre es obligatorio.")
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio.")
	private String apellido;

	@Past(message = "La fecha de nacimiento no puede ser futura.")
	@NotNull(message = "La fecha de nacimiento es obligatoria.")
	private LocalDate fechaNacimiento;

	@NotBlank
	private String ocupacion;

	private boolean activo;
	private LocalDate fechaRegistro;

	public AsistidoForm() {
		super();

	}

	// Para agregar uno nuevo
	public Asistido toEntidad() {
		return toEntidad(null);
	}

	// Para editar sin perder info previa
	public Asistido toEntidad(Asistido a) {
		if (a == null) {
			a = new Asistido();
		}
		a.setId(this.id);
		a.setDni(this.dni);
		a.setNombre(this.nombre);
		a.setApellido(this.apellido);
		a.setFechaNacimiento(this.fechaNacimiento);
		a.setOcupacion(this.ocupacion);
		a.setActivo(this.activo);
		a.setFechaRegistro(this.fechaRegistro);
		return a;
	}

	public static AsistidoForm desdeEntidad(Asistido a) {
		AsistidoForm form = new AsistidoForm();
		form.setId(a.getId());
		form.setDni(a.getDni());
		form.setNombre(a.getNombre());
		form.setApellido(a.getApellido());
		form.setFechaNacimiento(a.getFechaNacimiento());
		form.setOcupacion(a.getOcupacion());
		form.setActivo(a.isActivo());
		form.setFechaRegistro(a.getFechaRegistro());
		return form;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getDni() {
		return dni;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public void setDni(Long dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

}

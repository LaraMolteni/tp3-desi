package desi.tp.presentacion.familia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import desi.tp.entidades.Asistido;
import desi.tp.entidades.Familia;
import desi.tp.presentacion.asistido.AsistidoForm;
import desi.tp.servicios.AsistidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FamiliaForm {

	private Integer idFamilia;

	@NotBlank(message = "El nombre es obligatorio.")
	private String nombreFamilia;

	private LocalDate fechaRegistro;

	@Size(min = 1, message = "Debe agregar al menos un integrante.")
	@Valid
	private List<AsistidoForm> asistidos = new ArrayList<>();

	public FamiliaForm() {
		super();
	}

	public Familia toEntidad() {
		Familia f = new Familia();
		f.setIdFamilia(this.idFamilia);
		f.setNombre(this.nombreFamilia);
		f.setFechaRegistro(this.fechaRegistro);
		f.setActivo(true);
		List<Asistido> listaAsistidos = this.asistidos.stream().map(a -> {
			Asistido asistido = a.toEntidad();
			asistido.setFamilia(f);
			return asistido;
		}).collect(Collectors.toList());
		f.setAsistidos(listaAsistidos);

		return f;
	}

	public Familia actualizarEntidad(Familia existente, AsistidoService asistidoService) {
		existente.setNombre(this.nombreFamilia);

		List<Asistido> listaActualizada = new ArrayList<>();

		for (AsistidoForm af : this.asistidos) {
			Asistido original = null;
			if (af.getId() != null) {
				original = asistidoService.buscarPorId(af.getId());
			}
			Asistido asistido = af.toEntidad(original);
			asistido.setFamilia(existente);
			listaActualizada.add(asistido);
		}

		existente.setAsistidos(listaActualizada);
		return existente;
	}

	public static FamiliaForm desdeEntidad(Familia f) {
		FamiliaForm form = new FamiliaForm();
		form.setIdFamilia(f.getIdFamilia());
		form.setNombreFamilia(f.getNombre());
		form.setFechaRegistro(f.getFechaRegistro());
		form.setAsistidos(f.getAsistidos().stream().map(AsistidoForm::desdeEntidad).toList());
		return form;
	}

	public Integer getIdFamilia() {
		return idFamilia;
	}

	public void setIdFamilia(Integer idFamilia) {
		this.idFamilia = idFamilia;
	}

	public String getNombreFamilia() {
		return nombreFamilia;
	}

	public void setNombreFamilia(String nombreFamilia) {
		this.nombreFamilia = nombreFamilia;
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

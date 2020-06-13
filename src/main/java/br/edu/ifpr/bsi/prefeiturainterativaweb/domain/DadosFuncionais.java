package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class DadosFuncionais implements Serializable {

	private String matricula;
	private String cargo;
	private String departamento_ID;

	@Exclude
	private Departamento localDepartamento;

//---------------------- Encapsulamento ----------------------

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getDepartamento_ID() {
		return departamento_ID;
	}

	public void setDepartamento_ID(String departamento_ID) {
		this.departamento_ID = departamento_ID;
	}

	@Exclude
	public Departamento getLocalDepartamento() {
		return localDepartamento;
	}

	public void setLocalDepartamento(Departamento localDepartamento) {
		if (localDepartamento != null && localDepartamento.get_ID() != null)
			this.setDepartamento_ID(localDepartamento.get_ID());
		this.localDepartamento = localDepartamento;
	}

}

package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;

@Named("departamentoBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class DepartamentoBean extends AbstractBean {

	private Departamento departamento;

	@Produces
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (departamento == null)
			departamento = new Departamento();

		departamentos = DepartamentoDAO.getAll();
		hideStatusDialog();
		if (departamentos == null) {
			departamentos = new ArrayList<Departamento>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		}
	};

	public void selecionar(ActionEvent evento) {
		departamento = (Departamento) evento.getComponent().getAttributes().get("departamentoSelecionado");
	}

	public void salvar() {
		showStatusDialog();
		if (DepartamentoDAO.merge(departamento)) {
			hideStatusDialog();
			departamento = new Departamento();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public void remover() {

	}

	public void desabilitar() {

	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}
}

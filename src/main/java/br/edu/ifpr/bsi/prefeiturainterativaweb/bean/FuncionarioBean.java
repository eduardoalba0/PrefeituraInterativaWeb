package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;

@Named("funcionarioBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class FuncionarioBean implements Serializable {

	@Inject
	@Named("funcionarioLogado")
	private Funcionario funcionarioLogado;

	@Produces
	@Named("funcionarios")
	private List<Funcionario> funcionarios;

	private Funcionario funcionario;

	@PostConstruct
	public void init() {
		if (funcionario == null) {
			funcionario = new Funcionario();
		}

		funcionarios = UsuarioDAO.getAllFuncionarios();
		if (funcionarios == null) {
			funcionarios = new ArrayList<Funcionario>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public void selecionar(ActionEvent evento) {
		funcionario = (Funcionario) evento.getComponent().getAttributes().get("funcionarioSelecionado");
	}

	public void salvar() {
		if (UsuarioDAO.merge(funcionario)) {
			funcionario = new Funcionario();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public Funcionario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Funcionario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public List<Funcionario> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
	}
}

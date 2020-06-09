package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;

@Named("funcionarioBean")
@SessionScoped
@SuppressWarnings("serial")
public class FuncionarioBean extends AbstractBean {

	private Funcionario funcionario;

	@Produces
	@Named("funcionarios")
	private List<Funcionario> funcionarios;

	@Inject
	@Named("funcionarioLogado")
	private Funcionario funcionarioLogado;

	@Inject
	@Named("tiposusuario")
	private List<TipoUsuario> tiposUsuario;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (funcionario == null) {
			funcionario = new Funcionario();
		}

		funcionarios = UsuarioDAO.getAllFuncionarios();
		if (funcionarios == null) {
			hideStatusDialog();
			funcionarios = new ArrayList<Funcionario>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		} else {
			funcionarios.forEach((aux) -> {
				funcionario.setTipoUsuario(
						tiposUsuario.get(tiposUsuario.indexOf(new TipoUsuario(aux.getTipoUsuario_ID()))));
			});
			hideStatusDialog();
		}
	}

	@Override
	public void cadastrar() {
		funcionario = new Funcionario();
	}

	@Override
	public void selecionar(ActionEvent evento) {
		funcionario = (Funcionario) evento.getComponent().getAttributes().get("funcionarioSelecionado");
	}

	@Override
	public void salvar() {
		showStatusDialog();
		if (UsuarioDAO.merge(funcionario)) {
			hideStatusDialog();
			funcionario = new Funcionario();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void remover(ActionEvent evento) {

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

package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("funcionarioBean")
@SessionScoped
@SuppressWarnings("serial")
public class FuncionarioBean extends AbstractBean {

	private Funcionario funcionario;

	private List<Funcionario> funcionarios;

	@Produces
	private Funcionario funcionarioLogado;

	@Inject
	private List<TipoUsuario> tiposUsuario;

	@Override
	@PostConstruct
	public void init() {
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

	public void autenticar() {
		String _ID = FirebaseHelper.autenticar(funcionario);
		if (_ID != null && !_ID.trim().equals("")) {
			funcionarioLogado = UsuarioDAO.getFuncionario(_ID);
			funcionario = new Funcionario();
			hideStatusDialog();
			if (funcionarioLogado == null) {
				redirect("index.xhtml", "Falha ao autenticar dados. Consulte o suporte da ferramenta.");
			} else if (funcionarioLogado.getTipoUsuario_ID().equals("6b395be8-a7c1-4971-8dc0-afa04be63a00")) {
				funcionarioLogado = null;
				redirect("index.xhtml",
						"Seu usuário não possuí privilégios para acessar a plataforma. Consulte o suporte da ferramenta.");
			} else {
				funcionario = new Funcionario();
				redirect("solicitacoes.xhtml", "Bem vindo, " + funcionarioLogado.getNome() + "! ");
			}
		} else {
			showStatusDialog();
			PrimeFaces.current().executeScript("login();");
		}
	}

	public void deslogar() {
		funcionarioLogado = null;
		showStatusDialog();
		PrimeFaces.current().executeScript("logout();");
		redirect("index.xhtml", "Obrigado por utilizar nossos serviços.");
	}

	@Override
	public List<Funcionario> listar() {
		return null;
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
	public void salvarEditar() {
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
	public void removerDesabilitar(ActionEvent evento) {

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

	@Produces
	public List<Funcionario> getFuncionarios() {
		if (funcionarios == null)
			init();
		return funcionarios;
	}

	public void setFuncionarios(List<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public List<TipoUsuario> getTiposUsuario() {
		return tiposUsuario;
	}

	public void setTiposUsuario(List<TipoUsuario> tiposUsuario) {
		this.tiposUsuario = tiposUsuario;
	}

}

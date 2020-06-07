package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import com.google.gson.Gson;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("usuarioBean")
@SessionScoped
@SuppressWarnings("serial")
public class UsuarioBean extends AbstractBean {

	@Produces
	@Named("funcionarioLogado")
	private Funcionario funcionarioLogado;

	private Usuario usuario;

	@Override
	@PostConstruct
	public void init() {
		if (usuario == null)
			usuario = new Usuario();
		if (funcionarioLogado != null)
			redirect("solicitacoes.xhtml");
	}

	public void autenticar() {
		String uid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("uid");
		usuario = new Gson().fromJson(uid, Usuario.class);
		String _ID = FirebaseHelper.autenticar(usuario);
		if (_ID != null && !_ID.trim().equals("")) {
			funcionarioLogado = UsuarioDAO.getFuncionario(_ID);
			usuario = new Usuario();
			hideStatusDialog();
			if (funcionarioLogado == null) {
				redirect("index.xhtml", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
						"Falha ao autenticar dados. Consulte o suporte da ferramenta."));
			} else if (funcionarioLogado.getTipoUsuario_ID().equals("6b395be8-a7c1-4971-8dc0-afa04be63a00")) {
				funcionarioLogado = null;
				redirect("index.xhtml", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
						"Seu usuário não possuí privilégios para acessar a plataforma. Consulte o suporte da ferramenta."));
			} else {
				usuario = new Usuario();
				redirect("solicitacoes.xhtml", new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem vindo, ",
						funcionarioLogado.getNome() + "! "));
			}
		}

	}

	public void deslogar() {
		funcionarioLogado = null;
		redirect("index.xhtml",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Obrigado por utilizar nossos serviços."));
	}

	@Override
	public void salvar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selecionar(ActionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remover() {
		// TODO Auto-generated method stub

	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setFuncionarioLogado(Funcionario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Funcionario getFuncionarioLogado() {
		return funcionarioLogado;
	}

}

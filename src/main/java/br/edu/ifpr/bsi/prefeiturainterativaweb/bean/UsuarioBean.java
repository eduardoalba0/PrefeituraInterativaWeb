package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("usuarioBean")
@ViewScoped
@SuppressWarnings("serial")
public class UsuarioBean extends AbstractBean {
//todo enviar verificação de e-mail se ele não estiver confirmado
	private Usuario usuario;
	private Funcionario funcionario;
	private List<Usuario> usuarios;

	@Inject
	private List<TipoUsuario> tiposUsuario;

	@Override
	@PostConstruct
	public void init() {
		if (usuario == null)
			usuario = new Usuario();

		if (usuarios == null) {
			usuarios = UsuarioDAO.getAll();
		}
		if (usuarios == null) {
			hideStatusDialog();
			usuarios = new ArrayList<Usuario>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		} else {
			hideStatusDialog();
		}
	}

	@Override
	public void cadastrar() {
		usuario = new Usuario();
		funcionario = new Funcionario();
		usuario.setHabilitado(true);
	}

	@Override
	public List<Usuario> listar() {
		showStatusDialog();
		usuarios.forEach((aux) -> {
			aux.setLocalTipoUsuario(tiposUsuario.get(tiposUsuario.indexOf(new TipoUsuario(aux.getTipoUsuario_ID()))));
		});
		hideStatusDialog();
		return usuarios;
	}

	@Override
	public void selecionar(ActionEvent evento) {
		usuario = (Usuario) evento.getComponent().getAttributes().get("usuarioSelecionado");
	}

	public void redefinirSenha() {
		try {
			if (FirebaseHelper.alterarSenha(usuario).getUid() != null) {
				hideStatusDialog();
				showSuccessMessage("Senha alterada com sucesso.");
			} else {
				hideStatusDialog();
				showErrorMessage("Falha ao redefinir a senha do usuário. Consulte o suporte da ferramenta.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			hideStatusDialog();
			showErrorMessage("Falha ao redefinir a senha do usuário. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void salvarEditar() {
		try {
			if (usuario.get_ID() == null || usuario.get_ID().trim().equals(""))
				usuario.set_ID(FirebaseHelper.cadastrarUsuario(usuario).getUid());
			else
				usuario.set_ID(FirebaseHelper.alterarUsuario(usuario).getUid());
			boolean tasksSuccess = usuario.get_ID() != null && !usuario.get_ID().trim().equals("");
			if (tasksSuccess) {
				if (isTipoFuncionario())
					tasksSuccess = UsuarioDAO.merge(new Funcionario(usuario));
				else
					tasksSuccess = UsuarioDAO.merge(usuario);
			}
			hideStatusDialog();
			if (tasksSuccess)
				showSuccessMessage("Usuário cadastrado com sucesso.");
			else
				showErrorMessage("Ocorreu um erro ao cadastrar o usuário. Consulte o suporte da ferramenta.");

		} catch (Exception ex) {
			ex.printStackTrace();
			hideStatusDialog();
			showErrorMessage("Ocorreu um erro ao cadastrar o usuário. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void removerDesabilitar(ActionEvent evento) {
		try {
			usuario = (Usuario) evento.getComponent().getAttributes().get("usuarioSelecionado");
			if (usuario.isHabilitado())
				usuario.setHabilitado(false);
			else
				usuario.setHabilitado(true);
			if (FirebaseHelper.alterarUsuario(usuario) != null) {
				if (UsuarioDAO.merge(usuario)) {
					hideStatusDialog();
					if (usuario.isHabilitado())
						showSuccessMessage("Usuário habilitado com sucesso!");
					else
						showSuccessMessage("O usuário foi desabilitado. Seu acesso à ferramenta foi bloqueado.");
				} else {
					hideStatusDialog();
					showErrorMessage(
							"Ocorreu um erro ao gravar as alterações no banco de dados. Consulte o suporte da ferramenta.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			hideStatusDialog();
			showErrorMessage(
					"Ocorreu um erro ao gravar as alterações no banco de dados. Consulte o suporte da ferramenta.");
		}
	}

	public boolean isTipoFuncionario() {
		if (usuario == null)
			return false;
		if (usuario.getLocalTipoUsuario() == null)
			return false;
		else
			return usuario.getLocalTipoUsuario().isFuncionario();
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	@Produces
	public List<Usuario> getUsuarios() {
		if (usuarios == null)
			listar();
		return usuarios;
	}

	public List<TipoUsuario> getTiposUsuario() {
		return tiposUsuario;
	}

	public void setTiposUsuario(List<TipoUsuario> tiposUsuario) {
		this.tiposUsuario = tiposUsuario;
	}
}

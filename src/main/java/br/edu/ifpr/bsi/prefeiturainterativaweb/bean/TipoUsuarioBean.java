package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.TipoUsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Named("tipoUsuarioBean")
@SessionScoped
@SuppressWarnings("serial")
public class TipoUsuarioBean extends AbstractBean {

	private TipoUsuario tipoUsuario;

	private List<TipoUsuario> tiposUsuario;

	public void init() {
		if (tipoUsuario == null) {
			tipoUsuario = new TipoUsuario();
		}
		if (tiposUsuario == null)
			listar();

	}

	public List<TipoUsuario> listar() {
		tiposUsuario = TipoUsuarioDAO.getAll();
		if (tiposUsuario == null) {
			tiposUsuario = new ArrayList<TipoUsuario>();
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
		return tiposUsuario;
	}

	public void cadastrar() {
		tipoUsuario = new TipoUsuario();
		tipoUsuario.set_ID(UUID.randomUUID().toString());
		tipoUsuario.setPersonalizado(true);
	}

	public void selecionar(ActionEvent evento) {
		tipoUsuario = (TipoUsuario) evento.getComponent().getAttributes().get("tipoUsuarioSelecionado");
	}

	public void salvarEditar() {
		if (TipoUsuarioDAO.merge(tipoUsuario)) {
			tipoUsuario = new TipoUsuario();
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	public void removerDesabilitar(ActionEvent evento) {
		tipoUsuario = (TipoUsuario) evento.getComponent().getAttributes().get("tipoUsuarioSelecionado");
		if (!tipoUsuario.isPersonalizado()) {
			showErrorMessage("Você pode remover apenas os tipos de usuário personalizados.");
			return;
		}
		List<Usuario> task = UsuarioDAO.getAllPorTipo(tipoUsuario.get_ID());
		if (task != null && !task.isEmpty()) {
			hideStatusDialog();
			showErrorMessage(
					"Para remover este tipo de usuário, antes você deve desassociá-lo de todos os usuários que o possuem.");
		} else {
			if (TipoUsuarioDAO.remove(tipoUsuario))
				showSuccessMessage("O Tipo de usuário foi removido com sucesso!");
			else
				showErrorMessage("Ocorreu uma falha ao remover o tipo de usuário. Consulte o suporte da ferramenta.");
		}
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipousuario) {
		this.tipoUsuario = tipousuario;
	}
	
	@Produces
	@Named("tiposUsuario")
	public List<TipoUsuario> getTiposUsuario() {
		if (tiposUsuario == null)
			listar();
		return tiposUsuario;
	}

	public void setTiposUsuario(List<TipoUsuario> tiposUsuario) {
		this.tiposUsuario = tiposUsuario;
	}
}

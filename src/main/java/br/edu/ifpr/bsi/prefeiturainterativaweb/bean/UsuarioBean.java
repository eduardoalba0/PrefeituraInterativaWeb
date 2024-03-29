package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Dim_FuncionarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.DadosFuncionais;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("usuarioBean")
@SessionScoped
@SuppressWarnings("serial")
public class UsuarioBean extends AbstractBean {

	private Usuario usuario;
	private DadosFuncionais dadosFuncionais;
	private List<Usuario> usuarios;
	private List<Usuario> funcionarios;
	private String uid;
	private Usuario funcionarioLogado;

	@Inject
	@Named("tiposUsuario")
	private List<TipoUsuario> tiposUsuario;

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@PostConstruct
	public void init() {
		if (usuario == null)
			usuario = new Usuario();

		if (usuarios == null || funcionarios == null) {
			listar();
		}
		if (usuarios == null || funcionarios == null) {
			hideStatusDialog();
			usuarios = new ArrayList<Usuario>();
			funcionarios = new ArrayList<Usuario>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		} else {
			hideStatusDialog();
		}
	}

	public void cadastrar() {
		usuario = new Usuario();
		dadosFuncionais = new DadosFuncionais();
		usuario.setHabilitado(true);
		uid = UUID.randomUUID().toString();
	}

	public List<Usuario> listar() {
		showStatusDialog();
		usuarios = UsuarioDAO.getAll();
		funcionarios = new ArrayList<Usuario>();

		usuarios.forEach((aux) -> {
			aux.setLocalTipoUsuario(tiposUsuario.get(tiposUsuario.indexOf(new TipoUsuario(aux.getTipoUsuario_ID()))));
			if (aux.getLocalTipoUsuario().isFuncionario() && aux.getDadosFuncionais() != null) {
				aux.getDadosFuncionais().setLocalDepartamento(departamentos
						.get(departamentos.indexOf(new Departamento(aux.getDadosFuncionais().getDepartamento_ID()))));
				funcionarios.add(aux);
			}
		});
		hideStatusDialog();
		return usuarios;
	}

	public void selecionar(ActionEvent evento) {
		usuario = (Usuario) evento.getComponent().getAttributes().get("usuarioSelecionado");
		uid = usuario.get_ID();
		dadosFuncionais = usuario.getDadosFuncionais();
		if (dadosFuncionais == null)
			dadosFuncionais = new DadosFuncionais();
	}

	public void salvarEditar() {
		try {
			if (isTipoFuncionario())
				usuario.setDadosFuncionais(dadosFuncionais);
			else
				usuario.setDadosFuncionais(null);

			if (usuario.get_ID() == null || usuario.get_ID().trim().equals("")) {
				usuario.set_ID(uid);
				usuario.set_ID(FirebaseHelper.cadastrarUsuario(usuario).getUid());
			} else {
				usuario.set_ID(FirebaseHelper.alterarUsuario(usuario).getUid());
			}
			boolean tasksSuccess = usuario.get_ID() != null && !usuario.get_ID().trim().equals("");
			if (tasksSuccess) {
				tasksSuccess = UsuarioDAO.merge(usuario);
				if (isTipoFuncionario())
					tasksSuccess = new Dim_FuncionarioDAO().merge(new Dim_Funcionario(usuario));
				usuario = new Usuario();
				usuarios = null;
				init();
				hideStatusDialog();
				showSuccessMessage("Dados do Usuário gravados com sucesso.");
			} else {
				hideStatusDialog();
				showErrorMessage("Ocorreu um erro ao cadastrar o usuário. Consulte o suporte da ferramenta.");
			}
		} catch (Exception ex) {
			hideStatusDialog();
			ex.printStackTrace();
			if (ex.getCause().toString().contains("EMAIL_EXISTS"))
				showErrorMessage("O e-mail informado já pertence à um usuário.");
			else if (ex.getCause().toString().contains("INVALID_EMAIL"))
				showErrorMessage("O e-mail informado está inválido, por favor verifique-o e tente novamente.");
			else
				showErrorMessage("Ocorreu um erro ao cadastrar o usuário. Consulte o suporte da ferramenta.");
		}
	}

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
			hideStatusDialog();
			showErrorMessage(
					"Ocorreu um erro ao gravar as alterações no banco de dados. Consulte o suporte da ferramenta.");
		}
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

	public boolean isTipoFuncionario() {
		if (usuario == null)
			return false;
		if (usuario.getLocalTipoUsuario() == null)
			return false;
		else
			return usuario.getLocalTipoUsuario().isFuncionario();
	}

	public void autenticar() {
		String _ID = FirebaseHelper.autenticar(usuario);
		boolean tasksSuccess = _ID != null && !_ID.trim().equals("");
		if (tasksSuccess) {
			usuario = UsuarioDAO.get(_ID);
			tasksSuccess = usuario != null;

			if (tasksSuccess) {
				tasksSuccess = false;
				if (usuario.isHabilitado()) {
					if (usuario.getTipoUsuario_ID() != null) {
						if (tiposUsuario != null && !tiposUsuario.isEmpty()) {
							int index = tiposUsuario.indexOf(new TipoUsuario(usuario.getTipoUsuario_ID()));
							if (index >= 0 && tiposUsuario.get(index) != null) {
								if (tiposUsuario.get(index).isFuncionario()) {
									tasksSuccess = true;
									usuario.setLocalTipoUsuario(tiposUsuario
											.get(tiposUsuario.indexOf(new TipoUsuario(usuario.getTipoUsuario_ID()))));
								} else {
									hideStatusDialog();
									showErrorMessage(
											"Seu usuário não possuí privilégios para acessar a plataforma. Consulte o suporte da ferramenta.");
								}
							}
						}
					}
				}
			}
		} else {
			PrimeFaces.current().executeScript("login();");
			return;
		}

		if (tasksSuccess) {
			setFuncionarioLogado(usuario);
			hideStatusDialog();
			redirect("solicitacoes.xhtml", "Bem vindo, " + funcionarioLogado.getNome() + "! ");
		} else {
			hideStatusDialog();
			showErrorMessage("Falha ao autenticar dados. Consulte o suporte da ferramenta.");
		}
	}

	public void deslogar() {
		setFuncionarioLogado(null);
		;
		showStatusDialog();
		PrimeFaces.current().executeScript("logout();");
		redirect("index.xhtml", "Obrigado por utilizar nossos serviços.");
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setDadosFuncionais(DadosFuncionais dadosFuncionais) {
		this.dadosFuncionais = dadosFuncionais;
	}

	public DadosFuncionais getDadosFuncionais() {
		return dadosFuncionais;
	}

	public void setFuncionarioLogado(Usuario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	@Produces
	@Named("funcionarioLogado")
	public Usuario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	@Produces
	@Named("usuarios")
	public List<Usuario> getUsuarios() {
		if (usuarios == null)
			listar();
		return usuarios;
	}

	public void setFuncionarios(List<Usuario> funcionarios) {
		this.funcionarios = funcionarios;
	}

	@Produces
	@Named("funcionarios")
	public List<Usuario> getFuncionarios() {
		if (funcionarios == null)
			listar();
		return funcionarios;
	}

	public List<TipoUsuario> getTiposUsuario() {
		return tiposUsuario;
	}

	public void setTiposUsuario(List<TipoUsuario> tiposUsuario) {
		this.tiposUsuario = tiposUsuario;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}

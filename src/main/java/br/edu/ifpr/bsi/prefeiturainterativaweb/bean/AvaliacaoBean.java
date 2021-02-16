package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Named("avaliacaoBean")
@ViewScoped
@SuppressWarnings("serial")
public class AvaliacaoBean extends AbstractBean {

	private Solicitacao solicitacao;
	private List<Solicitacao> solicitacoes;

	private String center;

	@Inject
	@Named("funcionarioLogado")
	private Usuario funcionarioLogado;

	@Inject
	@Named("categorias")
	private List<Categoria> categorias;

	@Inject
	@Named("usuarios")
	private List<Usuario> usuarios;

	@Inject
	@Named("funcionarios")
	private List<Usuario> funcionarios;

	@Inject
	@Named("atendimentos")
	private List<Atendimento> atendimentos;

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@PostConstruct
	public void init() {
		if (solicitacao == null)
			solicitacao = new Solicitacao();

//TODO FILTRAR SOLICITAÇÕES POR DEPARTAMENTO
		solicitacoes = SolicitacaoDAO.getAll();
		if (solicitacoes == null) {
			hideStatusDialog();
			solicitacoes = new ArrayList<>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
	}

	public void cadastrar() {
		solicitacao = new Solicitacao();
		solicitacao.set_ID(UUID.randomUUID().toString());
	}

	public List<Solicitacao> listar() {
		solicitacoes.removeIf(solicitacao -> solicitacao.getAvaliacao() == null);
		solicitacoes.forEach((aux) -> {
			aux.setLocalCidadao(usuarios.get(usuarios.indexOf(new Usuario(aux.getUsuario_ID()))));
			aux.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID()))));
			if (aux.getFuncionarioConclusao_ID() != null)
				aux.setLocalFuncionarioConclusao(
						funcionarios.get(funcionarios.indexOf(new Usuario(aux.getFuncionarioConclusao_ID()))));
		});

		hideStatusDialog();
		return solicitacoes;
	}

	public void selecionar(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
		hideStatusDialog();
	}

	public Usuario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Usuario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setSolicitacoes(List<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public String getCenter() {
		if (center == null || center.isEmpty())
			center = "-26.484335, -51.991754";
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

}

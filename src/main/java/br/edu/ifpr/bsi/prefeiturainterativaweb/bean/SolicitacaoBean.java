package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Named("solicitacaoBean")
@ViewScoped
@SuppressWarnings("serial")
public class SolicitacaoBean extends AbstractBean {

	private Atendimento atendimento;
	private Solicitacao solicitacao;
	private List<Solicitacao> solicitacoes;

	@Inject
	@Named("funcionarioLogado")
	private Usuario funcionarioLogado;

	@Inject
	@Named("categorias")
	private List<Categoria> categorias;

	@Inject
	@Named("usuarios")
	private List<Usuario> usuarios;

	@Override
	@PostConstruct
	public void init() {
		if (solicitacao == null)
			solicitacao = new Solicitacao();

		solicitacoes = SolicitacaoDAO.getAll();
		if (solicitacoes == null) {
			hideStatusDialog();
			solicitacoes = new ArrayList<>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
	}

	@Override
	public void cadastrar() {
		solicitacao = new Solicitacao();
	}

	@Override
	public List<Solicitacao> listar() {
		solicitacoes.forEach((aux) -> {
			List<Categoria> localCategorias = new ArrayList<>();
			aux.getCategorias().forEach((string) -> {
				if (categorias != null && categorias.contains(new Categoria(string)))
					localCategorias.add(categorias.get(categorias.indexOf(new Categoria(string))));
			});
			aux.setLocalCidadao(usuarios.get(usuarios.indexOf(new Usuario(aux.getUsuario_ID()))));
			localCategorias.sort((Categoria o1, Categoria o2) -> o1.getDescricao().compareTo(o2.getDescricao()));
			aux.setLocalCategorias(localCategorias);
		});

		hideStatusDialog();
		return solicitacoes;
	}

	@Override
	public void selecionar(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
	}

	@Override
	public void salvarEditar() {
		if (SolicitacaoDAO.merge(solicitacao)) {
			hideStatusDialog();
			solicitacao = new Solicitacao();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void removerDesabilitar(ActionEvent evento) {

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

	@Produces
	@Named("solicitacoes")
	public List<Solicitacao> getSolicitacoes() {
		if (solicitacoes == null)
			init();
		return solicitacoes;
	}

	public void setSolicitacoes(List<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

}

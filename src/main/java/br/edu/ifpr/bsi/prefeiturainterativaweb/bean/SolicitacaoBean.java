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
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.DadosFuncionais;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;

@Named("solicitacaoBean")
@ViewScoped
@SuppressWarnings("serial")
public class SolicitacaoBean extends AbstractBean {

	private Solicitacao solicitacao;
	private Atendimento atendimento;

	@Inject
	@Named("funcionarioLogado")
	private DadosFuncionais funcionarioLogado;

	@Produces
	@Named("solicitacoes")
	private List<Solicitacao> solicitacoes;

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

	public DadosFuncionais getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(DadosFuncionais funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public List<Solicitacao> getSolicitacoes() {
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

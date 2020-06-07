package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;

@Named("solicitacaoBean")
@ViewScoped
@SuppressWarnings("serial")
public class SolicitacaoBean extends AbstractBean {

	@Inject
	@Named("funcionarioLogado")
	private Funcionario funcionarioLogado;

	private List<Solicitacao> solicitacoes;

	private Solicitacao solicitacao;
	private Atendimento atendimento;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (solicitacao == null)
			solicitacao = new Solicitacao();

		solicitacoes = SolicitacaoDAO.getAll();
		if (solicitacoes == null) {
			hideStatusDialog();
			solicitacoes = new ArrayList<>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		}
		hideStatusDialog();
	}

	@Override
	public void selecionar(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
	}

	@Override
	public void salvar() {
		showStatusDialog();
		if (SolicitacaoDAO.merge(solicitacao)) {
			hideStatusDialog();
			solicitacao = new Solicitacao();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			hideStatusDialog();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
	}

	@Override
	public void remover() {

	}

	public Funcionario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Funcionario funcionarioLogado) {
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

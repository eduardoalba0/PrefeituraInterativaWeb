package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;
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
public class SolicitacaoBean implements Serializable {

	@Inject
	@Named("funcionarioLogado")
	private Funcionario funcionarioLogado;

	private List<Solicitacao> solicitacoes;

	private Solicitacao solicitacao;
	private Atendimento atendimento;

	@PostConstruct
	public void listar() {

		if (solicitacao == null)
			solicitacao = new Solicitacao();

		solicitacoes = SolicitacaoDAO.getAll();
		if (solicitacoes == null) {
			solicitacoes = new ArrayList<>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public void selecionar(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
	}

	public void salvar() {
		if (SolicitacaoDAO.merge(solicitacao)) {
			solicitacao = new Solicitacao();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
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

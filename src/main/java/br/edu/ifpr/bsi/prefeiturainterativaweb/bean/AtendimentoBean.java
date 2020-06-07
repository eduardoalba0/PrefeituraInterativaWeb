package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("atendimentoBean")
@ViewScoped
@SuppressWarnings("serial")
public class AtendimentoBean extends AbstractBean {

	@Inject
	@Named("funcionarioLogado")
	private Funcionario funcionarioLogado;

	private List<Atendimento> atendimentos;

	private Atendimento atendimento;
	private Solicitacao solicitacao;

	@PostConstruct
	public void init() {
		if (atendimento == null)
			atendimento = new Atendimento();

		atendimentos = AtendimentoDAO.getAll();

		if (atendimentos == null) {
			atendimentos = new ArrayList<Atendimento>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public void selecionar(ActionEvent evento) {
		atendimento = (Atendimento) evento.getComponent().getAttributes().get("atendimentoSelecionada");
	}

	public void salvar() {
		if (AtendimentoDAO.merge(atendimento)) {
			atendimento = new Atendimento();
			Aviso aviso = new Aviso();
			aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
			aviso.setTitulo(atendimento.getAcao());
			aviso.setCorpo(atendimento.getResposta());
			aviso.setData(new Date());
			aviso.setSolicitacao_ID(solicitacao.get_ID());
			aviso.setToken(solicitacao.getUsuario().getToken());
			FirebaseHelper.enviarNotificacao(aviso);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
	}

	@Override
	public void remover() {
		// TODO Auto-generated method stub

	}

	public Funcionario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Funcionario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}
}

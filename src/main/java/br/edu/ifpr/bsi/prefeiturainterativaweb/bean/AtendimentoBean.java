package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("atendimentoBean")
@ViewScoped
@SuppressWarnings("serial")
public class AtendimentoBean extends AbstractBean {

	private Atendimento atendimento;
	private Solicitacao solicitacao;
	private List<Atendimento> atendimentos;

	@Inject
	@Named("funcionarioLogado")
	private Usuario funcionarioLogado;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (atendimento == null)
			atendimento = new Atendimento();

		atendimentos = AtendimentoDAO.getAll();

		if (atendimentos == null) {
			hideStatusDialog();
			atendimentos = new ArrayList<Atendimento>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
	}

	@Override
	public void cadastrar() {
		atendimento = new Atendimento();
		atendimento.setFuncionario(funcionarioLogado);
	}

	@Override
	public List<Atendimento> listar() {
		return atendimentos;
	}

	@Override
	public void selecionar(ActionEvent evento) {
		atendimento = (Atendimento) evento.getComponent().getAttributes().get("atendimentoSelecionada");
	}

	@Override
	public void salvarEditar() {
		showStatusDialog();
		if (AtendimentoDAO.merge(atendimento)) {
			atendimento = new Atendimento();
			Aviso aviso = new Aviso();
			aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
			aviso.setTitulo(atendimento.getAcao());
			aviso.setCorpo(atendimento.getResposta());
			aviso.setData(new Date());
			aviso.setSolicitacao_ID(solicitacao.get_ID());
			aviso.setToken(solicitacao.getLocalCidadao().getToken());
			FirebaseHelper.enviarNotificacao(aviso);
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void removerDesabilitar(ActionEvent evento) {
		// TODO Auto-generated method stub

	}

	public Usuario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Usuario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	@Produces
	@Named("atendimentos")
	public List<Atendimento> getAtendimentos() {
		if (atendimentos == null)
			init();
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

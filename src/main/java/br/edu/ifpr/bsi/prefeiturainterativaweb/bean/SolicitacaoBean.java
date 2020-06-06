package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("solicitacaoBean")
@SessionScoped
@SuppressWarnings("serial")
public class SolicitacaoBean implements Serializable {

	@Inject
	@Named("#{funcionarioLogado}")
	private Funcionario funcionarioLogado;
	private Solicitacao solicitacao;
	private Atendimento atendimento;
	private List<Solicitacao> solicitacoes;

	@PostConstruct
	public void listar() {
		try {
			solicitacoes = new ArrayList<>();
			for (QueryDocumentSnapshot objSnapshot : SolicitacaoDAO.getAll().get().getDocuments()) {
				Solicitacao snapshot = objSnapshot.toObject(Solicitacao.class);
				snapshot.setUsuario(UsuarioDAO.get(snapshot.getUsuario_ID()).get().toObject(Usuario.class));
				snapshot.setDepartamento(
						DepartamentoDAO.get(snapshot.getDepartamento_ID()).get().toObject(Departamento.class));
				snapshot.setLocalCategorias(
						CategoriaDAO.getAll(snapshot.getCategorias()).get().toObjects(Categoria.class));
				solicitacoes.add(snapshot);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void responder(ActionEvent evento) {
		System.out.println(funcionarioLogado.getDepartamento_ID());
		System.out.println(funcionarioLogado.getDepartamento_ID());
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
		atendimento = new Atendimento();
		atendimento.set_ID(UUID.randomUUID().toString());
		atendimento.setSolicitacao_ID(solicitacao.get_ID());
		atendimento.setFuncionario_ID(funcionarioLogado.get_ID());

	}

	public void salvar() {
		try {

			AtendimentoDAO.merge(atendimento).get();
			List<String> atendimentos = solicitacao.getAtendimentos();

			if (atendimentos == null)
				atendimentos = new ArrayList<>();
			atendimentos.add(atendimento.get_ID());
			solicitacao.setAtendimentos(atendimentos);

			SolicitacaoDAO.merge(solicitacao);

			Aviso aviso = new Aviso();
			aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
			aviso.setTitulo(atendimento.getAcao());
			aviso.setCorpo(atendimento.getResposta());
			aviso.setData(new Date());
			aviso.setSolicitacao_ID(solicitacao.get_ID());
			aviso.setToken(solicitacao.getUsuario().getToken());

			FirebaseHelper.enviarNotificacao(aviso);

		} catch (Exception ex) {
			ex.printStackTrace();
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

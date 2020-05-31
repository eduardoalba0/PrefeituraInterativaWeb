package br.edu.ifpr.bsi.prefeiturainterativaweb.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Usuario;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class SolicitacaoController implements Serializable {

	private List<Solicitacao> solicitacoes;
	private Solicitacao solicitacao;
	private Atendimento atendimento;

	@PostConstruct
	public void listar() {
		solicitacoes = new ArrayList<>();
		try {
			for (QueryDocumentSnapshot objSnapshot : SolicitacaoDAO.getAll().get().getDocuments()) {
				Solicitacao snapshot = objSnapshot.toObject(Solicitacao.class);
				snapshot.setUsuario(UsuarioDAO.get(snapshot.getUsuario_ID()).get().toObject(Usuario.class));
				snapshot.setDepartamento(DepartamentoDAO.get(snapshot.getDepartamento_ID()).get().toObject(Departamento.class));
				snapshot.setLocalCategorias(CategoriaDAO.getAll(snapshot.getCategorias()).get().toObjects(Categoria.class));
				solicitacoes.add(snapshot);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void visualizar(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("usuarioSelecionado");
		atendimento = new Atendimento();
		atendimento.set_ID(UUID.randomUUID().toString());
		atendimento.setSolicitacao_ID(solicitacao.get_ID());
		atendimento.setFuncionario_ID("jwK2FwfCHjQA9m6Axi74BXfNTcx2");

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

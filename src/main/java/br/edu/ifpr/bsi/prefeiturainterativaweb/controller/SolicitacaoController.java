package br.edu.ifpr.bsi.prefeiturainterativaweb.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Solicitacao;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class SolicitacaoController implements Serializable {

	private Solicitacao solicitacao;
	private List<Solicitacao> solicitacoes;

	@PostConstruct
	public void listar() {
		solicitacoes = new ArrayList<>();
		try {
			for (QueryDocumentSnapshot objSnapshot : SolicitacaoDAO.listar().get().getDocuments()) {
				solicitacoes.add(objSnapshot.toObject(Solicitacao.class));
			}
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

}

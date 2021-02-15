package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Avaliacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;

@Entity
@Table(name = "tb_dim_avaliacao")
@SuppressWarnings("serial")
public class Dim_Avaliacao extends GenericDomain {

	@Column
	private float nota;
	@Column
	private boolean solucionada;

	public Dim_Avaliacao() {

	}

	public Dim_Avaliacao(Solicitacao solicitacao) {
		this.set_ID(solicitacao.get_ID());
		Avaliacao avaliacao = solicitacao.getAvaliacao();
		if (avaliacao != null) {
			this.nota = avaliacao.getNota();
			this.solucionada = avaliacao.isSolucionada();
		}
	}

	public float getNota() {
		return nota;
	}

	public void setNota(float nota) {
		this.nota = nota;
	}

	public boolean isSolucionada() {
		return solucionada;
	}

	public void setSolucionada(boolean solucionada) {
		this.solucionada = solucionada;
	}

}
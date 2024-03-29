package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.ServerTimestamp;

@SuppressWarnings("serial")
public class Solicitacao implements Serializable {

	public Solicitacao() {
		urlImagens = new ArrayList<>();
		categorias = new ArrayList<>();
		atendimentos = new ArrayList<>();
	}

	public Solicitacao(String _ID) {
		this._ID = _ID;
		urlImagens = new ArrayList<>();
		categorias = new ArrayList<>();
		atendimentos = new ArrayList<>();
	}

	private String _ID;
	private String descricao;
	private String usuario_ID;
	private String departamento_ID;
	private String funcionarioConclusao_ID;
	private boolean concluida;
	private boolean anonima;
	private boolean staged;
	private List<String> urlImagens;
	private List<String> categorias;
	private List<String> atendimentos;
	private Localizacao localizacao;
	private Avaliacao avaliacao;
	private Date dataConclusao;

	@ServerTimestamp
	private Date dataAbertura;
	@Exclude
	private Departamento localDepartamento;
	@Exclude
	private Usuario localCidadao;
	@Exclude
	private Usuario localFuncionarioConclusao;
	@Exclude
	private String dataAberturaString;
	@Exclude
	private String dataConclusaoString;
	@Exclude
	private List<Categoria> localCategorias;
	@Exclude
	private List<String> localCaminhoImagens;
	@Exclude
	private List<Atendimento> localAtendimentos;

//---------------------- Encapsulamento ----------------------

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public boolean isConcluida() {
		return concluida;
	}

	public void setConcluida(boolean concluida) {
		this.concluida = concluida;
	}

	public boolean isAnonima() {
		return anonima;
	}

	public void setAnonima(boolean anonima) {
		this.anonima = anonima;
	}

	public boolean isStaged() {
		return staged;
	}

	public void setStaged(boolean staged) {
		this.staged = staged;
	}

	public List<String> getUrlImagens() {
		return urlImagens;
	}

	public void setUrlImagens(List<String> urlImagens) {
		this.urlImagens = urlImagens;
	}

	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

	public String getUsuario_ID() {
		return usuario_ID;
	}

	public void setUsuario_ID(String usuario_ID) {
		this.usuario_ID = usuario_ID;
	}

	public String getFuncionarioConclusao_ID() {
		return funcionarioConclusao_ID;
	}

	public void setFuncionarioConclusao_ID(String funcionarioConclusao_ID) {
		this.funcionarioConclusao_ID = funcionarioConclusao_ID;
	}

	public Date getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Date data) {
		if (dataAberturaString == null || dataAberturaString.isEmpty()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale("pt", "BR"));
			dataAberturaString = df.format(data);
		}
		this.dataAbertura = data;
	}

	public Date getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Date dataConclusao) {
		if (dataConclusao != null)
			if (dataConclusaoString == null || dataConclusaoString.isEmpty()) {
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
						new Locale("pt", "BR"));
				dataConclusaoString = df.format(dataConclusao);
			}
		this.dataConclusao = dataConclusao;
	}

	@Exclude
	public String getDataAberturaString() {
		return dataAberturaString;
	}

	@Exclude
	public String getDataConclusaoString() {
		return dataConclusaoString;
	}

	public void setDataAberturaString(String dataTempoString) {
		this.dataAberturaString = dataTempoString;
	}

	public String getDepartamento_ID() {
		return departamento_ID;
	}

	public void setDepartamento_ID(String departamento_ID) {
		this.departamento_ID = departamento_ID;
	}

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	public List<String> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<String> atendimentos) {
		this.atendimentos = atendimentos;
	}

	@Exclude
	public Departamento getLocalDepartamento() {
		return localDepartamento;
	}

	public void setLocalDepartamento(Departamento departamento) {
		if (departamento != null && departamento.get_ID() != null)
			this.departamento_ID = departamento.get_ID();
		this.localDepartamento = departamento;
	}

	@Exclude
	public List<Categoria> getLocalCategorias() {
		if (localCategorias != null)
			localCategorias.sort((Categoria o1, Categoria o2) -> o1.getDescricao().compareTo(o2.getDescricao()));
		return localCategorias;
	}

	public void setLocalCategorias(List<Categoria> localCategorias) {
		this.localCategorias = localCategorias;
	}

	@Exclude
	public List<String> getLocalCaminhoImagens() {
		return localCaminhoImagens;
	}

	public void setLocalCaminhoImagens(List<String> localCaminhoImagens) {
		this.localCaminhoImagens = localCaminhoImagens;
	}

	@Exclude
	public Usuario getLocalCidadao() {
		if (this.anonima && localCidadao != null)
			localCidadao.setNome("Anônimo");
		return localCidadao;
	}

	public void setLocalCidadao(Usuario usuario) {
		if (usuario != null && usuario.get_ID() != null)
			this.usuario_ID = usuario.get_ID();
		this.localCidadao = usuario;
	}

	@Exclude
	public Usuario getLocalFuncionarioConclusao() {
		return localFuncionarioConclusao;
	}

	public void setLocalFuncionarioConclusao(Usuario funcionarioConclusao) {
		this.localFuncionarioConclusao = funcionarioConclusao;
	}

	@Exclude
	public List<Atendimento> getLocalAtendimentos() {
		if (localAtendimentos != null)
			localAtendimentos.sort((Atendimento o1, Atendimento o2) -> o1.getData().compareTo(o2.getData()));
		return localAtendimentos;
	}

	public void setLocalAtendimentos(List<Atendimento> localAtendimentos) {
		this.localAtendimentos = localAtendimentos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Solicitacao solicitacao = (Solicitacao) o;
		return _ID.equals(solicitacao._ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_ID);
	}

	@Nonnull
	@Override
	public String toString() {
		return this.descricao;
	}
}

package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QuerySnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class SolicitacaoDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Solicitacoes");
	}

	public static boolean merge(Solicitacao solicitacao) {
		init();
		solicitacao.setStaged(false);
		return DatabaseHelper.merge(reference.document(solicitacao.get_ID()), solicitacao) != null;
	}

	public static boolean update(Solicitacao solicitacao, String campo, Object valor) {
		init();
		return DatabaseHelper.update(reference.document(solicitacao.get_ID()), campo, valor) != null;
	}

	public static boolean remove(Solicitacao solicitacao) {
		init();
		return DatabaseHelper.remove(reference.document(solicitacao.get_ID())) != null;
	}

	public static boolean remove(String solicitacao_ID) {
		init();
		return DatabaseHelper.remove(reference.document(solicitacao_ID)) != null;
	}

	public static Solicitacao get(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(Solicitacao.class);
	}

	public static List<Solicitacao> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("dataAbertura", Direction.ASCENDING));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Solicitacao.class);
	}

	public static List<Solicitacao> getAllPorStatus(boolean status) {
		init();
		QuerySnapshot lista = DatabaseHelper
				.getAll(reference.whereEqualTo("concluida", status).orderBy("dataAbertura", Query.Direction.ASCENDING));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Solicitacao.class);
	}

	public static List<Solicitacao> getAllPorCategoria(String categoria_ID) {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.whereArrayContains("categorias", categoria_ID));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Solicitacao.class);
	}

	public static List<Solicitacao> getAllPorDepartamento(String departamento_ID) {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(
				reference.whereEqualTo("departamento_ID", departamento_ID).orderBy("data", Query.Direction.ASCENDING));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Solicitacao.class);
	}

	public static List<Solicitacao> getAllPorDepartamentoStatus(String departamento_ID, boolean status) {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.whereEqualTo("departamento_ID", departamento_ID)
				.whereEqualTo("concluida", status).orderBy("data", Query.Direction.ASCENDING));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Solicitacao.class);
	}

}

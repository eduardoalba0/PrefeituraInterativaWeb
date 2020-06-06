package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class SolicitacaoDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Solicitacoes");
	}

	public static ApiFuture<QuerySnapshot> getAll() {
		init();
		return DatabaseHelper.getAll(reference);
	}

	public static ApiFuture<QuerySnapshot> getAllPorStatus(boolean status) {
		init();
		return DatabaseHelper
				.getQuery(reference.whereEqualTo("concluida", status).orderBy("data", Query.Direction.ASCENDING));
	}

	public static ApiFuture<QuerySnapshot> getAllPorDepartamento(String departamento_ID) {
		init();
		return DatabaseHelper.getQuery(
				reference.whereEqualTo("departamento_ID", departamento_ID).orderBy("data", Query.Direction.ASCENDING));
	}

	public static ApiFuture<QuerySnapshot> getAllPorDepartamentoStatus(String departamento_ID, boolean status) {
		init();
		return DatabaseHelper.getQuery(reference.whereEqualTo("departamento_ID", departamento_ID)
				.whereEqualTo("concluida", status).orderBy("data", Query.Direction.ASCENDING));
	}

	public static ApiFuture<WriteResult> merge(Solicitacao solicitacao) {
		init();
		return DatabaseHelper.merge(reference.document(solicitacao.get_ID()), solicitacao);
	}

}

package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class SolicitacaoDAO {

	private static CollectionReference reference;

	private static void init() throws Exception {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Solicitacoes");
	}

	public static ApiFuture<QuerySnapshot> listar() throws Exception {
		init();
		return DatabaseHelper.getAll(reference);
	}
}

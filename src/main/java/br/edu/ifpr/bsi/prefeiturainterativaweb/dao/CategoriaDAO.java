package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class CategoriaDAO {

	private static CollectionReference reference;

	private static void init() throws Exception {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Categorias");
	}

	public static ApiFuture<QuerySnapshot> getAll() throws Exception {
		init();
		return DatabaseHelper.getAll(reference);
	}
	
	public static ApiFuture<QuerySnapshot> getAll(List<String> ids) throws Exception {
		init();
		return DatabaseHelper.getQuery(reference
				.orderBy("descricao")
                .whereIn("_ID", ids)
                .whereEqualTo("habilitada", true));
	}

	public static ApiFuture<WriteResult> merge(Categoria categoria) throws Exception {
		init();
		return DatabaseHelper.merge(reference.document(categoria.get_ID()), categoria);
	}
	
}

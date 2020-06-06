package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class UsuarioDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Usuarios");
	}

	public static ApiFuture<QuerySnapshot> getAll() {
		init();
		return DatabaseHelper.getAll(reference);
	}

	public static ApiFuture<WriteResult> merge(Usuario usuario) {
		init();
		return DatabaseHelper.merge(reference.document(usuario.get_ID()), usuario);
	}

	public static ApiFuture<DocumentSnapshot> get(String _ID) {
		init();
		return DatabaseHelper.get(reference.document(_ID));
	}
}

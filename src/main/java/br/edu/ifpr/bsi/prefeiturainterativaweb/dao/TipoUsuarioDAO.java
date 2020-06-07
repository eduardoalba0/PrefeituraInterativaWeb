package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class TipoUsuarioDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("TiposUsuario");
	}

	public static boolean merge(TipoUsuario tipoUsuario) {
		init();
		return DatabaseHelper.merge(reference.document(tipoUsuario.get_ID()), tipoUsuario) != null;
	}

	public static boolean remove(TipoUsuario tipoUsuario) {
		init();
		return DatabaseHelper.remove(reference.document(tipoUsuario.get_ID())) != null;
	}

	public static boolean remove(String tipoUsuario_ID) {
		init();
		return DatabaseHelper.remove(reference.document(tipoUsuario_ID)) != null;
	}

	public static TipoUsuario get(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(TipoUsuario.class);
	}

	public static List<TipoUsuario> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("descricao"));
		if (lista == null)
			return null;
		else
			return lista.toObjects(TipoUsuario.class);
	}
}

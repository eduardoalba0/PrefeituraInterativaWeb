package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class UsuarioDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Usuarios");
	}

	public static boolean merge(Usuario usuario) {
		init();
		return DatabaseHelper.merge(reference.document(usuario.get_ID()), usuario) != null;
	}

	public static boolean remove(Usuario usuario) {
		init();
		return DatabaseHelper.remove(reference.document(usuario.get_ID())) != null;
	}

	public static Boolean remove(String usuario_ID) {
		init();
		return DatabaseHelper.remove(reference.document(usuario_ID)) != null;
	}

	public static Usuario get(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(Usuario.class);
	}

	public static List<Usuario> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference);
		if (lista == null)
			return null;
		else
			return lista.toObjects(Usuario.class);
	}

	public static List<Usuario> getAllFuncionarios() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.whereGreaterThan("dadosFuncionais", ""));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Usuario.class);
	}

	public static List<Usuario> getAllPorTipo(String tipo_ID) {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.whereEqualTo("tipoUsuario_ID", tipo_ID));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Usuario.class);
	}
}

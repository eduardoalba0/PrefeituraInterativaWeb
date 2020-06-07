package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
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

	public static boolean merge(Funcionario usuario) {
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

	public static Funcionario getFuncionario(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(Funcionario.class);
	}

	public static List<Usuario> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("nome", Direction.ASCENDING));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Usuario.class);
	}

	public static List<Usuario> getAllPorTipo(TipoUsuario tipo) {
		init();
		QuerySnapshot lista = DatabaseHelper
				.getAll(reference.orderBy("nome", Direction.ASCENDING).whereEqualTo("tipoUsuario_ID", tipo.get_ID()));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Usuario.class);
	}

	public static List<Funcionario> getAllFuncionarios() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("nome", Direction.ASCENDING)
				.whereLessThan("tipoUsuario_ID", "6b395be8-a7c1-4971-8dc0-afa04be63a00")
				.whereGreaterThan("tipoUsuario_ID", "6b395be8-a7c1-4971-8dc0-afa04be63a00"));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Funcionario.class);
	}
}

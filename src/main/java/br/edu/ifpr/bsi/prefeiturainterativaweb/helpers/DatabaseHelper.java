package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import java.io.FileInputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class DatabaseHelper {
	private Firestore db;

	public DatabaseHelper() throws Exception {
		if (FirebaseApp.getApps().isEmpty()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(ec.getResourceAsStream("/WEB-INF/firebase-credentials.json")))
					.setDatabaseUrl("https://prefeiturainterativa.firebaseio.com")
					.build();
			FirebaseApp.initializeApp(options);
		}
		db = FirestoreClient.getFirestore();
	}

	public <T> ApiFuture<WriteResult> inserirAtualizar(DocumentReference reference, T objeto) {

		return reference.set(objeto);

		/*
		 * .addOnCompleteListener(context, task -> { if (!task.isSuccessful()) { if
		 * (task.getException().toString().
		 * contains("FirebaseFirestoreException: PERMISSION_DENIED")) new
		 * SweetAlertDialog(context,
		 * SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
		 * .setContentText(context.getString(R.string.str_erro_db_permissao_gravar)).
		 * show(); else new SweetAlertDialog(context,
		 * SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
		 * .setContentText(context.getString(R.string.str_falha_inserir_db)).show(); }
		 * });
		 */
	}

	public ApiFuture<DocumentSnapshot> get(DocumentReference reference) {
		return reference.get();
				/*.addOnCompleteListener(context, task -> {
			if (!task.isSuccessful())
				if (task.getException().toString().contains("FirebaseFirestoreException: PERMISSION_DENIED"))
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
							.setContentText(context.getString(R.string.str_erro_db_permissao_consultar)).show();
				else
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
							.setContentText(context.getString(R.string.str_falha_consultar_db)).show();
		});*/
	}

	public ApiFuture<QuerySnapshot> getAll(CollectionReference reference) {
		return reference.get();
				/*.addOnCompleteListener(context, task -> {
			if (!task.isSuccessful())
				if (task.getException().toString().contains("FirebaseFirestoreException: PERMISSION_DENIED"))
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
							.setContentText(context.getString(R.string.str_erro_db_permissao_consultar)).show();
				else
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
							.setContentText(context.getString(R.string.str_falha_consultar_db)).show();
		});*/
	}

	public ApiFuture<QuerySnapshot> getQuery(Query query) {
		return query.get();
				/*.addOnCompleteListener(context, task -> {
			if (!task.isSuccessful())
				if (task.getException().toString().contains("FirebaseFirestoreException: PERMISSION_DENIED"))
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
							.setContentText(context.getString(R.string.str_erro_db_permissao_consultar)).show();
				else
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(R.string.str_erro)
							.setContentText(context.getString(R.string.str_falha_consultar_db)).show();

		});*/
	}

	public Firestore getDb() {
		return db;
	}
	
}

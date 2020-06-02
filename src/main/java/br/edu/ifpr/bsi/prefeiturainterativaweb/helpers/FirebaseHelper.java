package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

public class FirebaseHelper {
	// TODO IMPLEMENTAR AUTENTICAÇÃO COM JS

	private static FirebaseApp firebaseApp;
	private static FirebaseAuth auth;
	private static Firestore database;
	private static FirebaseMessaging messaging;
	private static StorageClient storage;

	private static void init() throws Exception {
		if (firebaseApp == null) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(
							GoogleCredentials.fromStream(ec.getResourceAsStream("/WEB-INF/firebase-credentials.json")))
					.setDatabaseUrl("prefeiturainterativa.firebaseio.com")
					.setStorageBucket("prefeiturainterativa.firebaseio.com").build();
			firebaseApp = FirebaseApp.initializeApp(options);
			auth = FirebaseAuth.getInstance(firebaseApp);
			database = FirestoreClient.getFirestore(firebaseApp);
			messaging = FirebaseMessaging.getInstance(firebaseApp);
			storage = StorageClient.getInstance(firebaseApp);
		}
	}

	public static ApiFuture<UserRecord> cadastrarUsuario(Usuario usuario) throws Exception {
		init();
		return auth.createUserAsync(new CreateRequest().setEmail(usuario.getEmail()).setPassword(usuario.getSenha())
				.setDisplayName(usuario.getNome()).setPhotoUrl(usuario.getUriFoto()).setEmailVerified(false)
				.setDisabled(!usuario.isHabilitado()));
	}

	public static ApiFuture<UserRecord> alterarUsuario(Usuario usuario) throws Exception {
		init();
		return auth.updateUserAsync(new UpdateRequest(usuario.get_ID()).setEmail(usuario.getEmail())
				.setPassword(usuario.getSenha()).setDisplayName(usuario.getNome()).setPhotoUrl(usuario.getUriFoto())
				.setEmailVerified(false).setDisabled(!usuario.isHabilitado()));
	}

	public static ApiFuture<String> verificarEmail(String email) throws Exception {
		init();
		return auth.generateEmailVerificationLinkAsync(email);
	}

	public static ApiFuture<String> redefinirSenha(String email) throws Exception {
		init();
		return auth.generatePasswordResetLinkAsync(email);
	}

	public static void carregarImagemUsuario(Usuario usuario) throws Exception {
		init();
		storage.bucket("Usuarios").create(usuario.get_ID(), Files.readAllBytes(Paths.get(usuario.getLocalUriFoto())));
	}

	public static void carregarAnexos(Usuario usuario, URI uri) throws Exception {
		init();
		storage.bucket("Solicitacoes").create(usuario.get_ID(), Files.readAllBytes(Paths.get(uri)));
	}

	public static void enviarNotificacao(Aviso aviso) throws Exception{
		init();
		Notification notification = Notification.builder()
				.setTitle(aviso.getTitulo())
				.setBody(aviso.getCorpo())
				.build();
		
		messaging.sendAsync(Message.builder()
				.putData("Categoria" , aviso.getCategoria())
				.putData("Solicitacao", aviso.getSolicitacao_ID())
				.setToken(aviso.getToken())
				.setNotification(notification)
				.build());
	}

	public static Firestore getDatabase() throws Exception {
		init();
		return database;
	}

}

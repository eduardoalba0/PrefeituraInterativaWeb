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

	private static FirebaseApp firebaseApp;
	private static FirebaseAuth auth;
	private static Firestore database;
	private static FirebaseMessaging messaging;
	private static StorageClient storage;

	private static void init() {
		try {
			if (firebaseApp == null) {
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials
								.fromStream(ec.getResourceAsStream("/WEB-INF/firebase-credentials.json")))
						.setDatabaseUrl("prefeiturainterativa.firebaseio.com")
						.setStorageBucket("prefeiturainterativa.firebaseio.com").build();
				firebaseApp = FirebaseApp.initializeApp(options);
				auth = FirebaseAuth.getInstance(firebaseApp);
				database = FirestoreClient.getFirestore(firebaseApp);
				messaging = FirebaseMessaging.getInstance(firebaseApp);
				storage = StorageClient.getInstance(firebaseApp);
			}
		} catch (Exception ex) {
			System.out.println("Falha ao iniciar o Firebase.");
		}
	}

	public static UserRecord buscarUsuario(String uid) {
		init();
		UserRecord record = null;
		try {
			record = auth.getUser(uid);
		} catch (Exception ex) {
			System.out.println("Falha ao buscar usuário");
		}
		return record;
	}

	public static UserRecord buscarUsuario(UserRecord userRecord) {
		return buscarUsuario(userRecord.getUid());
	}

	public static UserRecord buscarUsuario(Usuario usuario) {
		return buscarUsuario(usuario.get_ID());
	}

	public static ApiFuture<UserRecord> cadastrarUsuario(Usuario usuario) {
		init();
		return auth.createUserAsync(new CreateRequest().setEmail(usuario.getEmail()).setPassword(usuario.getSenha())
				.setDisplayName(usuario.getNome()).setPhotoUrl(usuario.getUriFoto()).setEmailVerified(false)
				.setDisabled(!usuario.isHabilitado()));
	}

	public static ApiFuture<UserRecord> alterarUsuario(Usuario usuario) {
		init();
		return auth.updateUserAsync(new UpdateRequest(usuario.get_ID()).setEmail(usuario.getEmail())
				.setPassword(usuario.getSenha()).setDisplayName(usuario.getNome()).setPhotoUrl(usuario.getUriFoto())
				.setEmailVerified(false).setDisabled(!usuario.isHabilitado()));
	}

	public static void carregarImagemUsuario(Usuario usuario) throws Exception {
		init();
		storage.bucket("Usuarios").create(usuario.get_ID(), Files.readAllBytes(Paths.get(usuario.getLocalUriFoto())));
	}

	public static void carregarAnexos(Usuario usuario, URI uri) throws Exception {
		init();
		storage.bucket("Solicitacoes").create(usuario.get_ID(), Files.readAllBytes(Paths.get(uri)));
	}

	public static void enviarNotificacao(Aviso aviso) throws Exception {
		init();
		Notification notification = Notification.builder().setTitle(aviso.getTitulo()).setBody(aviso.getCorpo())
				.build();
		messaging.sendAsync(Message.builder().putData("Categoria", aviso.getCategoria())
				.putData("Solicitacao", aviso.getSolicitacao_ID()).setToken(aviso.getToken())
				.setNotification(notification).build());
	}

	public static boolean userEquals(UserRecord user, UserRecord outroUser) {
		if (user == null || outroUser == null)
			return false;
		if (user.isDisabled() || outroUser.isDisabled())
			return false;
		if (!user.isEmailVerified() || !outroUser.isEmailVerified())
			return false;
		if (!outroUser.getDisplayName().equals(user.getDisplayName()))
			return false;
		if (!outroUser.getEmail().equals(user.getEmail()))
			return false;
		if (!outroUser.getProviderId().equals(user.getProviderId()))
			return false;
		if (outroUser.getPhotoUrl() != user.getPhotoUrl())
			return false;
		if (outroUser.getPhoneNumber() != user.getPhoneNumber())
			return false;
		return true;
	}

	protected static Firestore getDatabase() {
		init();
		return database;
	}

}

var firebaseApp;
var auth;

function initFirebase() {
	if (firebaseApp == null){
		var firebaseConfig = {
			apiKey: "AIzaSyCG8HM6i3TOTOdHAbmKU0TtZjou7hZVxms",
			authDomain: "prefeiturainterativa.firebaseapp.com",
			databaseURL: "https://prefeiturainterativa.firebaseio.com",
			projectId: "prefeiturainterativa",
			storageBucket: "prefeiturainterativa.appspot.com",
			messagingSenderId: "977874090377",
			appId: "1:977874090377:web:d96d5491f89fa72c468958",
			measurementId: "G-V5H2DKDLSH"
		};
		firebaseApp = firebase.initializeApp(firebaseConfig);
		auth = firebaseApp.auth();
		auth.onAuthStateChanged(function(user) {
			  if (user)
				  autenticarLogin([{name: "uid" , value:JSON.stringify(user)}]);
			});
	}
}

function login() {
	PF('growl').removeAll();
	PF('growl').renderMessage({summary:"Aguarde...", detail: "Autenticando seus dados...", severity: "info"});
	initFirebase();
	var  valemail = document.getElementById("formLogin:email").value;
	var  valsenha = document.getElementById("formLogin:senha").value;
	auth.signInWithEmailAndPassword(valemail, valsenha)
		.catch(function(error) {
			switch(error.code){
			case 'auth/wrong-password':	
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "A senha informada está inválida.", severity: "error"});
				break;
			case 'auth/user-not-found':
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "O e-mail informado não pertence a nenhuma conta.", severity: "error"});
				break;
			default:
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "Ocorreu um erro ao logar. Consulte o suporte do sistema.", severity: "error"});
			}
		});
}

function logout(){
	initFirebase();
	auth.signOut()
	.then(deslogar())
	.catch(function(error) {
		PF('growl').removeAll();
		PF('growl').renderMessage({summary:"Erro!", detail: "Ocorreu um erro ao deslogar. Consulte o suporte do sistema.", severity: "error"});
	});
}

function redefinirSenha() {
	PF('growl').removeAll();
	PF('growl').renderMessage({summary:"Aguarde.", detail: "Autenticando seus dados...", severity: "info"});
	var valemail = document.getElementById("formRedefinicao:email").value;
	if(auth == null)
		initFirebase();
	auth.sendPasswordResetEmail(valemail).then(function(){
		PF('growl').removeAll();
		PF('growl').renderMessage({summary:"Sucesso!", detail: "Clique no link enviado para seu e-mail para redefinir a senha.", severity: "info"});
	}).catch(function(error){
		switch(error.code){
		case 'auth/user-not-found':
			PF('growl').removeAll();
			PF('growl').renderMessage({summary:"Erro!", detail: "O e-mail informado não pertence a nenhuma conta.", severity: "error"});
			break
		}
	});
}

function initFirebase() {
	var firebaseConfig = {
		apiKey: "AIzaSyCh85g8SSkXs0dQvbcTkagxCm6cuXpaBHs",
		authDomain: "prefeiturainterativa.firebaseapp.com",
		databaseURL: "https://prefeiturainterativa.firebaseio.com",
		projectId: "prefeiturainterativa",
		storageBucket: "prefeiturainterativa.appspot.com",
		messagingSenderId: "977874090377",
		appId: "1:977874090377:web:d96d5491f89fa72c468958",
		measurementId: "G-V5H2DKDLSH"
	};
	firebase.initializeApp(firebaseConfig);
}
package app_gestion_produits;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Screen;

public class LoginController implements Initializable {

    // LIENS AVEC LE FICHIER FXML

    
    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblErreur;
    @FXML private Button        btnLogin;


    // INITIALISATION

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Connexion possible en appuyant sur ENTRÉE depuis le champ mot de passe
        txtPassword.setOnAction(event -> handleLogin());
        lblErreur.setVisible(false);
        lblErreur.setManaged(false);
    }

    // ACTIONS

    // Bouton Se connecter
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur utilisateur = UtilisateurService.authentifier(username, password);

        if (utilisateur != null) {
            ouvrirInterfacePrincipale(utilisateur);
        } else {
            afficherErreur("Nom d'utilisateur ou mot de passe incorrect.");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    // Bouton Annuler
    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.close();
    }

    // METHODES UTILITAIRES

    // Ferme le Login et ouvre l'interface principale
private void ouvrirInterfacePrincipale(Utilisateur utilisateur) {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/app_gestion_produits/MainInterface.fxml"));
        Parent root = loader.load();

        MainInterfaceController controller = loader.getController();
        controller.setUtilisateurConnecte(utilisateur);

        Stage stageMain = new Stage();
        stageMain.setTitle("Gestion des Produits — " + utilisateur.getNomComplet());

        // Prendre toute la fenêtre + centrer
        Screen screen = Screen.getPrimary();
        double largeur  = screen.getVisualBounds().getWidth();
        double hauteur  = screen.getVisualBounds().getHeight();

        stageMain.setScene(new Scene(root, largeur, hauteur));
        stageMain.setMaximized(true);   
        stageMain.centerOnScreen();    
        stageMain.show();

        Stage stageLogin = (Stage) btnLogin.getScene().getWindow();
        stageLogin.close();

    } catch (Exception e) {
        afficherErreur("Erreur lors de l'ouverture de l'application : " + e.getMessage());
        e.printStackTrace();
    }
}
    private void afficherErreur(String message) {
        lblErreur.setText("⚠  " + message);
        lblErreur.setVisible(true);
        lblErreur.setManaged(true);
    }
}
package app_gestion_produits;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App_Gestion_Produits extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Charger la fenêtre de Login
        Parent root = FXMLLoader.load(
            getClass().getResource("/app_gestion_produits/Login.fxml"));

        stage.setTitle("Connexion — Gestion des Produits");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen(); 
        stage.show();
    }


    public static void main(String[] args) {

        // Lancer l'application JavaFX (démarre sur Login)
        launch(args);
    }
}



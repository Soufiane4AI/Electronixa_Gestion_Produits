package app_gestion_produits;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainInterfaceController implements Initializable {


    // FORMULAIRE

    @FXML private TextField        txtCode;
    @FXML private TextField        txtLibelle;
    @FXML private ComboBox<String> cmbType;
    @FXML private TextField        txtQuantite;
    @FXML private CheckBox         chkDisponible;


    // BOUTONS

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnActualiser;
    @FXML private Button btnVider;
    @FXML private Button btnEffacerRecherche;
    @FXML private Button btnExportCSV;
    @FXML private Button btnExportPDF;
    @FXML private Button btnHistorique;
    @FXML private Button    btnAjusterStock;  // visible seulement si produit sélectionné
    @FXML private Label     lblStockActuel;   // affiche le stock actuel du produit sélectionné
    @FXML private TextField txtAjustement;    // champ dédié pour la valeur d'ajustement


    // RECHERCHE + TABLEAU PRODUITS

    @FXML private TextField txtRecherche;

    @FXML private TableView<Produit>            tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String>  colCode;
    @FXML private TableColumn<Produit, String>  colLibelle;
    @FXML private TableColumn<Produit, String>  colType;
    @FXML private TableColumn<Produit, Integer> colStock;
    @FXML private TableColumn<Produit, Boolean> colDispo;

    @FXML private Label lblCompteur;


    // SERVICES & ÉTAT

    private final ProduitService   produitService   = new ProduitService();
    private final ActionLogService actionLogService = new ActionLogService();
    private final ExportService    exportService    = new ExportService();

    private final ObservableList<Produit> listeTotale   = FXCollections.observableArrayList();
    private final ObservableList<Produit> listeAffichee = FXCollections.observableArrayList();

    private Produit     produitSelectionne  = null;
    private Utilisateur utilisateurConnecte = null;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    // INITIALISATION + GESTION DES RÔLES

    
    /**
     * Reçoit l'utilisateur connecté depuis LoginController.
     * Applique immédiatement les restrictions selon le rôle.
     * Appelé APRÈS loader.load() donc les boutons @FXML sont déjà injectés.
     */
    
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;

        boolean estAdmin = "ADMIN".equals(utilisateur.getRole());

        // Désactiver les boutons d'écriture pour les non-ADMIN
        btnAjouter.setDisable(!estAdmin);
        btnModifier.setDisable(!estAdmin);
        btnSupprimer.setDisable(!estAdmin);
        btnExportCSV.setDisable(!estAdmin);
        btnExportPDF.setDisable(!estAdmin);
        btnHistorique.setDisable(!estAdmin);
        btnAjusterStock.setDisable(!estAdmin);

        // Tooltip explicatif sur les boutons désactivés
        if (!estAdmin) {
            String msg = "Accès réservé à l'administrateur";
            btnAjouter.setTooltip(new Tooltip(msg));
            btnModifier.setTooltip(new Tooltip(msg));
            btnSupprimer.setTooltip(new Tooltip(msg));
            btnExportCSV.setTooltip(new Tooltip(msg));
            btnExportPDF.setTooltip(new Tooltip(msg));
            btnHistorique.setTooltip(new Tooltip(msg));
            btnAjusterStock.setTooltip(new Tooltip(msg));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbType.setItems(FXCollections.observableArrayList(
                "Informatique", "Électronique", "Bureautique", "Autre"));
        cmbType.getSelectionModel().selectFirst();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteEnStock"));
        colDispo.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));

        tableProduits.setItems(listeAffichee);

        tableProduits.getSelectionModel().selectedItemProperty().addListener(
            (obs, ancien, nouveau) -> {
                if (nouveau != null) {
                    produitSelectionne = nouveau;
                    preremplirFormulaire(nouveau);
                    // Afficher le bouton Ajuster stock + info stock actuel
                    // La quantité actuelle reste affichée (preremplirFormulaire la met déjà)
                    // Le champ Quantité garde sa valeur pour que Modifier fonctionne normalement
                    lblStockActuel.setText("Stock actuel : " + nouveau.getQuantiteEnStock()
                            + " unités  |  Tapez ±valeur ci-dessous pour ajuster :");
                    lblStockActuel.setVisible(true);
                    lblStockActuel.setManaged(true);
                    txtAjustement.clear();
                    txtAjustement.setVisible(true);
                    txtAjustement.setManaged(true);
                    btnAjusterStock.setVisible(true);
                    btnAjusterStock.setManaged(true);
                } else {
                    // Cacher le bouton si aucune sélection
                    lblStockActuel.setVisible(false);
                    lblStockActuel.setManaged(false);
                    txtAjustement.setVisible(false);
                    txtAjustement.setManaged(false);
                    btnAjusterStock.setVisible(false);
                    btnAjusterStock.setManaged(false);
                }
            }
        );

        txtRecherche.textProperty().addListener(
            (obs, ancien, nouveau) -> filtrerProduits(nouveau)
        );

        rafraichirTableau();
    }


    // CRUD PRODUITS

    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) return;
        try {
            Produit p = new Produit();
            remplirDepuisFormulaire(p);
            produitService.ajouterProduit(p);
            actionLogService.enregistrer("AJOUT", p, getUsername());
            viderChamps();
            rafraichirTableau();
            afficherAlerte("Succès", "Produit ajouté avec succès !", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "La quantité doit être un nombre entier.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            String msg = e.getMessage() != null && e.getMessage().contains("Duplicate")
                    ? "Le code produit \"" + txtCode.getText().trim() + "\" existe déjà en base."
                    : "Impossible d'ajouter : " + e.getMessage();
            afficherAlerte("Erreur", msg, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleModifier() {
        if (produitSelectionne == null) {
            afficherAlerte("Sélection manquante",
                    "Sélectionnez un produit dans le tableau pour le modifier.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (!validerFormulaire()) return;
        try {
            remplirDepuisFormulaire(produitSelectionne);
            produitService.modifierProduit(produitSelectionne);
            actionLogService.enregistrer("MODIFICATION", produitSelectionne, getUsername());
            viderChamps();
            rafraichirTableau();
            afficherAlerte("Succès", "Produit modifié avec succès !", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "La quantité doit être un nombre entier.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de modifier : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSupprimer() {
        if (produitSelectionne == null) {
            afficherAlerte("Sélection manquante",
                    "Sélectionnez un produit dans le tableau pour le supprimer.",
                    Alert.AlertType.WARNING);
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Supprimer le produit \"" + produitSelectionne.getLibelle() + "\" ?");
        confirmation.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                actionLogService.enregistrer("SUPPRESSION", produitSelectionne, getUsername());
                produitService.supprimerProduit(produitSelectionne);
                viderChamps();
                rafraichirTableau();
                afficherAlerte("Succès", "Produit supprimé !", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleActualiser() {
        rafraichirTableau();
        afficherAlerte("Actualisation", "Tableau mis à jour.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleVider() { viderChamps(); }

    @FXML
    private void handleAjusterStock() {
        if (produitSelectionne == null) return;

        String valeurSaisie = txtAjustement.getText().trim();
        if (valeurSaisie.isEmpty()) {
            afficherAlerte("Champ vide",
                    "Tapez une valeur positive pour ajouter ou négative pour soustraire.",
                    Alert.AlertType.WARNING);
            return;
        }

        int ajustement;
        try {
            ajustement = Integer.parseInt(valeurSaisie);
        } catch (NumberFormatException e) {
            afficherAlerte("Valeur invalide",
                    "Entrez un nombre entier (ex: 5 pour ajouter, -3 pour soustraire).",
                    Alert.AlertType.ERROR);
            return;
        }

        if (ajustement == 0) {
            afficherAlerte("Valeur nulle",
                    "L\'ajustement est 0 — aucune modification effectuée.",
                    Alert.AlertType.WARNING);
            return;
        }

        int stockActuel   = produitSelectionne.getQuantiteEnStock();
        int nouveauStock  = stockActuel + ajustement;

        // stock final ne peut pas être négatif
        if (nouveauStock < 0) {
            afficherAlerte("Stock insuffisant",
                    "Impossible : stock actuel = " + stockActuel
                    + ", ajustement = " + ajustement
                    + " → résultat = " + nouveauStock + " (négatif interdit).",
                    Alert.AlertType.WARNING);
            return;
        }

        // Confirmation avant d'appliquer
        String sens = ajustement > 0 ? "+" + ajustement : String.valueOf(ajustement);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer l'ajustement");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Produit : " + produitSelectionne.getLibelle()
                + "\nStock actuel : " + stockActuel
                + "\nAjustement   : " + sens
                + "\nNouveau stock : " + nouveauStock
                + "\nConfirmer ?");

        confirmation.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                produitSelectionne.setQuantiteEnStock(nouveauStock);
                try {
                    produitService.modifierProduit(produitSelectionne);

                    // Enregistrer dans l'historique
                    String details = "Ajustement stock : " + stockActuel + " → " + nouveauStock
                            + " (" + sens + ")";
                    ActionLog log = new ActionLog(
                            "AJUSTEMENT STOCK",
                            produitSelectionne.getCode(),
                            produitSelectionne.getLibelle(),
                            getUsername(),
                            details
                    );
                    actionLogService.enregistrerLog(log);

                    viderChamps();
                    rafraichirTableau();
                    afficherAlerte("Succès",
                            "Stock mis à jour : " + nouveauStock + " unités.",
                            Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    afficherAlerte("Erreur",
                            "Impossible de mettre à jour : " + e.getMessage(),
                            Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleEffacerRecherche() { txtRecherche.clear(); }

    @FXML
    private void handleDeconnexion() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/app_gestion_produits/Login.fxml"));
            Stage stageLogin = new Stage();
            stageLogin.setTitle("Connexion — Gestion des Produits");
            stageLogin.setScene(new Scene(root));
            stageLogin.setResizable(false);
            stageLogin.show();
            Stage stageMain = (Stage) tableProduits.getScene().getWindow();
            stageMain.close();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de se déconnecter : " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }


    // EXPORT CSV

    @FXML
    private void handleExportCSV() {
        if (listeAffichee.isEmpty()) {
            afficherAlerte("Export CSV", "Aucun produit à exporter.", Alert.AlertType.WARNING);
            return;
        }
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choisir le dossier d'export");
            File dossier = chooser.showDialog(tableProduits.getScene().getWindow());
            if (dossier == null) return;

            String nomFichier = exportService.genererNomFichier("produits", "csv");
            String chemin = dossier.getAbsolutePath() + File.separator + nomFichier;

            exportService.exporterCSV(listeAffichee, chemin);
            Desktop.getDesktop().open(dossier);
            afficherAlerte("Export CSV réussi", "Fichier créé :\n" + chemin, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            afficherAlerte("Erreur Export CSV",
                    "Impossible d'exporter : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // EXPORT PDF

    @FXML
    private void handleExportPDF() {
        if (listeAffichee.isEmpty()) {
            afficherAlerte("Export PDF", "Aucun produit à exporter.", Alert.AlertType.WARNING);
            return;
        }
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choisir le dossier d'export");
            File dossier = chooser.showDialog(tableProduits.getScene().getWindow());
            if (dossier == null) return;

            String nomFichier = exportService.genererNomFichier("rapport_produits", "html");
            String chemin = dossier.getAbsolutePath() + File.separator + nomFichier;
            String nom = utilisateurConnecte != null ? utilisateurConnecte.getNomComplet() : "Inconnu";

            exportService.exporterPDF(listeAffichee, nom, chemin);
            Desktop.getDesktop().open(new File(chemin));
            afficherAlerte("Export PDF réussi",
                    "Rapport ouvert dans votre navigateur.\n"
                    + "Pour sauvegarder en PDF : Ctrl+P → Enregistrer en PDF.\n"
                    + "Fichier : " + chemin, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            afficherAlerte("Erreur Export PDF",
                    "Impossible d'exporter : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // HISTORIQUE DES ACTIONS

    @FXML
    private void handleHistorique() {
        List<ActionLog> logs = actionLogService.listerTous();

        Stage stageHisto = new Stage();
        stageHisto.setTitle("Historique des actions");
        stageHisto.initModality(Modality.APPLICATION_MODAL);

        TableView<ActionLog> table = new TableView<>();
        table.setStyle("-fx-font-size: 12px;");

        TableColumn<ActionLog, String> cDate = new TableColumn<>("Date");
        cDate.setPrefWidth(145);
        cDate.setCellValueFactory(data ->
            new SimpleStringProperty(SDF.format(data.getValue().getDateAction())));

        TableColumn<ActionLog, String> cAction = new TableColumn<>("Action");
        cAction.setPrefWidth(110);
        cAction.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAction()));

        // Colorer les lignes selon l'action
        table.setRowFactory(tv -> new TableRow<ActionLog>() {
            @Override
            protected void updateItem(ActionLog item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getAction()) {
                        case "AJOUT":        setStyle("-fx-background-color: #eafaf1;"); break;
                        case "MODIFICATION": setStyle("-fx-background-color: #eaf4fb;"); break;
                        case "SUPPRESSION":  setStyle("-fx-background-color: #fdedec;"); break;
                        default:             setStyle("");
                    }
                }
            }
        });

        TableColumn<ActionLog, String> cCode = new TableColumn<>("Code");
        cCode.setPrefWidth(70);
        cCode.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProduitCode()));

        TableColumn<ActionLog, String> cLibelle = new TableColumn<>("Libellé");
        cLibelle.setPrefWidth(170);
        cLibelle.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProduitLibelle()));

        TableColumn<ActionLog, String> cUser = new TableColumn<>("Utilisateur");
        cUser.setPrefWidth(100);
        cUser.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUtilisateur()));

        TableColumn<ActionLog, String> cDetails = new TableColumn<>("Détails");
        cDetails.setPrefWidth(250);
        cDetails.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDetails()));

        table.getColumns().addAll(cDate, cAction, cCode, cLibelle, cUser, cDetails);
        table.setItems(FXCollections.observableArrayList(logs));
        table.setPlaceholder(new Label("Aucune action enregistrée."));

        Label compteur = new Label(logs.size() + " action(s) enregistrée(s)");
        compteur.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-padding: 4 0 0 4;");

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(8, table, compteur);
        vbox.setStyle("-fx-padding: 14; -fx-background-color: #f4f6f9;");
        javafx.scene.layout.VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        Scene scene = new Scene(vbox, 870, 500);
        stageHisto.setScene(scene);
        stageHisto.show();
    }

    
    // MÉTHODES UTILITAIRES

    private void rafraichirTableau() {
        produitSelectionne = null;
        listeTotale.clear();
        List<Produit> produits = produitService.listerProduits();
        if (produits != null) listeTotale.addAll(produits);
        filtrerProduits(txtRecherche.getText());
    }

    private void filtrerProduits(String texte) {
        listeAffichee.clear();
        if (texte == null || texte.trim().isEmpty()) {
            listeAffichee.addAll(listeTotale);
        } else {
            String motCle = texte.trim().toLowerCase();
            listeAffichee.addAll(
                listeTotale.stream()
                    .filter(p ->
                        (p.getLibelle() != null && p.getLibelle().toLowerCase().contains(motCle)) ||
                        (p.getType()    != null && p.getType().toLowerCase().contains(motCle)))
                    .collect(Collectors.toList())
            );
        }
        int nb = listeAffichee.size();
        lblCompteur.setText(nb + " produit" + (nb > 1 ? "s" : "") + " affiché" + (nb > 1 ? "s" : ""));
    }

    private void preremplirFormulaire(Produit p) {
        txtCode.setText(p.getCode());
        txtLibelle.setText(p.getLibelle());
        txtQuantite.setText(String.valueOf(p.getQuantiteEnStock()));
        chkDisponible.setSelected(p.isDisponibilite());
        cmbType.setValue(p.getType());
    }

    private void remplirDepuisFormulaire(Produit p) {
        p.setCode(txtCode.getText().trim());
        p.setLibelle(txtLibelle.getText().trim());
        p.setType(cmbType.getValue());
        p.setQuantiteEnStock(Integer.parseInt(txtQuantite.getText().trim()));
        p.setDisponibilite(chkDisponible.isSelected());
    }

    private boolean validerFormulaire() {
        if (txtCode.getText().trim().isEmpty() ||
            txtLibelle.getText().trim().isEmpty() ||
            txtQuantite.getText().trim().isEmpty()) {
            afficherAlerte("Champs manquants",
                    "Veuillez remplir Code, Libellé et Quantité.", Alert.AlertType.WARNING);
            return false;
        }
        try {
            int quantite = Integer.parseInt(txtQuantite.getText().trim());
            if (quantite < 0) {
                afficherAlerte("Quantité invalide",
                        "La quantité en stock ne peut pas être négative.", Alert.AlertType.WARNING);
                txtQuantite.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie",
                    "La quantité doit être un nombre entier valide.", Alert.AlertType.ERROR);
            txtQuantite.requestFocus();
            return false;
        }
        return true;
    }

    private void viderChamps() {
        txtCode.clear();
        txtLibelle.clear();
        txtQuantite.clear();
        txtQuantite.setPromptText("Ex : 10");
        chkDisponible.setSelected(false);
        cmbType.getSelectionModel().selectFirst();
        tableProduits.getSelectionModel().clearSelection();
        produitSelectionne = null;

        lblStockActuel.setVisible(false);
        lblStockActuel.setManaged(false);
        txtAjustement.setVisible(false);
        txtAjustement.setManaged(false);
        btnAjusterStock.setVisible(false);
        btnAjusterStock.setManaged(false);
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private String getUsername() {
        return utilisateurConnecte != null ? utilisateurConnecte.getUsername() : "inconnu";
    }
}
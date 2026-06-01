package app_gestion_produits;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Service d'export des produits en CSV et PDF.

public class ExportService {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat SDF_FICHIER = new SimpleDateFormat("yyyyMMdd_HHmmss");

    // EXPORT CSV

    /**
     * Exporte la liste des produits en fichier CSV.
     * @param produits  liste à exporter
     * @param cheminFichier  chemin complet du fichier de destination
     * @throws Exception en cas d'erreur d'écriture
     */
    public void exporterCSV(List<Produit> produits, String cheminFichier) throws Exception {

        // Java 8 compatible : OutputStreamWriter avec encodage UTF-8
        try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(cheminFichier),
                java.nio.charset.StandardCharsets.UTF_8)) {

            fw.write('\uFEFF');

            // En-tête
            fw.write("ID;Code;Libellé;Type;Quantité en stock;Disponible;Date export\n");

            String dateExport = SDF.format(new Date());

            for (Produit p : produits) {
                fw.write(
                    p.getId()                          + ";" +
                    echapper(p.getCode())              + ";" +
                    echapper(p.getLibelle())           + ";" +
                    echapper(p.getType())              + ";" +
                    p.getQuantiteEnStock()             + ";" +
                    (p.isDisponibilite() ? "Oui" : "Non") + ";" +
                    dateExport                         + "\n"
                );
            }
        }
    }

    /**
     * Échappe les champs CSV contenant des point-virgules ou guillemets.
     */
    private String echapper(String valeur) {
        if (valeur == null) return "";
        if (valeur.contains(";") || valeur.contains("\"") || valeur.contains("\n")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        return valeur;
    }

    // EXPORT PDF (HTML imprimable)

    /**
     * Génère un rapport PDF sous forme de fichier HTML stylisé,
     * prêt à être imprimé/sauvegardé en PDF depuis le navigateur.
     * @param produits        liste à exporter
     * @param nomUtilisateur  utilisateur connecté (affiché dans le rapport)
     * @param cheminFichier   chemin complet du fichier .html de destination
     * @throws Exception en cas d'erreur d'écriture
     */
    public void exporterPDF(List<Produit> produits, String nomUtilisateur,
                            String cheminFichier) throws Exception {

        String dateExport = SDF.format(new Date());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='fr'><head>")
            .append("<meta charset='UTF-8'>")
            .append("<title>Rapport des Produits</title>")
            .append("<style>")
            .append("  body { font-family: Arial, sans-serif; margin: 30px; color: #2c3e50; }")
            .append("  h1   { color: #2980b9; border-bottom: 2px solid #2980b9; padding-bottom: 8px; }")
            .append("  .meta { color: #7f8c8d; font-size: 13px; margin-bottom: 20px; }")
            .append("  table { width: 100%; border-collapse: collapse; font-size: 13px; }")
            .append("  th    { background-color: #2980b9; color: white; padding: 10px 12px; text-align: left; }")
            .append("  td    { padding: 8px 12px; border-bottom: 1px solid #ecf0f1; }")
            .append("  tr:nth-child(even) { background-color: #f9f9f9; }")
            .append("  tr:hover           { background-color: #eaf4fb; }")
            .append("  .badge-oui  { background:#27ae60; color:white; padding:2px 8px; border-radius:10px; font-size:11px; }")
            .append("  .badge-non  { background:#e74c3c; color:white; padding:2px 8px; border-radius:10px; font-size:11px; }")
            .append("  .footer { margin-top: 20px; font-size: 11px; color: #bdc3c7; text-align: right; }")
            .append("  @media print { body { margin: 15px; } }")
            .append("</style></head><body>");

        // En-tête du rapport
        html.append("<h1> 🖥 Rapport des Produits — Electronixa</h1>");
        html.append("<div class='meta'>")
            .append("Généré le : <strong>").append(dateExport).append("</strong>")
            .append(" &nbsp;|&nbsp; Utilisateur : <strong>").append(nomUtilisateur).append("</strong>")
            .append(" &nbsp;|&nbsp; Total produits : <strong>").append(produits.size()).append("</strong>")
            .append("</div>");

        // Tableau
        html.append("<table>")
            .append("<thead><tr>")
            .append("<th>ID</th><th>Code</th><th>Libellé</th><th>Type</th>")
            .append("<th>Stock</th><th>Disponible</th>")
            .append("</tr></thead><tbody>");

        for (Produit p : produits) {
            String badge = p.isDisponibilite()
                    ? "<span class='badge-oui'>Oui</span>"
                    : "<span class='badge-non'>Non</span>";
            html.append("<tr>")
                .append("<td>").append(p.getId()).append("</td>")
                .append("<td>").append(p.getCode()).append("</td>")
                .append("<td>").append(p.getLibelle()).append("</td>")
                .append("<td>").append(p.getType()).append("</td>")
                .append("<td style='text-align:center'>").append(p.getQuantiteEnStock()).append("</td>")
                .append("<td style='text-align:center'>").append(badge).append("</td>")
                .append("</tr>");
        }

        html.append("</tbody></table>");
        html.append("<div class='footer'>Electronixa — Système de Gestion des Produits</div>");
        html.append("</body></html>");

        // Java 8 compatible
        try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(cheminFichier),
                java.nio.charset.StandardCharsets.UTF_8)) {
            fw.write(html.toString());
        }
    }

    /**
     * Génère un nom de fichier horodaté.
     * Ex: "produits_20260526_042000.csv"
     */
    public String genererNomFichier(String prefixe, String extension) {
        return prefixe + "_" + SDF_FICHIER.format(new Date()) + "." + extension;
    }
}
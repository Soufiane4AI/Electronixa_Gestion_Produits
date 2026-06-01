package app_gestion_produits;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "action_log")
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "action", nullable = false)
    private String action; // "AJOUT", "MODIFICATION", "SUPPRESSION"

    @Column(name = "produit_code")
    private String produitCode;

    @Column(name = "produit_libelle")
    private String produitLibelle;

    @Column(name = "utilisateur", nullable = false)
    private String utilisateur; // username de l'utilisateur connecté

    @Column(name = "date_action", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAction;

    @Column(name = "details", length = 500)
    private String details; // informations supplémentaires

    public ActionLog() {}

    public ActionLog(String action, String produitCode, String produitLibelle,
                     String utilisateur, String details) {
        this.action         = action;
        this.produitCode    = produitCode;
        this.produitLibelle = produitLibelle;
        this.utilisateur    = utilisateur;
        this.dateAction     = new Date();
        this.details        = details;
    }

    public int getId(){
        return id; }
    public void setId(int id){
        this.id = id; }

    public String getAction(){
        return action; }
    public void setAction(String action){
        this.action = action; }

    public String getProduitCode(){
        return produitCode; }
    public void setProduitCode(String produitCode){
        this.produitCode = produitCode; }

    public String getProduitLibelle(){
        return produitLibelle; }
    public void setProduitLibelle(String produitLibelle){
        this.produitLibelle = produitLibelle; }

    public String getUtilisateur(){
        return utilisateur; }
    public void setUtilisateur(String utilisateur){
        this.utilisateur = utilisateur; }

    public Date getDateAction() {
        return dateAction; }
    public void setDateAction(Date dateAction){
        this.dateAction = dateAction; }

    public String getDetails(){
        return details; }
    public void setDetails(String details){
        this.details = details; }
    
}
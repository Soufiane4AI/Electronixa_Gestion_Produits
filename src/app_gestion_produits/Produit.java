package app_gestion_produits;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "produit") 
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id")
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "type")
    private String type;

    @Column(name = "quantite_stock")
    private int quantiteEnStock;

    @Column(name = "disponibilite")
    private boolean disponibilite;

    public Produit() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantiteEnStock() { return quantiteEnStock; }
    public void setQuantiteEnStock(int quantiteEnStock) { this.quantiteEnStock = quantiteEnStock; }

    public boolean isDisponibilite() { return disponibilite; }
    public void setDisponibilite(boolean disponibilite) { this.disponibilite = disponibilite; }
}
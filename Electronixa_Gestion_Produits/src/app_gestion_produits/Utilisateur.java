package app_gestion_produits;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nom_complet")
    private String nomComplet;

    @Column(name = "role")
    private String role; // "ADMIN" ou "USER"

    public Utilisateur() {}

    public Utilisateur(String username, String password, String nomComplet, String role) {
        this.username   = username;
        this.password   = password;
        this.nomComplet = nomComplet;
        this.role       = role;
    }


    public int getId(){ 
        return id; }
    public void setId(int id){
        this.id = id; }

    public String getUsername(){
        return username; }  
    public void setUsername(String username){
        this.username = username; }

    public String getPassword(){ 
        return password; }
    public void setPassword(String password){ 
        this.password = password; }

    public String getNomComplet(){ 
        return nomComplet; }
    public void setNomComplet(String nomComplet){
        this.nomComplet = nomComplet; }

    public String getRole(){
        return role; }
    public void setRole(String role){
        this.role = role; }
    
    
}
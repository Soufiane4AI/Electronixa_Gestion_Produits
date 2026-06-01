package app_gestion_produits;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ProduitService {

    // 1. CREATE : Ajouter un nouveau produit
    public void ajouterProduit(Produit produit) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(produit);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try { transaction.rollback(); } catch (Exception re) { re.printStackTrace(); }
            }
            // Évicte l'objet corrompu de la session avant de fermer
            try { session.evict(produit); } catch (Exception ignore) {}
            throw e; // ← renvoie l'exception pour que le contrôleur l'affiche
        } finally {
            try { session.close(); } catch (Exception ignore) {}
        }
    }

    // 2. READ : Récupérer la liste de tous les produits
    public List<Produit> listerProduits() {
        // Toujours une session fraîche pour éviter la session corrompue
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Produit> produits = new ArrayList<>();
        try {
            produits = session.createQuery("from Produit").list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { session.close(); } catch (Exception ignore) {}
        }
        return produits;
    }

    // 3. UPDATE : Mettre à jour un produit existant
    public void modifierProduit(Produit produit) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(produit);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try { transaction.rollback(); } catch (Exception re) { re.printStackTrace(); }
            }
            try { session.evict(produit); } catch (Exception ignore) {}
            throw e;
        } finally {
            try { session.close(); } catch (Exception ignore) {}
        }
    }

    // 4. DELETE : Supprimer un produit
    public void supprimerProduit(Produit produit) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //  merge() pour rattacher l'objet détaché à cette nouvelle session
            Produit produitAttache = (Produit) session.merge(produit);
            session.delete(produitAttache);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try { transaction.rollback(); } catch (Exception re) { re.printStackTrace(); }
            }
            throw e;
        } finally {
            try { session.close(); } catch (Exception ignore) {}
        }
    }
}
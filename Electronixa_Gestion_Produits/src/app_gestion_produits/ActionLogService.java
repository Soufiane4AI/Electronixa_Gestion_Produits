package app_gestion_produits;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class ActionLogService {

    /**
     * Enregistre une action en base.
     * Appelée après chaque AJOUT, MODIFICATION, SUPPRESSION.
     */
    public void enregistrer(String action, Produit produit, String usernameConnecte) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String details = "Type: " + produit.getType()
                    + " | Stock: " + produit.getQuantiteEnStock()
                    + " | Dispo: " + (produit.isDisponibilite() ? "Oui" : "Non");

            ActionLog log = new ActionLog(
                    action,
                    produit.getCode(),
                    produit.getLibelle(),
                    usernameConnecte,
                    details
            );

            session.save(log);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Enregistre directement un objet ActionLog déjà construit.
     * Utilisé pour les cas spéciaux comme l'ajustement de stock.
     */
    public void enregistrerLog(ActionLog log) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(log);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Retourne tous les logs triés du plus récent au plus ancien.
     */
    public List<ActionLog> listerTous() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<ActionLog> logs = new ArrayList<>();
        try {
            logs = session.createQuery(
                    "FROM ActionLog ORDER BY dateAction DESC").list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return logs;
    }
}
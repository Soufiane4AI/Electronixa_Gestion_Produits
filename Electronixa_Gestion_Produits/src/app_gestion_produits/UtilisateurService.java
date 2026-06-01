package app_gestion_produits;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UtilisateurService {


    // AUTHENTIFICATION

    // Vérifie les identifiants → retourne l'Utilisateur si trouvé, null sinon
    public static Utilisateur authentifier(String username, String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Utilisateur utilisateur = null;
        try {
            utilisateur = (Utilisateur) session
                    .createQuery("FROM Utilisateur WHERE username = :u AND password = :p")
                    .setParameter("u", username)
                    .setParameter("p", password)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return utilisateur;
    }


}
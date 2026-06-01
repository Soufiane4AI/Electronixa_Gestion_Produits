package app_gestion_produits;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Produit.class)
                    .addAnnotatedClass(Utilisateur.class)
                    .addAnnotatedClass(ActionLog.class)
                    .buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("La création de la connexion a échoué : " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
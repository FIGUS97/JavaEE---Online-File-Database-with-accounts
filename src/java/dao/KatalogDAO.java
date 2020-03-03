package dao;

import entity.Katalog;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class KatalogDAO {
    private static Session session;

    public static void insert(Katalog katalog) {
        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(katalog);
        session.getTransaction().commit();
    }

    public static List select(String zapytanie, ArrayList<String> params, ArrayList<String> paramNames) {
        List answer = new ArrayList<Katalog>();

        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.println(zapytanie);
        Query query = session.createQuery(zapytanie);
        if (params != null) {
            if (params.size() != paramNames.size()) {
                throw new IllegalArgumentException("Niepoprawnie podane argumenty zapytania!");
            }
            for (int i = 0; i < params.size(); i++) {
                query.setString(paramNames.get(i), params.get(i));
            }
        }

        answer = query.list();
        return answer;
    }

    public static void delete(Katalog katalog) {
        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(katalog);
        session.getTransaction().commit();
    }

    public static void update(Katalog katalog) {
        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(katalog);
        session.getTransaction().commit();
    }
}

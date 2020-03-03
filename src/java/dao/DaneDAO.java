package dao;

import entity.Dane;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DaneDAO {

    private static Session session;

    public static void insert(Dane dana) {
        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(dana);
        session.getTransaction().commit();
    }

    public static List select(String zapytanie, ArrayList<String> params, ArrayList<String> paramNames) {
        List answer = new ArrayList<Dane>();

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

    public static void delete(Dane dana) {
        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(dana);
        session.getTransaction().commit();
    }

    public static void update(Dane dana) {
        SessionFactory sessionFactory = new Configuration().configure("/hibernate-dane.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(dana);
        session.getTransaction().commit();
    }
}

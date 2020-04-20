package dao;

import entity.Uzytkownik;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class UzytkownikDAO {
    
    
    private static Session session;
        public static void insert(Uzytkownik user)
    {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }
    
    public static List select(String zapytanie, ArrayList<String> params, ArrayList<String> paramNames) {
        List answer = new ArrayList<Uzytkownik>();
        if(params.size() != paramNames.size()){
            throw new IllegalArgumentException("Niepoprawnie podane argumenty zapytania!");
        }
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
            
        Query query = session.createQuery(zapytanie);
        
        for(int i = 0; i < params.size(); i++)
        {
            query.setString(paramNames.get(i), params.get(i));
        }
        
        answer = query.list();
        return answer;
    }
    
    
    
    public static void delete(Uzytkownik user){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
    }
    
    
    public static void update(Uzytkownik user)
    {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import dao.UzytkownikDAO;
import entity.Uzytkownik;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@ManagedBean(name = "logowanie_bean")
@SessionScoped
public class OknoLogowania {

    private String message = "";
    private boolean poprawne = true;
    private int user_id;
    private String login;
    private String haslo;
    private Uzytkownik user;
    
    

    public boolean loguj(int tryb) { //1 - normalne logowanie, 2 - gość
        
        try{
            
            ArrayList parametry = new ArrayList();
            switch(tryb){
                case 1:
                    parametry.add(login);
                    parametry.add(haslo);
                    break;
                case 2:
                    parametry.add("Anonymous");
                    parametry.add("");
            }
            
            
            ArrayList parametryNazwy = new ArrayList();
            parametryNazwy.add("login");
            parametryNazwy.add("haslo");
            List list = UzytkownikDAO.select("from Uzytkownik where login=:login and haslo=:haslo", parametry, parametryNazwy);
            
            
            if(list.size()==1){
                user = (Uzytkownik) list.get(0);
                //System.out.println("Zalogowany: \n"+ user.getId() + " " + user.getLogin() + " " + user.getHaslo());
                poprawne = true;
                message = "";
                return true;
            } else {
                poprawne = false;
                message = "Niepoprawne dane logowania!";
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    
    }
    
    
    public OknoLogowania() {
    }
        
    
    public void setLogin(String login) {
        this.login = login;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getLogin() {
        return login;
    }

    public String getHaslo() {
        return haslo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Uzytkownik getUser() {
        return user;
    }

    public void setUser(Uzytkownik user) {
        this.user = user;
    }
    
    
    
}

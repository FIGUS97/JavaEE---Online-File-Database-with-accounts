package common;

import dao.UzytkownikDAO;
import entity.Uzytkownik;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "rejestracja_bean")
@SessionScoped
public class OknoRejestracji {

    private String login;
    private String haslo;
    private String hasloPowtornie;
    private String message = "";
    private Uzytkownik user;

    public void rejestruj() {
        if (haslo.equals(hasloPowtornie)) {
            user = new Uzytkownik();
            user.setLogin(login);
            user.setHaslo(haslo);
            user.setCzyadmin(false);
            UzytkownikDAO.insert(user);
            message = "Zarejestrowano!";
        } else {
            message = "Hasła nie pasują! Wpisz ponownie!";
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getHasloPowtornie() {
        return hasloPowtornie;
    }

    public void setHasloPowtornie(String hasloPowtornie) {
        this.hasloPowtornie = hasloPowtornie;
    }

}

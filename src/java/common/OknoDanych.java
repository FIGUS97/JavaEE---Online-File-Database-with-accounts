package common;

import dao.DaneDAO;
import dao.KatalogDAO;
import entity.Dane;
import entity.Katalog;
import entity.Uzytkownik;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

@ManagedBean(name = "dane_bean")
@SessionScoped
public class OknoDanych {

    @ManagedProperty("#{logowanie_bean.user}")
    private Uzytkownik user;

    public String message = "";
    private File plik;
    private Dane danaEncja;
    private Part plikPart;
    private String zawartoscPliku;
    private String nazwaFolderuEdycja;
    private boolean dodawanieFolderu;
    private Katalog obecnyFolder;
    private String nazwaObecnegoFolderu;
    private boolean edycja = false;
    private String folderDoPrzeniesienia;
    private String nowaNazwaPliku;
    private Dane modyfikowanaDana;
    private int idNowegoKatalogu;
    private String wybranaWidocznosc;
    
    

    public OknoDanych() {
        user = getUser();
        obecnyFolder = (Katalog) KatalogDAO.select("from Katalog where nazwa='root'", null, null).get(0);
        nazwaObecnegoFolderu = obecnyFolder.getNazwa();
    }
    
    public void updateWidocznosc(Dane d) {
        d.setWidocznosc(wybranaWidocznosc);
        DaneDAO.update(d);
    }
    
    public boolean czyWlasciciel(Dane d) {
        if(d.getIdWlasciciela() == user.getId()) {
            return true;
        } else {
            if(user.isCzyadmin()) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    public String resztaOpcjiWidocznosci(Dane d, int nr) {
        switch(d.getWidocznosc()){
            case "wszyscy":
                if(nr == 1) {
                    return "zarejestrowani";
                } else {
                    return "autor";
                }
            case "zarejestrowani":
                if(nr == 1) {
                    return "wszyscy";
                } else {
                    return "autor";
                }
            case "autor":
                if(nr == 1) {
                    return "zarejestrowani";
                } else {
                    return "wszyscy";
                }
        }
        return "brak";
    }

    public void folderWGore() {
        
        ArrayList<String> parametry = new ArrayList<String>();
        parametry.add("nazwaNadFolderu");
        
        ArrayList<String> wartosci = new ArrayList<String>();
        wartosci.add(obecnyFolder.getNadkatalog());
        
        Katalog nadFolder = (Katalog) KatalogDAO.select("from Katalog where nazwa=:nazwaNadFolderu", wartosci, parametry).get(0);
        zmienFolder(nadFolder);
    }
    
    public boolean wyloguj() {
        user = new Uzytkownik();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return true;
    }
    
    public void onload() {
        
        //Przeladowanie do strony ladowania jesli sesja sie skonczyla
        try {
        user = getUser();
        user.getId();
        } catch(NullPointerException e) {
            try{
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void usun(Dane dana) {
        DaneDAO.delete(dana);
        message = "Usunieto!";
    }

    public void modyfikuj(Dane dana) {
        modyfikowanaDana=dana;
        edycja=!edycja;
    }
    
    public void modyfikujZapisz() {
        if(sprawdzCzyPoprawnaModyfikacja()){
            modyfikowanaDana.setNazwa(nowaNazwaPliku);
            modyfikowanaDana.setIdKatalogu(idNowegoKatalogu);
            DaneDAO.update(modyfikowanaDana);
            edycja=false;
        } else {
            message="Niepoprawne dane!";
        }
        
    }
    
    private boolean sprawdzCzyPoprawnaModyfikacja() {
        try {
            ArrayList<String> wartosci = new ArrayList<String>();
            wartosci.add(folderDoPrzeniesienia);
            ArrayList<String> parametry = new ArrayList<String>();
            parametry.add("nazwaKatalogu");
            List<Katalog> katalog = KatalogDAO.select("from Katalog where nazwa=:nazwaKatalogu", wartosci, parametry);
            
            Katalog istnieje = (Katalog) katalog.get(0);
            idNowegoKatalogu = istnieje.getId();
            
            if(istnieje.getNazwa().length() > 0) {
                return true;
            } else {
                return false;
            }
            
        } catch(NullPointerException e) {
            return false;
        }
    }
    

    public void przyciskDodajFolder() {
        dodawanieFolderu = !dodawanieFolderu;
    }
    
    public void dodajFolder() {
        Katalog folder = new Katalog();
        folder.setNazwa(nazwaFolderuEdycja);
        folder.setNadkatalog(nazwaObecnegoFolderu);
        
        KatalogDAO.insert(folder);
    }

    

    public void upload() {
        try {
            InputStream input = plikPart.getInputStream();

            File file = new File("./" + plikPart.getSubmittedFileName());
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);

            }

            dodajDoBazyPlik(file);

            input.close();
            output.close();
        } catch (IOException e) {
            // Error handling
        }
    }

    public File byteToFile(byte[] bity) {
        File plik = new File("./temp");
        try {
            OutputStream os = new FileOutputStream(plik);

            os.write(bity);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plik;
    }
    
    public void zmienFolder(Katalog folder) {
        obecnyFolder=folder;
        nazwaObecnegoFolderu=obecnyFolder.getNazwa();
        try{
            FacesContext.getCurrentInstance().getExternalContext().redirect("zalogowany.xhtml");
            }catch (Exception ex){
                ex.printStackTrace();
            }
    }

    public void pobierz(Dane dana) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();

            externalContext.responseReset();
            
            char[] nazwa = dana.getNazwa().toCharArray();
            int pozycjaKropki = 0;
            String nazwaTypu;
            
            for( int i = 0; i < dana.getNazwa().length(); i++)
            {
                if(nazwa[i] == '.'){
                    pozycjaKropki = i;
                }
            }
            
            switch(dana.getNazwa().substring(pozycjaKropki+1))
            {
                case "txt":
                    nazwaTypu = "text/plain";
                    break;
                case "js":
                    nazwaTypu = "text/javascript";
                    break;
                case "jpg":
                    nazwaTypu = "image/jpg";
                    break;
                case "png":
                    nazwaTypu = "image/jpg";
                    break;
                case "avi":
                    nazwaTypu = "video/avi";
                    break;
                case "bmp":
                    nazwaTypu = "image/bmp";
                    break;
                case "cpp":
                    nazwaTypu = "text/plain";
                    break;
                case "css":
                    nazwaTypu = "text/css";
                    break;
                case "doc":
                    nazwaTypu = "application/msword";
                    break;
                case "docx":
                    nazwaTypu = "application/msword";
                    break;
                case "exe":
                    nazwaTypu = "application/octet-stream";
                    break;
                case "html":
                    nazwaTypu = "text/html";
                    break;
                case "java":
                    nazwaTypu = "text/plain";
                    break;
                case "jpeg":
                    nazwaTypu = "image/jpeg";
                    break;
                case "mp3":
                    nazwaTypu = "audio/mpeg3";
                    break;
                case "zip":
                    nazwaTypu = "application/x-zip-compressed";
                    break;
                default:
                    nazwaTypu = "text/plain";
                    break;
            }

            externalContext.setResponseContentType(nazwaTypu);
            //externalContext.setResponseHeader("Content-Disposition", message);

            FileInputStream inputStream = new FileInputStream(byteToFile(dana.getDana()));

            OutputStream outputStream = externalContext.getResponseOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            context.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void dodajDoBazyPlik(File plik) {
        danaEncja = new Dane();

        System.out.println(); //2016/11/16 12:08:43
        try {
            danaEncja.setNazwa(plik.getName());
            danaEncja.setIdWlasciciela(user.getId());
            danaEncja.setDana(Files.readAllBytes(plik.toPath()));
            danaEncja.setDataDodania(new Date());
            danaEncja.setIdKatalogu(obecnyFolder.getId());
            danaEncja.setWidocznosc("autor");
            DaneDAO.insert(danaEncja);
            message = "Dodano plik!";
        } catch (IOException e) {
            message = "Błąd przy dodawaniu pliku!";
        }
    }

    

    public List<Katalog> pobierzFoldery() {
        List<Katalog> list = new ArrayList<Katalog>();
        
        ArrayList<String> parametry = new ArrayList<String>();
        parametry.add("nadkatalog");
        
        ArrayList<String> wartosci = new ArrayList<String>();
        wartosci.add(nazwaObecnegoFolderu);
        
        list = KatalogDAO.select("from Katalog where nadkatalog=:nadkatalog", wartosci, parametry);
        
        return list;
    }

    public List<Dane> pobierzDane() {
        List<Dane> list = new ArrayList<Dane>();
        
        ArrayList<String> parametry = new ArrayList<String>();
        parametry.add("idFoldera");
        
        ArrayList<String> wartosci = new ArrayList<String>();
        wartosci.add(obecnyFolder.getId().toString());

        list = DaneDAO.select("from Dane where id_katalogu=:idFoldera", wartosci, parametry);
        
        

        return ustawWidocznosc(list);
    }
    
    public List<Dane> ustawWidocznosc( List<Dane> lista ) {
        
        for(int i = 0; i < lista.size(); i++) {
            switch(lista.get(i).getWidocznosc()) {
                case "wszyscy":
                    continue;
                case "autor":
                    if(user.getId() == lista.get(i).getIdWlasciciela()) {
                        continue;
                    } else {
                        if(user.isCzyadmin())
                        {
                            continue;
                        } else {
                            lista.remove(i);
                            continue;
                        }
                    }
                    
                case "zarejestrowani":
                    if(user.getId() != 1) {
                        continue;
                    } else {
                        lista.remove(i);
                        continue;
                    }
            }
        }
        return lista;
    }

    public Uzytkownik getUser() {
        return user;
    }

    public void setUser(Uzytkownik user) {
        this.user = user;
    }

    
    public String getNazwaObecnegoFolderu() {
        return nazwaObecnegoFolderu;
    }

    public void setNazwaObecnegoFolderu(String nazwaObecnegoFolderu) {
        this.nazwaObecnegoFolderu = nazwaObecnegoFolderu;
    }
    
    public boolean isDodawanieFolderu() {
        return dodawanieFolderu;
    }

    public void setDodawanieFolderu(boolean dodawanieFolderu) {
        this.dodawanieFolderu = dodawanieFolderu;
    }

    public String getNazwaFolderuEdycja() {
        return nazwaFolderuEdycja;
    }

    public void setNazwaFolderuEdycja(String nazwaFolderu) {
        this.nazwaFolderuEdycja = nazwaFolderu;
    }
    
    public Part getFile() {
        return plikPart;
    }

    public void setFile(Part plikPart) {
        this.plikPart = plikPart;
    }

    public String getFileContent() {
        return zawartoscPliku;
    }

    public void setFileContent(String zawartoscPliku) {
        this.zawartoscPliku = zawartoscPliku;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Katalog getObecnyFolder() {
        return obecnyFolder;
    }

    public void setObecnyFolder(Katalog obecnyFolder) {
        this.obecnyFolder = obecnyFolder;
    }

    public boolean isEdycja() {
        return edycja;
    }

    public void setEdycja(boolean edycja) {
        this.edycja = edycja;
    }

    public String getFolderDoPrzeniesienia() {
        return folderDoPrzeniesienia;
    }

    public void setFolderDoPrzeniesienia(String folderDoPrzeniesienia) {
        this.folderDoPrzeniesienia = folderDoPrzeniesienia;
    }

    public String getNowaNazwaPliku() {
        return nowaNazwaPliku;
    }

    public void setNowaNazwaPliku(String nowaNazwaPliku) {
        this.nowaNazwaPliku = nowaNazwaPliku;
    }

    public String getWybranaWidocznosc() {
        return wybranaWidocznosc;
    }

    public void setWybranaWidocznosc(String wybranaWidocznosc) {
        this.wybranaWidocznosc = wybranaWidocznosc;
    }
    
    
}

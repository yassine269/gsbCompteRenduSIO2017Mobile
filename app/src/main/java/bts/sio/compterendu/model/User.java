package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class User {

    private int id;
    @SerializedName("usr_matricule")
    private String usrMatricule;
    @SerializedName("usr_nom")
    private String usrNom;
    @SerializedName("usr_prenom")
    private String usrPrenom;
    @SerializedName("usr_adresse")
    private String usrAdresse;
    @SerializedName("usr_cp")
    private int usrCp;
    @SerializedName("usr_ville")
    private String usrVille;
    @SerializedName("usr_date_embauche")
    private String usrDateEmbauche;
    @SerializedName("usr_fonction")
    private Fonction usrFonction;
    @SerializedName("usr_departement")
    private Departement usrDepartement;
    @SerializedName("usr_region")
    private Region usrRegion;
    @SerializedName("usr_secteur")
    private Secteur usrSecteur;
    @SerializedName("usr_supp")
    private User usrSupp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsrMatricule() {
        return usrMatricule;
    }

    public void setUsrMatricule(String usrMatricule) {
        this.usrMatricule = usrMatricule;
    }

    public String getUsrNom() {
        return usrNom;
    }

    public void setUsrNom(String usrNom) {
        this.usrNom = usrNom;
    }

    public String getUsrPrenom() {
        return usrPrenom;
    }

    public void setUsrPrenom(String usrPrenom) {
        this.usrPrenom = usrPrenom;
    }

    public String getUsrAdresse() {
        return usrAdresse;
    }

    public void setUsrAdresse(String usrAdresse) {
        this.usrAdresse = usrAdresse;
    }

    public Integer getUsrCp() {
        return usrCp;
    }

    public void setUsrCp(Integer usrCp) {
        this.usrCp = usrCp;
    }

    public String getUsrVille() {
        return usrVille;
    }

    public void setUsrVille(String usrVille) {
        this.usrVille = usrVille;
    }

    public String getUsrDateEmbauche() {
        return usrDateEmbauche;
    }

    public void setUsrDateEmbauche(String usrDateEmbauche) {
        this.usrDateEmbauche = usrDateEmbauche;
    }

    public Fonction getUsrFonction() {
        return usrFonction;
    }

    public void setUsrFonction(Fonction usrFonction) {
        this.usrFonction = usrFonction;
    }

    public Departement getUsrDepartement() {
        return usrDepartement;
    }

    public void setUsrDepartement(Departement usrDepartement) {
        this.usrDepartement = usrDepartement;
    }

    public Region getUsrRegion() {
        return usrRegion;
    }

    public void setUsrRegion(Region usrRegion) {
        this.usrRegion = usrRegion;
    }

    public Secteur getUsrSecteur() {
        return usrSecteur;
    }

    public void setUsrSecteur(Secteur usrSecteur) {
        this.usrSecteur = usrSecteur;
    }

    public User getUsrSupp() {
        return usrSupp;
    }

    public void setUsrSupp(User usrSupp) {
        this.usrSupp = usrSupp;
    }

    public String toString(){
        return this.getUsrNom()+" | "+getUsrPrenom();
    }
}

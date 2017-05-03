package bts.sio.compterendu.model;

import com.google.gson.annotations.Expose;

/**
 * Created by TI-tygangsta on 24/04/2017.
 */

public class Praticien {
    @Expose(serialize = true, deserialize = true)
    private int id;
    @Expose(serialize = false, deserialize = true)
    private String pra_nom;
    @Expose(serialize = false, deserialize = true)
    private String pra_prenom;
    @Expose(serialize = false, deserialize = true)
    private String pra_adresse;
    @Expose(serialize = false, deserialize = true)
    private String pra_cp;
    @Expose(serialize = false, deserialize = true)
    private String pra_ville;
    @Expose(serialize = false, deserialize = true)
    private String pra_coef_notoriete;
    @Expose(serialize = false, deserialize = true)
    private TypePraticien pra_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPra_nom() {
        return pra_nom;
    }

    public void setPra_nom(String pra_nom) {
        this.pra_nom = pra_nom;
    }

    public String getPra_prenom() {
        return pra_prenom;
    }

    public void setPra_prenom(String pra_prenom) {
        this.pra_prenom = pra_prenom;
    }

    public String getPra_adresse() {
        return pra_adresse;
    }

    public void setPra_adresse(String pra_adresse) {
        this.pra_adresse = pra_adresse;
    }

    public String getPra_cp() {
        return pra_cp;
    }

    public void setPra_cp(String pra_cp) {
        this.pra_cp = pra_cp;
    }

    public String getPra_ville() {
        return pra_ville;
    }

    public void setPra_ville(String pra_ville) {
        this.pra_ville = pra_ville;
    }

    public String getPra_coef_notoriete() {
        return pra_coef_notoriete;
    }

    public void setPra_coef_notoriete(String pra_coef_notoriete) {
        this.pra_coef_notoriete = pra_coef_notoriete;
    }

    public TypePraticien getPra_type() {
        return pra_type;
    }

    public void setPra_type(TypePraticien pra_type) {
        this.pra_type = pra_type;
    }

    public String toString(){
        return this.getPra_nom()+" | "+this.getPra_prenom();
    }
}

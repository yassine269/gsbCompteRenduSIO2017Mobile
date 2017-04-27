package bts.sio.compterendu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TI-tygangsta on 25/04/2017.
 */

public class Medicament {
    private int id;
    private String med_depot_legal;
    private String med_nom_commercial;
    private List<MedConstitution> med_compositions;
    private String med_effets;
    private String med_contre_indic;
    private int med_prix_echant;
    private Famille med_famille;
    private Prescrire med_prescrire;

    public Medicament() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMed_depot_legal() {
        return med_depot_legal;
    }

    public void setMed_depot_legal(String med_depot_legal) {
        this.med_depot_legal = med_depot_legal;
    }

    public String getMed_nom_commercial() {
        return med_nom_commercial;
    }

    public void setMed_nom_commercial(String med_nom_commercial) {
        this.med_nom_commercial = med_nom_commercial;
    }

    public List<MedConstitution> getMed_compositions() {
        return med_compositions;
    }

    public void setMed_compositions(List<MedConstitution> med_compositions) {
        this.med_compositions = med_compositions;
    }

    public String getMed_effets() {
        return med_effets;
    }

    public void setMed_effets(String med_effets) {
        this.med_effets = med_effets;
    }

    public String getMed_contre_indic() {
        return med_contre_indic;
    }

    public void setMed_contre_indic(String med_contre_indic) {
        this.med_contre_indic = med_contre_indic;
    }

    public int getMed_prix_echant() {
        return med_prix_echant;
    }

    public void setMed_prix_echant(int med_prix_echant) {
        this.med_prix_echant = med_prix_echant;
    }


    public Famille getMed_famille() {
        return med_famille;
    }

    public void setMed_famille(Famille med_famille) {
        this.med_famille = med_famille;
    }

    public Prescrire getMed_prescrire() {
        return med_prescrire;
    }

    public void setMed_prescrire(Prescrire med_prescrire) {
        this.med_prescrire = med_prescrire;
    }
}

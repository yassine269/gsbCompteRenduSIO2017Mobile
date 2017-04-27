package bts.sio.compterendu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yassine on 13/03/2017.
 */

public class RapportVisite {

    private int id;
    private Visiteur rap_visiteur;
    private Praticien rap_praticien;
    private Motif rap_motif;
    private String rap_date;
    private String rap_saisie_date;
    private String rap_bilan;
    private int rap_coef_impact;
    private List<RapportEchant> rap_echantillons;


    public List<RapportEchant> getRap_echantillons() {
        return rap_echantillons;
    }

    public void setRap_echantillons(List<RapportEchant> rap_echantillons) {
        this.rap_echantillons = rap_echantillons;
    }

    public String getRap_date() {
        return rap_date;
    }

    public void setRap_date(String rap_date) {
        this.rap_date = rap_date;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Praticien getRap_praticien() {
        return rap_praticien;
    }

    public void setRap_praticien(Praticien rap_praticien) {
        this.rap_praticien = rap_praticien;
    }

    public Motif getRap_motif() {
        return rap_motif;
    }

    public void setRap_motif(Motif rap_motif) {
        this.rap_motif = rap_motif;
    }

    public String getRap_saisie_date() {
        return rap_saisie_date;
    }

    public void setRap_saisie_date(String rap_saisie_date) {
        this.rap_saisie_date = rap_saisie_date;
    }

    public String getRap_bilan() {
        return rap_bilan;
    }

    public void setRap_bilan(String rap_bilan) {
        this.rap_bilan = rap_bilan;
    }

    public int getRap_coef_impact() {
        return rap_coef_impact;
    }

    public void setRap_coef_impact(int rap_coef_impact) {
        this.rap_coef_impact = rap_coef_impact;
    }

    public Visiteur getRap_visiteur() {
        return rap_visiteur;
    }

    public void setRap_visiteur(Visiteur rap_visiteur) {
        this.rap_visiteur = rap_visiteur;
    }
}

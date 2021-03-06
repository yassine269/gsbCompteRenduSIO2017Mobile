package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Yassine on 13/03/2017.
 */

public class RapportVisite {

    private int id;
    @SerializedName("rap_visiteur")
    private Visiteur rapVisiteur;
    @SerializedName("rap_praticien")
    private Praticien rapPraticien;
    @SerializedName("rap_motif")
    private Motif rapMotif;
    @SerializedName("rap_date")
    private String rapDate;
    @SerializedName("rap_saisie_date")
    private String rapSaisieDate;
    @SerializedName("rap_bilan")
    private String rapBilan;
    @SerializedName("rap_coef_impact")
    private int rapCoefImpact;
    @SerializedName("rap_echantillons")
    private ArrayList<RapportEchant> rapEchantillons;



    public ArrayList<RapportEchant> getRapEchantillons() {
        return rapEchantillons;
    }

    public void setRapEchantillons(ArrayList<RapportEchant> rapEchantillons) {
        this.rapEchantillons = rapEchantillons;
    }

    public String getRapDate() {
        return rapDate;
    }

    public void setRapDate(String rapDate) {
        this.rapDate = rapDate;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Praticien getRapPraticien() {
        return rapPraticien;
    }

    public void setRapPraticien(Praticien rapPraticien) {
        this.rapPraticien = rapPraticien;
    }

    public Motif getRapMotif() {
        return rapMotif;
    }

    public void setRapMotif(Motif rapMotif) {
        this.rapMotif = rapMotif;
    }

    public String getRapSaisieDate() {
        return rapSaisieDate;
    }

    public void setRapSaisieDate(String rapSaisieDate) {
        this.rapSaisieDate = rapSaisieDate;
    }

    public String getRapBilan() {
        return rapBilan;
    }

    public void setRapBilan(String rapBilan) {
        this.rapBilan = rapBilan;
    }

    public int getRapCoefImpact() {
        return rapCoefImpact;
    }

    public void setRapCoefImpact(int rapCoefImpact) {
        this.rapCoefImpact = rapCoefImpact;
    }

    public Visiteur getRapVisiteur() {
        return rapVisiteur;
    }

    public void setRapVisiteur(Visiteur rapVisiteur) {
        this.rapVisiteur = rapVisiteur;
    }
}

package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RapportVisitePost {

    private int id;
    @SerializedName("rap_visiteur")
    private int rapVisiteur;
    @SerializedName("rap_praticien")
    private int rapPraticien;
    @SerializedName("rap_motif")
    private int rapMotif;
    @SerializedName("rap_date")
    private String rapDate;
    @SerializedName("rap_saisie_date")
    private String rapSaisieDate;
    @SerializedName("rap_bilan")
    private String rapBilan;
    @SerializedName("rap_coef_impact")
    private int rapCoefImpact;
    @SerializedName("rap_echantillons")
    private List<RapportEchantPost> rapEchantillons;

    public int getRapVisiteur() {
        return rapVisiteur;
    }

    public void setRapVisiteur(int rapVisiteur) {
        this.rapVisiteur = rapVisiteur;
    }

    public int getRapPraticien() {
        return rapPraticien;
    }

    public void setRapPraticien(int rapPraticien) {
        this.rapPraticien = rapPraticien;
    }

    public int getRapMotif() {
        return rapMotif;
    }

    public void setRapMotif(int rapMotif) {
        this.rapMotif = rapMotif;
    }

    public List<RapportEchantPost> getRapEchantillons() {
        return rapEchantillons;
    }

    public void setRapEchantillons(List<RapportEchantPost> rapEchantillons) {
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
}

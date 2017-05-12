package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class ActCompl {

    private int id;
    @SerializedName("ac_lieu")
    private String acLieu;
    @SerializedName("ac_date")
    private String acDate;
    @SerializedName("ac_theme")
    private String acTheme;
    @SerializedName("ac_praticien")
    private ArrayList<Praticien> acPraticien;
    @SerializedName("ac_act_real")
    private ArrayList<ActRea> acActReal;
    @SerializedName("ac_states")
    private String acStates;

    public ArrayList<Praticien> getAcPraticien() {
        return acPraticien;
    }

    public void setAcPraticien(ArrayList<Praticien> acPraticien) {
        this.acPraticien = acPraticien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAcLieu() {
        return acLieu;
    }

    public void setAcLieu(String acLieu) {
        this.acLieu = acLieu;
    }

    public String getAcDate() {
        return acDate;
    }

    public void setAcDate(String acDate) {
        this.acDate = acDate;
    }

    public String getAcTheme() {
        return acTheme;
    }

    public void setAcTheme(String acTheme) {
        this.acTheme = acTheme;
    }

    public ArrayList<ActRea> getAcActReal() {
        return acActReal;
    }

    public void setAcActReal(ArrayList<ActRea> acActReal) {
        this.acActReal = acActReal;
    }

    public String getAcStates() {
        return acStates;
    }

    public void setAcStates(String acStates) {
        this.acStates = acStates;
    }
}

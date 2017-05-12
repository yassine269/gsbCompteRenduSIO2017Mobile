package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class ActComplPost {

    private int id;
    @SerializedName("ac_lieu")
    private String acLieu;
    @SerializedName("ac_date")
    private String acDate;
    @SerializedName("ac_theme")
    private String acTheme;
    @SerializedName("ac_praticien")
    private ArrayList<Integer> acPraticien;
    @SerializedName("ac_act_real")
    private List<ActReaPost> acActReal;
    @SerializedName("ac_states")
    private String acStates;

    public ArrayList<Integer> getAcPraticien() {
        return acPraticien;
    }

    public void setAcPraticien(ArrayList<Integer> acPraticien) {
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

    public List<ActReaPost> getAcActReal() {
        return acActReal;
    }

    public void setAcActReal(List<ActReaPost> acActReal) {
        this.acActReal = acActReal;
    }

    public String getAcStates() {
        return acStates;
    }

    public void setAcStates(String acStates) {
        this.acStates = acStates;
    }
}

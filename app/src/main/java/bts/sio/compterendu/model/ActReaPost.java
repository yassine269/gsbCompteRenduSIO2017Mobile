package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class ActReaPost {


    private int id;
    @SerializedName("act_rea_visiteur")
    private int actReaVisiteur;
    @SerializedName("act_rea_budget")
    private int actReaBudget;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActReaVisiteur() {
        return actReaVisiteur;
    }

    public void setActReaVisiteur(int actReaVisiteur) {
        this.actReaVisiteur = actReaVisiteur;
    }


    public int getActReaBudget() {
        return actReaBudget;
    }

    public void setActReaBudget(int actReaBudget) {
        this.actReaBudget = actReaBudget;
    }
}

package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class ActRea {


    private int id;
    @SerializedName("act_rea_visiteur")
    private User actReaVisiteur;
    @SerializedName("act_rea_act_compl")
    private ActCompl actReaActCompl;
    @SerializedName("act_rea_budget")
    private int actReaBudget;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getActReaVisiteur() {
        return actReaVisiteur;
    }

    public void setActReaVisiteur(User actReaVisiteur) {
        this.actReaVisiteur = actReaVisiteur;
    }

    public ActCompl getActReaActCompl() {
        return actReaActCompl;
    }

    public void setActReaActCompl(ActCompl actReaActCompl) {
        this.actReaActCompl = actReaActCompl;
    }

    public int getActReaBudget() {
        return actReaBudget;
    }

    public void setActReaBudget(int actReaBudget) {
        this.actReaBudget = actReaBudget;
    }

    public String toString(){
        return this.getActReaVisiteur().getUsrNom()+" | "+this.getActReaBudget()+"€";
    }
}

package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class Fonction {

    private int id;
    @SerializedName("fonct_libelle")
    private String fonctLibelle;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFonctLibelle() {
        return fonctLibelle;
    }

    public void setFonctLibelle(String fonctLibelle) {
        this.fonctLibelle = fonctLibelle;
    }
}

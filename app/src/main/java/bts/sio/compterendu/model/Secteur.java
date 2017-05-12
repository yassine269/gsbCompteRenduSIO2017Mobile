package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class Secteur {

    private int id;
    @SerializedName("sec_libelle")
    private String secLibelle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecLibelle() {
        return secLibelle;
    }

    public void setSecLibelle(String secLibelle) {
        this.secLibelle = secLibelle;
    }
}

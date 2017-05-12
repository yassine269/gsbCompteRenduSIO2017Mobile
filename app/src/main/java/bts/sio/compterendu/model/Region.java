package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class Region {

    private int id;
    @SerializedName("reg_libelle")
    private String regLibelle;
    @SerializedName("reg_secteur")
    private Secteur regSecteur;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegLibelle() {
        return regLibelle;
    }

    public void setRegLibelle(String regLibelle) {
        this.regLibelle = regLibelle;
    }

    public Secteur getRegSecteur() {
        return regSecteur;
    }

    public void setRegSecteur(Secteur regSecteur) {
        this.regSecteur = regSecteur;
    }
}

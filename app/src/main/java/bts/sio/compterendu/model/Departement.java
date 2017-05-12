package bts.sio.compterendu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TI-tygangsta on 04/05/2017.
 */

public class Departement {

    protected int id;
    @SerializedName("dep_libelle")
    private String  depLibelle;
    @SerializedName("dep_region")
    private Region depRegion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepLibelle() {
        return depLibelle;
    }

    public void setDepLibelle(String depLibelle) {
        this.depLibelle = depLibelle;
    }

    public Region getDepRegion() {
        return depRegion;
    }

    public void setDepRegion(Region depRegion) {
        this.depRegion = depRegion;
    }
}

package bts.sio.compterendu.model;

import com.google.gson.annotations.Expose;

/**
 * Created by TI-tygangsta on 24/04/2017.
 */

public class Motif {
    @Expose(serialize = true, deserialize = true)
    private int id;
    @Expose(serialize = false, deserialize = true)
    private String motif_libelle;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMotif_libelle() {
        return motif_libelle;
    }

    public void setMotif_libelle(String motif_libelle) {
        this.motif_libelle = motif_libelle;
    }
    public String toString(){
        return this.getMotif_libelle();
    }
}

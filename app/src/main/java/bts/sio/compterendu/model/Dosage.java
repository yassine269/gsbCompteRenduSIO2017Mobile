package bts.sio.compterendu.model;

/**
 * Created by TI-tygangsta on 25/04/2017.
 */

public class Dosage {
    private int id;
    private int dos_quantite;
    private String dos_unite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDos_quantite() {
        return dos_quantite;
    }

    public void setDos_quantite(int dos_quantite) {
        this.dos_quantite = dos_quantite;
    }

    public String getDos_unite() {
        return dos_unite;
    }

    public void setDos_unite(String dos_unite) {
        this.dos_unite = dos_unite;
    }
}

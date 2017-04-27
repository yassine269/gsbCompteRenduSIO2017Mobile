package bts.sio.compterendu.model;

/**
 * Created by TI-tygangsta on 25/04/2017.
 */

public class RapportEchant {
    private int id;
    private Medicament rap_echant_medicament;
    private int rap_echant_quantite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Medicament getRap_echant_medicament() {
        return rap_echant_medicament;
    }

    public void setRap_echant_medicament(Medicament rap_echant_medicament) {
        this.rap_echant_medicament = rap_echant_medicament;
    }

    public int getRap_echant_quantite() {
        return rap_echant_quantite;
    }

    public void setRap_echant_quantite(int rap_echant_quantite) {
        this.rap_echant_quantite = rap_echant_quantite;
    }
}

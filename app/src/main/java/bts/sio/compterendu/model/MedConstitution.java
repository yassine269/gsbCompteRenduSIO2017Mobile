package bts.sio.compterendu.model;

/**
 * Created by TI-tygangsta on 25/04/2017.
 */

public class MedConstitution {
    private int id;
    private Composant const_composant;
    private int const_quantite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Composant getConst_composant() {
        return const_composant;
    }

    public void setConst_composant(Composant const_composant) {
        this.const_composant = const_composant;
    }

    public int getConst_quantite() {
        return const_quantite;
    }

    public void setConst_quantite(int const_quantite) {
        this.const_quantite = const_quantite;
    }
}
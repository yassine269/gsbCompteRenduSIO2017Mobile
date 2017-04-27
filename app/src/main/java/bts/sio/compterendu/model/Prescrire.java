package bts.sio.compterendu.model;

/**
 * Created by TI-tygangsta on 25/04/2017.
 */

public class Prescrire {
    private int id;
    private Medicament pres_med;
    private Dosage pres_dosage;
    private TypeIndividu pres_type_indiv;
    private String pres_posologie;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Medicament getPres_med() {
        return pres_med;
    }

    public void setPres_med(Medicament pres_med) {
        this.pres_med = pres_med;
    }

    public Dosage getPres_dosage() {
        return pres_dosage;
    }

    public void setPres_dosage(Dosage pres_dosage) {
        this.pres_dosage = pres_dosage;
    }

    public TypeIndividu getPres_type_indiv() {
        return pres_type_indiv;
    }

    public void setPres_type_indiv(TypeIndividu pres_type_indiv) {
        this.pres_type_indiv = pres_type_indiv;
    }

    public String getPres_posologie() {
        return pres_posologie;
    }

    public void setPres_posologie(String pres_posologie) {
        this.pres_posologie = pres_posologie;
    }
}

package bts.sio.compterendu.model;

/**
 * Created by TI-tygangsta on 24/04/2017.
 */

public class TypePraticien {
    private int id;
    private String type_libelle;
    private String type_lieu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType_libelle() {
        return type_libelle;
    }

    public void setType_libelle(String type_libelle) {
        this.type_libelle = type_libelle;
    }

    public String getType_lieu() {
        return type_lieu;
    }

    public void setType_lieu(String type_lieu) {
        this.type_lieu = type_lieu;
    }

}

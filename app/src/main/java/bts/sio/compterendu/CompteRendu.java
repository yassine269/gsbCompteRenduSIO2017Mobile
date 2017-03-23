package bts.sio.compterendu;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yassine on 13/03/2017.
 */

public class CompteRendu {

    private int id;
    private int numRap;
    private String praticien;
    private Date dateRap;
    private String motifVisit;
    private String bilan;
    private String nomEchant;
    private int nbEchant;

    public int getId() {
        return id;
    }

    public int getNumRap() {
        return numRap;
    }

    public String getPraticien() {
        return praticien;
    }

    public Date getDateRap() {
        return dateRap;
    }

    public String getMotifVisit() {
        return motifVisit;
    }

    public String getBilan() {
        return bilan;
    }

    public String getNomEchant() {
        return nomEchant;
    }

    public int getNbEchant() {
        return nbEchant;
    }

}

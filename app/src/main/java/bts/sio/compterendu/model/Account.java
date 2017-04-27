package bts.sio.compterendu.model;

import android.content.Intent;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TI-tygangsta on 18/04/2017.
 */

public class Account {

    private int id;
    private String username;
    private String salt;
    private String fonction;
    private String clearPass=null;

    public String hashPassword(String salt, String clearPassword) {
        String hash = "";
        try {
            //Log.d("AuthProvider", "start hashing password...");
            String salted = null;
            if(salt == null || "".equals(salt)) {
                salted = clearPassword;
            } else {
                salted = clearPassword + "{" + salt + "}";
            }
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte sha[] = md.digest(salted.getBytes());
            for(int i = 1; i < 5000; i++) {
                byte c[] = new byte[sha.length + salted.getBytes().length];
                System.arraycopy(sha, 0, c, 0, sha.length);
                System.arraycopy(salted.getBytes(), 0, c, sha.length, salted.getBytes().length);
                sha = md.digest(c);
            }
            hash = new String(Base64.encode(sha,Base64.NO_WRAP));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //do something with this exception
        }
        //Log.d("AuthProvider", "hashing password is done!");
        return hash;
    }
    public boolean checkConnection(Calendar limitConnect){
        Calendar currentTime= Calendar.getInstance();
        Date currentDate = new Date();
        // Instantiate a Date object
        currentTime.setTime(currentDate);
        if (limitConnect.after(currentDate)){
            return false;
        }
        return true;
    }

    public Account(){

    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClearPass() {
        return clearPass;
    }

    public void setClearPass(String clearPass) {
        this.clearPass = clearPass;
    }
}

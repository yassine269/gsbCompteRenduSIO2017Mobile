package bts.sio.compterendu.model;

import com.google.gson.annotations.Expose;

/**
 * Created by TI-tygangsta on 24/04/2017.
 */

public class Visiteur {
    @Expose(serialize = true, deserialize = true)
    private int id;
    @Expose(serialize = false, deserialize = true)
    private String username;

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}

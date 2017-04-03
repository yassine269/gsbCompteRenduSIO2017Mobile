package bts.sio.compterendu;

public class Comment {
    private long id;
    private String comment;
    private String mdp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp1) {
        this.mdp = mdp1;
    }

    // Sera utilisée par ArrayAdapter dans la ListView
    @Override
    public String toString() {
        return comment;
    }
}
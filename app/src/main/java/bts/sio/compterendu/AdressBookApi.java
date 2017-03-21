package bts.sio.compterendu;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Yassine on 13/03/2017.
 */

public interface AdressBookApi {
    @GET("/CompteRendu")
    Call<List<CompteRendu>> getCompteRenduList();

    @GET("/CompteRendu/{UnCompteRendu}")
    Call<CompteRendu> getUnCompteRendu(@Path("UnCompteRendu") int UnCompteRenduId);
}

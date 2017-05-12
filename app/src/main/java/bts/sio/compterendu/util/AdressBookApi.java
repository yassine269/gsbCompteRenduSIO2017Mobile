package bts.sio.compterendu.util;

import java.util.Collection;
import java.util.List;

import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.ActCompl;
import bts.sio.compterendu.model.ActComplPost;
import bts.sio.compterendu.model.Medicament;
import bts.sio.compterendu.model.Motif;
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.model.RapportEchant;
import bts.sio.compterendu.model.RapportVisite;
import bts.sio.compterendu.model.RapportVisitePost;
import bts.sio.compterendu.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Yassine on 13/03/2017.
 */

public interface AdressBookApi {
    String ENDPOINT="http://10.0.2.2/gsbCompteRendu/web/app_dev.php/api/";
    //COMPTE ET LOGIN
    @GET("publics/{user}/users")
    Call<Account>account(@Path("user") String user);
    @GET("login")
    Call<LoginApi>getApiLogin();
    // PRATICIEN
    @GET("praticien")
    Call<List<Praticien>> getPraticienList();
    @GET("onepraticiens/{id}")
    Call<Praticien> getOnePraticien(@Path("id") int id);
    // MEDICAMENT
    @GET("medicament")
    Call<List<Medicament>> getMedicamentList();
    @GET("onemedicaments/{id}")
    Call<Medicament> getOneMedicament(@Path("id") int id);
    // COLLABORATEUR // USER
    @GET("user")
    Call<List<User>> getCollabList();
    @GET("oneusers/{id}")
    Call<User> getOneCollab(@Path("id") int id);
    //MOTIF
    @GET("motif")
    Call<List<Motif>> getMotifList();
    @GET("onemotifs/{id}")
    Call<Motif> getOneMotif(@Path("id") int id);
    //RAPPORTS DE VISITE
    @GET("rapports")
    Call<List<RapportVisite>> getRapportList(
            @Query("redacteur") int redaceur,
            @Query("template") String templateKey
    );
    @GET("rapports")
    Call<List<RapportVisite>> getRapportList(
            @Query("redacteur") int redaceur,
            @Query("region") int redacteurRegion,
            @Query("secteur") int redacteurSecteur
    );

    @GET("onerapports/{id}")
    Call<RapportVisite> getOneCr(@Path("id") int id);
    @POST("rapports")
    Call<RapportVisitePost> saisieCR(@Body RapportVisitePost rapportVisite);
    @PATCH("updaterapport")
    Call<RapportVisitePost> modifCR(@Body RapportVisitePost rapportVisite);
    @GET("actcompl")
    Call<List<ActCompl>> getActComplList();
    @GET("actcompl")
    Call<List<ActCompl>> getActComplList(
            @Query("template") String templateKey,
            @Query("realisation") int realisation
    );
    @GET("oneactcompls/{id}")
    Call<ActCompl> getOneActCompl(@Path("id") int id);
    @POST("actcompls")
    Call<ActComplPost> saisieActCompl(@Body ActComplPost actComplPost);
    @PATCH("updateactcompl")
    Call<ActComplPost> modifActCompl(@Body ActComplPost actComplPost);
}

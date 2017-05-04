package bts.sio.compterendu.util;

import java.util.Collection;
import java.util.List;

import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.Medicament;
import bts.sio.compterendu.model.Motif;
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.model.RapportEchant;
import bts.sio.compterendu.model.RapportVisite;
import bts.sio.compterendu.model.RapportVisitePost;
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
    @GET("onerapports/{id}")
    Call<RapportVisite> getOneCr(@Path("id") int id);
    @POST("rapports")
    Call<RapportVisitePost> saisieCR(@Body RapportVisitePost rapportVisite);
    @PATCH("updaterapport")
    Call<RapportVisitePost> modifCR(@Body RapportVisitePost rapportVisite);
}

package bts.sio.compterendu.util;

import java.util.List;

import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.RapportEchant;
import bts.sio.compterendu.model.RapportVisite;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Yassine on 13/03/2017.
 */

public interface AdressBookApi {
    @GET("publics/{user}/users")
    Call<Account>account(@Path("user") String user);
    @GET("login")
    Call<LoginApi>getApiLogin();
    @GET("rapports")
    Call<List<RapportVisite>> getRapportList();
    @GET("onerapports/{id}")
    Call<RapportVisite> getOneCr(@Path("id") int id);
    @GET("/RapportVisite/{UnCompteRendu}")
    Call<RapportVisite> getUnCompteRendu(@Path("UnCompteRendu") int UnCompteRenduId);
    @FormUrlEncoded
    @POST("user/edit")
    Call<RapportVisite> saisieCR(
            @Field("rapEchantillons") List<RapportEchant> rapportEchants,
            @Field("rapVisiteur") int id_visiteur,
            @Field("rapMotif") int id_motif,
            @Field("rapDate") String rap_date,
            @Field("rapBilan") String rap_bilan,
            @Field("rapCoefImpact") int rap_coef_impact);
}

package bts.sio.compterendu.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import bts.sio.compterendu.security.WsseToken;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TI-tygangsta on 23/04/2017.
 */

public class RetrofitConnect {

    private String identifiant;
    private WsseToken token;

    public RetrofitConnect(String identifiant,WsseToken token){
        this.identifiant=identifiant;
        this.token=token;
    }
    public Retrofit buildRequest(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original=chain.request();
                Request request=original.newBuilder()
                        .header("x-wsse","UsernameToken Username="+'\"'+identifiant+'\"'+
                                ", PasswordDigest="+'\"'+token.getDigest()+'\"'+
                                ", Nonce="+'\"'+token.getNonce()+'\"'+", Created="+'\"'+token.getCreatedAt()+'\"')
                        .header("Content-TypePraticien","application/json")
                        .header(WsseToken.HEADER_AUTHORIZATION,token.getAuthorizationHeader())
                        .method(original.method(),original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        Gson gson = new GsonBuilder()
                .setDateFormat("dd-MMM-yyyy")
                .create();
        OkHttpClient client=httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/gsbCompteRendu/web/app_dev.php/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        return retrofit;
    }
}

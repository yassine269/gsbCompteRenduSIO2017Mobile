package bts.sio.compterendu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static bts.sio.compterendu.R.id.numRapTxt;

public class ConsulterCRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultercr);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getCompteRenduList().enqueue(new Callback<List<CompteRendu>>() {
            @Override
            public void onResponse(Call<List<CompteRendu>> call, Response<List<CompteRendu>> response) {
                Log.i("retrofit","Download ok");
                for (CompteRendu compterendu:response.body()) {
                    
                }

            }

            @Override
            public void onFailure(Call<List<CompteRendu>> call, Throwable t) {

            }
        });
    }
}

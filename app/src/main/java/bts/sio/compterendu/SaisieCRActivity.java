package bts.sio.compterendu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static bts.sio.compterendu.R.id.numRap;

public class SaisieCRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisiecr);


        Intent page = getIntent();
        String numero = page.getStringExtra("numRapSelected");
        int numeroFinal=numero.charAt(numero.length()-1);
        int numRapFinal=page.getIntegerArrayListExtra("listCR").get(numeroFinal);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getUnCompteRendu(numRapFinal).enqueue(new Callback<CompteRendu>() {
            @Override
            public void onResponse(Call<CompteRendu> call, Response<CompteRendu> response) {
                ((TextView) findViewById(R.id.numRap)).setText(response.body().getNumRap());
                ((TextView) findViewById(R.id.praticien)).setText(response.body().getPraticien());
                ((TextView) findViewById(R.id.dateRap)).setText(response.body().getDateRap().toString());
                //((Spinner) findViewById(R.id.motifSpinner)).get(response.body().getMotifVisit());
                ((TextView) findViewById(R.id.bilan)).setText(response.body().getBilan());
                //((TextView) findViewById(R.id.echantSpinner)).setText(response.body().getNomEchant());
                ((TextView) findViewById(R.id.quant)).setText(response.body().getNbEchant());
            }

            @Override
            public void onFailure(Call<CompteRendu> call, Throwable t) {
                Log.i("retrofit","Erreur lors de l'ajout d'un compte rendu à la page de saisie");
            }
        });

    }
}

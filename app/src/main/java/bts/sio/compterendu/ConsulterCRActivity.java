package bts.sio.compterendu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static bts.sio.compterendu.R.id.numRap;

public class ConsulterCRActivity extends AppCompatActivity {

    ListView mListView;
    String[] prenoms = new String[]{
            "Antoine", "Benoit", "Cyril", "David", "Eloise", "Florent",
            "Gerard", "Hugo", "Ingrid", "Jonathan", "Kevin", "Logan",
            "Mathieu", "Noemie", "Olivia", "Philippe", "Quentin", "Romain",
            "Sophie", "Tristan", "Ulric", "Vincent", "Willy", "Xavier",
            "Yann", "Zoé"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultercr);


        mListView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ConsulterCRActivity.this,
                android.R.layout.simple_list_item_1, prenoms);
        mListView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        String textTest = ((EditText) findViewById(numRap)).getText().toString();
        service.getUnCompteRendu(Integer.parseInt(textTest)).enqueue(new Callback<CompteRendu>() {
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

            }
        });
    }
}

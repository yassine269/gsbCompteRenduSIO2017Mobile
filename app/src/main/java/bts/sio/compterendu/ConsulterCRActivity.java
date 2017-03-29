package bts.sio.compterendu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static bts.sio.compterendu.R.id.numRap;

public class ConsulterCRActivity extends AppCompatActivity {

    public ListView mListView;
    public List listCompteRendu = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultercr);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getCompteRenduList().enqueue(new Callback<List<CompteRendu>>() {
            @Override
            public void onResponse(Call<List<CompteRendu>> call, Response<List<CompteRendu>> response) {
                for (CompteRendu compteRendu:response.body()) {
                    listCompteRendu.add(compteRendu.getNumRap());
                }
                mListView = (ListView) findViewById(R.id.listView);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ConsulterCRActivity.this,
                        android.R.layout.simple_list_item_1, listCompteRendu);
                mListView.setAdapter(adapter);


                final ListView lv = mListView;
                lv.setClickable(true);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                        String result = (String) lv.getItemAtPosition(position);

                        Intent page_suivante = new Intent(getApplicationContext(),SaisieCRActivity.class);
                        page_suivante.putExtra("numRapSelected", result);
                        startActivity(page_suivante);

                    }
                });
            }

            @Override
            public void onFailure(Call<List<CompteRendu>> call, Throwable t) {
                Log.i("retrofit","Erreur lors de l'ajout d'un compte rendu à la ListView");
            }
        });




    }
}

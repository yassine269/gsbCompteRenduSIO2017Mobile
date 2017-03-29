package bts.sio.compterendu;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import bts.sio.compterendu.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AndroidSpinnerExampleActivity extends Activity implements OnItemSelectedListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.motifSpinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        final List<String> motifs = new ArrayList<String>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getCompteRenduList().enqueue(new Callback<List<CompteRendu>>() {
            @Override
            public void onResponse(Call<List<CompteRendu>> call, Response<List<CompteRendu>> response) {
                for (CompteRendu compteRendu:response.body()) {
                    motifs.add(compteRendu.getMotifVisit());
                }
            }

            @Override
            public void onFailure(Call<List<CompteRendu>> call, Throwable t) {
                Log.i("retrofit","Erreur lors de l'ajout d'un motif de compte rendu au spinner des motifs");
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, motifs);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
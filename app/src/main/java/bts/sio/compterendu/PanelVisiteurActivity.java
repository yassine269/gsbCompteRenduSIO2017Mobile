package bts.sio.compterendu;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PanelVisiteurActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiteur);

        Button bt_saisircr = (Button) findViewById(R.id.SaisirCR);
        bt_saisircr.setOnClickListener(this);

        Button bt_consultcr = (Button) findViewById(R.id.ConsulterCR);
        bt_consultcr.setOnClickListener(this);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTest);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Automobile");
        categories.add("Business Services");
        categories.add("Computers");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.SaisirCR:
                Intent page_saisie = new Intent(getApplicationContext(),SaisieCRActivity.class);
                startActivity(page_saisie);
                break;
            case R.id.ConsulterCR:
                Intent page_consulter = new Intent(getApplicationContext(),ConsulterCRActivity.class);
                startActivity(page_consulter);


                //Button bt_ok = (Button) findViewById(R.id.valid_button);
                //bt_ok.setText("Ok");

                //nb = (TextView) findViewById(R.id.nb_txt);
                //nb.setText("");
                //((TextView) findViewById(R.id.password_txt)).setText("");
                //Toast.makeText(getApplicationContext(),"annuler", Toast.LENGTH_SHORT).show();
                break;

        }

    }
}

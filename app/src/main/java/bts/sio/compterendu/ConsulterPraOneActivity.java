package bts.sio.compterendu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.MedConstitution;
import bts.sio.compterendu.model.Medicament;
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.model.RapportVisite;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by TI-tygangsta on 25/04/2017.
 */

public class ConsulterPraOneActivity extends AppCompatActivity {
    private CRReaderDbHelper mDbHelper;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_praticien_one);
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);
        long timeMillis = intent.getLongExtra("limitConnect", 0);
        int praId=intent.getIntExtra("pra_id",0);
        final Calendar limitConnect = Calendar.getInstance();
        limitConnect.setTimeInMillis(timeMillis);
        mDbHelper = new CRReaderDbHelper(getApplicationContext());
        //INIT BDD READABLE
        final Account user = mDbHelper.getUser(getApplicationContext());
        if (!user.checkConnection(limitConnect)) {
            Intent intentLogin = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentLogin);
        } else {
            Date limitConnectReplace = new Date(); // Instantiate a Date object
            limitConnect.setTime(limitConnectReplace);
            limitConnect.add(Calendar.MINUTE, 5);
        }
        // INIT LOADER
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Récupération du praticien...");
        mProgressDialog.show();
        //***********PREPARATION HEADER WSSE******************
        String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
        final WsseToken token  = new WsseToken(user,passEncript);
        RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
        Retrofit retrofit=conncecting.buildRequest();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getOnePraticien(praId).enqueue(new Callback<Praticien>() {
            @Override
            public void onResponse(Call<Praticien> call, Response<Praticien> response) {
                //END LOADER
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.i("DOWNLOAD :: ","OK");
                Praticien praticien = response.body();
                setTextViewHandler(praticien); // Appel méthode pour affichage
            }

            @Override
            public void onFailure(Call<Praticien> call, Throwable t) {
                //END LOADER
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.i("DOWNLOAD :: ","FAIL");
                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez",Toast.LENGTH_SHORT).show();

            }
        });
        Button bt_retour = (Button) findViewById(R.id.retour_button);
        bt_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterPraActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
    }
    // Affichage des informations récupérer
    public void setTextViewHandler(Praticien pra){
        final TextView ui_praId=(TextView)findViewById(R.id.praticien_id_view);
        final TextView ui_praNom=(TextView)findViewById(R.id.pra_nom_view);
        final TextView ui_praPrenom=(TextView)findViewById(R.id.pra_prenom_view);
        final TextView ui_praAdresse=(TextView)findViewById(R.id.pra_adresse_view);
        final TextView ui_praCp=(TextView)findViewById(R.id.pra_cp_view);
        final TextView ui_praVille=(TextView)findViewById(R.id.pra_ville_view);
        final TextView ui_praCoefNot=(TextView)findViewById(R.id.pra_coef_view);
        final TextView ui_praType=(TextView)findViewById(R.id.pra_type_view);
        if (pra!=null){
            ui_praId.setText(Integer.toString(pra.getId()));
            ui_praNom.setText(pra.getPra_nom());
            ui_praPrenom.setText(pra.getPra_prenom());
            ui_praAdresse.setText(pra.getPra_adresse());
            ui_praCp.setText(pra.getPra_cp());
            ui_praVille.setText(pra.getPra_ville());
            ui_praType.setText(pra.getPra_type().getType_libelle());
        }
        else {
            Log.i("recup praticien","NON");

        }
    }
}

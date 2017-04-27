package bts.sio.compterendu;

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
import bts.sio.compterendu.model.RapportEchant;
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

public class ConsulterCrOneActivity  extends AppCompatActivity {
    private CRReaderDbHelper mDbHelper;
    private RapportVisite cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultercr_one);
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);
        long timeMillis = intent.getLongExtra("limitConnect", 0);
        int crId=intent.getIntExtra("cr_id",0);
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
        //***********PREPARATION HEADER WSSE******************
        String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
        final WsseToken token  = new WsseToken(user,passEncript);
        RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
        Retrofit retrofit=conncecting.buildRequest();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getOneCr(crId).enqueue(new Callback<RapportVisite>() {
            @Override
            public void onResponse(Call<RapportVisite> call, Response<RapportVisite> response) {
                RapportVisite cr = response.body();
                setTextViewHandler(cr); // Appel méthode pour affichage
            }

            @Override
            public void onFailure(Call<RapportVisite> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez",Toast.LENGTH_SHORT).show();

            }
        });
        Button bt_retour = (Button) findViewById(R.id.retour_button);
        bt_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterCRActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
    }
    // Affichage des informations récupérer
    public void setTextViewHandler(RapportVisite cr){
        final TextView ui_crId=(TextView)findViewById(R.id.cr_id_view);
        final TextView ui_crVisit=(TextView)findViewById(R.id.cr_visit_view);
        final TextView ui_crPraticien=(TextView)findViewById(R.id.cr_praticien_view);
        final TextView ui_crDate=(TextView)findViewById(R.id.cr_date_view);
        final TextView ui_crMotif=(TextView)findViewById(R.id.cr_motif_view);
        final TextView ui_crBilan=(TextView)findViewById(R.id.cr_bilan_view);
        final TextView ui_crCoefImpact=(TextView)findViewById(R.id.cr_coef_impact_view);
        final TextView ui_crEchantillons=(TextView)findViewById(R.id.cr_echantillons_view);
        if (cr!=null){
            String cr_date=cr.getRap_date();
            cr_date=cr_date.substring(0,10);
            ui_crId.setText(Integer.toString(cr.getId()));
            ui_crVisit.setText(cr.getRap_visiteur().getUsername());
            ui_crPraticien.setText(cr.getRap_praticien().getPra_nom()+" | "+cr.getRap_praticien().getPra_prenom());
            ui_crDate.setText(cr_date);
            ui_crBilan.setText(cr.getRap_bilan());
            ui_crMotif.setText(cr.getRap_motif().getMotif_libelle());
            ui_crCoefImpact.setText(Integer.toString(cr.getRap_coef_impact()));
            for (RapportEchant rapportEchant :cr.getRap_echantillons()){
                    ui_crEchantillons.append(rapportEchant.getRap_echant_medicament().getMed_depot_legal()+", ");

            }
        }
        else {
            Log.i("recup rapport","NON");

        }
    }
}

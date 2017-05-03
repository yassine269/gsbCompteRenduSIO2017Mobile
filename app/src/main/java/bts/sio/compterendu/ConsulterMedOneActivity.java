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

import bts.sio.compterendu.ConsulterCRActivity;
import bts.sio.compterendu.MainActivity;
import bts.sio.compterendu.R;
import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.MedConstitution;
import bts.sio.compterendu.model.Medicament;
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

public class ConsulterMedOneActivity extends AppCompatActivity {
    private CRReaderDbHelper mDbHelper;
    private RapportVisite cr;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_medicament_one);
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);
        long timeMillis = intent.getLongExtra("limitConnect", 0);
        int medId=intent.getIntExtra("med_id",0);
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
        mProgressDialog.setMessage("Récupération du médicament...");
        mProgressDialog.show();
        //***********PREPARATION HEADER WSSE******************
        String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
        final WsseToken token  = new WsseToken(user,passEncript);
        RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
        Retrofit retrofit=conncecting.buildRequest();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getOneMedicament(medId).enqueue(new Callback<Medicament>() {
            @Override
            public void onResponse(Call<Medicament> call, Response<Medicament> response) {
                //END LOADER
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.i("DOWNLOAD :: ","OK");
                Medicament med = response.body();
                setTextViewHandler(med); // Appel méthode pour affichage
            }

            @Override
            public void onFailure(Call<Medicament> call, Throwable t) {
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
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterMedActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
    }
    // Affichage des informations récupérer
    public void setTextViewHandler(Medicament med){
        final TextView ui_medId=(TextView)findViewById(R.id.medicament_id_view);
        final TextView ui_medDepotLegal=(TextView)findViewById(R.id.med_depot_legal_view);
        final TextView ui_medNomCom=(TextView)findViewById(R.id.med_nom_com_view);
        final TextView ui_medCompositions=(TextView)findViewById(R.id.med_compositions_view);
        final TextView ui_medEffets=(TextView)findViewById(R.id.med_effets_view);
        final TextView ui_medContreIndic=(TextView)findViewById(R.id.medicament_contrindic_view);
        final TextView ui_medFamille=(TextView)findViewById(R.id.med_famille_view);
        final TextView ui_medPrixEchant=(TextView)findViewById(R.id.prix_echant_view);
        if (med!=null){
            ui_medId.setText(Integer.toString(med.getId()));
            ui_medDepotLegal.setText(med.getMed_depot_legal());
            ui_medNomCom.setText(med.getMed_nom_commercial());
            ui_medEffets.setText(med.getMed_effets());
            ui_medContreIndic.setText(med.getMed_contre_indic());
            ui_medFamille.setText(med.getMed_famille().getFam_libelle());
            ui_medPrixEchant.setText(Integer.toString(med.getMed_prix_echant()));


            for (MedConstitution medConstitution :med.getMed_compositions()){
                    ui_medCompositions.append(medConstitution.getConst_composant().getComp_libelle()+", ");

            }
        }
        else {
            Log.i("recup medicment","NON");

        }
    }
}

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
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.model.User;
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

public class ConsulterCollabOneActivity extends AppCompatActivity {
    private CRReaderDbHelper mDbHelper;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultercollab_one);
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);
        long timeMillis = intent.getLongExtra("limitConnect", 0);
        int collabId=intent.getIntExtra("collab_id",0);
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
        mProgressDialog.setMessage("Récupération du collaborateur...");
        mProgressDialog.show();
        //***********PREPARATION HEADER WSSE******************
        String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
        final WsseToken token  = new WsseToken(user,passEncript);
        RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
        Retrofit retrofit=conncecting.buildRequest();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getOneCollab(collabId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                //END LOADER
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.i("DOWNLOAD :: ","OK");
                User collab = response.body();
                setTextViewHandler(collab); // Appel méthode pour affichage
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
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
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterCollabActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
    }
    // Affichage des informations récupérer
    public void setTextViewHandler(User collab){
        final TextView ui_collabId=(TextView)findViewById(R.id.collab_id_view);
        final TextView ui_collabNom=(TextView)findViewById(R.id.collab_nom_view);
        final TextView ui_collabPrenom=(TextView)findViewById(R.id.collab_prenom_view);
        final TextView ui_collabMatricule=(TextView)findViewById(R.id.collab_matricule_view);
        final TextView ui_collabVille=(TextView)findViewById(R.id.collab_ville_view);
        final TextView ui_collabCp=(TextView)findViewById(R.id.collab_cp_view);
        final TextView ui_collabRegion=(TextView)findViewById(R.id.collab_region_view);
        final TextView ui_collabDepartement=(TextView)findViewById(R.id.collab_departement_view);
        final TextView ui_collabSecteur=(TextView)findViewById(R.id.collab_secteur_view);
        final TextView ui_collabSupp=(TextView)findViewById(R.id.collab_supp_view);
        final TextView ui_collabAdresse=(TextView)findViewById(R.id.collab_adresse_view);
        final TextView ui_collabDateEmbauche=(TextView)findViewById(R.id.collab_date_embauche_view);
        final TextView ui_collabFonction=(TextView)findViewById(R.id.collab_fonction_view);
        if (collab!=null){
            ui_collabId.setText(Integer.toString(collab.getId()));
            ui_collabNom.setText(collab.getUsrNom());
            ui_collabPrenom.setText(collab.getUsrPrenom());
            ui_collabMatricule.setText(collab.getUsrMatricule());
            ui_collabVille.setText(collab.getUsrVille());
            ui_collabCp.setText(Integer.toString(collab.getUsrCp()));
            ui_collabRegion.setText(collab.getUsrRegion().getRegLibelle());
            ui_collabDepartement.setText(collab.getUsrDepartement().getDepLibelle());
            ui_collabSecteur.setText(collab.getUsrSecteur().getSecLibelle());
            ui_collabSupp.setText(collab.getUsrSupp().getUsrNom()+" | "+collab.getUsrSupp().getUsrPrenom());
            ui_collabAdresse.setText(collab.getUsrAdresse());
            ui_collabDateEmbauche.setText(collab.getUsrDateEmbauche().toString());
            ui_collabFonction.setText(collab.getUsrFonction().getFonctLibelle());
        }
        else {
            Log.i("recup collaborateur : ","NON");

        }
    }
}

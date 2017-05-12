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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.ActCompl;
import bts.sio.compterendu.model.ActComplPost;
import bts.sio.compterendu.model.ActRea;
import bts.sio.compterendu.model.ActReaPost;
import bts.sio.compterendu.model.Praticien;
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

public class ConsulterAcOneActivity extends AppCompatActivity {
    private CRReaderDbHelper mDbHelper;
    private ProgressDialog mProgressDialog;
    private String templateKey;
    private ActCompl ac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_actcompl_one);
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);
        long timeMillis = intent.getLongExtra("limitConnect", 0);
        templateKey = intent.getStringExtra("templateKey");
        int acId=intent.getIntExtra("acId",0);
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
        Button bt_actionRefuse=(Button) findViewById(R.id.btn_refuse);
        Button bt_actionValid=(Button) findViewById(R.id.btn_valid);
        if (user.getFonction().equals("Visiteur")){
            bt_actionRefuse.setVisibility(View.GONE);
            bt_actionValid.setVisibility(View.GONE);
        }
        if (user.getFonction().equals("Delegue")){
        }
        if (user.getFonction().equals("Responsable")){
            bt_actionRefuse.setVisibility(View.GONE);
            bt_actionValid.setVisibility(View.GONE);
        }
        // INIT LOADER
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Récupération de l'activité complémentaire...");
        mProgressDialog.show();
        //***********PREPARATION HEADER WSSE******************
        String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
        final WsseToken token  = new WsseToken(user,passEncript);
        RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
        Retrofit retrofit=conncecting.buildRequest();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getOneActCompl(acId).enqueue(new Callback<ActCompl>() {
            @Override
            public void onResponse(Call<ActCompl> call, Response<ActCompl> response) {
                //END LOADER
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.i("DOWNLOAD :: ","OK");
                ac = response.body();
                setTextViewHandler(ac); // Appel méthode pour affichage
            }

            @Override
            public void onFailure(Call<ActCompl> call, Throwable t) {
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
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterACActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        WsseToken tokenB = new WsseToken(user, passEncript);
        RetrofitConnect conncectingB = new RetrofitConnect(user.getUsername(), tokenB);
        Retrofit retrofitB = conncectingB.buildRequest();
        final AdressBookApi serviceB = retrofitB.create(AdressBookApi.class);
        bt_actionValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.setAcStates("VALIDER");
                ArrayList<ActRea> actReals=ac.getAcActReal();
                ArrayList<ActReaPost> actReaPosts=new ArrayList<ActReaPost>();
                ArrayList<Praticien> actPrats=ac.getAcPraticien();
                ArrayList<Integer> actPraPost=new ArrayList<Integer>();
                for (Praticien praticien:actPrats){
                    actPraPost.add(praticien.getId());
                }
                for (ActRea actRea:actReals){
                    ActReaPost actReaPost = new ActReaPost();
                    actReaPost.setActReaBudget(actRea.getActReaBudget());
                    actReaPost.setActReaVisiteur(actRea.getActReaVisiteur().getId());
                    actReaPost.setId(actRea.getId());
                    actReaPosts.add(actReaPost);
                }
                ActComplPost actComplPost = new ActComplPost();
                actComplPost.setAcPraticien(actPraPost);
                actComplPost.setAcActReal(actReaPosts);
                actComplPost.setAcLieu(ac.getAcLieu());
                actComplPost.setAcTheme(ac.getAcTheme());
                actComplPost.setAcDate(ac.getAcDate());
                actComplPost.setAcStates(ac.getAcStates());
                actComplPost.setId(ac.getId());
                serviceB.modifActCompl(actComplPost).enqueue(new Callback<ActComplPost>() {
                    @Override
                    public void onResponse(Call<ActComplPost> call, Response<ActComplPost> response) {
                        Log.i("RESPONSE MODIF :", "OKOK" + response.body() + response.code());
                        Log.i("RESPONSE CODE :", " CODE ::  " + response.code());
                        // SI LA RESSOURCE EST CREER : CODE 201  ET RENVOIE PAGE CR
                        if (response.code() == 200) {
                            Toast.makeText(ConsulterAcOneActivity.this, "Activité complémentaire Valider !", Toast.LENGTH_SHORT).show();
                            Intent intentVisit = new Intent(getApplicationContext(), ConsulterACActivity.class);
                            intentVisit.putExtra("userId", user.getId());
                            intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                            intentVisit.putExtra("templateKey", templateKey);
                            startActivity(intentVisit);
                        } else {
                            // SINON AFFICAGE MSG ERREUR ET LOG CODE
                            Toast.makeText(ConsulterAcOneActivity.this, "Un probléme est survenur lors de la validation...", Toast.LENGTH_SHORT).show();
                            Log.w("MODIF AC :", " : ECHEC" + response.body() + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ActComplPost> call, Throwable t) {
                        Log.e("RESPONSE CREATE :", "ERROR" + t);
                    }
                });
            }
        });
        bt_actionRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.setAcStates("REFUSER");
                ArrayList<ActRea> actReals=ac.getAcActReal();
                ArrayList<ActReaPost> actReaPosts=new ArrayList<ActReaPost>();
                ArrayList<Praticien> actPrats=ac.getAcPraticien();
                ArrayList<Integer> actPraPost=new ArrayList<Integer>();
                for (Praticien praticien:actPrats){
                    actPraPost.add(praticien.getId());
                }
                for (ActRea actRea:actReals){
                    ActReaPost actReaPost = new ActReaPost();
                    actReaPost.setActReaBudget(actRea.getActReaBudget());
                    actReaPost.setActReaVisiteur(actRea.getActReaVisiteur().getId());
                    actReaPost.setId(actRea.getId());
                    actReaPosts.add(actReaPost);
                }
                ActComplPost actComplPost = new ActComplPost();
                actComplPost.setAcPraticien(actPraPost);
                actComplPost.setAcActReal(actReaPosts);
                actComplPost.setAcLieu(ac.getAcLieu());
                actComplPost.setAcTheme(ac.getAcTheme());
                actComplPost.setAcDate(ac.getAcDate());
                actComplPost.setAcStates(ac.getAcStates());
                serviceB.modifActCompl(actComplPost).enqueue(new Callback<ActComplPost>() {
                    @Override
                    public void onResponse(Call<ActComplPost> call, Response<ActComplPost> response) {
                        Log.i("RESPONSE MODIF :", "OKOK" + response.body() + response.code());
                        Log.i("RESPONSE CODE :", " CODE ::  " + response.code());
                        // SI LA RESSOURCE EST CREER : CODE 201  ET RENVOIE PAGE CR
                        if (response.code() == 200) {
                            Toast.makeText(ConsulterAcOneActivity.this, "Activité complémentaire refuser !", Toast.LENGTH_SHORT).show();
                            Intent intentVisit = new Intent(getApplicationContext(), ConsulterACActivity.class);
                            intentVisit.putExtra("userId", user.getId());
                            intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                            intentVisit.putExtra("templateKey", templateKey);
                            startActivity(intentVisit);
                        } else {
                            // SINON AFFICAGE MSG ERREUR ET LOG CODE
                            Toast.makeText(ConsulterAcOneActivity.this, "Un probléme est survenur lors de la validation...", Toast.LENGTH_SHORT).show();
                            Log.w("MODIF AC :", " : ECHEC" + response.body() + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ActComplPost> call, Throwable t) {
                        Log.e("RESPONSE CREATE :", "ERROR" + t);
                    }
                });
            }
        });
    }
    // Affichage des informations récupérer
    // Affichage des informations récupérer
    public void setTextViewHandler(ActCompl ac){
        final TextView ui_acId=(TextView)findViewById(R.id.actcompl_id_view);
        final TextView ui_acDate=(TextView)findViewById(R.id.actcompl_date_view);
        final TextView ui_acLieu=(TextView)findViewById(R.id.actcompl_lieu_view);
        final TextView ui_acPra=(TextView)findViewById(R.id.actcompl_praticien_view);
        final TextView ui_acReal=(TextView)findViewById(R.id.actcompl_realisation_view);
        final TextView ui_acTheme=(TextView)findViewById(R.id.actcompl_theme_view);
        final TextView ui_acStates=(TextView)findViewById(R.id.actcompl_states_view);
        if (ac!=null){
            String realisateurs = "";
            ArrayList<ActRea> listActRea= ac.getAcActReal();
            for (ActRea actRea:listActRea){
                realisateurs=realisateurs+" "+actRea.getActReaVisiteur().getUsrNom()+" ;";
            }
            String praticiens = "";
            ArrayList<Praticien> listAcPra= ac.getAcPraticien();
            for (Praticien praticien:listAcPra){
                praticiens=praticiens+" "+praticien.getPra_nom()+" ;";
            }
            String ac_date=ac.getAcDate();
            ac_date=ac_date.substring(0,10);
            ui_acId.setText(Integer.toString(ac.getId()));
            ui_acLieu.setText(ac.getAcLieu());
            ui_acDate.setText(ac_date);
            ui_acTheme.setText(ac.getAcTheme());
            ui_acStates.setText(ac.getAcStates());
            ui_acPra.setText(praticiens);
            ui_acReal.setText(realisateurs);

        }
        else {
            Log.i("recup rapport","NON");

        }
    }
}

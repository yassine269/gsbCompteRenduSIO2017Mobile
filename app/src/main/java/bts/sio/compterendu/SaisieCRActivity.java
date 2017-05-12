package bts.sio.compterendu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.collect.Range;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.Medicament;
import bts.sio.compterendu.model.Motif;
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.model.RapportEchant;
import bts.sio.compterendu.model.RapportEchantPost;
import bts.sio.compterendu.model.RapportVisite;
import bts.sio.compterendu.model.RapportVisitePost;
import bts.sio.compterendu.model.Visiteur;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static java.util.Locale.FRANCE;

public class SaisieCRActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    // INIT VARIABLE
    private CRReaderDbHelper mDbHelper;
    private ProgressDialog mProgressDialog;
    private List<Medicament> _medList;
    private String templateKey;
    private ArrayAdapter<Praticien> adapterPra;
    private ArrayAdapter<Motif> adapterMotif;

    public int Doit(){
        return 0;
    }
    // FRAGMENT CLASS FOR DATEPICKER
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog,this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            EditText dob= (EditText) getActivity(). findViewById(R.id.cr_date_saisie);
            month=month+1;
            dob.setText(year+"-"+month+"-"+day);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisiecr);
        // GET ACCOUNT AND VERIFY TIMING
        final Intent intent = getIntent();
        final int userId=intent.getIntExtra("userId",0);
        final long timeMillis=intent.getLongExtra("limitConnect",0);
        templateKey = intent.getStringExtra("templateKey");
        final Calendar limitConnect= Calendar.getInstance();
        limitConnect.setTimeInMillis(timeMillis);
        mDbHelper=new CRReaderDbHelper(getApplicationContext());
        //INIT BDD READABLE
        final Account user = mDbHelper.getUser(getApplicationContext());
        if (!user.checkConnection(limitConnect)){
            Intent intentLogin=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intentLogin);
        }else {
            Date limitConnectReplace = new Date(); // Instantiate a Date object
            limitConnect.setTime(limitConnectReplace);
            limitConnect.add(Calendar.MINUTE, 5);
        }
        // INIT VALIDATE
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        // AFFECT RULES
        mAwesomeValidation.addValidation(this, R.id.cr_coef_saisie, Range.closed(0,10), R.string.err_coef);
        mAwesomeValidation.addValidation(this, R.id.cr_bilan_saisie, RegexTemplate.NOT_EMPTY, R.string.err_bilan);
        mAwesomeValidation.addValidation(this, R.id.cr_echant_quant_saisie,Range.closed(1,10) , R.string.err_echant_quant);

        EditText btn_date_picker=((EditText) findViewById(R.id.cr_date_saisie));
        btn_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // RECUPERATION DE LA LISTE DE MEDICAMENT
        final ArrayList<Medicament> data;
        data = new ArrayList<Medicament>();
        String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
        WsseToken token  = new WsseToken(user,passEncript);
        RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
        Retrofit retrofit=conncecting.buildRequest();
        AdressBookApi service = retrofit.create(AdressBookApi.class);
        service.getMedicamentList().enqueue(new Callback<List<Medicament>>() {
            @Override
            public void onResponse(Call<List<Medicament>> call, Response<List<Medicament>> response) {
                //END LOADER

                if (response.body()!=null){
                    for (Medicament medicament:response.body()){
                        data.add(medicament);
                    }
                    Log.i("REPONSE MEDICAMENT : ","OK");

                }
                else {
                    Log.w("REPONSE MEDICAMENT : ","NULL"+response.code());
                }

                Spinner spinnerMed = (Spinner) findViewById(R.id.spinner_med);
// Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<Medicament> adapterMed = new ArrayAdapter<Medicament>(SaisieCRActivity.this,android.R.layout.simple_spinner_item,data);
// Specify the layout to use when the list of choices appears
                adapterMed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerMed.setAdapter(adapterMed);
                spinnerMed.setOnItemSelectedListener(SaisieCRActivity.this);
            }

            @Override
            public void onFailure(Call<List<Medicament>> call, Throwable t) {
                //END LOADER
                Log.i("Download ::","ERROR");
                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
            }
        });

        // RECUPERATION DE LA LISTE DE PRATICIEN
        final ArrayList<Praticien> dataPra;
        dataPra = new ArrayList<Praticien>();
        WsseToken tokenB  = new WsseToken(user,passEncript);
        RetrofitConnect conncectingB = new RetrofitConnect(user.getUsername(),tokenB);
        Retrofit retrofitB=conncectingB.buildRequest();
        service = retrofitB.create(AdressBookApi.class);
        service.getPraticienList().enqueue(new Callback<List<Praticien>>() {
            @Override
            public void onResponse(Call<List<Praticien>> call, Response<List<Praticien>> response) {
                //END LOADER
                Log.i("Download ::","OK");
                if (response.body()!=null){
                    for (Praticien praticien:response.body()){
                        dataPra.add(praticien);
                    }
                    Log.i("REPONSE PRATICIEN : ","OK");

                }
                else {
                    Log.w("REPONSE PRATICIEN : ","NULL"+response.code());
                }
                Spinner spinnerPra = (Spinner) findViewById(R.id.spinner_pra);
// Create an ArrayAdapter using the string array and a default spinner layout
                adapterPra = new ArrayAdapter<Praticien>(SaisieCRActivity.this,android.R.layout.simple_spinner_item,dataPra);
// Specify the layout to use when the list of choices appears
                adapterPra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerPra.setAdapter(adapterPra);
                spinnerPra.setOnItemSelectedListener(SaisieCRActivity.this);
            }

            @Override
            public void onFailure(Call<List<Praticien>> call, Throwable t) {
                //END LOADER
                Log.i("Download ::","ERROR");
                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
            }
        });

        // RECUPERATION DE LA LISTE DE MOTIFS
        final ArrayList<Motif> dataMotif;
        dataMotif = new ArrayList<Motif>();
        WsseToken tokenC  = new WsseToken(user,passEncript);
        RetrofitConnect conncectingC = new RetrofitConnect(user.getUsername(),tokenC);
        Retrofit retrofitC=conncectingC.buildRequest();
        service = retrofitC.create(AdressBookApi.class);
        service.getMotifList().enqueue(new Callback<List<Motif>>() {
            @Override
            public void onResponse(Call<List<Motif>> call, Response<List<Motif>> response) {
                //END LOADER
                Log.i("Download ::","OK");
                if (response.body()!=null){
                    for (Motif motif:response.body()){
                        dataMotif.add(motif);
                    }
                }
                else {
                    Log.w("REPONSE MOTIF : ","NULL"+response.code());
                }

                Spinner spinnerMotif = (Spinner) findViewById(R.id.spinner_motif);
// Create an ArrayAdapter using the string array and a default spinner layout
                adapterMotif = new ArrayAdapter<Motif>(SaisieCRActivity.this,android.R.layout.simple_spinner_item,dataMotif);
// Specify the layout to use when the list of choices appears
                adapterMotif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerMotif.setAdapter(adapterMotif);
                spinnerMotif.setOnItemSelectedListener(SaisieCRActivity.this);
            }

            @Override
            public void onFailure(Call<List<Motif>> call, Throwable t) {
                //END LOADER
                Log.i("Download ::","ERROR");
                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
            }
        });

        // INITIALISATION DE LA LA LISTE D'ECHANTILLONS CHOISIS ET DE LA LISTE D'ECHANTILLONS ENVOYER EN JSON ( FORMAT DONNE DIFERENT VOIR MODEL )
        final ListView listEchantView = (ListView) findViewById(R.id.list_cr_echant);
        final ArrayList<RapportEchantPost> rapportEchantsPost =new ArrayList<RapportEchantPost>();
        final ArrayList<RapportEchant> rapportEchants =new ArrayList<RapportEchant>();
        final ArrayAdapter<RapportEchant> adapter = new ArrayAdapter<RapportEchant>(this,android.R.layout.simple_list_item_checked,rapportEchants);
        listEchantView.setAdapter(adapter);
        // BOUTONS D'AJOUT D'ECHANTILLONS
        // COMPORTEMENT :
        // AJOUT DE L'ECHANILONS DANS LA LISTVIEW
        // AJOUT DE L'ECHANTILLONS DANS LA LISTE D'ECHANTILLONPOST POUR ENVOI JSON
        Button btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerMed = (Spinner) findViewById(R.id.spinner_med);
                TextView txt_qt = (TextView) findViewById(R.id.cr_echant_quant_saisie);
                RapportEchant rapportEchant = new RapportEchant();
                rapportEchant.setRap_echant_medicament((Medicament) spinnerMed.getSelectedItem());
                rapportEchant.setRap_echant_quantite(Integer.parseInt(txt_qt.getText().toString()));
                RapportEchantPost rapportEchantPost=new RapportEchantPost();
                rapportEchantPost.setRap_echant_medicament(rapportEchant.getRap_echant_medicament().getId());
                rapportEchantPost.setRap_echant_quantite(rapportEchant.getRap_echant_quantite());
                rapportEchantsPost.add(rapportEchantPost);
                rapportEchants.add(rapportEchant);
                adapter.notifyDataSetChanged();
            }
        });
        //VALIDATION DE LA SAISIE
        Button btn_valid = (Button) findViewById(R.id.valid_saisie_cr);
        btn_valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FORMATAGE DE L'OBJET RAPPORTVISITPOST POUR ENVOIE JSON
                if (mAwesomeValidation.validate()){
                    Spinner spinnerPra = (Spinner) findViewById(R.id.spinner_pra);
                    Praticien pra =(Praticien) spinnerPra.getSelectedItem();
                    TextView ui_cr_bilan_saisie = (TextView) findViewById(R.id.cr_bilan_saisie);
                    TextView ui_cr_date_saisie = (TextView) findViewById(R.id.cr_date_saisie);
                    TextView ui_cr_coef_impact_saisie = (TextView) findViewById(R.id.cr_coef_saisie);
                    Spinner spinnerMotif = (Spinner) findViewById(R.id.spinner_motif);
                    Motif motif =(Motif) spinnerMotif.getSelectedItem();
                    String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
                    WsseToken token  = new WsseToken(user,passEncript);
                    RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
                    Retrofit retrofit=conncecting.buildRequest();
                    AdressBookApi service = retrofit.create(AdressBookApi.class);
                    RapportVisitePost rapportVisite = new RapportVisitePost();
                    rapportVisite.setRapEchantillons(rapportEchantsPost);
                    rapportVisite.setRapVisiteur(userId);
                    rapportVisite.setRapMotif(motif.getId());
                    rapportVisite.setRapPraticien(pra.getId());
                    rapportVisite.setRapBilan(ui_cr_bilan_saisie.getText().toString());
                    rapportVisite.setRapCoefImpact(Integer.parseInt(ui_cr_coef_impact_saisie.getText().toString()));
                    rapportVisite.setRapDate(ui_cr_date_saisie.getText().toString());
                    if (templateKey.equals("view")){
                        service.saisieCR(rapportVisite).enqueue(new Callback<RapportVisitePost>() {
                            @Override
                            public void onResponse(Call<RapportVisitePost> call, Response<RapportVisitePost> response) {
                                Log.i("RESPONSE CREATE :","OKOK"+response.body()+response.code());
                                Log.i("RESPONSE CODE :"," CODE ::  "+response.code());
                                // SI LA RESSOURCE EST CREER : CODE 201  ET RENVOIE PAGE CR
                                if (response.code()==201){
                                    Toast.makeText(SaisieCRActivity.this,"Compte rendu créer !",Toast.LENGTH_SHORT).show();
                                    Intent intentVisit = new Intent(getApplicationContext(),CrActivity.class);
                                    intentVisit.putExtra("userId",user.getId());
                                    intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                                    startActivity(intentVisit);
                                }
                                else{
                                    // SINON AFFICAGE MSG ERREUR ET LOG CODE
                                    Toast.makeText(SaisieCRActivity.this,"Un probléme est survenur lors de la création...",Toast.LENGTH_SHORT).show();
                                    Log.w("CREATION CR :"," : ECHEC"+response.body()+response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<RapportVisitePost> call, Throwable t) {
                                Log.e("RESPONSE CREATE :","ERROR"+t);
                            }
                        });
                    }
                    else if (templateKey.equals("edit")){
                        final int crId = intent.getIntExtra("crId", 0);
                        rapportVisite.setId(crId);
                        service.modifCR(rapportVisite).enqueue(new Callback<RapportVisitePost>() {
                            @Override
                            public void onResponse(Call<RapportVisitePost> call, Response<RapportVisitePost> response) {
                                Log.i("RESPONSE MODIF :","OKOK"+response.body()+response.code());
                                Log.i("RESPONSE CODE :"," CODE ::  "+response.code());
                                // SI LA RESSOURCE EST CREER : CODE 201  ET RENVOIE PAGE CR
                                if (response.code()==200){
                                    Toast.makeText(SaisieCRActivity.this,"Compte rendu modifier !",Toast.LENGTH_SHORT).show();
                                    Intent intentVisit = new Intent(getApplicationContext(),CrActivity.class);
                                    intentVisit.putExtra("userId",user.getId());
                                    intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                                    startActivity(intentVisit);
                                }
                                else{
                                    // SINON AFFICAGE MSG ERREUR ET LOG CODE
                                    Toast.makeText(SaisieCRActivity.this,"Un probléme est survenur lors de la création...",Toast.LENGTH_SHORT).show();
                                    Log.w("MODIF CR :"," : ECHEC"+response.body()+response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<RapportVisitePost> call, Throwable t) {
                                Log.e("RESPONSE CREATE :","ERROR"+t);
                            }
                        });
                    }
                }
            }
        });
        if(templateKey!=null) {
            if (templateKey.equals("edit")) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Récupération du compte rendu...");
                mProgressDialog.show();
                final int crId = intent.getIntExtra("crId", 0);
                passEncript = user.hashPassword(user.getSalt(), user.getClearPass());
                final WsseToken token0 = new WsseToken(user, passEncript);
                RetrofitConnect conncecting0 = new RetrofitConnect(user.getUsername(), token0);
                Retrofit retrofit0 = conncecting0.buildRequest();
                AdressBookApi service0 = retrofit0.create(AdressBookApi.class);
                service0.getOneCr(crId).enqueue(new Callback<RapportVisite>() {
                    @Override
                    public void onResponse(Call<RapportVisite> call, Response<RapportVisite> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("DOWNLOAD :: ", "OK");
                        RapportVisite cr = response.body();
                        setTextViewHandler(cr);// Appel méthode pour affichage
                        for (RapportEchant rapportEchant:cr.getRapEchantillons()){
                            rapportEchants.add(rapportEchant);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<RapportVisite> call, Throwable t) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("DOWNLOAD :: ", "FAIL");
                        Toast.makeText(getApplicationContext(), "Erreur réseaux, veuillez réessayez", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    // LISTENNER FOR SPINNER'S
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner:
                Log.i("PASSAGE","OK");
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()){
            case R.id.spinner:
                Log.i("PASSAGE","NOTHIGCHOICE");
                break;
        }

    }

    public void setTextViewHandler(RapportVisite cr){
        final Spinner ui_crPraticien=(Spinner) findViewById(R.id.spinner_pra);
        final TextView ui_crDate=(TextView)findViewById(R.id.cr_date_saisie);
        final Spinner ui_crMotif=(Spinner) findViewById(R.id.spinner_motif);
        final TextView ui_crBilan=(TextView)findViewById(R.id.cr_bilan_saisie);
        final TextView ui_crCoefImpact=(TextView)findViewById(R.id.cr_coef_saisie);
        if (cr!=null){
            String cr_date=cr.getRapDate();
            cr_date=cr_date.substring(0,10);
            ui_crPraticien.setSelection(adapterPra.getPosition(cr.getRapPraticien()));
            ui_crDate.setText(cr_date);
            ui_crBilan.setText(cr.getRapBilan());
            ui_crMotif.setSelection(adapterMotif.getPosition(cr.getRapMotif()));
            ui_crCoefImpact.setText(Integer.toString(cr.getRapCoefImpact()));
        }
        else {
            Log.i("recup rapport","NON");

        }
    }

}

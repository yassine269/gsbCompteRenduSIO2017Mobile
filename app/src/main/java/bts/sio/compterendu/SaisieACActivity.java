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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.ActCompl;
import bts.sio.compterendu.model.ActComplPost;
import bts.sio.compterendu.model.ActRea;
import bts.sio.compterendu.model.ActReaPost;
import bts.sio.compterendu.model.Medicament;
import bts.sio.compterendu.model.Motif;
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.model.RapportEchant;
import bts.sio.compterendu.model.RapportEchantPost;
import bts.sio.compterendu.model.RapportVisite;
import bts.sio.compterendu.model.RapportVisitePost;
import bts.sio.compterendu.model.User;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SaisieACActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    // INIT VARIABLE
    private CRReaderDbHelper mDbHelper;
    private ProgressDialog mProgressDialog;
    private String templateKey;
    private ArrayAdapter<Praticien> adapterPra;
    private ArrayAdapter<Motif> adapterMotif;
    private ArrayAdapter<User> adapterCollab;
    final ArrayList<Praticien> dataPra=new ArrayList<Praticien>();
    final ArrayList<Integer> choicePraPost=new ArrayList<Integer>();
    final ArrayList<Praticien> choicePra=new ArrayList<Praticien>();


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
            EditText dob= (EditText) getActivity(). findViewById(R.id.ac_date_saisie);
            month=month+1;
            dob.setText(year+"-"+month+"-"+day);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisieactcompl);
        // GET ACCOUNT AND VERIFY TIMING
        final Intent intent = getIntent();
        final int userId = intent.getIntExtra("userId", 0);
        final long timeMillis = intent.getLongExtra("limitConnect", 0);
        templateKey = intent.getStringExtra("templateKey");
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
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        // AFFECT RULES
        mAwesomeValidation.addValidation(this, R.id.ac_lieu_saisie, RegexTemplate.NOT_EMPTY, R.string.err_lieu);

        EditText btn_date_picker = ((EditText) findViewById(R.id.ac_date_saisie));
        btn_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        String passEncript = user.hashPassword(user.getSalt(), user.getClearPass());

        // RECUPERATION DE LA LISTE DE PRATICIEN
        final ListView listPraView = (ListView) findViewById(R.id.list_ac_praticien);
        final ArrayAdapter<Praticien> adapterChoicePra = new ArrayAdapter<Praticien>(this, android.R.layout.simple_list_item_checked, choicePra);
        listPraView.setAdapter(adapterChoicePra);
        WsseToken tokenB = new WsseToken(user, passEncript);
        RetrofitConnect conncectingB = new RetrofitConnect(user.getUsername(), tokenB);
        Retrofit retrofitB = conncectingB.buildRequest();
        AdressBookApi service = retrofitB.create(AdressBookApi.class);
        service.getPraticienList().enqueue(new Callback<List<Praticien>>() {
            @Override
            public void onResponse(Call<List<Praticien>> call, Response<List<Praticien>> response) {
                //END LOADER
                Log.i("Download ::", "OK");
                if (response.body() != null) {
                    for (Praticien praticien : response.body()) {
                        dataPra.add(praticien);
                    }
                    Log.i("REPONSE PRATICIEN : ", "OK");

                } else {
                    Log.w("REPONSE PRATICIEN : ", "NULL" + response.code());
                }
                Spinner spinnerPra = (Spinner) findViewById(R.id.spinner_pra);
// Create an ArrayAdapter using the string array and a default spinner layout
                adapterPra = new ArrayAdapter<Praticien>(SaisieACActivity.this, android.R.layout.simple_spinner_item, dataPra);
// Specify the layout to use when the list of choices appears
                adapterPra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerPra.setAdapter(adapterPra);
                spinnerPra.setOnItemSelectedListener(SaisieACActivity.this);
            }

            @Override
            public void onFailure(Call<List<Praticien>> call, Throwable t) {
                //END LOADER
                Log.i("Download ::", "ERROR");
                Toast.makeText(getApplicationContext(), "Erreur réseaux, veuillez réessayez  " + t, Toast.LENGTH_SHORT).show();
            }
        });
        // RECUPERATION LIST USER
        final ArrayList<User> dataCollab;
        dataCollab = new ArrayList<User>();
        WsseToken tokenCollab = new WsseToken(user, passEncript);
        RetrofitConnect conncectingCollab = new RetrofitConnect(user.getUsername(), tokenCollab);
        Retrofit retrofitCollab = conncectingCollab.buildRequest();
        service = retrofitCollab.create(AdressBookApi.class);
        service.getCollabList().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                //END LOADER
                Log.i("Download ::", "OK");
                if (response.body() != null) {
                    for (User collab : response.body()) {
                        dataCollab.add(collab);
                    }
                    Log.i("REPONSE USER : ", "OK");

                } else {
                    Log.w("REPONSE USER : ", "NULL" + response.code());
                }
                Spinner spinnerCollab = (Spinner) findViewById(R.id.spinner_user);
// Create an ArrayAdapter using the string array and a default spinner layout
                adapterCollab = new ArrayAdapter<User>(SaisieACActivity.this, android.R.layout.simple_spinner_item, dataCollab);
// Specify the layout to use when the list of choices appears
                adapterCollab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerCollab.setAdapter(adapterCollab);
                spinnerCollab.setOnItemSelectedListener(SaisieACActivity.this);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                //END LOADER
                Log.i("Download ::", "ERROR");
                Toast.makeText(getApplicationContext(), "Erreur réseaux, veuillez réessayez  " + t, Toast.LENGTH_SHORT).show();
            }
        });

        // INITIALISATION DE LA LA LISTE DE REALISATION CHOISIS ET DE LA LISTE DE REALISATIONS ENVOYER EN JSON ( FORMAT DONNE DIFERENT VOIR MODEL )
        final ListView listRealView = (ListView) findViewById(R.id.list_ac_realisation);
        final ArrayList<ActReaPost> actReaPosts = new ArrayList<ActReaPost>();
        final ArrayList<ActRea> actReas = new ArrayList<ActRea>();
        final ArrayAdapter<ActRea> adapter = new ArrayAdapter<ActRea>(this, android.R.layout.simple_list_item_checked, actReas);
        listRealView.setAdapter(adapter);
        // BOUTONS D'AJOUT DE REALISATION
        // COMPORTEMENT :
        // AJOUT DE LA REALISATION DANS LA LISTVIEW
        // AJOUT DE LA REALISATION DANS LA LISTE D'ACTREALPOST POUR ENVOI JSON
        Button btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerUser = (Spinner) findViewById(R.id.spinner_user);
                TextView txt_budget = (TextView) findViewById(R.id.ac_realisation_budget_saisie);
                ActRea actRea = new ActRea();
                actRea.setActReaVisiteur((User) spinnerUser.getSelectedItem());
                actRea.setActReaBudget(Integer.parseInt(txt_budget.getText().toString()));
                ActReaPost actReaPost = new ActReaPost();
                actReaPost.setActReaVisiteur(actRea.getActReaVisiteur().getId());
                actReaPost.setActReaBudget(actRea.getActReaBudget());
                actReaPosts.add(actReaPost);
                actReas.add(actRea);
                adapter.notifyDataSetChanged();
            }
        });
        Button bt_add_pra = (Button) findViewById(R.id.btn_addPra);
        bt_add_pra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerPra = (Spinner) findViewById(R.id.spinner_pra);
                choicePra.add( (Praticien) spinnerPra.getSelectedItem());
                choicePraPost.add( ((Praticien) spinnerPra.getSelectedItem()).getId());
                adapterChoicePra.notifyDataSetChanged();
            }
        });
        Button btn_valid = (Button) findViewById(R.id.valid_saisie_ac);
        btn_valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FORMATAGE DE L'OBJET RAPPORTVISITPOST POUR ENVOIE JSON
                if (mAwesomeValidation.validate()) {
                    TextView ui_ac_theme_saisie = (TextView) findViewById(R.id.ac_theme_saisie);
                    TextView ui_ac_date_saisie = (TextView) findViewById(R.id.ac_date_saisie);
                    TextView ui_ac_lieu_saisie = (TextView) findViewById(R.id.ac_lieu_saisie);
                    String passEncript = user.hashPassword(user.getSalt(), user.getClearPass());
                    WsseToken token = new WsseToken(user, passEncript);
                    RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(), token);
                    Retrofit retrofit = conncecting.buildRequest();
                    AdressBookApi service = retrofit.create(AdressBookApi.class);
                    ActComplPost actComplPost = new ActComplPost();
                    actComplPost.setAcPraticien(choicePraPost);
                    actComplPost.setAcActReal(actReaPosts);
                    actComplPost.setAcLieu(ui_ac_lieu_saisie.getText().toString());
                    actComplPost.setAcTheme(ui_ac_theme_saisie.getText().toString());
                    actComplPost.setAcDate(ui_ac_date_saisie.getText().toString());
                    if (templateKey.equals("view")) {
                        service.saisieActCompl(actComplPost).enqueue(new Callback<ActComplPost>() {
                            @Override
                            public void onResponse(Call<ActComplPost> call, Response<ActComplPost> response) {
                                Log.i("RESPONSE CREATE :", "OKOK" + response.body() + response.code());
                                Log.i("RESPONSE CODE :", " CODE ::  " + response.code());
                                // SI LA RESSOURCE EST CREER : CODE 201  ET RENVOIE PAGE CR
                                if (response.code() == 201) {
                                    Toast.makeText(SaisieACActivity.this, "Activité complémentaire créer !", Toast.LENGTH_SHORT).show();
                                    Intent intentVisit = new Intent(getApplicationContext(), AcActivity.class);
                                    intentVisit.putExtra("userId", user.getId());
                                    intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                                    startActivity(intentVisit);
                                } else {
                                    // SINON AFFICAGE MSG ERREUR ET LOG CODE
                                    Toast.makeText(SaisieACActivity.this, "Un probléme est survenur lors de la création...", Toast.LENGTH_SHORT).show();
                                    Log.w("CREATION AC :", " : ECHEC" + response.body() + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ActComplPost> call, Throwable t) {
                                Log.e("RESPONSE CREATE :", "ERROR" + t);
                            }
                        });
                    } else if (templateKey.equals("edit")) {
                        final int acId = intent.getIntExtra("acId", 0);
                        actComplPost.setId(acId);
                        service.modifActCompl(actComplPost).enqueue(new Callback<ActComplPost>() {
                            @Override
                            public void onResponse(Call<ActComplPost> call, Response<ActComplPost> response) {
                                Log.i("RESPONSE MODIF :", "OKOK" + response.body() + response.code());
                                Log.i("RESPONSE CODE :", " CODE ::  " + response.code());
                                // SI LA RESSOURCE EST CREER : CODE 201  ET RENVOIE PAGE CR
                                if (response.code() == 201) {
                                    Toast.makeText(SaisieACActivity.this, "Activité complémentaire modifier !", Toast.LENGTH_SHORT).show();
                                    Intent intentVisit = new Intent(getApplicationContext(), AcActivity.class);
                                    intentVisit.putExtra("userId", user.getId());
                                    intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                                    startActivity(intentVisit);
                                } else {
                                    // SINON AFFICAGE MSG ERREUR ET LOG CODE
                                    Toast.makeText(SaisieACActivity.this, "Un probléme est survenur lors de la modification...", Toast.LENGTH_SHORT).show();
                                    Log.w("MODIF AC :", " : ECHEC" + response.body() + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ActComplPost> call, Throwable t) {
                                Log.e("RESPONSE CREATE :", "ERROR" + t);
                            }
                        });
                    }
                }
            }
        });

        if (templateKey != null) {
            if (templateKey.equals("edit")) {
                final int acId = intent.getIntExtra("acId", 0);
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Récupération de l'activité complémentaire...");
                mProgressDialog.show();
                passEncript = user.hashPassword(user.getSalt(), user.getClearPass());
                final WsseToken token0 = new WsseToken(user, passEncript);
                RetrofitConnect conncecting0 = new RetrofitConnect(user.getUsername(), token0);
                Retrofit retrofit0 = conncecting0.buildRequest();
                AdressBookApi service0 = retrofit0.create(AdressBookApi.class);
                service0.getOneActCompl(acId).enqueue(new Callback<ActCompl>() {
                    @Override
                    public void onResponse(Call<ActCompl> call, Response<ActCompl> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("DOWNLOAD :: ", "OK");
                        ActCompl ac = response.body();
                        setTextViewHandler(ac);// Appel méthode pour affichage
                        for (ActRea actRea : ac.getAcActReal()) {
                            actReas.add(actRea);
                            adapter.notifyDataSetChanged();
                        }
                        for (Praticien praticien : ac.getAcPraticien()) {
                            choicePra.add(praticien);
                            adapterPra.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ActCompl> call, Throwable t) {
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
            case R.id.list_ac_praticien:
                adapterPra.add((Praticien) parent.getSelectedItem());
                adapterPra.notifyDataSetChanged();
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

    public void setTextViewHandler(ActCompl ac){
        final TextView ui_ac_theme_saisie = (TextView) findViewById(R.id.ac_theme_saisie);
        final TextView ui_ac_date_saisie = (TextView) findViewById(R.id.ac_date_saisie);
        final TextView ui_ac_lieu_saisie = (TextView) findViewById(R.id.ac_lieu_saisie);
        if (ac!=null){
            String ac_date=ac.getAcDate();
            ac_date=ac_date.substring(0,10);
            ui_ac_date_saisie.setText(ac_date);
            ui_ac_lieu_saisie.setText(ac.getAcLieu());
            ui_ac_theme_saisie.setText(ac.getAcTheme());
        }
        else {
            Log.i("recup AC","NON");

        }
    }

}

package bts.sio.compterendu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.RapportVisite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConsulterCRActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;
    private RecyclerView ui_CrListRecyclerView;
    private Calendar limitConnect;
    private ProgressDialog mProgressDialog;
    private String templateKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_rapport_activity);
        Intent intent = getIntent();
        int userId=intent.getIntExtra("userId",0);
        long timeMillis=intent.getLongExtra("limitConnect",0);
        templateKey = intent.getStringExtra("templateKey");
        if (templateKey==null){
            templateKey="view";
        }
        limitConnect= Calendar.getInstance();
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
        int redacteur = user.getId();

        Button bt_retour = (Button) findViewById(R.id.btn_retour);
        bt_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),CrActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_action = (Button) findViewById(R.id.btn_action);
        if (user.getFonction().equals("Delegue") || user.getFonction().equals("Responsable")){
            bt_action.setVisibility(View.GONE);
        }
        if (templateKey.equals("view")){
            ((TextView) findViewById(R.id.ConsultationCR)).setText("Consultation de compte rendus");
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentVisit = new Intent(getApplicationContext(),ConsulterCRActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                    intentVisit.putExtra("templateKey","edit");
                    startActivity(intentVisit);
                }
            });
        }else if (templateKey.equals("edit")){
            bt_action.setText("Consultation");
            ((TextView) findViewById(R.id.ConsultationCR)).setText("Modification de compte rendus");
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentVisit = new Intent(getApplicationContext(),ConsulterCRActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                    intentVisit.putExtra("templateKey","view");
                    startActivity(intentVisit);
                }
            });
        }
        // INIT LOADER
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Chargement des compte rendus...");
        mProgressDialog.show();
        ui_CrListRecyclerView = (RecyclerView)findViewById(R.id.crList_recycler_view);
        ui_CrListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ui_CrListRecyclerView.setAdapter(new CrListAdapter());

    }

    class CrListAdapter extends RecyclerView.Adapter<CrCardHolder> {
        private List<RapportVisite> _crList;

        public CrListAdapter(){
            //***********PREPARATION HEADER WSSE******************
            final Account user = mDbHelper.getUser(getApplicationContext());
            String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
            final WsseToken token  = new WsseToken(user,passEncript);
            RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
            Retrofit retrofit=conncecting.buildRequest();
            AdressBookApi service = retrofit.create(AdressBookApi.class);
            if (user.getFonction().equals("Visiteur")){
                service.getRapportList(user.getId(),templateKey).enqueue(new Callback<List<RapportVisite>>() {
                    @Override
                    public void onResponse(Call<List<RapportVisite>> call, Response<List<RapportVisite>> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","OK");
                        _crList=response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<RapportVisite>> call, Throwable t) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","ERROR");
                        Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (user.getFonction().equals("Delegue")){
                service.getRapportList(user.getId(),1,0).enqueue(new Callback<List<RapportVisite>>() {
                    @Override
                    public void onResponse(Call<List<RapportVisite>> call, Response<List<RapportVisite>> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","OK");
                        _crList=response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<RapportVisite>> call, Throwable t) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","ERROR");
                        Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (user.getFonction().equals("Responsable")){
                service.getRapportList(user.getId(),0,1).enqueue(new Callback<List<RapportVisite>>() {
                    @Override
                    public void onResponse(Call<List<RapportVisite>> call, Response<List<RapportVisite>> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","OK");
                        _crList=response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<RapportVisite>> call, Throwable t) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","ERROR");
                        Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        @Override
        public CrCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("ON-CREATE-VIEW :","OKOK");
            View cell = LayoutInflater.from(ConsulterCRActivity.this).inflate(R.layout.cr_cell,parent,false);
            CrCardHolder holder = new CrCardHolder(cell);
            return holder;
        }

        @Override
        public void onBindViewHolder(CrCardHolder holder, final int position) {
            final Account user = mDbHelper.getUser(getApplicationContext());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cr_id=_crList.get(position).getId();
                    if (templateKey.equals("view")){
                        Intent intentVisit = new Intent(getApplicationContext(),ConsulterCrOneActivity.class);
                        intentVisit.putExtra("userId",user.getId());
                        intentVisit.putExtra("userConnect",limitConnect);
                        intentVisit.putExtra("templateKey",templateKey);
                        intentVisit.putExtra("cr_id",cr_id);
                        startActivity(intentVisit);
                    }
                    else if (templateKey.equals("edit")){
                        Intent intentVisit = new Intent(getApplicationContext(),SaisieCRActivity.class);
                        intentVisit.putExtra("userId",user.getId());
                        intentVisit.putExtra("userConnect",limitConnect);
                        intentVisit.putExtra("crId",cr_id);
                        intentVisit.putExtra("templateKey",templateKey);
                        startActivity(intentVisit);

                    }
                }
            });
            Log.i("ON-BIND-VIEW :","OKOK");
            holder.layoutForCr(_crList.get(position));
        }

        @Override
        public int getItemCount() {
            int itemCount=0;
            if (_crList!=null){
                itemCount=_crList.size();
            }
            return itemCount;
        }
    }
    class CrCardHolder extends RecyclerView.ViewHolder{
        private final TextView ui_crId;
        private final TextView ui_crVisit;
        private final TextView ui_crPraticien;
        private final TextView ui_crDate;
        public CrCardHolder(View cell) {
            super(cell);
            ui_crId=(TextView) cell.findViewById(R.id.cr_id);
            ui_crVisit=(TextView) cell.findViewById(R.id.cr_visit);
            ui_crPraticien=(TextView) cell.findViewById(R.id.rapPraticien);
            ui_crDate=(TextView) cell.findViewById(R.id.rapDate);
        }
        public void layoutForCr(RapportVisite rapportVisite){
            ui_crId.setText(Integer.toString(rapportVisite.getId()));
            String usr_nom= rapportVisite.getRapVisiteur().getUsername();
            ui_crVisit.setText(usr_nom);
            String cr_date=rapportVisite.getRapDate();
            cr_date=cr_date.substring(0,10);
            ui_crDate.setText(cr_date);
            String pra_nom=rapportVisite.getRapPraticien().getPra_nom();
            ui_crPraticien.setText(pra_nom);

        }
    }
}

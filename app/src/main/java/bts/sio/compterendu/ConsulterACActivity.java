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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.ActCompl;
import bts.sio.compterendu.model.ActRea;
import bts.sio.compterendu.model.RapportVisite;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConsulterACActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;
    private RecyclerView ui_AcListRecyclerView;
    private Calendar limitConnect;
    private ProgressDialog mProgressDialog;
    private String templateKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_actcompl_activity);
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
                Intent intentVisit = new Intent(getApplicationContext(),AcActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_action = (Button) findViewById(R.id.btn_action);
        switch (templateKey) {
            case "view":
                ((TextView) findViewById(R.id.ConsulterActCompl)).setText("Consultation d'activités complémentaire");
                switch (user.getFonction()) {
                    case "Delegue":
                        bt_action.setText("Validation");
                        bt_action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentVisit = new Intent(getApplicationContext(), ConsulterACActivity.class);
                                intentVisit.putExtra("userId", user.getId());
                                intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                                intentVisit.putExtra("templateKey", "validate");
                                startActivity(intentVisit);
                            }
                        });
                        break;
                    case "Responsable":
                        bt_action.setVisibility(View.GONE);
                        break;
                    default:
                        bt_action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentVisit = new Intent(getApplicationContext(), ConsulterACActivity.class);
                                intentVisit.putExtra("userId", user.getId());
                                intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                                intentVisit.putExtra("templateKey", "edit");
                                startActivity(intentVisit);
                            }
                        });
                        break;
                }
                break;
            case "edit":
                bt_action.setText("Consultation");
                ((TextView) findViewById(R.id.ConsulterActCompl)).setText("Modification d'activités complémentaire");
                bt_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentVisit = new Intent(getApplicationContext(), ConsulterACActivity.class);
                        intentVisit.putExtra("userId", user.getId());
                        intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                        intentVisit.putExtra("templateKey", "view");
                        startActivity(intentVisit);
                    }
                });
                break;
            case "validate":
                bt_action.setText("Consultation");
                ((TextView) findViewById(R.id.ConsulterActCompl)).setText("Validation d'activités complémentaire");
                bt_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentVisit = new Intent(getApplicationContext(), ConsulterACActivity.class);
                        intentVisit.putExtra("userId", user.getId());
                        intentVisit.putExtra("userConnect", limitConnect.getTimeInMillis());
                        intentVisit.putExtra("templateKey", "view");
                        startActivity(intentVisit);
                    }
                });
                break;
        }
        // INIT LOADER
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Chargement des activités complémentaire...");
        mProgressDialog.show();
        ui_AcListRecyclerView = (RecyclerView)findViewById(R.id.actcomplList_recycler_view);
        ui_AcListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ui_AcListRecyclerView.setAdapter(new AcListAdapter());

    }

    class AcListAdapter extends RecyclerView.Adapter<AcCardHolder> {
        private List<ActCompl> _acList;

        public AcListAdapter(){
            //***********PREPARATION HEADER WSSE******************
            final Account user = mDbHelper.getUser(getApplicationContext());
            String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
            final WsseToken token  = new WsseToken(user,passEncript);
            RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
            Retrofit retrofit=conncecting.buildRequest();
            AdressBookApi service = retrofit.create(AdressBookApi.class);
            if (user.getFonction().equals("Visiteur")){
                service.getActComplList(templateKey,user.getId()).enqueue(new Callback<List<ActCompl>>() {
                    @Override
                    public void onResponse(Call<List<ActCompl>> call, Response<List<ActCompl>> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","OK");
                        _acList=response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<ActCompl>> call, Throwable t) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","ERROR");
                        Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                    }
                });

            }
            if (user.getFonction().equals("Delegue")){
                service.getActComplList(user.getId(),1,0,templateKey).enqueue(new Callback<List<ActCompl>>() {
                    @Override
                    public void onResponse(Call<List<ActCompl>> call, Response<List<ActCompl>> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","OK DELEGUE");
                        _acList=response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<ActCompl>> call, Throwable t) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","ERROR");
                        Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else if (user.getFonction().equals("Responsable")){
                service.getActComplList(user.getId(),0,1,templateKey).enqueue(new Callback<List<ActCompl>>() {
                    @Override
                    public void onResponse(Call<List<ActCompl>> call, Response<List<ActCompl>> response) {
                        //END LOADER
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.i("Download ::","OK");
                        _acList=response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<ActCompl>> call, Throwable t) {
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
        public AcCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("ON-CREATE-VIEW :","OKOK");
            View cell = LayoutInflater.from(ConsulterACActivity.this).inflate(R.layout.actcompl_cell,parent,false);
            AcCardHolder holder = new AcCardHolder(cell);
            return holder;
        }

        @Override
        public void onBindViewHolder(AcCardHolder holder, final int position) {
            final Account user = mDbHelper.getUser(getApplicationContext());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ac_id=_acList.get(position).getId();
                    if (templateKey.equals("view")){
                        Intent intentVisit = new Intent(getApplicationContext(),ConsulterAcOneActivity.class);
                        intentVisit.putExtra("userId",user.getId());
                        intentVisit.putExtra("userConnect",limitConnect);
                        intentVisit.putExtra("templateKey",templateKey);
                        intentVisit.putExtra("acId",ac_id);
                        startActivity(intentVisit);
                    }
                    else if (templateKey.equals("edit")){
                        Intent intentVisit = new Intent(getApplicationContext(),SaisieACActivity.class);
                        intentVisit.putExtra("userId",user.getId());
                        intentVisit.putExtra("userConnect",limitConnect);
                        intentVisit.putExtra("acId",ac_id);
                        intentVisit.putExtra("templateKey",templateKey);
                        startActivity(intentVisit);

                    }else if (templateKey.equals("validate")){
                        Intent intentVisit = new Intent(getApplicationContext(),ConsulterAcOneActivity.class);
                        intentVisit.putExtra("userId",user.getId());
                        intentVisit.putExtra("userConnect",limitConnect);
                        intentVisit.putExtra("acId",ac_id);
                        intentVisit.putExtra("templateKey",templateKey);
                        startActivity(intentVisit);

                    }
                }
            });
            Log.i("ON-BIND-VIEW :","OKOK");
            holder.layoutForAc(_acList.get(position));
        }

        @Override
        public int getItemCount() {
            int itemCount=0;
            if (_acList!=null){
                itemCount=_acList.size();
            }
            return itemCount;
        }
    }
    class AcCardHolder extends RecyclerView.ViewHolder{
        private final TextView ui_acId;
        private final TextView ui_acReal;
        private final TextView ui_acDate;
        private final TextView ui_acStates;
        public AcCardHolder(View cell) {
            super(cell);
            ui_acId=(TextView) cell.findViewById(R.id.actcompl_id);
            ui_acReal=(TextView) cell.findViewById(R.id.ac_rea);
            ui_acDate=(TextView) cell.findViewById(R.id.ac_date);
            ui_acStates=(TextView) cell.findViewById(R.id.ac_states);
        }
        public void layoutForAc(ActCompl actCompl){
            String realisateurs = "";
            ArrayList<ActRea> listActRea= actCompl.getAcActReal();
            for (ActRea actRea:listActRea){
                realisateurs=realisateurs+" "+actRea.getActReaVisiteur().getUsrNom()+" ;";
            }
            ui_acId.setText(Integer.toString(actCompl.getId()));
            ui_acReal.setText(realisateurs);
            String ac_date=actCompl.getAcDate();
            ac_date=ac_date.substring(0,10);
            ui_acDate.setText(ac_date);
            ui_acStates.setText(actCompl.getAcStates());

        }
    }
}

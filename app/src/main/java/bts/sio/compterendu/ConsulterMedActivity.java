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
import bts.sio.compterendu.model.Account;
import bts.sio.compterendu.model.Medicament;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConsulterMedActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;
    private RecyclerView ui_medListRecyclerView;
    private Calendar limitConnect;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_medicament_activity);
        Intent intent = getIntent();
        int userId=intent.getIntExtra("userId",0);
        long timeMillis=intent.getLongExtra("limitConnect",0);
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
        Button bt_retour = (Button) findViewById(R.id.btn_retour);
        bt_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getFonction().equals("Visiteur")){
                    Intent intentVisit = new Intent(getApplicationContext(),PanelVisiteurActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                    startActivity(intentVisit);
                }
                if (user.getFonction().equals("Delegue")){
                    Intent intentDeleg = new Intent(getApplicationContext(),PanelDelegueActivity.class);
                    intentDeleg.putExtra("userId",user.getId());
                    intentDeleg.putExtra("userConnect",limitConnect.getTimeInMillis());
                    startActivity(intentDeleg);
                }
                if (user.getFonction().equals("Responsable")){
                    Intent intentResp = new Intent(getApplicationContext(),PanelResponsableActivity.class);
                    intentResp.putExtra("userId",user.getId());
                    intentResp.putExtra("userConnect",limitConnect.getTimeInMillis());
                    startActivity(intentResp);
                }

            }
        });
        // INIT LOADER
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Chargement des médicaments...");
        mProgressDialog.show();
        ui_medListRecyclerView = (RecyclerView)findViewById(R.id.crList_recycler_view);
        ui_medListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ui_medListRecyclerView.setAdapter(new MedListAdapter());

    }

    class MedListAdapter extends RecyclerView.Adapter<MedCardHolder> {
        private List<Medicament> _medList;

        public MedListAdapter(){
            //***********PREPARATION HEADER WSSE******************
            final Account user = mDbHelper.getUser(getApplicationContext());
            String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
            final WsseToken token  = new WsseToken(user,passEncript);
            RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
            Retrofit retrofit=conncecting.buildRequest();
            AdressBookApi service = retrofit.create(AdressBookApi.class);
            service.getMedicamentList().enqueue(new Callback<List<Medicament>>() {
                @Override
                public void onResponse(Call<List<Medicament>> call, Response<List<Medicament>> response) {
                    //END LOADER
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.i("Download ::","OK");
                    _medList =response.body();
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<Medicament>> call, Throwable t) {
                    //END LOADER
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.i("Download ::","ERROR");
                    Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public MedCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("ON-CREATE-VIEW :","OKOK");
            View cell = LayoutInflater.from(ConsulterMedActivity.this).inflate(R.layout.medicament_cell,parent,false);
            MedCardHolder holder = new MedCardHolder(cell);
            return holder;
        }

        @Override
        public void onBindViewHolder(MedCardHolder holder, final int position) {
            final Account user = mDbHelper.getUser(getApplicationContext());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int med_id= _medList.get(position).getId();
                    Intent intentVisit = new Intent(getApplicationContext(),ConsulterMedOneActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect);
                    intentVisit.putExtra("med_id",med_id);
                    startActivity(intentVisit);
                }
            });
            Log.i("ON-BIND-VIEW :","OKOK");
            holder.layoutForMed(_medList.get(position));
        }

        @Override
        public int getItemCount() {
            int itemCount=0;
            if (_medList !=null){
                itemCount= _medList.size();
            }
            return itemCount;
        }
    }
    class MedCardHolder extends RecyclerView.ViewHolder{
        private final TextView ui_medId;
        private final TextView ui_medDepotLegal;
        private final TextView ui_med_NomCom;
        private final TextView ui_medFamille;
        public MedCardHolder(View cell) {
            super(cell);
            ui_medId =(TextView) cell.findViewById(R.id.med_id);
            ui_medDepotLegal =(TextView) cell.findViewById(R.id.med_depot_legal);
            ui_med_NomCom =(TextView) cell.findViewById(R.id.med_famille);
            ui_medFamille =(TextView) cell.findViewById(R.id.med_nom_commercial);
        }
        public void layoutForMed(Medicament medicament){
            ui_medId.setText(Integer.toString(medicament.getId()));
            ui_medDepotLegal.setText(medicament.getMed_depot_legal());
            ui_medFamille.setText(medicament.getMed_famille().getFam_libelle());
            ui_med_NomCom.setText(medicament.getMed_nom_commercial());

        }
    }
}

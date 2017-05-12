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
import bts.sio.compterendu.model.Praticien;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConsulterPraActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;
    private RecyclerView ui_praListRecyclerView;
    private Calendar limitConnect;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_praticien_activity);
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
                    Intent intentResp = new Intent(getApplicationContext(),PanelActivity.class);
                    intentResp.putExtra("userId",user.getId());
                    intentResp.putExtra("userConnect",limitConnect.getTimeInMillis());
                    startActivity(intentResp);
            }
        });
        // INIT LOADER
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Chargement des praticiens...");
        mProgressDialog.show();
        ui_praListRecyclerView = (RecyclerView)findViewById(R.id.praList_recycler_view);
        ui_praListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ui_praListRecyclerView.setAdapter(new PraListAdapter());

    }

    class PraListAdapter extends RecyclerView.Adapter<PraCardHolder> {
        private List<Praticien> _praList;

        public PraListAdapter(){
            //***********PREPARATION HEADER WSSE******************
            final Account user = mDbHelper.getUser(getApplicationContext());
            String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
            final WsseToken token  = new WsseToken(user,passEncript);
            RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
            Retrofit retrofit=conncecting.buildRequest();
            AdressBookApi service = retrofit.create(AdressBookApi.class);
            service.getPraticienList().enqueue(new Callback<List<Praticien>>() {
                @Override
                public void onResponse(Call<List<Praticien>> call, Response<List<Praticien>> response) {
                    //END LOADER
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.i("Download ::","OK");
                    _praList =response.body();
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<Praticien>> call, Throwable t) {
                    //END LOADER
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.i("Download ::","ERROR");
                    Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public PraCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("ON-CREATE-VIEW :","OKOK");
            View cell = LayoutInflater.from(ConsulterPraActivity.this).inflate(R.layout.praticien_cell,parent,false);
            PraCardHolder holder = new PraCardHolder(cell);
            return holder;
        }

        @Override
        public void onBindViewHolder(PraCardHolder holder, final int position) {
            final Account user = mDbHelper.getUser(getApplicationContext());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pra_id= _praList.get(position).getId();
                    Intent intentVisit = new Intent(getApplicationContext(),ConsulterPraOneActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect);
                    intentVisit.putExtra("pra_id",pra_id);
                    startActivity(intentVisit);
                }
            });
            Log.i("ON-BIND-VIEW :","OKOK");
            holder.layoutForMed(_praList.get(position));
        }

        @Override
        public int getItemCount() {
            int itemCount=0;
            if (_praList !=null){
                itemCount= _praList.size();
            }
            return itemCount;
        }
    }
    class PraCardHolder extends RecyclerView.ViewHolder{
        private final TextView ui_praId;
        private final TextView ui_praNom;
        private final TextView ui_praPrenom;
        private final TextView ui_praType;

        public PraCardHolder(View cell) {
            super(cell);
            ui_praId =(TextView) cell.findViewById(R.id.pra_id);
            ui_praNom =(TextView) cell.findViewById(R.id.pra_nom);
            ui_praPrenom =(TextView) cell.findViewById(R.id.pra_prenom);
            ui_praType =(TextView) cell.findViewById(R.id.pra_type);
        }
        public void layoutForMed(Praticien praticien){
            ui_praId.setText(Integer.toString(praticien.getId()));
            ui_praNom.setText(praticien.getPra_nom());
            ui_praPrenom.setText(praticien.getPra_prenom());
            ui_praType.setText(praticien.getPra_type().getType_libelle());

        }
    }
}

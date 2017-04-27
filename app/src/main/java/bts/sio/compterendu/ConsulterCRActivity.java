package bts.sio.compterendu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_rapport_activity);
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
            service.getRapportList().enqueue(new Callback<List<RapportVisite>>() {
                @Override
                public void onResponse(Call<List<RapportVisite>> call, Response<List<RapportVisite>> response) {
                    Log.i("Download ::","OK");
                    _crList=response.body();
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<RapportVisite>> call, Throwable t) {

                }
            });
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
                    Intent intentVisit = new Intent(getApplicationContext(),ConsulterCrOneActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect);
                    intentVisit.putExtra("cr_id",cr_id);
                    startActivity(intentVisit);
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
            ui_crPraticien=(TextView) cell.findViewById(R.id.rap_praticien);
            ui_crDate=(TextView) cell.findViewById(R.id.rap_date);
        }
        public void layoutForCr(RapportVisite rapportVisite){
            ui_crId.setText(Integer.toString(rapportVisite.getId()));
            String usr_nom= rapportVisite.getRap_visiteur().getUsername();
            ui_crVisit.setText(usr_nom);
            String cr_date=rapportVisite.getRap_date();
            cr_date=cr_date.substring(0,10);
            ui_crDate.setText(cr_date);
            String pra_nom=rapportVisite.getRap_praticien().getPra_nom();
            ui_crPraticien.setText(pra_nom);

        }
    }
}

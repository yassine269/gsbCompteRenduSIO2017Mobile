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
import bts.sio.compterendu.model.User;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.RetrofitConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConsulterCollabActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;
    private RecyclerView ui_collabListRecyclerView;
    private Calendar limitConnect;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_collaborateur_activity);
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
        mProgressDialog.setMessage("Chargement des collaborateurs...");
        mProgressDialog.show();
        ui_collabListRecyclerView = (RecyclerView)findViewById(R.id.collabList_recycler_view);
        ui_collabListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ui_collabListRecyclerView.setAdapter(new CollabListAdapter());

    }

    class CollabListAdapter extends RecyclerView.Adapter<CollabCardHolder> {
        private List<User> _userList;

        public CollabListAdapter(){
            //***********PREPARATION HEADER WSSE******************
            final Account user = mDbHelper.getUser(getApplicationContext());
            String passEncript=user.hashPassword(user.getSalt(),user.getClearPass());
            final WsseToken token  = new WsseToken(user,passEncript);
            RetrofitConnect conncecting = new RetrofitConnect(user.getUsername(),token);
            Retrofit retrofit=conncecting.buildRequest();
            AdressBookApi service = retrofit.create(AdressBookApi.class);
            service.getCollabList().enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    //END LOADER
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.i("Download ::","OK");
                    _userList =response.body();
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    //END LOADER
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.i("Download ::","ERROR");
                    Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public CollabCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("ON-CREATE-VIEW :","OKOK");
            View cell = LayoutInflater.from(ConsulterCollabActivity.this).inflate(R.layout.collaborateur_cell,parent,false);
            CollabCardHolder holder = new CollabCardHolder(cell);
            return holder;
        }

        @Override
        public void onBindViewHolder(CollabCardHolder holder, final int position) {
            final Account user = mDbHelper.getUser(getApplicationContext());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int collab_id= _userList.get(position).getId();
                    Intent intentVisit = new Intent(getApplicationContext(),ConsulterCollabOneActivity.class);
                    intentVisit.putExtra("userId",user.getId());
                    intentVisit.putExtra("userConnect",limitConnect);
                    intentVisit.putExtra("collab_id",collab_id);
                    startActivity(intentVisit);
                }
            });
            Log.i("ON-BIND-VIEW :","OKOK");
            holder.layoutForCollab(_userList.get(position));
        }

        @Override
        public int getItemCount() {
            int itemCount=0;
            if (_userList !=null){
                itemCount= _userList.size();
            }
            return itemCount;
        }
    }
    class CollabCardHolder extends RecyclerView.ViewHolder{
        private final TextView ui_collabId;
        private final TextView ui_collabNom;
        private final TextView ui_collabPrenom;
        private final TextView ui_collabMatricule;

        public CollabCardHolder(View cell) {
            super(cell);
            ui_collabId =(TextView) cell.findViewById(R.id.collab_id);
            ui_collabNom =(TextView) cell.findViewById(R.id.collab_nom);
            ui_collabPrenom =(TextView) cell.findViewById(R.id.collab_prenom);
            ui_collabMatricule =(TextView) cell.findViewById(R.id.collab_matricule);
        }
        public void layoutForCollab(User user){
            ui_collabId.setText(Integer.toString(user.getId()));
            ui_collabNom.setText(user.getUsrNom());
            ui_collabPrenom.setText(user.getUsrPrenom());
            ui_collabMatricule.setText(user.getUsrMatricule());

        }
    }
}

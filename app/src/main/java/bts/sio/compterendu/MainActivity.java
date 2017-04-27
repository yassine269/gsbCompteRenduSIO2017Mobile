package bts.sio.compterendu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import bts.sio.compterendu.helper.CRReaderContract;
import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.security.WsseToken;
import bts.sio.compterendu.util.AdressBookApi;
import bts.sio.compterendu.util.LoginApi;
import bts.sio.compterendu.util.RetrofitConnect;
import bts.sio.compterendu.model.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;
    private Calendar calLimitConnect;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Test si connexion utilisateur expirer ou non
        Account user=checkAccount();
        if (user.getUsername() != null){
            ((TextView) findViewById(R.id.nb_txt)).setText(user.getUsername());
            ((TextView) findViewById(R.id.nb_txt)).setFocusable(false);
        }
        //********LISTENER VALID BUTTON**************
        Button bt_ok = (Button) findViewById(R.id.valid_button);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account user=checkAccount();
                //***********SI UN COMPTE EXISTE EN BASE********************
                if (user.getUsername() != null) {
                    final String identifiant = ((TextView) findViewById(R.id.nb_txt)).getText().toString();
                    final String mdp = ((TextView) findViewById(R.id.password_txt)).getText().toString();
                    String passEncript=user.hashPassword(user.getSalt(),mdp);
                    final WsseToken token  = new WsseToken(user,passEncript);
                    //***********PREPARATION HEADER WSSE******************
                    RetrofitConnect conncecting = new RetrofitConnect(identifiant,token);
                    Retrofit retrofit=conncecting.buildRequest();
                    AdressBookApi service = retrofit.create(AdressBookApi.class);
                    service.getApiLogin().enqueue(new Callback<LoginApi>() {
                        @Override
                        public void onResponse(Call<LoginApi> call, Response<LoginApi> response) {
                            if (response.body()==null){
                                Toast.makeText(getApplicationContext(),"Nom d'utilisateur érroné, veuillez réessayez",Toast.LENGTH_SHORT).show();
                                Log.i("RESP NULL CONNECT:  ","ok"+response.code());
                            }
                            //Si un résultat est renvoyer
                            else {
                                if (response.body().getResponse().equals("OK")) {
                                    Log.i("PASSAGE:", "VALIDE BUTTON CONNECTION EN BASE" + response.body().getResponse());
                                    Account user = checkAccount();
                                    connectUser(user);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginApi> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    // Si aucun compte en base
                    final String identifiant = ((TextView) findViewById(R.id.nb_txt)).getText().toString();
                    final String mdp = ((TextView) findViewById(R.id.password_txt)).getText().toString();
                    // récupération du salt et vérification du username
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://10.0.2.2/gsbCompteRendu/web/app_dev.php/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    AdressBookApi service = retrofit.create(AdressBookApi.class);
                    service.account(identifiant).enqueue(new Callback<Account>() {
                        @Override
                        public void onResponse(Call<Account> call, Response<Account> response) {
                            //Requete pour récuperer les info public du user
                            //Si aucun résultat avec le nom d'utilisateur rentrer
                            Log.i("PASSAGE:","VALIDE BUTTON CONNECTION NON EN BASE");
                            if (response.body()==null){
                                Toast.makeText(getApplicationContext(),"Nom d'utilisateur érroné, veuillez réessayez",Toast.LENGTH_SHORT).show();
                            }
                            //Si un résultat est renvoyer
                            else {
                                //Insertion des information du compte en base
                                CRReaderDbHelper mDbHelper = new CRReaderDbHelper(getApplicationContext());
                                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(CRReaderContract.CREntry._ID, response.body().getId());
                                values.put(CRReaderContract.CREntry.COLUMN_USER_USERNAME, response.body().getUsername());
                                values.put(CRReaderContract.CREntry.COLUMN_USER_SALT, response.body().getSalt());
                                values.put(CRReaderContract.CREntry.COLUMN_USER_FONCTION, response.body().getFonction());
                                values.put(CRReaderContract.CREntry.COLUMN_USER_CLEARPASS, mdp);
                                long newRowId = db.insert(CRReaderContract.CREntry.TABLE_NAME, null, values);
                                Log.i("BDD INSERT OK", "OKOK");
                                //Création du TIMESTAMP pour session
                            }
                        }

                        @Override
                        public void onFailure(Call<Account> call, Throwable t) {
                            Log.i("BDD INSERT"," NO WORK"+t);
                            Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez",Toast.LENGTH_SHORT).show();
                        }
                    });
                    user=checkAccount();
                    Log.i("USER :","USER RENTRER EN BASE :"+user.getUsername());
                    if (user.getUsername()!=null){
                        String passEncript=user.hashPassword(user.getSalt(),mdp);
                        final WsseToken token  = new WsseToken(user,passEncript);
                        //***********PREPARATION HEADER WSSE******************
                        RetrofitConnect conncecting = new RetrofitConnect(identifiant,token);
                        retrofit=conncecting.buildRequest();
                        service = retrofit.create(AdressBookApi.class);
                        service.getApiLogin().enqueue(new Callback<LoginApi>() {
                            @Override
                            public void onResponse(Call<LoginApi> call, Response<LoginApi> response) {
                                if(response.body().getResponse().equals("OK")){
                                    Log.i("PASSAGE:","VALIDE BUTTON CONNECTION NON EN BASE");
                                    Account user=checkAccount();
                                    connectUser(user);
                                }
                            }
                            @Override
                            public void onFailure(Call<LoginApi> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
        //********LISTENER CANCEL BUTTON**************

        Button bt_annuler = (Button) findViewById(R.id.cancel_button);
        bt_annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account user = checkAccount();
                if (user.getUsername() == null){
                    ((TextView) findViewById(R.id.nb_txt)).setText("");
                }
                ((TextView) findViewById(R.id.password_txt)).setText("");
                Toast.makeText(getApplicationContext(),"Annulé", Toast.LENGTH_SHORT).show();
            }
        });

        //********LISTENER CHANGE BUTTON**************

        Button bt_change = (Button) findViewById(R.id.change_button);
        bt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=((TextView) findViewById(R.id.nb_txt)).getText().toString();
                ((TextView) findViewById(R.id.nb_txt)).setText("");
                ((TextView) findViewById(R.id.password_txt)).setText("");
                // Define 'where' part of query.
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String selection = CRReaderContract.CREntry.COLUMN_USER_USERNAME + " LIKE ?";
// Specify arguments in placeholder order.
                String[] selectionArgs = { username };
// Issue SQL statement.
                db.delete(CRReaderContract.CREntry.TABLE_NAME, selection, selectionArgs);
                Toast.makeText(getApplicationContext(),"Compte supprimer !", Toast.LENGTH_SHORT).show();
                Log.i("DELETE ACCOUNT : ","OKOK");
                recreate();
            }
        });

    }
    public Account checkAccount(){
        //INIT BDD READABLE
        mDbHelper = new CRReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Request BDD pour savoir si un compte user existe
        String[] projection={
                CRReaderContract.CREntry._ID,
                CRReaderContract.CREntry.COLUMN_USER_USERNAME,
                CRReaderContract.CREntry.COLUMN_USER_SALT,
                CRReaderContract.CREntry.COLUMN_USER_FONCTION,
        };
        Cursor cursor = db.query(
                CRReaderContract.CREntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        String username = null;
        String salt =null;
        String fonction =null;
        Account user = new Account();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(0);
            username=cursor.getString(1);
            salt=cursor.getString(2);
            fonction=cursor.getString(3);
            user.setId(itemId);
            user.setUsername(username);
            user.setSalt(salt);
            user.setFonction(fonction);
        }
        //Fermeture DB
        cursor.close();
        return user;
    }
    public void connectUser(Account user){
        Date limitConnect = new Date( ); // Instantiate a Date object
        calLimitConnect = Calendar.getInstance();
        calLimitConnect.setTime(limitConnect);
        calLimitConnect.add(Calendar.MINUTE, 5);
        limitConnect = calLimitConnect.getTime();
        if (user.getFonction().equals("Visiteur")){
            Intent intentVisit = new Intent(getApplicationContext(),PanelVisiteurActivity.class);
            intentVisit.putExtra("userId",user.getId());
            intentVisit.putExtra("userConnect",calLimitConnect.getTimeInMillis());
            startActivity(intentVisit);
        }
        if (user.getFonction().equals("Delegue")){
            Intent intentDeleg = new Intent(getApplicationContext(),PanelDelegueActivity.class);
            intentDeleg.putExtra("userId",user.getId());
            intentDeleg.putExtra("userConnect",calLimitConnect.getTimeInMillis());
            startActivity(intentDeleg);
        }
        if (user.getFonction().equals("Responsable")){
            Intent intentResp = new Intent(getApplicationContext(),PanelResponsableActivity.class);
            intentResp.putExtra("userId",user.getId());
            intentResp.putExtra("userConnect",calLimitConnect.getTimeInMillis());
            startActivity(intentResp);
        }
    }
    /**
     * Transformation byte[] to Hexadecimal
     */
    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();

    }
}

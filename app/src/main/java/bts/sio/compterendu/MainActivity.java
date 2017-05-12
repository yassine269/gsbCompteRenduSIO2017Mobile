package bts.sio.compterendu;

import android.app.ProgressDialog;
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
        }else {
            ((TextView) findViewById(R.id.password_txt)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.change_button)).setVisibility(View.GONE);
        }
        //********LISTENER VALID BUTTON**************
        Button bt_ok = (Button) findViewById(R.id.valid_button);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // INIT LOADER
                final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                Account user=checkAccount();
                //***********SI UN COMPTE EXISTE EN BASE********************
                if (user.getUsername() != null) {
                    mProgressDialog.setMessage("Authentification...");
                    final String identifiant = ((TextView) findViewById(R.id.nb_txt)).getText().toString();
                    final String mdp = ((TextView) findViewById(R.id.password_txt)).getText().toString();
                    String passEncript=user.hashPassword(user.getSalt(),mdp);
                    final WsseToken token  = new WsseToken(user,passEncript);
                    SQLiteDatabase db=mDbHelper.getReadableDatabase();
                    ContentValues values=new ContentValues();
                    values.put(CRReaderContract.CREntry.COLUMN_USER_CLEARPASS,mdp);

                    String selection = CRReaderContract.CREntry.COLUMN_USER_USERNAME +" LIKE ?";
                    String[] selectionArgs = {user.getUsername()};
                    int count = db.update(
                            CRReaderContract.CREntry.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs);
                    //***********PREPARATION HEADER WSSE******************
                    RetrofitConnect conncecting = new RetrofitConnect(identifiant,token);
                    Retrofit retrofit=conncecting.buildRequest();
                    AdressBookApi service = retrofit.create(AdressBookApi.class);
                    service.getApiLogin().enqueue(new Callback<LoginApi>() {
                        @Override
                        public void onResponse(Call<LoginApi> call, Response<LoginApi> response) {
                            //END LOADER
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
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
                            //END LOADER
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    // Si aucun compte en base
                    mProgressDialog.setMessage("Vérification du nom d'utilisateur...");
                    final String identifiant = ((TextView) findViewById(R.id.nb_txt)).getText().toString();
                    final String mdp = ((TextView) findViewById(R.id.password_txt)).getText().toString();
                    // récupération du salt et vérification du username
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(AdressBookApi.ENDPOINT)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    AdressBookApi service = retrofit.create(AdressBookApi.class);
                    service.account(identifiant).enqueue(new Callback<Account>() {
                        @Override
                        public void onResponse(Call<Account> call, Response<Account> response) {
                            //END LOADER
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            //Requete pour récuperer les info public du user
                            //Si aucun résultat avec le nom d'utilisateur rentrer
                            Log.i("BTN_VALIDER:"," Tentative d'enregistrement d'un nouveau compte utilisateur");
                            if (response.body()==null){
                                Toast.makeText(getApplicationContext(),"Nom d'utilisateur érroné, veuillez réessayez",Toast.LENGTH_SHORT).show();
                            }
                            //Si un résultat est renvoyer
                            else {
                                //Insertion des information du compte en base
                                String mdp = ((TextView) findViewById(R.id.password_txt)).getText().toString();
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
                                recreate();
                                //Création du TIMESTAMP pour session
                            }
                        }

                        @Override
                        public void onFailure(Call<Account> call, Throwable t) {
                            //END LOADER
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            Log.i("BDD INSERT"," NO WORK"+t);
                            Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
                        }
                    });


                    user=checkAccount();
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
                                Toast.makeText(getApplicationContext(),"Erreur réseaux, veuillez réessayez  "+t,Toast.LENGTH_SHORT).show();
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
            Intent intentVisit = new Intent(getApplicationContext(),PanelActivity.class);
            intentVisit.putExtra("userId",user.getId());
            intentVisit.putExtra("userConnect",calLimitConnect.getTimeInMillis());
            startActivity(intentVisit);
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

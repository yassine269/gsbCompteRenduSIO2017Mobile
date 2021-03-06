package bts.sio.compterendu;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;

public class CrActivity extends Activity {

    private CRReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cr);
        Intent intent = getIntent();
        int userId=intent.getIntExtra("userId",0);
        long timeMillis=intent.getLongExtra("limitConnect",0);
        Button bt_consultcr = (Button) findViewById(R.id.consultCR);
        Button bt_modifiercr = (Button) findViewById(R.id.modifCR);
        Button bt_saisiecr = (Button) findViewById(R.id.saisieCR);
        final Calendar limitConnect= Calendar.getInstance();
        limitConnect.setTimeInMillis(timeMillis);
        mDbHelper=new CRReaderDbHelper(getApplicationContext());
        final Account user = mDbHelper.getUser(getApplicationContext());
        if (user.getFonction().equals("Visiteur")){
            Log.i("FONCTION :","VISITEUR");

        }
        if (user.getFonction().equals("Delegue")){
            bt_saisiecr.setVisibility(View.GONE);
            bt_modifiercr.setVisibility(View.GONE);
            Log.i("FONCTION :","DELEGUE");
        }
        if (user.getFonction().equals("Responsable")){
            bt_saisiecr.setVisibility(View.GONE);
            bt_modifiercr.setVisibility(View.GONE);
            Log.i("FONCTION :","RESPONSABLE");

        }
        Log.i("FONCTION :",""+user.getFonction());

        //INIT BDD READABLE
        if (!user.checkConnection(limitConnect)){
            Intent intentLogin=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intentLogin);
        }else {
            Date limitConnectReplace = new Date(); // Instantiate a Date object
            limitConnect.setTime(limitConnectReplace);
            limitConnect.add(Calendar.MINUTE, 5);
        }
        bt_consultcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterCRActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","view");
                startActivity(intentVisit);
            }
        });
        bt_modifiercr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterCRActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","edit");
                startActivity(intentVisit);
            }
        });
        bt_saisiecr.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),SaisieCRActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","view");
                startActivity(intentVisit);
            }
        });
        Button bt_retour = (Button) findViewById(R.id.retour_button);
        bt_retour.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intentResp = new Intent(getApplicationContext(),PanelActivity.class);
                    intentResp.putExtra("userId",user.getId());
                    intentResp.putExtra("userConnect",limitConnect.getTimeInMillis());
                    startActivity(intentResp);
            }
        });
    }

}

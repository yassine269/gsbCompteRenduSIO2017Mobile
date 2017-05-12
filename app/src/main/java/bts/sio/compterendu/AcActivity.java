package bts.sio.compterendu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;

public class AcActivity extends Activity {

    private CRReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac);
        Intent intent = getIntent();
        int userId=intent.getIntExtra("userId",0);
        long timeMillis=intent.getLongExtra("limitConnect",0);
        final Calendar limitConnect= Calendar.getInstance();
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
        Button bt_consultac = (Button) findViewById(R.id.consultAC);
        bt_consultac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterACActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","view");
                startActivity(intentVisit);
            }
        });
        Button bt_modifierac = (Button) findViewById(R.id.modifAC);
        bt_modifierac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterACActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","edit");
                startActivity(intentVisit);
            }
        });
        Button bt_validerac = (Button) findViewById(R.id.validAC);
        bt_validerac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterACActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","validate");
                startActivity(intentVisit);
            }
        });
        Button bt_saisieac = (Button) findViewById(R.id.saisieAC);
        bt_saisieac.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),SaisieACActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                intentVisit.putExtra("templateKey","view");
                startActivity(intentVisit);
            }
        });
        if (user.getFonction().equals("Visiteur")){
            bt_validerac.setVisibility(View.GONE);
        }
        if (user.getFonction().equals("Delegue")){
            bt_saisieac.setVisibility(View.GONE);
            bt_modifierac.setVisibility(View.GONE);
        }
        if (user.getFonction().equals("Responsable")){
            bt_saisieac.setVisibility(View.GONE);
            bt_modifierac.setVisibility(View.GONE);
            bt_validerac.setVisibility(View.GONE);
        }
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

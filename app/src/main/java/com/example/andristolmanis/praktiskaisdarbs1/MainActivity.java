package com.example.andristolmanis.praktiskaisdarbs1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//NFC imports
import android.nfc.NfcAdapter;
import android.app.PendingIntent;
import android.nfc.Tag;

import java.util.Arrays;
import java.util.List;

import static android.widget.TextView.BufferType.EDITABLE;

public class MainActivity extends AppCompatActivity {

    Button buttonDialog;
    Button buttonActivityTwo;
    String[] namesArray;
    Button btnSave;
    SharedPreferences sp;

    public static final String USER_PREF = "USER_PREF" ;
    public static final String KEY_TEXT = "KEY_TEXT";

    //----------------------------------------------------------------------------------------------NFC Stuff
    NfcAdapter nfcAdapter; // Definē nfc adapteri
    PendingIntent pendingIntent; //
    boolean nfcSearch = false; // Bools kas ļauj nolasīt nfc pēc pogas nospiešanas
    Tag nfcTag; // Definē nfc tagu - satur visus uztvertos datus
    byte[] nfcTagID; // nfc taga bitu virkne
    String nfcTagSerialNumber; // nfc taga hex, pēc pārveidošanas no bitiem
    Context context; // īsti nezinu ko tas dara, bet bez tā neiet

    EditText textt; //Teksta lauks kurā ieraksta iegūto id
    Button btnNFC; // Poga kas atļauj nolasīt nfc programmai, tomēr pats telefons var nolasīt nfc


    //----------------------------------------------------------------------------------------------NFC Stuff ends for now


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonDialog = (Button) findViewById(R.id.buttonDialogs);
        buttonActivityTwo = (Button) findViewById(R.id.buttonActivity2);
        btnSave = (Button) findViewById(R.id.btnSave);
        sp = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

        // <----------------------------------------------------------------------------------------More NFC stuff
        textt = (EditText) findViewById(R.id.textToBeSaved);
        btnNFC = (Button) findViewById(R.id.btnNFC);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        context = this;
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "Ierīcei nav NFC! Tas ir nepieceišams.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        // <----------------------------------------------------------------------------------------More NFC stuff ends here
        buttonDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                namesArray = new String[] {"Andris Tolmanis", "Dita Prūse", "Annija Viktorija Zaķe"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("2. Grupas dialoga logs!");
                final boolean[] checkedNames = new boolean[]{false, false, false};
                final List<String> namesList = Arrays.asList(namesArray);
                dialog.setMultiChoiceItems(namesArray, checkedNames, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        String currentItem = namesList.get(which);
                        Toast.makeText(MainActivity.this, currentItem+ " "+ izvelets(isChecked), Toast.LENGTH_SHORT).show();
                    }
                });
                // Dialog "yes" button
                dialog.setPositiveButton("Labi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Labi poga tika nospiesta!", Toast.LENGTH_SHORT).show();
                    }
                });
                // Dialog "cancel" button
                dialog.setNeutralButton("Atcelt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Atclet poga tika nospiesta!", Toast.LENGTH_SHORT).show();

                    }
                });
                AlertDialog dialogShow = dialog.create();
                dialogShow.show();
            }

        });
        buttonActivityTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondaryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        // PD3
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String name  = textt.getText().toString();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_TEXT, name);
                editor.commit();

                Toast.makeText(MainActivity.this, "Saglabāts!", Toast.LENGTH_SHORT).show();
            }
        });

        // <----------------------------------------------------------------------------------------NFC stuff again
        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nfcSearch = true;// Ļauj lietotnei reaģēt kad nolasakādu nfc
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent){
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) && nfcSearch == true){
            getTagInfo(intent);
            //Toast.makeText(MainActivity.this, "Oh snap, dude! I found some sauce!", Toast.LENGTH_SHORT).show();
            textt.setText(nfcTagSerialNumber, EDITABLE);
            nfcSearch = false;
            // Te likt kodu, kas izpildas kad nfc ir nolasīts
            // piemēram
            Toast.makeText(MainActivity.this, "Darījums veikts ar iekārtu "+nfcTagSerialNumber, Toast.LENGTH_SHORT).show();
        }
    }
    private void getTagInfo(Intent intent) {
        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcTagID = nfcTag.getId();
        nfcTagSerialNumber = bytesToHex(nfcTagID);
    }


    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    //______________________________________________________________________________________________NFC stuff ends

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialog(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("2. Grupas dialoga logs");
            dialog.setMessage("");
            dialog.setPositiveButton("Labi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

    }
    protected String izvelets(boolean check) {
        if (check == true) {
            return "tiek izvēlēts!";
        } else {
            return "tiek neizvēlēts!";
        }
    }

}

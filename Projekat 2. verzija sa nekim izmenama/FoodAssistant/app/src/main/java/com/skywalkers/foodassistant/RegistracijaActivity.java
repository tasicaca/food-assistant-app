package com.skywalkers.foodassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;

public class RegistracijaActivity extends AppCompatActivity {

    String username;
    String password;
    int idOdabranogNivoaAktivnosti=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);

        // Samo u konstruktoru inicijalizujemo kompomente, radi ustede memorije prilikom kreiranja
        Button btnRegistrujSe = (Button) findViewById(R.id.btnRegistrujSe);
        TextView usernameString = (TextView) findViewById(R.id.textViewUsername);
        final TextView textViewOpisNivoaAktivnosti = (TextView) findViewById(R.id.textViewOpisNivoaAktivnosti);
        final EditText editTextIme = (EditText) findViewById(R.id.editTextIme);
        final EditText editTextPrezime =  (EditText) findViewById(R.id.editTextPrezime);
        final EditText editTextVisina = (EditText) findViewById(R.id.editTextVisina);
        final EditText editTextMasa = (EditText) findViewById(R.id.editTextMasa);
        final EditText editTextGodiste = (EditText) findViewById(R.id.editTextGodiste);
        final Spinner spnrPol = (Spinner) findViewById(R.id.spnrPol);
        final Spinner spnrRezimIshrane = (Spinner) findViewById(R.id.spnrRezimIshrane);
        Spinner spnrNivoAktivnosti = (Spinner) findViewById(R.id.spnrNivoAktivnosti);

        textViewOpisNivoaAktivnosti.setText("");

        if (getIntent()!=null)
        {
            Bundle podaci = getIntent().getExtras();

            username = podaci.getString("com.skywalkers.foodassistant.username_reg");
            password = podaci.getString("com.skywalkers.foodassistant.password_reg");
            usernameString.setText(username);
        }

        // Postavi granice za godiste
        editTextGodiste.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!editTextGodiste.getText().toString().equals("")){
                    int value = Integer.parseInt(editTextGodiste.getText().toString());
                    if (value<1918) // Najstarije godiste je 1918
                        editTextGodiste.setText("1900");
                    else if (value> 2008) // Min. 10 godina je potrebno za koriscenje aplikacije
                        editTextGodiste.setText("2008");
                }
            }
        });

        // Postavi granice za masu
        editTextMasa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if (!editTextMasa.getText().toString().equals("")){
                        int value = Integer.parseInt(editTextMasa.getText().toString());
                        if (value>300) // Ukoliko je uneta masa veca od 300kg, postaviti granicu
                            editTextMasa.setText("300");
                        else if (value<=0)
                            editTextMasa.setText("50");
                    }
            }
        });

        editTextVisina.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!editTextVisina.getText().toString().equals("")){
                    int value = Integer.parseInt(editTextVisina.getText().toString());
                    if (value>300) // Max dozvoljena visina je 300cm
                        editTextVisina.setText("300");
                    else if (value<=0)
                        editTextVisina.setText("100");
                }
            }
        });

        spnrNivoAktivnosti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opisAktivnosti="";
                switch (position){
                    case 0: case 1: opisAktivnosti=""; // Odaberi opcija i neaktivan
                        break;
                    case 2: opisAktivnosti="1-3 dana nedeljno"; // Retko aktivan
                        break;
                    case 3: opisAktivnosti="4-7 dana u nedelji"; // Aktivan
                        break;
                    case 4: opisAktivnosti="Sportista"; // Veoma aktivan
                        break;
                    default:
                        opisAktivnosti="";
                }

                idOdabranogNivoaAktivnosti = position;
                textViewOpisNivoaAktivnosti.setText(opisAktivnosti);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idOdabranogNivoaAktivnosti=0;
                textViewOpisNivoaAktivnosti.setText("");
            }
        });

        btnRegistrujSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int polID = spnrPol.getSelectedItemPosition();
                int rezimIshraneID = spnrRezimIshrane.getSelectedItemPosition();
                boolean imePoljeValidno = !editTextIme.getText().toString().equals("");
                boolean prezimePoljeValidno = !editTextPrezime.getText().toString().equals("");
                boolean godisteValidno = !editTextGodiste.getText().toString().equals("");
                boolean masaValidna = !editTextMasa.getText().toString().equals("");
                boolean visinaValidna = !editTextVisina.getText().toString().equals("");

                boolean uslovi= (polID>0 && rezimIshraneID>0 && idOdabranogNivoaAktivnosti>0) &&
                        (imePoljeValidno && prezimePoljeValidno) && (godisteValidno && masaValidna && visinaValidna);

                if (uslovi) { // Ako su sva polja popunjena ispravno

                    // Napraviti request i pristupiti php serveru da li postoji korisnik u bazi
                    String url = FADBConnector.adresa+"?add=noviKorisnik&user="+username+"&pass="+password+"&ime="+
                            editTextIme.getText().toString()+"&prezime="+editTextPrezime.getText().toString()+"&pol="+
                            polID+"&god="+editTextGodiste.getText().toString()+"&visina="+editTextVisina.getText().toString()+
                            "&masa="+editTextMasa.getText().toString()+"&nivAkt="+idOdabranogNivoaAktivnosti+"&rezIsh="+rezimIshraneID;

                    //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
                    JsonObjectRequest zahtev = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // Ovde definisemo sta se radi kada uspesno dobijemo odgovor

                                        if (response.has("querySucc") && response.optBoolean("querySucc") ){

                                        // TODO: Parsuj podatke i sacuvaj ih lokalno

                                        zavrsiRegistracijuIPredjiNaLogin(username,password);
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // U slucaju greske odraditi ovo
                                    Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
                                    Log.i("error",error.getMessage());
                                    //Toast.makeText(getApplicationContext(),"Greska pri uspostavljanju konekcije. Pokusajte ponovo.",Toast.LENGTH_SHORT).show();
                                }
                            });
                    // Izvrsiti zahtev
                    FADBConnector.getInstance(getApplicationContext()).addToRequestQueue(zahtev);



                }
                else{
                    Toast.makeText(getApplicationContext(), "Jedno ili vise nepopunjenih polja.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    } // Kraj onCreate funkcije

    protected void zavrsiRegistracijuIPredjiNaLogin(String username, String password)
    {
        Intent krajRegistracije = new Intent(getApplicationContext(), MainActivity.class);
        krajRegistracije.putExtra("com.skywalkers.foodassistant.username_endreg", username);
        krajRegistracije.putExtra("com.skywalkers.foodassistant.password_endreg", password);

        setResult(RESULT_OK, krajRegistracije);
        finish();
    }

} // Kraj RegistracijaActivity klase

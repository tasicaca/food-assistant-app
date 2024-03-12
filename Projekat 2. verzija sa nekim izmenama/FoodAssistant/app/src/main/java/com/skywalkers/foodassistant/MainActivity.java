package com.skywalkers.foodassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button btnPrijavljivanje;
    Button btnRegistracija;
    EditText editTextUsername;
    EditText editTextPassword;
    String test="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Ispitati da li postoji fajl sa username i passwordom za autologin
        // ako postoji, ucitati podatke i pokrenuti DefaultniActivity

        btnPrijavljivanje = (Button) this.findViewById(R.id.btnPrijavljivanje);
        btnRegistracija = (Button) this.findViewById(R.id.btnRegistracija);
        editTextUsername = (EditText) this.findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) this.findViewById(R.id.editTextPassword);

        btnRegistracija.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String user = editTextUsername.getText().toString();
                final String pass = editTextPassword.getText().toString();

                if ( !user.equals("") && !pass.equals("") ){

                // Napraviti request i pristupiti php serveru da li postoji korisnik u bazi
                String url = FADBConnector.adresa+"?check=daLiPostojiUser&user="+user;
                JsonObjectRequest zahtev = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Ovde definisemo sta se radi kada uspesno dobijemo odgovor
                                if (response.has("querySucc")){
                                    boolean postojiKorisnik = response.optBoolean("querySucc");
                                    if (postojiKorisnik==false)
                                        idiNaRegistracioniActivity(user,pass);
                                    else
                                        Toast.makeText(getApplicationContext(),"Odabrano korisnicko ime je zauzeto.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // U slucaju greske odraditi ovo
                                Toast.makeText(getApplicationContext(),"Greska pri uspostavljanju konekcije. Pokusajte ponovo.",Toast.LENGTH_SHORT).show();
                            }
                        });
                // Izvrsiti zahtev
                FADBConnector.getInstance(getApplicationContext()).addToRequestQueue(zahtev);

                }
                else{
                    if (user.equals(""))
                        Toast.makeText(getApplicationContext(),"Unesite zeljeno korisnicko ime.",Toast.LENGTH_SHORT).show();
                    else if (pass.equals(""))
                        Toast.makeText(getApplicationContext(),"Unesite lozinku za Vas nalog.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrijavljivanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = editTextUsername.getText().toString();
                final String pass = editTextPassword.getText().toString();

                if ( !user.equals("") && !pass.equals("") ){

                    // Napraviti request i pristupiti php serveru da li postoji korisnik u bazi
                    String url = FADBConnector.adresa+"?check=autentifikuj&user="+user+"&pass="+pass;
                    JsonObjectRequest zahtev = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // Ovde definisemo sta se radi kada uspesno dobijemo odgovor
                                    if (response.has("querySucc")){
                                        boolean validniPodaci = response.optBoolean("querySucc");
                                        if (validniPodaci==true)
                                            idiNaGlavniEkran(user,pass);
                                        else
                                            Toast.makeText(getApplicationContext(),"Neispravno korisnicko ime ili lozinka.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // U slucaju greske odraditi ovo
                                    Toast.makeText(getApplicationContext(),"Greska pri uspostavljanju konekcije. Pokusajte ponovo.",Toast.LENGTH_SHORT).show();
                                }
                            });
                    // Izvrsiti zahtev
                    FADBConnector.getInstance(getApplicationContext()).addToRequestQueue(zahtev);
                }
                else{
                    if (user.equals(""))
                        Toast.makeText(getApplicationContext(),"Unesite Vase korisnicko ime.",Toast.LENGTH_SHORT).show();
                    else if (pass.equals(""))
                        Toast.makeText(getApplicationContext(),"Unesite lozinku za Vas nalog.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    } // Kraj onCreate-a

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode== RESULT_OK){

                if (getIntent()!=null){
                    // Ako smo se vratili sa registracionog activity-ja
                    // Popuni polja za korisnicko ime i lozinku

                    Bundle podaci = getIntent().getExtras();

                    if (podaci!=null){
                        String user = podaci.getString("com.skywalkers.foodassistant.username_endreg");
                        String pass = podaci.getString("com.skywalkers.foodassistant.password_endreg");

                        editTextUsername.setText(user);
                        editTextPassword.setText(pass);
                    }
                }

            }
        }
    } // Kraj onActivityResult-a

    protected void idiNaRegistracioniActivity(String username, String password){
        Intent registracija = new Intent(getApplicationContext(), RegistracijaActivity.class);
        registracija.putExtra("com.skywalkers.foodassistant.username_reg",username);
        registracija.putExtra("com.skywalkers.foodassistant.password_reg",password);
        startActivityForResult(registracija,1);
    }

    protected void idiNaGlavniEkran(String username, String password){

        Intent login = new Intent(getApplicationContext(), DefaultniActivity.class);

        // TODO: Sacuvati lozinku i password za autologin u fajlu

        startActivity(login);
    }


} // Kraj MainActivity-ja

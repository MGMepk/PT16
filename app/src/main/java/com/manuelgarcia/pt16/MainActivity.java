package com.manuelgarcia.pt16;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter, tAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView;
    private boolean sdAvailable = false;
    private boolean sdWriteAccess = false;


    final static String API_KEY = "69bb1dd5b22b586a9d2eb6056d9c3b43";

    private String city;
    private EditText editTextCity;
    private int DATASET_COUNT = 60;
    private Button butJ, butX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.textView);

        LinearLayout linearLayout = findViewById(R.id.layout_info);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

            }
        });

        //a la PT16, només un botó, fes d ampliació parsing Json i altres..
        butJ = findViewById(R.id.buttonJ);
        butX = findViewById(R.id.buttonX);
        butJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openWeather(true);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        butX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openWeather(false);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        String estat = Environment.getExternalStorageState();
        // comprova si hi ha SD i si puc escriure en ella
        if (estat.equals(Environment.MEDIA_MOUNTED)) {
            sdAvailable = true;
            sdWriteAccess = true;
        } else if (estat.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            sdAvailable = true;
            sdWriteAccess = false;
        } else {
            sdAvailable = false;
            sdWriteAccess = false;
            //manifest --> uses-permission
        }
        editTextCity = findViewById(R.id.editTextCity);


        //aquesta és una càrrega de la llista recycler de manera estàtica
        //com a ampliació de la PT16, estaria bé posar aquest codi de sota a un fragment dinàmic
        //i al onCreateView, que retornés aquesta vista que és el recycler. De paràmetre d'entrada,
        //al fragment, se li ha de passar el nom de la ciutat (per que al onCreateView també ha de
        //mirar si ja hi és al SqlLite...
        // TODO: 18/12/19
        recyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // el nostre recycler té tamany fixe al layout, ho declarem, així és més eficient.
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            input.add("Test" + i);
        }

        mAdapter = new MyAdapter(input);

        recyclerView.setAdapter(mAdapter);

    }

    //es crida quan clickes botó principal, únic que hi haurà  la pràctica.
    // el paràmetre format no cal per la PT16, només un
    public void logica(Boolean format, String nomCiutat) throws ParseException, JSONException, XmlPullParserException {
        //previ a descarregar cap fitxer xml o json, veure si hi és a la bbdd amb el criteri d' actualització de dades que ens convé...
        //mitja hora ha passat ja desde la primera línia de temperatures, ha passat,
        //o potser volem veure que la ultima fila donada de dades, la seva hora no sigui anterior al dia actual.o sigui, max 5 dies.

        TemperaturesHelper2 temperaturesHelper = new TemperaturesHelper2(this);

        if (!(temperaturesHelper.estaCiutatDescarregada(nomCiutat) && temperaturesHelper.estaActualitzada(nomCiutat))) {

            //eliminar dades obsoletes
            temperaturesHelper.eliminaDades(nomCiutat);

            //descàrrega xml /json
            openWeather(format);

            //temperaturesHelper.guarda(nomCiutat, temps<Bloc>);
        }
        // si està ja descarregada i actualitzada,...
        // inflar fragments dinàmics (segons radio buttons de formats, o spinners (innovació demanada a la PT16)
        // ... o sino, com fins ara, cridar a mostra l'adapter
        temperaturesHelper.close();
    }

    public void openWeather(Boolean JSonFormat) throws XmlPullParserException, JSONException {
        String nomCiutat;
        city = editTextCity.getText().toString();
        if (!city.isEmpty()) nomCiutat = city;
        else nomCiutat = "Petropavlovsk";

        //nomCiutat="Irkutsk";   aquesta és com Petropavlovsk, si t'agrada el fred...

        String result;
        String myUrl;

        if (!JSonFormat)
            myUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" +
                    nomCiutat + "&units=metric&mode=xml&appid=" + API_KEY;
        else
            myUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" +
                    nomCiutat + "&units=metric&mode=json&appid=" + API_KEY;

        Log.d("test", myUrl);
        Descarregador descarregador = new Descarregador(this);
        try {
            result = descarregador.execute(myUrl).get();

            List<Bloc> temps;

            if (!(result == null)) {
                Parser pars = new Parser();
                if (!JSonFormat) {
                    temps = pars.parsejaXml(result);
                    Bloc temp = new Bloc("data i hora", "22", "hot", "100");
                    temps.add(temp);
                } else {
                    temps = pars.parsejaJSon(result);
                    Bloc temp = new Bloc("data i  hora", "22", "hot", "100");
                    temps.add(temp);
                }

                if (temps != null) {
                    tAdapter = new CustomAdapter(temps);
                    recyclerView.setAdapter(tAdapter);
                }

                //guardem temps a BBDD
                TemperaturesHelper2 temperaturesHelper2 = new TemperaturesHelper2(this);
                temperaturesHelper2.guarda(nomCiutat, temps);

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("test", "openWeather: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("test", "openWeather: " + e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("test", "openWeather: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("test", "openWeather: " + e.getMessage());
        }

    }

    class Descarregador extends AsyncTask<String, Void, String> {

        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;

        Descarregador(Context context) {
        }

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];

            String result = "";
            String inputLine;
            try {

                URL myUrl = new URL(stringUrl);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

                Log.d("test", stringUrl);

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                InputStreamReader streamReader
                        = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                connection.disconnect();
                result = stringBuilder.toString();

                Log.d("test", " resultat:" + result);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("teste", e.getMessage() + " " + e.getCause());

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            int id = item.getItemId();
            if (id == R.id.readMemInt) {

                FileInputStream fis = openFileInput("memint2.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();
                String lines;
                while ((lines = bufferedReader.readLine()) != null) {
                    stringBuffer.append(lines + "\n");

                }
                textView.setText(stringBuffer.toString());


                try {
                    BufferedReader fin = new BufferedReader(new InputStreamReader(
                            openFileInput("meminterna.txt")));
                    String line = fin.readLine();  //o bucle, .append
                    textView.setText(line);
                    fin.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("test", "Error: " + e.getMessage());

                }
                return true;

            } else if (id == R.id.readProgram) {
                //ex, descripcions de jugadors... dins el apk
                //no es pot escriure dins el apk en runtime, va signat.
                //data/data/my_apk/files/meminterna.txt
                // Device file explorer, abans android device monitor
                try {
                    InputStream fraw = getResources().openRawResource(R.raw.fraw);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fraw));
                    String line = null;
                    line = bufferedReader.readLine();
                    textView.setText(line);
                    fraw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return true;
            } else if (id == R.id.writeMemInt) {

                String myMess = editTextCity.getText().toString();
                FileOutputStream fout2 = openFileOutput("memint2.txt", Context.MODE_PRIVATE);
                fout2.write("el que jo li passi per editText".getBytes());
                //fout2.write(myMess.getBytes());
                fout2.close();


                try {
                    OutputStreamWriter fout = new OutputStreamWriter(
                            openFileOutput("meminterna.txt", Context.MODE_PRIVATE));
                    fout.write("Contingut del fitxer de mem. interna");
                    fout.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("test", e.getMessage() + " " + e.getCause());

                }
                return true;

            } else if (id == R.id.readSD) {
                if (sdAvailable) {
                    try {
                        File ruta_sd = Environment.getExternalStorageDirectory();
                        File f = new File(ruta_sd.getAbsolutePath(), "filesd.txt");
                        BufferedReader fin = new BufferedReader(new InputStreamReader(
                                new FileInputStream(f)));
                        String line = fin.readLine();
                        textView.setText(line);
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else Toast.makeText(this, "no és sdAvailable", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.writeSD) {
                if (sdAvailable && sdWriteAccess) {
                    try {
                        File ruta_sd = Environment.getExternalStorageDirectory();
                        File f = new File(ruta_sd.getAbsolutePath(), "filesd.txt");
                        OutputStreamWriter fout = null;
                        try {
                            fout = new OutputStreamWriter(
                                    new FileOutputStream(f));
                            Log.d("testtotok", ruta_sd.getAbsolutePath());

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.d("testSDD", e.getMessage() + e.getCause());

                        }
                        fout.write("Contingut del fitxer de la SD");
                        fout.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("testSD", e.getMessage() + e.getCause());
                        //Toast.makeText(this, String.valueOf(sdAvailable)+"," + String.valueOf(sdWriteAccess), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            } else if (id == R.id.openWeather) {
                //exemple HttpURLConnection
                //ha de ser crida AsyncTask desde versió 4 Android
                openWeather(false);
                return true;

            }
            return super.onOptionsItemSelected(item);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("onOptionsItemSelected: ", e.getMessage() + " " + e.getCause());
        }
        return true;
    }


}

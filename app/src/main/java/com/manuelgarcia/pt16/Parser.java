package com.manuelgarcia.pt16;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class Parser extends AppCompatActivity {

    public List<Bloc> parsejaJSon(String json) throws JSONException {

        String time = null;
        String temperature = null;
        String calorFred = null;
        Bloc temp;
        String humid = null;

        List<Bloc> llista = new ArrayList<>();
        String direccion = "";

        //Creem un objecte JSONObject para poder acceder als atributs o camps
        JSONObject respuestaJSON = null;   //Creo un JSONObject a partir del StringBuilder passat
        try {
            respuestaJSON = new JSONObject(json);

            JSONArray resultsJSON = respuestaJSON.getJSONArray("list");

            if (resultsJSON.length() > 0) {
                Integer integer = 0;
                while (integer < resultsJSON.length()) {
                    temperature = resultsJSON.getJSONObject(integer).getJSONObject("main").getString("temp");

                    humid = resultsJSON.getJSONObject(integer).getJSONObject("main").getString("humidity");
                    String dia = resultsJSON.getJSONObject(integer).getString("dt_txt").substring(8, 10);
                    String mes = resultsJSON.getJSONObject(integer).getString("dt_txt").substring(5, 7);
                    String hora = resultsJSON.getJSONObject(integer).getString("dt_txt").substring(11, 16);
                    time = dia + " - " + mes + " | " + hora;
                    if (temperature != null) {
                        if (Double.parseDouble(temperature) >= 20) {
                            calorFred = "hot";
                        } else {
                            calorFred = "cold";
                        }
                    }

                    temp = new Bloc(time, temperature, calorFred, humid);
                    Log.d("JSON", "bloc " + String.valueOf(integer) + " " + time + " " + temperature + " " + humid + " " + calorFred);
                    llista.add(temp);
                    time = null;
                    humid = null;
                    temperature = null;
                    integer += 1;

                    Log.d("JSON", "dades " + direccion);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("test", "parsejaJSon: " + e.getMessage());
        } catch (Exception e) {
            Log.d("test", "exc: " + e.getMessage());

        }
        return llista;
    }

    public List<Bloc> parsejaXml(String xml) throws XmlPullParserException, IOException {

        String time = null;
        String temperature = null;
        String calorFred = null, humid = null;
        Bloc temp;

        List<Bloc> llista = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();


        xpp.setInput(new StringReader(xml));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("time")) {
                    time = xpp.getAttributeValue(null, "from");
                }
                if (xpp.getName().equals("temperature")) {
                    temperature = xpp.getAttributeValue(null, "value");
                }
                if (xpp.getName().equals("humidity")) {
                    humid = xpp.getAttributeValue(null, "value");
                }

                if (temperature != null) {
                    if (Double.parseDouble(temperature) >= 20) {
                        calorFred = "hot";
                    } else {
                        calorFred = "cold";
                    }
                }

                if (time != null && temperature != null) {
                    try {
                        time = time.replace("T", " ");
                        temp = new Bloc(time, temperature, calorFred, humid);
                        // Log.d("test", "parsejant "+time+temperature+calorFred+humid);
                        llista.add(temp);
                        time = null;
                        temperature = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("test", "parsejaXml: " + e.getMessage());
                    }
                }
            }

            eventType = xpp.next();

        }
        if (llista == null) Log.d("test", "llista nula");
        return llista;
    }

}

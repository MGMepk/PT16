package com.manuelgarcia.pt16;

public class Bloc {

    /* A la PT16, la classe s'ha de dir Bloc */

    private String data;
    private String tempe;
    private String imatge;
    private String humidity;


    Bloc(String data, String tempe, String imatge, String humid) {
        {
        }

        this.data = data;
        this.tempe = tempe;
        this.imatge = imatge;
        this.humidity = humid;

    }

    String getHumidity() {
        return humidity;
    }

    void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    String getData() {
        return data;
    }

    String getTempe() {
        return tempe;
    }

    String getImatge() {
        return imatge;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTempe(String tempe) {
        this.tempe = tempe;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }
}

package com.manuelgarcia.pt16;

class TemperaturesContract {
    private TemperaturesContract() {

    }

    static final String TABLE_NAME = "openWeather";
    static final String NOMBRE_COLUMNA_NOMCIUTAT = "Nom";
    static final String NOMBRE_COLUMNA_HORES = "Hores";
    static final String NOMBRE_COLUMNA_TEMPS = "Temps";


    static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TemperaturesContract.TABLE_NAME + " (" +
            TemperaturesContract.NOMBRE_COLUMNA_NOMCIUTAT + " TEXT," +
            TemperaturesContract.NOMBRE_COLUMNA_HORES + " TEXT," +
            TemperaturesContract.NOMBRE_COLUMNA_TEMPS + " TEXT)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TemperaturesContract.TABLE_NAME;

}

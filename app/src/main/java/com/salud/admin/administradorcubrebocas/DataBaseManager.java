package com.salud.admin.administradorcubrebocas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBaseManager {

    public static final String TABLE_NAME_USUARIOS = "USUARIOS";
    public static final String COLUMN_ID_USUARIO = "_id";
    public static final String COLUMN_NAME_USUARIO = "nombre";
    public static final String COLUMN_FAVORITE_USUARIO = "favorite";

    public static final String CREATE_TABLE_USUARIO = "create table " + TABLE_NAME_USUARIOS + " ("
            + COLUMN_ID_USUARIO + " integer primary key autoincrement,"
            + COLUMN_NAME_USUARIO + " text not null,"
            + COLUMN_FAVORITE_USUARIO + " INTEGER not null DEFAULT 0);";

    public static final String DROP_TABLE_USUARIO = "DROP TABLE IF EXISTS " + TABLE_NAME_USUARIOS + ";";


    public static final String TABLE_NAME_CUBREBOCAS = "CUBREBOCAS";
    public static final String COLUMN_ID_CUBREBOCAS = "_id";
    public static final String COLUMN_USUARIO_CUBREBOCAS = "usuario";
    public static final String COLUMN_DESC_CUBREBOCAS = "descripcion";
    public static final String COLUMN_IMAGEN_CUBREBOCAS = "imagen";
    public static final String COLUMN_TIPO_CUBREBOCAS = "tipo";
    public static final String COLUMN_MAX_USOS_CUBREBOCAS = "max_usos";

    public static final String CREATE_TABLE_CUBREBOCAS = "create table " + TABLE_NAME_CUBREBOCAS + " ("
            + COLUMN_ID_CUBREBOCAS + " integer primary key autoincrement,"
            + COLUMN_USUARIO_CUBREBOCAS + " integer not null,"
            + COLUMN_DESC_CUBREBOCAS + " text not null,"
            + COLUMN_TIPO_CUBREBOCAS + " text not null,"
            + COLUMN_IMAGEN_CUBREBOCAS + " text not null,"
            + COLUMN_MAX_USOS_CUBREBOCAS + " INTEGER DEFAULT 0,"
            + " FOREIGN KEY (" + COLUMN_USUARIO_CUBREBOCAS + ") REFERENCES " + TABLE_NAME_USUARIOS + "(" + COLUMN_ID_USUARIO + "));";

    public static final String DROP_TABLE_CUBREBOCAS = "DROP TABLE IF EXISTS " + TABLE_NAME_CUBREBOCAS + ";";


    public static final String TABLE_NAME_USO = "USOS";
    public static final String COLUMN_ID_USO = "_id";
    public static final String COLUMN_CUBREBOCAS_USO = "cubrebocas";
    public static final String COLUMN_FECHA_HORA_USO = "fecha_hora";
    public static final String COLUMN_LAVADA_USO = "lavada";

    public static final String CREATE_TABLE_USO = "create table " + TABLE_NAME_USO + " ("
            + COLUMN_ID_USO + " integer primary key autoincrement,"
            + COLUMN_CUBREBOCAS_USO + " integer NOT NULL,"
            + COLUMN_FECHA_HORA_USO + " DATETIME NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime')),"
            + COLUMN_LAVADA_USO + " INTEGER not null DEFAULT 0,"
            + " FOREIGN KEY (" + COLUMN_CUBREBOCAS_USO + ") REFERENCES " + TABLE_NAME_CUBREBOCAS + "(" + COLUMN_ID_CUBREBOCAS + "));";

    public static final String DROP_TABLE_USO = "DROP TABLE IF EXISTS " + TABLE_NAME_USO + ";";


    public static final String TABLE_NAME_CONFIGURACION = "CONFIGURACION";
    public static final String COLUMN_ID_CONFIGURACION = "_id";
    public static final String COLUMN_ACTIVAR_RECORDATORIO = "activar_recordatorio";
    public static final String COLUMN_DISTANCIA_RECORDATORIO = "distacia_recordatorio";

    public static final String CREATE_TABLE_CONFIGURACION = "create table " + TABLE_NAME_CONFIGURACION + " ("
            + COLUMN_ID_CONFIGURACION + " integer primary key autoincrement,"
            + COLUMN_ACTIVAR_RECORDATORIO + " INTEGER not null DEFAULT 0,"
            + COLUMN_DISTANCIA_RECORDATORIO + " INTEGER not null DEFAULT 0);";

    public static final String DROP_TABLE_CONFIGURACION = "DROP TABLE IF EXISTS " + TABLE_NAME_CONFIGURACION + ";";


    public static final String TABLE_NAME_PUNTOS_RECORDATORIO = "PUNTOS_RECORDATORIO";
    public static final String COLUMN_ID_PUNTO_RECORDATORIO = "_id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_LATITUD = "latitud";
    public static final String COLUMN_LONGITUD = "longitud";

    public static final String CREATE_TABLE_PUNTOS_RECORDATORIO = "create table " + TABLE_NAME_PUNTOS_RECORDATORIO + " ("
            + COLUMN_ID_PUNTO_RECORDATORIO + " integer primary key autoincrement,"
            + COLUMN_TITULO + " TEXT not null,"
            + COLUMN_LATITUD + " REAL not null DEFAULT 0,"
            + COLUMN_LONGITUD + " REAL not null DEFAULT 0);";

    public static final String DROP_TABLE_PUNTOS_RECORDATORIO = "DROP TABLE IF EXISTS " + TABLE_NAME_PUNTOS_RECORDATORIO + ";";


    private DBHelper helper;

    private SQLiteDatabase db;

    public DataBaseManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }


    private ContentValues generarContentValuesUsuario(String nombre, int favorite) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_NAME_USUARIO, nombre);
        valores.put(COLUMN_FAVORITE_USUARIO, favorite);
        return valores;
    }

    public DBHelper getHelper() {
        return helper;
    }

    public void insertarUsuario(String nombre, int favorite) {
        db.insert(TABLE_NAME_USUARIOS, null, generarContentValuesUsuario(nombre, favorite));
    }

    public void insertarUsuario2(String nombre, int favorite) {
        db.execSQL("insert into " + TABLE_NAME_USUARIOS + " (" + COLUMN_NAME_USUARIO + ", " + COLUMN_FAVORITE_USUARIO + ") values ('" + nombre + "', '" + favorite + "');");
    }

    public void eliminarUsuarioTodo() {
        db.delete(TABLE_NAME_USUARIOS, null, null);
    }

    public void eliminarUsuario(String nombre) {
        db.delete(TABLE_NAME_USUARIOS, COLUMN_NAME_USUARIO + "=?", new String[]{nombre});
    }

    public void eliminarUsuarioPorId(int id) {
        db.delete(TABLE_NAME_USUARIOS, COLUMN_ID_USUARIO + "=?", new String[]{"" + id});
    }

    public void modificarUsuario(String nombre, int favorite) {
        db.update(TABLE_NAME_USUARIOS, generarContentValuesUsuario(nombre, favorite), COLUMN_NAME_USUARIO + "=?", new String[]{nombre});
    }

    public void modificarUsuarioPorID(int id, String nombre, int favorite) {
        db.update(TABLE_NAME_USUARIOS, generarContentValuesUsuario(nombre, favorite), COLUMN_ID_USUARIO + "=?", new String[]{"" + id});
    }

    public void todosUsuariosComoNoFavoritos() {
        db.execSQL("UPDATE " + TABLE_NAME_USUARIOS + " SET " + COLUMN_FAVORITE_USUARIO + " = 0;");
    }

    public Cursor cargarCursorUsuarios() {
        return db.query(TABLE_NAME_USUARIOS, new String[]{COLUMN_ID_USUARIO, COLUMN_NAME_USUARIO, COLUMN_FAVORITE_USUARIO}, null, null, null, null, null);
    }

    public Cursor buscarUsuario(String nombre) {
        return db.query(TABLE_NAME_USUARIOS, new String[]{COLUMN_ID_USUARIO, COLUMN_NAME_USUARIO, COLUMN_FAVORITE_USUARIO}, COLUMN_NAME_USUARIO + "=?", new String[]{nombre}, null, null, null);
    }

    public Cursor buscarUsuarioPorID(int id) {
        return db.query(TABLE_NAME_USUARIOS, new String[]{COLUMN_ID_USUARIO, COLUMN_NAME_USUARIO, COLUMN_FAVORITE_USUARIO}, COLUMN_ID_USUARIO + "=?", new String[]{"" + id}, null, null, null);
    }

    public Cursor buscarUsuarioFavorito() {
        return db.query(TABLE_NAME_USUARIOS, new String[]{COLUMN_ID_USUARIO, COLUMN_NAME_USUARIO, COLUMN_FAVORITE_USUARIO}, COLUMN_FAVORITE_USUARIO + "= 1", null, null, null, null);
    }

    public Cursor buscarUsuarioRandom() {
        return db.query(TABLE_NAME_USUARIOS, new String[]{COLUMN_ID_USUARIO, COLUMN_NAME_USUARIO, COLUMN_FAVORITE_USUARIO}, null, null, null, null, "RANDOM() LIMIT 1");
    }


    private ContentValues generarContentValuesCubrebocas(int usuario, String descripcion, String imagen, String tipo, int max_usos) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_USUARIO_CUBREBOCAS, usuario);
        valores.put(COLUMN_DESC_CUBREBOCAS, descripcion);
        valores.put(COLUMN_IMAGEN_CUBREBOCAS, imagen);
        valores.put(COLUMN_TIPO_CUBREBOCAS, tipo);
        valores.put(COLUMN_MAX_USOS_CUBREBOCAS, max_usos);
        return valores;
    }

    public void insertaarCubrebocas(int usuario, String descripcion, String imagen, String tipo, int max_usos) {
        db.insert(TABLE_NAME_CUBREBOCAS, null, generarContentValuesCubrebocas(usuario, descripcion, imagen, tipo, max_usos));
    }

    public void insertarCubrebocas2(int usuario, String descripcion, String imagen, String tipo, int max_usos) {
        db.execSQL("insert into " + TABLE_NAME_CUBREBOCAS + " (" + COLUMN_USUARIO_CUBREBOCAS + ", " + COLUMN_DESC_CUBREBOCAS + ", " + COLUMN_IMAGEN_CUBREBOCAS + ", " + COLUMN_TIPO_CUBREBOCAS + ", " + COLUMN_MAX_USOS_CUBREBOCAS + ") values (" + usuario + ", '" + descripcion + "', '" + imagen + "', '" + tipo + "', " + max_usos + ");");
    }

    public void eliminarCubrebocasTodo() {
        db.delete(TABLE_NAME_CUBREBOCAS, null, null);
    }

    public void eliminarCubrebocasPorId(int id) {
        db.delete(TABLE_NAME_CUBREBOCAS, COLUMN_ID_CUBREBOCAS + "=?", new String[]{"" + id});
    }

    public void modificarCubrebocasPorId(int id, int usuario, String descripcion, String imagen, String tipo, int max_usos) {
        db.update(TABLE_NAME_CUBREBOCAS, generarContentValuesCubrebocas(usuario, descripcion, imagen, tipo, max_usos), COLUMN_ID_CUBREBOCAS + "=?", new String[]{"" + id});
    }

    public Cursor cargarCursorCubrebocas() {
        return db.query(TABLE_NAME_CUBREBOCAS, new String[]{COLUMN_ID_CUBREBOCAS, COLUMN_USUARIO_CUBREBOCAS, COLUMN_DESC_CUBREBOCAS, COLUMN_IMAGEN_CUBREBOCAS, COLUMN_TIPO_CUBREBOCAS, COLUMN_MAX_USOS_CUBREBOCAS}, null, null, null, null, null);
    }

    public Cursor cargrCubrebocasPorUsuario(int id_usuario) {
        return db.query(TABLE_NAME_CUBREBOCAS, new String[]{COLUMN_ID_CUBREBOCAS, COLUMN_USUARIO_CUBREBOCAS, COLUMN_DESC_CUBREBOCAS, COLUMN_IMAGEN_CUBREBOCAS, COLUMN_TIPO_CUBREBOCAS, COLUMN_MAX_USOS_CUBREBOCAS}, COLUMN_USUARIO_CUBREBOCAS + "=?", new String[]{"" + id_usuario}, null, null, null);
    }

    public Cursor buscarCubrebocas(String descripcion) {
        return db.query(TABLE_NAME_CUBREBOCAS, new String[]{COLUMN_ID_CUBREBOCAS, COLUMN_USUARIO_CUBREBOCAS, COLUMN_DESC_CUBREBOCAS, COLUMN_IMAGEN_CUBREBOCAS, COLUMN_TIPO_CUBREBOCAS, COLUMN_MAX_USOS_CUBREBOCAS}, COLUMN_DESC_CUBREBOCAS + "=?", new String[]{descripcion}, null, null, null);
    }

    public Cursor buscarCubrebocasPorID(int id) {
        return db.query(TABLE_NAME_CUBREBOCAS, new String[]{COLUMN_ID_CUBREBOCAS, COLUMN_USUARIO_CUBREBOCAS, COLUMN_DESC_CUBREBOCAS, COLUMN_IMAGEN_CUBREBOCAS, COLUMN_TIPO_CUBREBOCAS, COLUMN_MAX_USOS_CUBREBOCAS}, COLUMN_ID_CUBREBOCAS + "=?", new String[]{"" + id}, null, null, null);
    }

    public Cursor buscarCubrebocasRandom() {
        return db.query(TABLE_NAME_CUBREBOCAS, new String[]{COLUMN_ID_CUBREBOCAS, COLUMN_USUARIO_CUBREBOCAS, COLUMN_DESC_CUBREBOCAS, COLUMN_IMAGEN_CUBREBOCAS, COLUMN_TIPO_CUBREBOCAS, COLUMN_MAX_USOS_CUBREBOCAS}, null, null, null, null, "RANDOM() LIMIT 1");
    }


    private ContentValues generarContentValuesUso(int cubrebocas, Date fecha_hora, int lavada) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_CUBREBOCAS_USO, cubrebocas);
        valores.put(COLUMN_FECHA_HORA_USO, String.valueOf(fecha_hora));
        valores.put(COLUMN_LAVADA_USO, lavada);
        return valores;
    }

    public void insertarUso(int cubrebocas, Date fecha_hora, int lavada) {
        db.insert(TABLE_NAME_CUBREBOCAS, null, generarContentValuesUso(cubrebocas, fecha_hora, lavada));
    }

    public void insertarUso2(int cubrebocas, Date fecha_hora, int lavada) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fecha = formatter.format(fecha_hora);
        db.execSQL("insert into " + TABLE_NAME_USO + " (" + COLUMN_CUBREBOCAS_USO + ", " + COLUMN_FECHA_HORA_USO + ", " + COLUMN_LAVADA_USO + ") values (" + cubrebocas + ", '" + fecha + "', " + lavada + ");");
    }

    public void eliminarUsoTodo() {
        db.delete(TABLE_NAME_USO, null, null);
    }

    public void eliminarUsoPorId(int id) {
        db.delete(TABLE_NAME_USO, COLUMN_ID_USO + "=?", new String[]{"" + id});
    }

    public void modificarUsoPorId(int id, int cubrebocas, Date fecha_hora, int lavada) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fecha = formatter.format(fecha_hora);
        db.update(TABLE_NAME_USO, generarContentValuesUso(cubrebocas, fecha_hora, lavada), COLUMN_ID_USO + "=?", new String[]{"" + id});
    }

    public void modificarUsoPorId2(int id, int cubrebocas, Date fecha_hora, int lavada) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fecha = formatter.format(fecha_hora);
        String sql = "update " + TABLE_NAME_USO + " set " + COLUMN_CUBREBOCAS_USO + " = " + cubrebocas + ", " + COLUMN_FECHA_HORA_USO + " = '" + fecha + "', " + COLUMN_LAVADA_USO + " = " + lavada + " where " + COLUMN_ID_USO + " = " + id + ";";
        db.execSQL(sql);
    }

    public Cursor cargarCursorUso() {
        return db.query(TABLE_NAME_USO, new String[]{COLUMN_ID_USO, COLUMN_CUBREBOCAS_USO, COLUMN_FECHA_HORA_USO, COLUMN_LAVADA_USO}, null, null, null, null, null);
    }

    public Cursor cargarCursorUsoPorCubrebocas(int id_cubrebocas) {
        return db.query(TABLE_NAME_USO, new String[]{COLUMN_ID_USO, COLUMN_CUBREBOCAS_USO, COLUMN_FECHA_HORA_USO, COLUMN_LAVADA_USO}, COLUMN_CUBREBOCAS_USO + "=?", new String[]{"" + id_cubrebocas}, null, null, null, null);
    }

    public Cursor buscarUso(Date fecha_hora) {
        return db.query(TABLE_NAME_USO, new String[]{COLUMN_ID_USO, COLUMN_CUBREBOCAS_USO, COLUMN_FECHA_HORA_USO, COLUMN_LAVADA_USO}, COLUMN_FECHA_HORA_USO + "=?", new String[]{String.valueOf(fecha_hora)}, null, null, null);
    }

    public Cursor buscarUsoPorID(int id) {
        return db.query(TABLE_NAME_USO, new String[]{COLUMN_ID_USO, COLUMN_CUBREBOCAS_USO, COLUMN_FECHA_HORA_USO, COLUMN_LAVADA_USO}, COLUMN_ID_USO + "=?", new String[]{"" + id}, null, null, null);
    }

    public Cursor buscarUsoPorCubrebocas(int cubrebocas) {
        return db.query(TABLE_NAME_USO, new String[]{COLUMN_ID_USO, COLUMN_CUBREBOCAS_USO, COLUMN_FECHA_HORA_USO, COLUMN_LAVADA_USO}, COLUMN_CUBREBOCAS_USO + "=?", new String[]{"" + cubrebocas}, null, null, null);
    }

    public Cursor buscarUsoRandom() {
        return db.query(TABLE_NAME_USO, new String[]{COLUMN_ID_USO, COLUMN_CUBREBOCAS_USO, COLUMN_FECHA_HORA_USO, COLUMN_LAVADA_USO}, null, null, null, null, "RANDOM() LIMIT 1");
    }


    private ContentValues generarContentValuesConfiguracion(int activar_recordatorio, int distancia_recordatorio) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_ACTIVAR_RECORDATORIO, activar_recordatorio);
        valores.put(COLUMN_DISTANCIA_RECORDATORIO, distancia_recordatorio);
        return valores;
    }

    public void insertarConfiguracion(int activar_recordatorio, int distancia_recordatorio) {
        db.insert(TABLE_NAME_CONFIGURACION, null, generarContentValuesConfiguracion(activar_recordatorio, distancia_recordatorio));
    }

    public void insertarConfiguracion2(int activar_recordatorio, int distancia_recordatorio) {
        db.execSQL("insert into " + TABLE_NAME_CONFIGURACION + " (" + COLUMN_ACTIVAR_RECORDATORIO + ", " + COLUMN_DISTANCIA_RECORDATORIO + ") values (" + activar_recordatorio + ", " + distancia_recordatorio + ");");
    }

    public void eliminarConfiguracionTodo() {
        db.delete(TABLE_NAME_CONFIGURACION, null, null);
    }

    public void eliminarConfiguracionPorId(int id) {
        db.delete(TABLE_NAME_CONFIGURACION, COLUMN_ID_CONFIGURACION + "=?", new String[]{"" + id});
    }

    public void modificarConfiguracionPorId(int id, int activar_recordatorio, int distancia_recordatorio) {
        db.update(TABLE_NAME_CONFIGURACION, generarContentValuesConfiguracion(activar_recordatorio, distancia_recordatorio), COLUMN_ID_CONFIGURACION + "=?", new String[]{"" + id});
    }

    public void modificarConfiguracionPorId2(int id, int activar_recordatorio, int distancia_recordatorio) {
        String sql = "update " + TABLE_NAME_CONFIGURACION + " set " + COLUMN_ACTIVAR_RECORDATORIO + " = " + activar_recordatorio + ", " + COLUMN_DISTANCIA_RECORDATORIO + " = " + distancia_recordatorio + " where " + COLUMN_ID_CONFIGURACION + " = " + id + ";";
        db.execSQL(sql);
    }

    public Cursor cargarCursorConfiguracion() {
        return db.query(TABLE_NAME_CONFIGURACION, new String[]{COLUMN_ID_CONFIGURACION, COLUMN_ACTIVAR_RECORDATORIO, COLUMN_DISTANCIA_RECORDATORIO}, null, null, null, null, null);
    }

    public Cursor buscarConfiguracionPorID(int id) {
        return db.query(TABLE_NAME_CONFIGURACION, new String[]{COLUMN_ID_CONFIGURACION, COLUMN_ACTIVAR_RECORDATORIO, COLUMN_DISTANCIA_RECORDATORIO}, COLUMN_ID_CONFIGURACION + "=?", new String[]{"" + id}, null, null, null);
    }

    public Cursor buscarConfiguracionRandom() {
        return db.query(TABLE_NAME_CONFIGURACION, new String[]{COLUMN_ID_CONFIGURACION, COLUMN_ACTIVAR_RECORDATORIO, COLUMN_DISTANCIA_RECORDATORIO}, null, null, null, null, "RANDOM() LIMIT 1");
    }


    private ContentValues generarContentValuesPuntosRecordatorio(String titulo, double latitud, double longitud) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_TITULO, titulo);
        valores.put(COLUMN_LATITUD, latitud);
        valores.put(COLUMN_LONGITUD, longitud);
        return valores;
    }

    public void insertarPuntosRecordatorio(String titulo, double latitud, double longitud) {
        db.insert(TABLE_NAME_PUNTOS_RECORDATORIO, null, generarContentValuesPuntosRecordatorio(titulo, latitud, longitud));
    }

    public void insertarPuntosRecordatorio2(String titulo, double latitud, double longitud) {
        db.execSQL("insert into " + TABLE_NAME_PUNTOS_RECORDATORIO + " (" + COLUMN_TITULO + ", " + COLUMN_LATITUD + ", " + COLUMN_LONGITUD + ") values ('" + titulo + "', " + latitud + ", " + longitud + ");");
    }

    public void eliminarPuntosRecordatorioTodo() {
        db.delete(TABLE_NAME_PUNTOS_RECORDATORIO, null, null);
    }

    public void eliminarPuntosRecordatorioPorId(int id) {
        db.delete(TABLE_NAME_PUNTOS_RECORDATORIO, COLUMN_ID_PUNTO_RECORDATORIO + "=?", new String[]{"" + id});
    }

    public void eliminarPuntosRecordatorioPorLatLong(double latitud, double longitud) {
        db.delete(TABLE_NAME_PUNTOS_RECORDATORIO, COLUMN_LATITUD + "=? and " + COLUMN_LONGITUD + "=?", new String[]{"" + latitud, "" + longitud});
    }

    public void modificarPuntosRecordatorioPorId(int id, String titulo, double latitud, double longitud) {
        db.update(TABLE_NAME_PUNTOS_RECORDATORIO, generarContentValuesPuntosRecordatorio(titulo, latitud, longitud), COLUMN_ID_PUNTO_RECORDATORIO + "=?", new String[]{"" + id});
    }

    public void modificarPuntosRecordatorioPorLatLong(String titulo, double latitud, double longitud) {
        db.update(TABLE_NAME_PUNTOS_RECORDATORIO, generarContentValuesPuntosRecordatorio(titulo, latitud, longitud), COLUMN_LATITUD + "=? and " + COLUMN_LONGITUD + "=?", new String[]{"" + latitud, "" + longitud});
    }

    public void modificarPuntosRecordatorioPorId2(int id, String titulo, double latitud, double longitud) {
        String sql = "update " + TABLE_NAME_PUNTOS_RECORDATORIO + " set " + COLUMN_TITULO + " = '" + titulo + "', " + COLUMN_LATITUD + " = " + latitud + ", " + COLUMN_LONGITUD + " = " + longitud + " where " + COLUMN_ID_PUNTO_RECORDATORIO + " = " + id + ";";
        db.execSQL(sql);
    }

    public void modificarPuntosRecordatorioPorLatLong2(String titulo, double latitud, double longitud) {
        String sql = "update " + TABLE_NAME_PUNTOS_RECORDATORIO + " set " + COLUMN_TITULO + " = '" + titulo + "', " + COLUMN_LATITUD + " = " + latitud + ", " + COLUMN_LONGITUD + " = " + longitud + " where " + COLUMN_LATITUD + " = " + latitud + " and " + COLUMN_LONGITUD + " = " + longitud + ";";
        db.execSQL(sql);
    }

    public Cursor cargarCursorPuntosRecordatorio() {
        return db.query(TABLE_NAME_PUNTOS_RECORDATORIO, new String[]{COLUMN_ID_PUNTO_RECORDATORIO, COLUMN_TITULO, COLUMN_LATITUD, COLUMN_LONGITUD}, null, null, null, null, null);
    }

    public Cursor buscarPuntosRecordatorioPorID(int id) {
        return db.query(TABLE_NAME_PUNTOS_RECORDATORIO, new String[]{COLUMN_ID_PUNTO_RECORDATORIO, COLUMN_TITULO, COLUMN_LATITUD, COLUMN_LONGITUD}, COLUMN_ID_PUNTO_RECORDATORIO + "=?", new String[]{"" + id}, null, null, null);
    }

    public Cursor buscarPuntosRecordatorioRandom() {
        return db.query(TABLE_NAME_PUNTOS_RECORDATORIO, new String[]{COLUMN_ID_PUNTO_RECORDATORIO, COLUMN_TITULO, COLUMN_LATITUD, COLUMN_LONGITUD}, null, null, null, null, "RANDOM() LIMIT 1");
    }

}
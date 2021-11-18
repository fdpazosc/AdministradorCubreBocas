package com.salud.admin.administradorcubrebocas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ADMIN_CUBREBOCAS.db";
    private static final int DB_VERSION = 1;
    private Context contexto;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseManager.CREATE_TABLE_USUARIO);
        db.execSQL(DataBaseManager.CREATE_TABLE_CUBREBOCAS);
        db.execSQL(DataBaseManager.CREATE_TABLE_USO);
        db.execSQL(DataBaseManager.CREATE_TABLE_PUNTOS_RECORDATORIO);
        db.execSQL(DataBaseManager.CREATE_TABLE_CONFIGURACION);

        /*InputStream is = null;
        try {
            is = contexto.getAssets().open("data.sql");
            if (is != null) {
                db.beginTransaction();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while (!TextUtils.isEmpty(line)) {
                    db.execSQL(line);
                    line = reader.readLine();
                }
                db.setTransactionSuccessful();
            }
        } catch (Exception ex) {
            Log.d("actriz", ""+ex.getMessage());
        } finally {
            db.endTransaction();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d("actriz", ""+e.getMessage());
                }
            }
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DataBaseManager.DROP_TABLE_USO);
        db.execSQL(DataBaseManager.DROP_TABLE_CUBREBOCAS);
        db.execSQL(DataBaseManager.DROP_TABLE_USUARIO);
        db.execSQL(DataBaseManager.DROP_TABLE_PUNTOS_RECORDATORIO);
        db.execSQL(DataBaseManager.DROP_TABLE_CONFIGURACION);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
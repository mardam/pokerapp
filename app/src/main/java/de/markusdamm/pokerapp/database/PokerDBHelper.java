package de.markusdamm.pokerapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.markusdamm.pokerapp.R;

/**
 * Created by Markus Damm on 28.03.2015.
 */
public class PokerDBHelper extends SQLiteOpenHelper {
    private Context context;

    public PokerDBHelper(Context context) {
        super(
                context,
                context.getResources().getString(R.string.dbname),
                null,
                Integer.parseInt(context.getResources().getString(R.string.version)));
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sql: context.getResources().getStringArray(R.array.create)){
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

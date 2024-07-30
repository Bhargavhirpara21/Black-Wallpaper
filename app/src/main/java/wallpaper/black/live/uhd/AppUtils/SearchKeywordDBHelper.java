package wallpaper.black.live.uhd.AppUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import wallpaper.black.live.uhd.Model.SearchKeywordModel;

public class SearchKeywordDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "searchdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myhistory";
    private static final String ID_COL = "id";
    private static final String KEYWORDS = "keywords";


    public SearchKeywordDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEYWORDS + " TEXT)";
        db.execSQL(query);
    }

    public int InsertKeyWord(String search_last) {
        int id;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEYWORDS,search_last);
        id= (int) db.insert(TABLE_NAME,null,values);
        db.close();
        return id;
    }

    public ArrayList<SearchKeywordModel> getAllKeywordData() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorKeyword = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<SearchKeywordModel> keywordList = new ArrayList<>();

        if (cursorKeyword.moveToFirst()) {
            do {
                keywordList.add(new SearchKeywordModel(cursorKeyword.getInt(0)
                        ,cursorKeyword.getString(1)));
            } while (cursorKeyword.moveToNext());
        }

        cursorKeyword.close();
        return keywordList;
    }

    public Integer deleteKeyWord (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

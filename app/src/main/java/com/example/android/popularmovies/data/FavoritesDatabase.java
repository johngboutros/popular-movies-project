package com.example.android.popularmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by john on 01/04/18.
 */

@Database(entities = {Movie.class}, version = 1)
public abstract class FavoritesDatabase extends RoomDatabase {

}

//package com.example.android.popularmovies.data;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.provider.BaseColumns;
//
//import net.simonvt.schematic.annotation.AutoIncrement;
//import net.simonvt.schematic.annotation.DataType;
//import net.simonvt.schematic.annotation.Database;
//import net.simonvt.schematic.annotation.ExecOnCreate;
//import net.simonvt.schematic.annotation.IfNotExists;
//import net.simonvt.schematic.annotation.NotNull;
//import net.simonvt.schematic.annotation.OnConfigure;
//import net.simonvt.schematic.annotation.OnCreate;
//import net.simonvt.schematic.annotation.OnUpgrade;
//import net.simonvt.schematic.annotation.PrimaryKey;
//import net.simonvt.schematic.annotation.Table;
//
//import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
//import static net.simonvt.schematic.annotation.DataType.Type.REAL;
//import static net.simonvt.schematic.annotation.DataType.Type.TEXT;
//
///**
// * Created by john on 01/04/18.
// */
//
//@Database(version = FavoritesDatabase.VERSION)
//public final class FavoritesDatabase {
//
//    private FavoritesDatabase() {
//    }
//
//    public static final int VERSION = 1;
//
//    @Table(FavoriteColumns.class)
//    @IfNotExists
//    public static final String FAVORITES = "favorites";
//
//    public interface FavoriteColumns {
//
//        @DataType(INTEGER)
//        @PrimaryKey
//        @AutoIncrement
//        String _ID = BaseColumns._ID;
//
//        @DataType(INTEGER)
//        @NotNull
//        String UID = "uid";
//
//        @DataType(TEXT)
//        @NotNull
//        String TITLE = "title";
//
//        @DataType(TEXT)
//        String POSTER_PATH = "posterPath";
//
//        @DataType(TEXT)
//        String OVERVIEW = "overview";
//
//        @DataType(TEXT)
//        String RELEASE_DATE = "releaseDate";
//
//        @DataType(REAL)
//        String VOTE_AVERAGE = "voteAverage";
//
//        @DataType(TEXT)
//        String CREATION_DATE = "creationDate";
//
//    }
//
//    @OnCreate
//    public static void onCreate(Context context, SQLiteDatabase db) {
//    }
//
//    @OnUpgrade
//    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
//                                 int newVersion) {
//    }
//
//    @OnConfigure
//    public static void onConfigure(SQLiteDatabase db) {
//    }
//
//    @ExecOnCreate
//    public static final String EXEC_ON_CREATE = "SELECT * FROM " + FAVORITES;
//}

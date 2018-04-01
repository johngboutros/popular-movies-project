package com.example.android.popularmovies;

import android.net.Uri;

import com.example.android.popularmovies.data.FavoritesDatabase;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by john on 01/04/18.
 */

@ContentProvider(authority = FavoritesProvider.AUTHORITY, database = FavoritesDatabase.class)
public final class FavoritesProvider {

    private FavoritesProvider(){
    }

    public static final String AUTHORITY = "com.example.android.popularmovies.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String FAVORITES_PATH = "favorites";

    @TableEndpoint(table = FavoritesDatabase.FAVORITES)
    public static class Favorites {

        @ContentUri(
                path = FAVORITES_PATH,
                type = "vnd.android.cursor.dir",
                defaultSort = FavoritesDatabase.FavoriteColumns.CREATION_DATE + " DESC")
        public static final Uri LIST =
                Uri.parse("content://" + AUTHORITY + "/" + FAVORITES_PATH);

        @InexactContentUri(
                path = FAVORITES_PATH + "/#",
                name = "UID",
                type = "vnd.android.cursor.item",
                whereColumn = FavoritesDatabase.FavoriteColumns.UID,
                pathSegment = 1)
        public static Uri withUid(long uid) {
            return Uri.parse("content://" + AUTHORITY + "/" + FAVORITES_PATH + "/" + uid);
        }
    }


}

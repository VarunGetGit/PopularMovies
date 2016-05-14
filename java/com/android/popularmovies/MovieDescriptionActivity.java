package com.android.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ebsd_vpuchnanda on 12/May/2016.
 */
public class MovieDescriptionActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieDescriptionActivity.class.getSimpleName();
    GridView mGridView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.movie_description);
            Intent intent = getIntent();


            ((ImageView) (findViewById(R.id.movie_backdropPoster))).setImageBitmap((Bitmap) intent.getParcelableExtra("BitmapImage"));

            ((TextView) (findViewById(R.id.movie_overview_label))).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) (findViewById(R.id.movie_overview))).setText(intent.getStringExtra("MoviesOverview"));

            ((TextView) (findViewById(R.id.movie_releasedate_label))).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) (findViewById(R.id.movie_releasedate))).setText(intent.getStringExtra("MovieReleaseDates"));

            ((TextView) (findViewById(R.id.movie_title_label))).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) (findViewById(R.id.movie_title))).setText(intent.getStringExtra("MovieTitles"));

            ((TextView) (findViewById(R.id.movie_popularity_label))).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) (findViewById(R.id.movie_popularity))).setText(intent.getStringExtra("MoviesPopularity"));

            ((TextView) (findViewById(R.id.movie_votecount_label))).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) (findViewById(R.id.movie_votecount))).setText(intent.getStringExtra("MoviesVoteCount"));

            ((TextView) (findViewById(R.id.movie_voteaverage_label))).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) (findViewById(R.id.movie_voteaverage))).setText(intent.getStringExtra("MoviesVoteAverage"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error occurred while displaying one of the movie descriptions", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error occurred while displaying one of the movie descriptions", e);
        }

    }

}

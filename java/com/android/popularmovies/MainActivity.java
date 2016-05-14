package com.android.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    GridView mGridView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.grid_viewmovies);
        // set mGridView adapter to our CursorAdapter
        ImageView imageView = (ImageView) findViewById(R.id.grid_viewimage);
        /*String[] sArray = new String[1];
        sArray[0]="1";*/

        new ImageDownloaderTask(imageView).execute("http://api.themoviedb.org/3/movie/popular?api_key=xyz");
        Log.i(LOG_TAG, "Created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "Started");
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Resumed");
        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "Paused");
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "Stopped");
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Destroyed");
        // The activity is about to be destroyed.
    }

    public class CustomGridAdapter extends BaseAdapter {

        private Bitmap[] bitmapItems;

        public CustomGridAdapter(Bitmap[] items) {

            this.bitmapItems = items;
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {

            try {
                ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.gridimage_viewitem, parent, false);
                imageView.setImageBitmap(bitmapItems[position]);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //v.inflate(getApplicationContext(), R.layout.movie_description, null);
                        Intent intent = new Intent(view.getContext(), MovieDescriptionActivity.class);
                        intent.putExtra("BitmapImage", bitMapMoviesBackdropPoster[position]);
                        intent.putExtra("MoviesOverview", sArrayMoviesOverview[position]);
                        intent.putExtra("MovieReleaseDates", sArrayMovieReleaseDates[position]);
                        intent.putExtra("MovieTitles", sArrayMovieTitles[position]);
                        intent.putExtra("MoviesPopularity", sArrayMoviesPopularity[position]);
                        intent.putExtra("MoviesVoteCount", sArrayMoviesVoteCount[position]);
                        intent.putExtra("MoviesVoteAverage", sArrayMoviesVoteAverage[position]);
                        startActivity(intent);
                        /*ImageView imageView = (ImageView) v.inflate(getApplicationContext(), R.layout.movie_description, null).findViewById(R.id.movie_backdropPoster);
                        imageView.setImageBitmap(bitMapMoviesBackdropPoster[position]);*/
                        //startActivity(imageView);
                    }
                });
                convertView = imageView;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error occurred while setting the movie image", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error occurred while setting the movie image", e);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return bitmapItems.length;
        }

        @Override
        public Object getItem(int position) {
            return bitmapItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap[]> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap[] doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        private Bitmap[] downloadBitmap(String url) {

            HttpURLConnection urlConnection = null;
            String sJsonMovies = null;
            BufferedReader reader = null;
            Bitmap[] bipMapMoviesPosters = null;
            try {

                urlConnection = urlConnect(url);
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    sJsonMovies = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                if (stringBuilder.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    sJsonMovies = null;
                }
                sJsonMovies = stringBuilder.toString();
                JSONObject popularMoviesJson = new JSONObject(sJsonMovies);
                if (!popularMoviesJson.getString("results").contains("Error:")) {
                    bipMapMoviesPosters = getMoviesPosterImageFromJson(sJsonMovies);
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred while accessing the popular movies results from the movies url", Toast.LENGTH_SHORT).show();
                    Throwable th = new Throwable();
                    Log.e(LOG_TAG, "Error occurred while accessing the popular movies results from the movies url", th);
                    sJsonMovies = null;
                }

            } catch (IOException e) {

                Toast.makeText(getApplicationContext(), "Error occurred while unable to connect with the URL", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error unable to connect", e);
                sJsonMovies = null;
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Error occurred due to break down in the process flow", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error in process", e);
                sJsonMovies = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error Occurred while closing the reader", e);
                        Toast.makeText(getApplicationContext(), "Error Occurred while closing the reader", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            return bipMapMoviesPosters;
        }


        private Bitmap[] getMoviesPosterImageFromJson(String sJsonMovies)
                throws JSONException {


            JSONObject popularMoviesJson = new JSONObject(sJsonMovies);
            JSONArray posterPathArray = popularMoviesJson.getJSONArray("results");


            Bitmap[] bitMapMoviesPosters = new Bitmap[posterPathArray.length()];
            bitMapMoviesBackdropPoster = new Bitmap[posterPathArray.length()];
            sArrayMoviesOverview = new String[posterPathArray.length()];
            sArrayMovieReleaseDates = new String[posterPathArray.length()];
            sArrayMovieTitles = new String[posterPathArray.length()];
            sArrayMoviesPopularity = new String[posterPathArray.length()];
            sArrayMoviesVoteCount = new String[posterPathArray.length()];
            sArrayMoviesVoteAverage = new String[posterPathArray.length()];
            for (int i = 0; i < posterPathArray.length(); i++) {

                // Get the JSON object representing the movie results
                JSONObject moviePosterPathResult = posterPathArray.getJSONObject(i);
                HttpURLConnection urlConnection = null;

                String[] moviesPosterPaths = null;
                try {
                    urlConnection = urlConnect("http://image.tmdb.org/t/p/w500/" + moviePosterPathResult.getString("poster_path") + "?api_key=xyz");
                    // Get the input stream
                    InputStream inputStream = urlConnection.getInputStream();
                    if (inputStream != null) {
                        // Stream was empty.  No point in parsing.
                        bitMapMoviesPosters[i] = BitmapFactory.decodeStream(inputStream);
                    } else {
                        bitMapMoviesPosters[i] = null; //BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    }

                    urlConnection = urlConnect("http://image.tmdb.org/t/p/w185/" + moviePosterPathResult.getString("backdrop_path") + "?api_key=xyz");
                    // Get the input stream
                    inputStream = urlConnection.getInputStream();
                    if (inputStream != null) {
                        // Stream was empty.  No point in parsing.
                        bitMapMoviesBackdropPoster[i] = BitmapFactory.decodeStream(inputStream);
                    } else {
                        bitMapMoviesBackdropPoster[i] = null; //BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    }

                    sArrayMoviesOverview[i] = moviePosterPathResult.getString("overview");
                    sArrayMovieReleaseDates[i] = moviePosterPathResult.getString("release_date");
                    sArrayMovieTitles[i] = moviePosterPathResult.getString("title");
                    sArrayMoviesPopularity[i] = moviePosterPathResult.getString("popularity");
                    sArrayMoviesVoteCount[i] = moviePosterPathResult.getString("vote_count");
                    sArrayMoviesVoteAverage[i] = moviePosterPathResult.getString("vote_average");

                } catch (IOException e) {

                    Toast.makeText(getApplicationContext(), "Error occurred while unable to Connect with the URL", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Error unable to connect", e);
                    sJsonMovies = null;
                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Error occurred due to break down in the process flow", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Error in process", e);
                    sJsonMovies = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            return bitMapMoviesPosters;

        }


        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            if (isCancelled()) {
                bitmaps = null;
            }
            CustomGridAdapter gridAdapter = new CustomGridAdapter(bitmaps);
            mGridView.setAdapter(gridAdapter);
        }
    }

    protected Bitmap[] bitMapMoviesBackdropPoster = null;
    protected String[] sArrayMoviesOverview = null;
    protected String[] sArrayMovieReleaseDates = null;
    protected String[] sArrayMovieTitles = null;
    protected String[] sArrayMoviesPopularity = null;
    protected String[] sArrayMoviesVoteCount = null;
    protected String[] sArrayMoviesVoteAverage = null;

    protected HttpURLConnection urlConnect(String sUrl) {

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(sUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(), "Error occurred while unable to Connect with the URL", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error unable to connect", e);

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Error occurred due to break down in the process flow", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error in process", e);
        }
        return urlConnection;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

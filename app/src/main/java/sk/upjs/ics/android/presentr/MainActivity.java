package sk.upjs.ics.android.presentr;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<String>> {

    public static final int USER_LIST_LOADER = 0;
    private ListView peopleListView;
    private ArrayAdapter<String> adapter;
    private PresenceBroadcastReceiver presenceBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent startPresenceService = new Intent(this, PresenceService.class);
        startService(startPresenceService);

        peopleListView = (ListView) findViewById(R.id.peopleListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        peopleListView.setAdapter(adapter);

        getLoaderManager().initLoader(USER_LIST_LOADER, Bundle.EMPTY, this);

        PresenceScheduler.schedule(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(PresenceService.PRESENCE_INTENT_ACTION);
        this.presenceBroadcastReceiver = new PresenceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.presenceBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.presenceBroadcastReceiver);
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.iAmHereAction:
                sendPresence();
                return true;
            case R.id.refreshPeopleAction:
                refreshPeople();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshPeople() {
        this.adapter.clear();
        getLoaderManager().restartLoader(0, Bundle.EMPTY, this);
    }

    private void sendPresence() {
        SendPresenceAsyncTask sendPresenceAsyncTask = new SendPresenceAsyncTask();
        sendPresenceAsyncTask.execute("robert1");
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new PresenceLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> presentUsers) {
        this.adapter.clear();
        this.adapter.addAll(presentUsers);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        this.adapter.clear();
    }

    private class SendPresenceAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                String username = params[0];
                String urlString = "http://ics.upjs.sk/~novotnyr/android/demo/presentr/index.php/available-users/" + username;
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
                return true;
            } catch (IOException e) {
                Log.e(getClass().getName(), "Unable to send presence", e);
                return false;
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            String message;
            if(success) {
                message = "You are here!";
            } else {
                message = "Try again!";
            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private class PresenceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<String> users = (List<String>) intent.getSerializableExtra(PresenceService.PRESENCE_INTENT_EXTRAS);
            adapter.clear();
            adapter.addAll(users);
        }
    }
}

package sk.upjs.ics.android.presentr;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PresenceDao {
    public static final String DEFAULT_SERVICE_URL = "http://ics.upjs.sk/~novotnyr/android/demo/presentr/index.php/available-users";

    private URL serviceUrl;

    public PresenceDao() {
        try {
            this.serviceUrl = new URL(DEFAULT_SERVICE_URL);
        } catch (MalformedURLException e) {
            // URL is hardwired and well-formed
        }
    }

    public List<String> loadUsers() {
        InputStream in = null;
        try {
            in = this.serviceUrl.openStream();
            String json = toString(in);

            JSONArray people = new JSONArray(json);
            List<String> peopleNames = new ArrayList<String>();
            for(int i = 0; i < people.length(); i++) {
                JSONObject person = (JSONObject) people.get(i);
                String login = (String) person.get("login");
                peopleNames.add(login);
            }

            return peopleNames;
        } catch (IOException e) {
            Log.e(getClass().getName(), "I/O Exception while loading users", e);
            return Collections.EMPTY_LIST;
        } catch (JSONException e) {
            Log.e(getClass().getName(), "JSON parsing error while loading users", e);
            return Collections.EMPTY_LIST;
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w(getClass().getName(), "Unable to close input stream", e);
                }
            }
        }
    }

    private String toString(InputStream in) {
        Scanner scanner = new Scanner(in, "utf-8");
        StringBuilder sb = new StringBuilder();
        while(scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        return sb.toString();
    }
}

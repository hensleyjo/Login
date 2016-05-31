package com.example.jordan.login;

import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jordan on 5/20/2016.
 */
public class Client extends AsyncTask<String, Void, Integer> {

    private String url = "https://attabit.com/api/";
    public AsyncResponse delegate = null;
    private int method;

    public Client(int method){
        this.method = method;
    }

    @Override
    protected void onPostExecute(Integer result){
        if (method == 0){
            delegate.processLogin(result);
        }
        else if (method == 1){
            delegate.processSignUp(result);
        }
    }


    @Override
    protected Integer doInBackground(String... params) {
        if (params.length == 2){
            try{
                return login(params[0], params[1]);
            }catch (Exception e){
            }
        }
        if (params.length == 3){
            try{
                return signUp(params[0], params[1], params[2]);
            }catch (Exception e){
            }
        }
        return  null;
    }

    private int login(String userName, String passWord)
            throws MalformedURLException, IOException, org.json.JSONException{

        String myURL = "https://attabit.com/api/select_user.php";
        URL url = new URL(myURL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        JSONObject person = new JSONObject();
        person.put("username", userName);
        person.put("passwd", passWord);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(person.toString());
        wr.flush();

        StringBuilder json = new StringBuilder();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            json.append(line + "\n");
        }
        br.close();

        JSONObject obj = new JSONObject(json.toString());
        String s = obj.getString("returnCode");

        wr.close();
        conn.disconnect();

        int response = Integer.parseInt(s);
        return response;
    }

    private int signUp(String userName, String passWord, String phone)
            throws MalformedURLException, IOException, org.json.JSONException{

        String myURL = url + "insert_user.php";
        URL url = new URL(myURL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setDoInput(true);
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/json; charset=UTF-8");

        JSONObject person = new JSONObject();
        person.put("username", userName);
        person.put("passwd", passWord);
        person.put("mobile", phone);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(person.toString());
        wr.flush();

        StringBuilder json = new StringBuilder();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            json.append(line + "\n");
        }
        br.close();

        JSONObject obj = new JSONObject(json.toString());
        String s = obj.getString("returnCode");

        wr.close();
        conn.disconnect();

        int response = Integer.parseInt(s);
        return response;
    }

    public interface AsyncResponse {
        void processLogin(Integer output);
        void processSignUp(Integer output);
    }
}

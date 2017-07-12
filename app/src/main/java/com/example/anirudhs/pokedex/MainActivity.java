package com.example.anirudhs.pokedex;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {


    EditText enterPokemon;
    Button searchButton;
    TextView detailsText;
    String pokename;
    ImageView pokeImage;
    private String imageUrl;
    private ProgressBar loadBar;
    Handler handler = new Handler();
    void Displayerror(final String Errortext){

        handler.post(new Runnable() {

            @Override
            public void run() {
                pokeImage.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, Errortext, Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterPokemon = (EditText) findViewById(R.id.enterPokemon);
        searchButton = (Button) findViewById(R.id.searchbutton);
        pokename = enterPokemon.getText().toString();
        detailsText = (TextView)findViewById(R.id.detailsText);
        pokeImage= (ImageView)findViewById(R.id.pokeImage);
        loadBar = (ProgressBar)findViewById(R.id.loadBar);

        loadBar.setVisibility(View.GONE);


        searchButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        loadBar.setVisibility(View.VISIBLE);
                        new PokeTask().execute("https://pokeapi.co/api/v2/pokemon/" + enterPokemon.getText());

                    }
                }
        );


    }
    public class PokeTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader = null;

            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                if(connection.getResponseCode()==200)
                    connection.connect();
                else
                {
                    Displayerror("Invalid Pokemon Name");
                }


                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);

                }
                String finalJSON = buffer.toString();
                try {
                    JSONObject parentObject = new JSONObject(finalJSON);
                    JSONArray forms = parentObject.getJSONArray("forms");
                    JSONObject PokemonName = forms.getJSONObject(0);
                    JSONArray types = parentObject.getJSONArray("types");
                    JSONObject type = types.getJSONObject(0);
                    JSONObject tpyName = type.getJSONObject("type");

                    String name = PokemonName.getString("name");
                    int weight = parentObject.getInt("weight");
                    int height = parentObject.getInt("height");
                    String poketype = tpyName.getString("name");


                    JSONObject sprites = parentObject.getJSONObject("sprites");

                    imageUrl = sprites.getString("front_default");
                    return "Name     :          " + name + "\n" + "Height    :          " + height + "\n" + "Weight   :          " + weight + "\n"+ "Type       :          "+ poketype;
                }catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loadBar.setVisibility(View.GONE);
            pokeImage.setVisibility(View.VISIBLE);
            detailsText.setText(result);
            Picasso.with(getApplicationContext()).load(imageUrl).into(pokeImage);

        }
    }

}

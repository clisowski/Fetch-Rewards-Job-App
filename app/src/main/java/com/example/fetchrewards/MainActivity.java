package com.example.fetchrewards;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.fetchrewards.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<String> list;
    ArrayList<Sorter> sorterArrayList;
    ArrayAdapter<String> listAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeList();
        binding.fetchDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new fetchData().start();
            }
        });

    }

    private void initializeList() {
        sorterArrayList = new ArrayList<>();
        list = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, R.layout.row , list);
        binding.userList.setAdapter(listAdapter);
    }

    class fetchData extends Thread{

        String data = "";


        @Override
        public void run(){

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });


            try {
                URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = bufferedReader.readLine()) != null){

                    data = data + line;
                }

                if (!data.isEmpty()){
                    JSONArray jsonArray = new JSONArray(data);
                    list.clear();
                    sorterArrayList.clear();

                    for (int i = 0; i < jsonArray.length(); i++){
                        String add = "";
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.isNull("name") || object.getString("name").trim().isEmpty()){
                            continue;
                        }
                        int id = Integer.parseInt(object.getString("id"));
                        int listId = Integer.parseInt(object.getString("listId"));
                        String nameVal = object.getString("name");
                        Sorter temp = new Sorter(listId, nameVal, id);
                        sorterArrayList.add(temp);


                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Collections.sort(sorterArrayList);

            for (int i = 0; i < sorterArrayList.size(); i++){
                String add = "";
                add =  "listId: " + sorterArrayList.get(i).getListId() + ", Name: " + sorterArrayList.get(i).getName();
                list.add(add);
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                        listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
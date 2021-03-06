package com.example.teamprojectt;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity2 extends AppCompatActivity {

    // DB 연결

    private static String TAG = "phptest_HomeActivity2";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_IDUSER = "idUser";
    private static final String TAG_PROJECTNAME = "projectName";
    private static final String TAG_EMAIL = "eMail";
    private static final String TAG_PHONENUMBER = "phoneNumber";


    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;


    ///////////////////////////////////////////////////
    // 10/04 오류 해결
    // 10/06 RecyclerView => ListView로 다시수정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage2);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mlistView = (ListView)findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();

        GetData task = new GetData();
        task.execute("http://su1318ho.dothome.co.kr/getjson.php");

    }

    ///////////////////////////////////////////////////

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(HomeActivity2.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

                mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
//

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                // InsertData => GetData
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

////////////
// 10/06  //
////////////

    private void showResult(){


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            // 배열의 인덱스는 0부터 시작하므로 length(길이) 에서 -1 해준다.
            for(int i=0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String idUser = item.getString(TAG_IDUSER);
                String projectName = item.getString(TAG_PROJECTNAME);
                String eMail = item.getString(TAG_EMAIL);

                String phoneNumber = item.getString(TAG_PHONENUMBER);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_IDUSER, idUser);
                hashMap.put(TAG_PROJECTNAME, projectName);
                hashMap.put(TAG_EMAIL, eMail);
                hashMap.put(TAG_PHONENUMBER, phoneNumber);

                mArrayList.add(hashMap);

//                mArrayList.add(personalData);
//                mAdapter.notifyDataSetChanged();
            }


            ListAdapter adapter = new SimpleAdapter(
                    HomeActivity2.this, mArrayList, R.layout.mypage2_list,
                    new String[]{TAG_IDUSER, TAG_PROJECTNAME, TAG_EMAIL, TAG_PHONENUMBER},
                    new int[]{R.id.textView_list_idUser, R.id.textView_list_projectName,
                              R.id.textView_list_eMail, R.id.textView_list_phoneNumber }
            );

            mlistView.setAdapter(adapter);


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}





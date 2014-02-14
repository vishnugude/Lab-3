package com.min.vishnu;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final int RESULT_SPEECH = 1;

	private ImageButton btnSpeak;
	private ImageButton submit;
	private TextView txtText;
	private TextView edittext;
	private String res="";
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.setThreadPolicy(policy); 
		txtText = (TextView) findViewById(R.id.txtText);
		edittext = (TextView) findViewById(R.id.edittext);

		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		submit = (ImageButton) findViewById(R.id.submit);
		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
					txtText.setText("");
				} catch (ActivityNotFoundException a) {
					Toast t = Toast.makeText(getApplicationContext(),
							"Ops! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(res != "")
				{
				HttpClient httpclient = new DefaultHttpClient();
   				HttpResponse response;
   				String responseString = null;
   				try {
   				 HttpPost httpPost = new HttpPost("http://api.openweathermap.org/data/2.5/weather?q="+res);
   	             response = httpclient.execute(httpPost);
   				    StatusLine statusLine = response.getStatusLine();
   				    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
   				        ByteArrayOutputStream out = new ByteArrayOutputStream();
   				        response.getEntity().writeTo(out);
   				        out.close();
   				        responseString = out.toString();
   				     String combine = JSONAnalysis(responseString);
   				     String temp = combine.split(";")[0];
   				     String weather = combine.split(";")[1];
   				     float tem=Float.parseFloat(temp)-273;
   				     float maxt=Float.parseFloat(weather)-273;
   				     edittext.setText("temp is: "+round(tem,2) + " C"+"\n" + "max temp: "+round(maxt,2)+" C");
   				    } else{
   				        //Closes the connection.
   				    	res="sorry no results found";
   				    	edittext.setText(res);
   				        response.getEntity().getContent().close();
   				        throw new IOException(statusLine.getReasonPhrase());
   				    }
   				} catch (ClientProtocolException e) {
   				    res="sorry something went wrong ! try again";
   				 edittext.setText(res);
   					e.printStackTrace();
   				} catch (IOException e) {
   				 res="sorry something went wrong ! try again";
   				edittext.setText(res);
   					e.printStackTrace();
   				}
				}else
   				{
   					res="Please speak the city name first";
   					edittext.setText(res);
   				}
   			 	
                        }
				
			
		});

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				res= text.get(0);
				txtText.setText(text.get(0));
				
				
			}
			break;
		}

		}
	}
	
	public String JSONAnalysis(String jsonString)
    {
    	String combine="1";
    	String temperature="";
    	String weather="";
    	JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(jsonString);
			JSONObject  obser=jsonObj.getJSONObject("main");    	
	    	
	    	temperature=obser.getString("temp");
	    	weather = obser.getString("temp_max");
	       	
	    	combine = temperature+";"+weather;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			res="Sorry No city Found";
			edittext.setText(res);
			e.printStackTrace();
		}
 
    
    	
    	return combine;
    }
	 public static float round(float d, int decimalPlace) {
	        BigDecimal bd = new BigDecimal(Float.toString(d));
	        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	        return bd.floatValue();
	    }
}
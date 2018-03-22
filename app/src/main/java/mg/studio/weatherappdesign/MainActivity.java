package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://free-api.heweather.com/s6/weather/now?"+
                    "location=chongqing&"+
                    "key=d9327c1041524734be9c8d2ef4f92292";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestProperty("accept", "*/*");
                urlConnection.connect();

                //urlConnection.setDoOutput(true);

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature

                String result=buffer.toString();
                int l = result.indexOf("tmp") + "tmp\":\"".length();
                int r = result.indexOf("\"",l);
                String tmp = result.substring(l,r);
                Log.d("MainActivity: result = ",result);
                Log.d("MainActivity",tmp);
                return tmp;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String dayOfWeek=String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
            int day=Integer.parseInt(dayOfWeek);
            String[] weekDay={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"," Saturday"};
            String day_of_week=weekDay[day-1];
            ((TextView) findViewById(R.id.day_of_week)).setText(day_of_week);
            int tv_year=calendar.get(Calendar.YEAR);
            int tv_mon=calendar.get(Calendar.MONTH)+1;
            int tv_day=calendar.get(Calendar.DAY_OF_MONTH);
            String tv_date=tv_mon+"/"+tv_day+"/"+tv_year;
            ((TextView) findViewById(R.id.tv_date)).setText(tv_date);
        }
    }
}

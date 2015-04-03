package group15.mrthermostat;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;


public class weather extends Fragment {

    Typeface weatherFont;

    TextView leftTop;
    TextView leftCenter;
    TextView leftBottom;
    TextView rightTop;
    TextView rightCenter;
    TextView rightBottom;
    TextView weatherIcon;

    Handler handler;

    public weather() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        leftTop = (TextView)rootView.findViewById(R.id.weather_left_top);
        leftCenter = (TextView)rootView.findViewById(R.id.weather_left_center);
        leftBottom = (TextView)rootView.findViewById(R.id.weather_left_bottom);
        rightTop = (TextView)rootView.findViewById(R.id.weather_right_top);
        rightCenter = (TextView)rootView.findViewById(R.id.weather_right_center);
        rightBottom = (TextView)rootView.findViewById(R.id.weather_right_bottom);
        weatherIcon = (TextView)rootView.findViewById(R.id.weatherImg);

        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = WeatherFetch.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        String tempHi, tempLow, tempCurr, wind;

        try {
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject windStuff = json.getJSONObject("wind");

            wind = String.format("%.0f", windStuff.getDouble("speed"));
            tempCurr = String.format("%.0f", main.getDouble("temp"));
            tempHi = String.format("%.0f", main.getDouble("temp_max"));
            tempLow = String.format("%.0f", main.getDouble("temp_min"));

            leftTop.setText(json.getString("name").toUpperCase(Locale.US));
            leftCenter.setText(details.getString("description").toUpperCase(Locale.US));
            leftBottom.setText(tempCurr + "\u00B0F");
            //json.getJSONObject("sys").getString("country"));

            rightTop.setText("Hi: " + tempHi + "\u00B0F\nLow: " + tempLow + "\u00B0F");
            rightCenter.setText("Humidity: " + main.getString("humidity") + "%");
            rightBottom.setText("Wind: " + wind + "mph");

            //DateFormat df = DateFormat.getDateTimeInstance();
            //String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            //updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

package com.example.joan.place;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Create_trip extends Fragment {
    public Button createfirst;
    public Button lastLocation;
    public Button Matrix;
    private int mYear;
    private int mMonth;
    private int mDate;
    private String FirstWeekday;
    private Button doSetDate;
    private Button doSetTime;
    private Button pickplace;
    //private EditText setTimeDuration;
    private Button all_OK;
    private Button add_POI;
    private GoogleApiClient client;
    public List<HashMap<String, String>> mainlist = null;
    private ImageButton poiitem_restaurant;
    private ImageButton poiitem_travel;
    private ImageButton poiitem_bank;
    private ImageButton poiitem_salon;
    private ImageButton poiitem_hospital;
    private ImageButton poiitem_hotel;
    private ImageButton poiitem_book;
    private ImageButton poiitem_cafe;
    private ImageButton poiitem_mall;

    //for poi reference
    public ArrayList<String> all_poi_name=new ArrayList<>();
    public ArrayList<String> all_poi_rating=new ArrayList<>();
    public ArrayList<LatLng> all_poi_latlng=new ArrayList<>();
    public ArrayList<String> all_poi_photo=new ArrayList<>();
    public ArrayList<String> all_poi_address=new ArrayList<>();
    public ArrayList<String> all_poi_phone=new ArrayList<>();
    public ArrayList<String> all_poi_website=new ArrayList<>();
    public ArrayList<String> all_poi_openingtime=new ArrayList<>();
    //end of poi ref

    //private  ImageView img_trip;
    public boolean found_best=false;
    public boolean allow_distance=false;
    private Button poi_self_ok;
    private TextView POIchoice;
    private EditText self_choice_typed;
    private String choice_of_poi;
    private Place firstplace;
    private Place lastplace;
    private String first_place_msg;
    private String last_place_msg;
    private String formatDate;
    private String formatTime;
    private String first_time_set;

    ArrayList<String> myDataset = new ArrayList<>();
    ArrayList<String> ref_dataset = new ArrayList<>();
    private final static int PLACE_PICKER_REQUEST = 1;
    private final static int PLACE_PICKER_REQUEST_LAST=2;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    View Mainview;
    MyAdapter myAdapter;
    POIAdapter poiAdapter;
    ArrayList<String> POI_choice_list=new ArrayList<>();
    ArrayList<Date> POI_length_list=new ArrayList<>();

    public Create_trip() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mainview = inflater.inflate(R.layout.fragment_create_trip, container, false);
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        createfirst = (Button) Mainview.findViewById(R.id.first_trip_button);
        all_OK = (Button) Mainview.findViewById(R.id.all_ok);
        Matrix=(Button) Mainview.findViewById(R.id.Matrix);
        lastLocation=(Button)Mainview.findViewById(R.id.last_place);
        add_POI=(Button)Mainview.findViewById(R.id.add_poi);
        poiAdapter = new POIAdapter();
        final RecyclerView mList = (RecyclerView) Mainview.findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager1);

        createfirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater1 = getActivity().getLayoutInflater();
                final View v = inflater1.inflate(R.layout.alert_choose_firat_location, null);
                new AlertDialog.Builder(getActivity()).setTitle("起點選擇")
                        .setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //visitDuration = setTimeDuration.getText().toString();
                        //Toast.makeText(getContext(), formatDate + "\n" + formatTime + "\n" + msg + "\n" + visitDuration, Toast.LENGTH_LONG).show();
                    }
                }).show();
                pickplace = (Button) v.findViewById(R.id.pickplace);
                pickplace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Intent intent = builder.build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                });

                doSetDate = (Button) v.findViewById(R.id.datepicker_first);
                doSetDate.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDate = c.get(Calendar.DAY_OF_MONTH);
                        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                formatDate = setDateFormat(year, month, day);
                                SimpleDateFormat dateFormat=new SimpleDateFormat("EEEE");
                                Date date=new Date(year,month,day-1);
                                Log.e(""+formatDate,""+dateFormat.format(date));
                                FirstWeekday=dateFormat.format(date);
                                doSetDate.setText(""+year+"-"+month+"-"+day);
                            }



                        }, mYear, mMonth, mDate).show();
                    }
                });

                doSetTime = (Button) v.findViewById(R.id.timepicker_first);
                doSetTime.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        // Create a new instance of TimePickerDialog and return it
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                SimpleDateFormat time=new SimpleDateFormat("hh:mm aa");
                                Time tme = new Time(hourOfDay,minute,0);//seconds by default set to zero
                                first_time_set=time.format(tme);
                                Log.e("time",""+time.format(tme));
                                doSetTime.setText(""+time.format(tme));

                            }
                        }, hour, minute, false).show();
                    }
                });

            }
        });


        add_POI.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                POI_choice_list.add("poi");
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                try {
                    Date time = simpleDateFormat.parse("00:30");
                    Log.e("poi_time",""+time);
                    POI_length_list.add(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mList.setAdapter(poiAdapter);
            }
        });

        Matrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng L1=new LatLng(22.999687,120.218095);
                LatLng L2=new LatLng(22.992621,120.213365);
                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+L1.latitude+","+L1.longitude +
                        "&destinations="+L2.latitude+","+L2.longitude +
                        "&key=AIzaSyDomABgA1RgXQaE31JakIQi9Cw66nhHGAc";
                Distance_Matrix task = new Distance_Matrix();
                task.execute(url);
            }
        });

        lastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater1 = getActivity().getLayoutInflater();
                final View v = inflater1.inflate(R.layout.alert_last_choice, null);
                new AlertDialog.Builder(getActivity()).setTitle("終點選擇")
                        .setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //visitDuration = setTimeDuration.getText().toString();
                        //Toast.makeText(getContext(), formatDate + "\n" + formatTime + "\n" + msg + "\n" + visitDuration, Toast.LENGTH_LONG).show();
                    }
                }).show();
                pickplace = (Button) v.findViewById(R.id.pickplace);
                pickplace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Intent intent = builder.build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST_LAST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Button as_first=(Button)v.findViewById(R.id.as_start);
                as_first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lastLocation.setText(first_place_msg);
                    }
                });

                doSetDate = (Button) v.findViewById(R.id.datepicker_first);
                doSetDate.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDate = c.get(Calendar.DAY_OF_MONTH);
                        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                formatDate = setDateFormat(year, month, day);
                                doSetDate.setText(formatDate);
                            }

                        }, mYear, mMonth, mDate).show();
                    }
                });

                doSetTime = (Button) v.findViewById(R.id.timepicker_first);
                doSetTime.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        // Create a new instance of TimePickerDialog and return it
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                formatTime = hourOfDay + ":" + minute;
                                doSetTime.setText(formatTime);

                            }
                        }, hour, minute, false).show();
                    }
                });
            }
        });

        all_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getUrl(firstplace.getLatLng(), POI_choice_list.get(0));
                PlacesTask task = new PlacesTask();
                task.execute(url);

            }
        });




        return Mainview;
    }

    private String getUrl(LatLng latlng, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latlng.latitude + "," + latlng.longitude);
        googlePlacesUrl.append("&radius=2000");
        googlePlacesUrl.append("&rankby=prominence");
        googlePlacesUrl.append("&keyword=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyDomABgA1RgXQaE31JakIQi9Cw66nhHGAc");
        //Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    private void set_selfok() {
        poi_self_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("" + self_choice_typed.getText());
            }
        });
    }


    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                firstplace = PlacePicker.getPlace(data, getActivity());
                first_place_msg = String.format("%s", firstplace.getName());
                pickplace.setText(first_place_msg);
                createfirst.setText(first_place_msg);
                Toast.makeText(getContext(), first_place_msg, Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode==PLACE_PICKER_REQUEST_LAST)
        {
            if (resultCode == Activity.RESULT_OK) {
                lastplace = PlacePicker.getPlace(data, getActivity());
                last_place_msg = String.format("Place: %s", lastplace.getName());
                pickplace.setText(last_place_msg);
                Toast.makeText(getContext(), last_place_msg, Toast.LENGTH_LONG).show();

            }
        }
    }

    private void setPoiitem_restaurant() {
        poiitem_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("restaurant");
            }
        });
    }

    private void setPoiitem_travel() {
        poiitem_travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("景點");
            }
        });
    }

    private void setPoiitem_bank() {
        poiitem_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("銀行");
            }
        });
    }

    private void setPoiitem_salon() {
        poiitem_salon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("理髮廳");
            }
        });
    }

    private void setPoiitem_hospital() {
        poiitem_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("醫院");
            }
        });
    }

    private void setpoiitem_cafe() {
        poiitem_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("咖啡廳");
            }
        });
    }

    private void setPoiitem_book() {
        poiitem_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("書局");
            }
        });
    }

    private void setPoiitem_hotel() {

        poiitem_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("旅館");
            }
        });
    }

    private void setPoiitem_mall() {
        poiitem_mall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POIchoice.setText("賣場");
            }
        });
    }




    private String getUrl_detail(String Place_id) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlacesUrl.append("placeid=" + Place_id);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyDomABgA1RgXQaE31JakIQi9Cw66nhHGAc");
        //Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", "" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.joan.place/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.joan.place/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class Distance_Matrix extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            ParseMatrix parserTask=new ParseMatrix();
            parserTask.execute(result);
        }
    }

    private class ParseMatrix extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_Matrix place_matrix=new Place_Matrix();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = place_matrix.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            for(int i=0;i<list.size();i++)
            {
                HashMap<String, String> Matrix_info = list.get(i);
                Log.e("distance text",""+Matrix_info.get("distance_text"));
                Log.e("duration text",""+Matrix_info.get("duration_text"));
                Date time;
                SimpleDateFormat s1=new SimpleDateFormat("ss");
                SimpleDateFormat s2=new SimpleDateFormat("HH:mm");
                try {
                    time=s1.parse(Matrix_info.get("duration_value"));
                    Date time3 = new SimpleDateFormat("HH:mm").parse(s2.format(time));
                    Date setTime=s2.parse(first_time_set);
                    long time4=setTime.getTime()+time3.getTime();
                    Date date3 = new SimpleDateFormat("HH:mm").parse(s2.format(time4));
                    Log.e("time3",""+date3);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = placeJson.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            mainlist = list;
            for (int i = 0; i < list.size(); i++) {
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
                // Getting latitude of the place

                if(hmPlace.get("place_id")!=null)
                {
                    String url = getUrl_detail(hmPlace.get("place_id").toString());
                    PlacesTaskDetail task = new PlacesTaskDetail();
                    task.execute(url);
                }
            }

        }
    }


    private class PlacesTaskDetail extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTaskDetail parserTask = new ParserTaskDetail();
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Place Details in JSON format
     */
    private class ParserTaskDetail extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceDetail placeDetailsJsonParser = new PlaceDetail();

            try {
                jObject = new JSONObject(jsonData[0]);
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);
                Double rating=0.0;
                if(hPlaceDetails.get("rating").compareTo("-NA-")!=0&&found_best==false)
                {
                    rating=Double.parseDouble(hPlaceDetails.get("rating"));
                }
                if(hPlaceDetails.get(FirstWeekday).compareTo("null")!=0&&rating>=3.5)
                {
                    String opening_hour=hPlaceDetails.get(FirstWeekday);
                    String[] separated = opening_hour.split(": ");
                    if(separated[1].compareTo("Closed")!=0)
                    {
                        String[] time=separated[1].split(",");
                        for(int i=0;i<time.length;i++)
                        {
                            //Log.e("time "+i,""+time[i]);
                            if(first_time_set.contains("AM"))
                            {
                                if(time[i].compareTo("Open 24 hours")!=0)
                                {
                                    String[] timelength=time[i].split(" – ");
                                    if(timelength[0].contains("AM")==false&&timelength[0].contains("PM")==false)
                                    {
                                        if(timelength[1].contains("AM")==true)timelength[0]+="AM";
                                        else timelength[0]+="PM";
                                    }

                                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                                    Date open_start = parseFormat.parse(timelength[0]);
                                    Date end_time=parseFormat.parse(timelength[1]);
                                    Date setTime=parseFormat.parse(first_time_set);
                                    Date time1 = new SimpleDateFormat("HH:mm").parse(displayFormat.format(open_start));
                                    Date time2 = new SimpleDateFormat("HH:mm").parse(displayFormat.format(end_time));
                                    Date time3 = new SimpleDateFormat("HH:mm").parse(displayFormat.format(setTime));
                                    if (time3.after(time1) && time3.before(time2)) {
                                        found_best=true;
                                        allow_distance=true;
                                        Log.e("into here","yaya");
                                        Log.e("name",""+hPlaceDetails.get("name"));
                                        Log.e("rating",""+hPlaceDetails.get("rating"));
                                        all_poi_name.add(hPlaceDetails.get("name"));

                                        String photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference="
                                                + hPlaceDetails.get("ref_photo") + "&key=" + "AIzaSyDomABgA1RgXQaE31JakIQi9Cw66nhHGAc";
                                        all_poi_photo.add(photo);
                                        double lat = Double.parseDouble(hPlaceDetails.get("lat"));
                                        double lng = Double.parseDouble(hPlaceDetails.get("lng"));
                                        Log.e("lat in main",""+lat);
                                        Log.e("lng in main",""+lng);
                                        all_poi_latlng.add(new LatLng(lat,lng));
                                        all_poi_rating.add(hPlaceDetails.get("rating"));
                                        all_poi_address.add(hPlaceDetails.get("address"));
                                        all_poi_phone.add(hPlaceDetails.get("formatted_phone"));
                                        all_poi_website.add(hPlaceDetails.get("website"));
                                        all_poi_openingtime.add(hPlaceDetails.get("FirstWeekday"));
                                        LatLng L1=firstplace.getLatLng();
                                        LatLng L2=all_poi_latlng.get(0);

                                    }


                                }
                            }
                            else if(first_time_set.contains("PM"))
                            {
                                if(time[i].compareTo("Open 24 hours")!=0)
                                {
                                    String[] timelength=time[i].split(" – ");
                                    if(timelength[0].contains("AM")==false&&timelength[0].contains("PM")==false)
                                    {
                                        if(timelength[1].contains("AM")==true)timelength[0]+="AM";
                                        else timelength[0]+="PM";
                                    }

                                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                                    Date open_start = parseFormat.parse(timelength[0]);
                                    Date end_time=parseFormat.parse(timelength[1]);
                                    Date setTime=parseFormat.parse(first_time_set);
                                    Date time1 = new SimpleDateFormat("HH:mm").parse(displayFormat.format(open_start));
                                    Date time2 = new SimpleDateFormat("HH:mm").parse(displayFormat.format(end_time));
                                    Date time3 = new SimpleDateFormat("HH:mm").parse(displayFormat.format(setTime));
                                    if (time3.after(time1) && time3.before(time2)) {
                                        Log.e("True","yaya");
                                    }


                                }
                            }
                        }


                    }
                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return hPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {
            if(allow_distance==true)
            {
                allow_distance=false;
                LatLng L1=firstplace.getLatLng();
                LatLng L2=all_poi_latlng.get(0);
                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+L1.latitude+","+L1.longitude +
                        "&destinations="+L2.latitude+","+L2.longitude +
                        "&key=AIzaSyDomABgA1RgXQaE31JakIQi9Cw66nhHGAc";
                Distance_Matrix task = new Distance_Matrix();
                task.execute(url);
            }


            /*if (ref_dataset.size() == myDataset.size()) {


                // myAdapter = new MyAdapter(myDataset, ref_dataset, latlng_card);
                /*RecyclerView mList = (RecyclerView) Mainview.findViewById(R.id.list_view);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mList.setLayoutManager(layoutManager);
                mList.setAdapter(myAdapter);
            }*/


        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;
        private List<String> photodata;
        private List<LatLng> latLngs;


        public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
            public TextView mTextView;
            public ImageView IMG;
            public MapView mapView;
            public GoogleMap MgoogleMap;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.info_text);
                IMG = (ImageView) v.findViewById(R.id.img);
                mapView = (MapView) v.findViewById(R.id.map_card);
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                MgoogleMap = googleMap;
                LatLng sydney = new LatLng(-34, 151);
                MgoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                MgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        }

        public MyAdapter(List<String> data, List<String> photo_data, List<LatLng> latLngs1) {
            mData = data;
            photodata = photo_data;
            latLngs = latLngs1;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.forcard, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextView.setText(mData.get(position));
            Picasso.with(getContext()).load(photodata.get(position)).resize(1000, 600)
                    .into(holder.IMG);
            holder.mapView.onCreate(null);
            holder.mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng sydney = latLngs.get(position);
                    googleMap.addMarker(new MarkerOptions().position(sydney).title(mData.get(position)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                }
            });
            holder.mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapsActivity maps = new MapsActivity(latLngs.get(position));
                    Intent intent = new Intent(getActivity(), maps.getClass());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

    }

    public class POIAdapter extends RecyclerView.Adapter<POIAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button poi_choice;
            public Spinner length;
            final String[] limit = {"0.5 hr","1 hr", "1.5 hr", "2 hr","3 hr","6 hr","8 hr","12 hr"};
            public ViewHolder(View v) {
                super(v);
                poi_choice=(Button)v.findViewById(R.id.cardview_btn_POI);
                length=(Spinner)v.findViewById(R.id.spinner_poi);
                ArrayAdapter<String> lunchList = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        limit);
                length.setAdapter(lunchList);
            }
        }

        public POIAdapter() {

        }

        @Override
        public POIAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.poi_cardview, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            holder.poi_choice.setText(POI_choice_list.get(position));
            holder.poi_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater1 = getActivity().getLayoutInflater();
                    final View v1 = inflater1.inflate(R.layout.poi_test, null);
                    final AlertDialog.Builder dialog_list = new AlertDialog.Builder(getActivity());
                    dialog_list.setTitle("POI").setView(v1).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            choice_of_poi = POIchoice.getText().toString();
                            Toast.makeText(getContext(), "" + choice_of_poi, Toast.LENGTH_LONG).show();
                            holder.poi_choice.setText(choice_of_poi);
                            //Log.e("position",""+position);
                            POI_choice_list.set(position,choice_of_poi);
                        }
                    }).show();

                    poiitem_restaurant = (ImageButton) v1.findViewById(R.id.restaurant);
                    setPoiitem_restaurant();
                    poiitem_salon = (ImageButton) v1.findViewById(R.id.salon);
                    setPoiitem_salon();
                    poiitem_cafe = (ImageButton) v1.findViewById(R.id.cafe);
                    setpoiitem_cafe();
                    poiitem_travel = (ImageButton) v1.findViewById(R.id.travelspot);
                    setPoiitem_travel();
                    poiitem_hospital = (ImageButton) v1.findViewById(R.id.hospital);
                    setPoiitem_hospital();
                    poiitem_hotel = (ImageButton) v1.findViewById(R.id.hotel);
                    setPoiitem_hotel();
                    poiitem_bank = (ImageButton) v1.findViewById(R.id.bank);
                    setPoiitem_bank();
                    poiitem_book = (ImageButton) v1.findViewById(R.id.bookstore);
                    setPoiitem_book();
                    poiitem_mall = (ImageButton) v1.findViewById(R.id.department_store);
                    setPoiitem_mall();
                    self_choice_typed = (EditText) v1.findViewById(R.id.self_typed);
                    POIchoice = (TextView) v1.findViewById(R.id.POI_choice);
                    poi_self_ok = (Button) v1.findViewById(R.id.self_OK);
                    set_selfok();
                }
            });

            holder.length.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                    Date time;
                    switch (i)
                    {
                        case 0:
                            try {
                                time = simpleDateFormat.parse("00:30");
                                Log.e("time",""+time);
                                POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            try {
                                time = simpleDateFormat.parse("01:00");
                                Log.e("time",""+time);
                                POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            break;
                        case 2:
                            try {
                                time = simpleDateFormat.parse("01:30");
                                Log.e("time",""+time);
                                POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 3:
                            try {
                                time = simpleDateFormat.parse("02:00");
                                Log.e("time",""+time);POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 4:
                            try {
                                time = simpleDateFormat.parse("03:00");
                                Log.e("time",""+time);
                                POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 5:
                            try {
                                time = simpleDateFormat.parse("06:00");
                                Log.e("time",""+time);POI_length_list.set(position,time);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 6:
                            try {
                                time = simpleDateFormat.parse("08:00");
                                Log.e("time",""+time);
                                POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 7:
                            try {
                                time = simpleDateFormat.parse("12:00");
                                Log.e("time",""+time);
                                POI_length_list.set(position,time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                    }


                }

                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                    try {
                        Date time = simpleDateFormat.parse("00:30");
                        Log.e("time",""+time);
                        POI_length_list.set(position,time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return POI_choice_list.size();
        }

    }
}

package com.example.celebrityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView ;
    Button option0,option1,option2,option3;

    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    Random r =new Random();
    String result = "";

    int locationOfCorrectAnswer=0;
    int celebIndex=0;


    public void nextImage(){
        ImageDownloader task = new ImageDownloader();
        Bitmap image;

        try {

            celebIndex = r.nextInt(celebnames.size());
            locationOfCorrectAnswer = r.nextInt(4);
            image = task.execute(celeburls.get(celebIndex)).get();
            imageView.setImageBitmap(image);
            answers.clear();

            for (int i = 0; i <4; i++) {
                if (i == locationOfCorrectAnswer)
                    answers.add(celebnames.get(celebIndex));
                else {
                    int wronganswer = r.nextInt(celebnames.size());
                    while (wronganswer == celebIndex) {
                        wronganswer = r.nextInt(celebnames.size());


                    }
                    answers.add(celebnames.get(wronganswer));
                }
            }

            option0.setText(answers.get(0));
            option1.setText(answers.get(1));
            option2.setText(answers.get(2));
            option3.setText(answers.get(3));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void choseCeleb(View view) {


        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

        } else{
            Toast.makeText(getApplicationContext(),"Wrong! It was " + celebnames.get(celebIndex),Toast.LENGTH_SHORT).show();
        }
        nextImage();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        option0 = findViewById(R.id.button0);
        option1= findViewById(R.id.button1);

        option2 = findViewById(R.id.button2);
        option3 = findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        try {
            result=task.execute("https://www.imdb.com/list/ls052283250/").get();

            Pattern p = Pattern.compile("src=\"(.*?).jpg\"");
            Matcher m = p.matcher(result);
            while(m.find()){
                celeburls.add(m.group(1)+".jpg");
            }
            p=Pattern.compile("<img alt=\"(.*?)\"");
            m=p.matcher(result);
            while(m.find()){
                celebnames.add(m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        nextImage();
        // Log.i("result",result);
    }


    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {


            URL url ;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String content ="";
            URL url ;
            HttpURLConnection connection = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    stringBuilder.append(current);
                    data = reader.read();
                }
                content = stringBuilder.toString();
                return content;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

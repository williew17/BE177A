package com.example.chancek.watchtalktest;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExitPage extends WearableActivity {

    AmazonS3 s3Client;
    String bucket = "quanhaibucket";
    File uploadit;
    File uploadit_Times;
    TransferUtility transferUtility;

    TextView mTextView;
    int uploadsComplete;
    int numFiles = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_page);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        // Get credentials for Amazon S3 Bucket
        s3credentialsProvider();
        setTransferUtility();

        uploadsComplete = 0;

       //Display date finished
        TextView textExit = findViewById(R.id.textExit);

        Bundle extras = getIntent().getExtras();
        String dateFinished = extras.getString("Date");
        String filename = extras.getString("Filename");
        String timesFilename = extras.getString("TimesFilename");

        // Find path of the saved JSON file
        File file = new File(getFilesDir() + "/" + filename);
        String dir = file.getAbsolutePath();
        Log.d("pathname",dir);
        uploadit = new File(dir);

        // Find path of the saved times file
        File timeFile = new File(getFilesDir() + "/" + timesFilename);
        String timeDir = timeFile.getAbsolutePath();
        Log.d("pathname",timeDir);
        uploadit_Times = new File(timeDir);

        FileInputStream in;
        String fileString = "";

        // filename == "" if the file writer threw an exception in SurveyQuestion
        // If the filename is valid, read the file and display results.
        if (!filename.equals("")) {
            try {
                in = openFileInput(filename);
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                fileString = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                fileString = "Exception thrown";
            }
        }

        String totalText = "Your survey is complete:" + dateFinished + "\n" + fileString;

        textExit.setText(totalText);

        uploadtoS3(filename, uploadit);
        uploadtoS3(timesFilename, uploadit_Times);

    }

    public void s3credentialsProvider(){
    // Initialize the AWS Credential
        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider =
                new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:b942d0f3-2da3-44f5-86a9-621c47f04aa1", // Identity Pool ID
                        Regions.US_EAST_1 // Region
                );
        createAmazonS3Client(cognitoCachingCredentialsProvider);
    }

    public void createAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        // Create an S3 client
        s3Client = new AmazonS3Client(credentialsProvider);
        // Set the region of your S3 bucket
        s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
    }

    public void setTransferUtility(){
        transferUtility = TransferUtility.builder()
                .context(getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(s3Client)
                .build();
    }

    public void uploadtoS3(String filename, File toUpload){
        TransferObserver transferObserver = transferUtility.upload(
                bucket,
                filename,
                toUpload
        );
        transferObserverListener(transferObserver);
    }

    public void transferObserverListener(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Toast.makeText(getApplicationContext(), "Upload: "
                        + state, Toast.LENGTH_SHORT).show();
                if(state == TransferState.COMPLETED)
                {
                    uploadsComplete++;
                }

                if(uploadsComplete == numFiles)
                {
                    QuitApp();
                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Toast.makeText(getApplicationContext(), "Uploading: %"
                        + percentage, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(int id, Exception ex) {
                Log.e("error", "error");
            }
        });
    }

    public void QuitApp(){
        new CountDownTimer(1000,100){
            public void onTick(long millisUntilFinished) {
                //do nothing
            }

            public void onFinish() {
                WrapUp();
            }

        }.start();


    }

    public void WrapUp()
    {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

        this.finishAffinity();
    }
}

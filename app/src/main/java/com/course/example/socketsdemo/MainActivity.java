/*
 * This is the client side of an application that communicates over Sockets.
 * The server side may be hosted on any server with an IP address.
 * All creation and use of sockets must be done on a background thread.
 * When the button is clicked, the thread is started.
 * Notice the permissions in the Manifest.
 * A thread may not write to the UI. Only the main activity thread may do that.
 */

package com.course.example.socketsdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    private Button button;
    private EditText field;

    private Socket socket = null;
    private Thread t = null;

    private double radius = 0;
    private double area = 0;

    // IO streams
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.TextView01);
        button = (Button) findViewById(R.id.Button01);
        field = (EditText) findViewById(R.id.EditText01);
        field.setHint("Enter Radius");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Get the radius from the text field
                radius = Double.parseDouble(field.getText().toString());

                // create thread to do socket work
                t = new Thread(background);
                t.start();

                // wait for thread to finish
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Log.e("SocketsDemo", "Interrupted");
                }

                //thread ended, format data
                String str = String.format("%.2f", area);

                // Display value
                text.append("Radius is " + radius + "\n");
                text.append("Area received from the server is " + str
                        + '\n');
                field.setText("");
                field.setHint("Enter Radius");
            }
        });

    }

    // background task for socket work
    Runnable background = new Runnable() {
        public void run() {
            try {
                // Create a socket to connect to the server
                socket = new Socket("frodo.bentley.edu", 10000);

                // Create an input stream to receive data from the server
                fromServer = new DataInputStream(socket.getInputStream());

                // Create an output stream to send data to the server
                toServer = new DataOutputStream(socket.getOutputStream());
                // Send the radius to the server
                toServer.writeDouble(radius);
                toServer.flush();

                // Get area from the server
                area = fromServer.readDouble();
                socket.close();

                Log.i("SocketsDemo", "IO Complete");

            } catch (IOException ex) {
                Log.e("SocketsDemo", "IO Exception");
            }
        }
    };
}

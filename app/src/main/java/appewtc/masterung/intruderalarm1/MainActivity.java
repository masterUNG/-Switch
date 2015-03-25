package appewtc.masterung.intruderalarm1;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;


public class MainActivity extends IOIOActivity{

    private TextView txtTextView;
    private boolean bolStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initial Widget
        txtTextView = (TextView) findViewById(R.id.txtShow);
        showTextView();
    }   // onCreate

    public void showTextView() {
        if (bolStatus) {
            txtTextView.setText("ON");
        } else {
            txtTextView.setText("OFF");
        }
    }

    class Looper extends BaseIOIOLooper {
        //Explicit
        private DigitalOutput myOutput;
        private DigitalInput myInput;
        @Override
        protected void setup() throws ConnectionLostException, InterruptedException {
            myOutput = ioio_.openDigitalOutput(0, false);
            myInput = ioio_.openDigitalInput(1, DigitalInput.Spec.Mode.PULL_UP);
//            super.setup();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connected IOIO OK", 5000).show();
                }
            });
        }   // setup

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
//            super.loop();

           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   try {

                       while (!myInput.read()) {
                           bolStatus = !bolStatus;
                           myOutput.write(bolStatus);
                           if (!bolStatus) {
                               txtTextView.setText("ON");
                           } else {
                               txtTextView.setText("OFF");
                           }
                           Thread.sleep(100);
                       }

                   } catch (Exception e) {
                       Log.d("IOIO", "IOIO ==>" + e.toString());
                   }
               }
           });

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }   // loop
    }   // Looper Class

    protected IOIOLooper createIOIOLooper() {

        return new Looper();
    }

}    // Main Class

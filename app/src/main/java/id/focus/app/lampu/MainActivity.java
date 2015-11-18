package id.focus.app.lampu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

    private ImageButton btnSwitch;
    private Camera camera;
    private boolean isFlashOn;
    private boolean cekFlash;
    private Parameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnSwitch = (ImageButton) findViewById(R.id.imageButton1);
        // cek flash di device
        cekFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!cekFlash) {

            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Maaf, Hp Anda Tidak Support Flash Light");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
            return;
        }
        // get the camera
        getCamera();
        // displaying button image
        toggleButtonImage();

        // Switch button click event to toggle flash on/off
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    flashMati();
                } else {
                    // turn on flash
                    flashHidup();
                }
            }
        });
    }

    /*
        Mendapatkan kamera hardware
     */
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.d("Camera Error. Error: ", e.getMessage());
            }
        }
    }

    // Turning On flash
    private void flashHidup() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
            // changing button/switch image
            toggleButtonImage();
        }
    }

    // Turning Off flash
    private void flashMati() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            // changing button/switch image
            toggleButtonImage();
        }
    }

    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage(){
        if(isFlashOn){
            btnSwitch.setImageResource(R.drawable.switch_on);
        }else{
            btnSwitch.setImageResource(R.drawable.switch_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //wakeLock.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //wakeLock.release();
        // on pause turn off the flash
        flashMati();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //wakeLock.acquire();
        // on resume turn on the flash
        if(cekFlash)
            flashHidup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //wakeLock.acquire();
        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            //wakeLock.release();
            camera = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showAbout(null);
            return true;
        }
        if (id == R.id.action_exit) {
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout(View view) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tentang");
        builder.setMessage("Flash Light versi 1.29.1 " +
                "\nDevelope by Teguh Narwadi")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
        builder.show();
    }
}

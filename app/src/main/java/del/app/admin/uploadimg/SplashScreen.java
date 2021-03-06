package del.app.admin.uploadimg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class SplashScreen extends AppCompatActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
	
		
		
        new Handler().postDelayed(new Runnable(){

                @Override
                public void run() {
					Intent next = new Intent();
					next.setClass(SplashScreen.this, MainActivity.class);
					startActivity(next);
					finish();
                }
            }, 2000);
    }
}

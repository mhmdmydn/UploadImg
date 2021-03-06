package del.app.admin.uploadimg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Comparator;
import android.net.Uri;
import android.animation.ValueAnimator;

public class MainActivity extends AppCompatActivity implements PermissionsUtil.IPermissionsCallback {

	private PermissionsUtil permissionsUtil;
	private RecyclerView recyclerView;
	private ImageAdapter adapter;
	private ArrayList<ImageModel> filesList = new ArrayList<>();
	private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View _v) {
                    onBackPressed();
                }
            });
		initPermission();
		initView(); 
    }
	public ArrayList<ImageModel> getImgModel() {
		ImageModel f;
		String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
		File targetDirectory = new File(targetPath);
		files = targetDirectory.listFiles();
		if (files == null) {

		}
		try {
			Arrays.sort(files, new Comparator(){

					@Override
					public int compare(Object p1, Object p2) {
						if (((File)p1).lastModified() > ((File)p2).lastModified()) {
							return -1;
						} else if (((File)p1).lastModified() < ((File)p2).lastModified()) {
							return +1;
						} else {
							return 0;
						}
					}
				});
            for (File fi : files) {
                if (fi.getName().toLowerCase().endsWith(".jpg") 
                    || fi.getName().toLowerCase().endsWith(".png")
                    || fi.getName().toLowerCase().endsWith(".jpeg")) {
					f = new ImageModel();
					f.setName(fi.getName());
					f.setUri(Uri.fromFile(fi));
					f.setPath(fi.getAbsolutePath());
					f.setFileName(fi.getName());
					filesList.add(f);
                }
            }
		} catch (Exception e ) {
			e.printStackTrace();
		}

		return filesList;
	}


	private void initView() {
		recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
	}

	private void initialize() {
		ExternalTask et = new ExternalTask();
		et.execute(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Telegram/Telegram Images");
	}

	private void initPermission() {
		permissionsUtil =  PermissionsUtil
			.with(this)
			.requestCode(0)
			.isDebug(true)
			.permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE)
			.request();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		permissionsUtil.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onPermissionsGranted(int requestCode, String[] permission) {
		initialize();
		showMsg("Permissions Granted");
	}

	@Override
	public void onPermissionsDenied(int requestCode, String[] permission) {
		initPermission();
		showMsg("Permissions Denied");
	}

	private void showMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private class ExternalTask extends AsyncTask<String, Integer, ArrayList<ImageModel>> {

		ProgressDialog prog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			prog = new ProgressDialog(MainActivity.this);
			prog.setTitle("Search Files");
			prog.setMessage("Please Wait...");
			prog.setCancelable(false);
			prog.setIndeterminate(false);
			prog.setMax(100);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.show();
		}

		@Override
		protected ArrayList<ImageModel> doInBackground(String... params) {

			ImageModel f;
			String targetPath = params[0];
			File targetDirectory = new File(targetPath);
			files = targetDirectory.listFiles();
			int count = files.length;
			if (files == null) {

			}
			try {
				Arrays.sort(files, new Comparator(){

						@Override
						public int compare(Object p1, Object p2) {
							if (((File)p1).lastModified() > ((File)p2).lastModified()) {
								return -1;
							} else if (((File)p1).lastModified() < ((File)p2).lastModified()) {
								return +1;
							} else {
								return 0;
							}
						}
					});
				for (File fi : files) {
					if (fi.getName().toLowerCase().endsWith(".jpg") 
                        || fi.getName().toLowerCase().endsWith(".png")
                        || fi.getName().toLowerCase().endsWith(".jpeg")) {
						f = new ImageModel();
						f.setName(fi.getName());
						f.setUri(Uri.fromFile(fi));
						f.setPath(fi.getAbsolutePath());
						f.setFileName(fi.getName());
						filesList.add(f);

					}
				}
				for (int i = 0; i < count; i++) {
					publishProgress((int) ((i / (float) count) * 100));

					if (isCancelled())break;
				}

			} catch (Exception e ) {
				e.printStackTrace();
			}


			return filesList;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			prog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<ImageModel> result) {
			super.onPostExecute(result);

			recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
			recyclerView.setHasFixedSize(true);
			adapter = new ImageAdapter(MainActivity.this, result);
			recyclerView.setAdapter(adapter);
			adapter.notifyDataSetChanged();


			if (prog != null && prog.isShowing()) {
				prog.dismiss();
			}
		}

	}
}

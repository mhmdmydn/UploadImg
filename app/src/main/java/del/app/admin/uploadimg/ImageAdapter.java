package del.app.admin.uploadimg;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.net.URLEncoder;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.text.Html;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import android.graphics.drawable.BitmapDrawable;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

	private Context context;
	private Bitmap bitmap;
	private Boolean check = true;
	private String getImageName;
	private ArrayList<ImageModel> img;
	private String URL = "URL Here";


	public ImageAdapter(Context context, ArrayList<ImageModel> img) {
		this.context = context;
		this.img = img;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_row, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {

		final ImageModel im = img.get(position);
		final Uri uri = Uri.parse(im.getUri().toString());
		final File file = new File(im.getPath());

		holder.title.setText(im.getName());
		Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
		holder.img.setImageDrawable(new BitmapDrawable(image));

		holder.img.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) {
					BitmapFactory.Options bmOptions = new BitmapFactory.Options();
					bitmap = BitmapFactory.decodeFile(im.getPath(), bmOptions);
					bitmap = Bitmap.createScaledBitmap(bitmap, holder.img.getWidth(), holder.img.getHeight(), true);
					getImageName = im.getName();

					uploadToServer();
				}
			});
	}

	@Override
	public int getItemCount() {
		return img.size();
	}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        public ViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);

        }
	}


	public void uploadToServer() {

		ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

		class UploadTask extends AsyncTask<Void, Void, String>{

			ProgressDialog prog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				prog  = new ProgressDialog(context);
				prog.setTitle("Upload Image");
				prog.setMessage("Please wait...");
				prog.setCancelable(false);
				prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				prog.show();
			}

			@Override
			protected String doInBackground(Void[] p1) {

				ImageProsessClass ipc = new ImageProsessClass();

				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("image_name", getImageName);
				hm.put("image_path", ConvertImage);

				String finalData = ipc.ImageHttpRequest(URL, hm);

				return finalData;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				showMsg(result);

				if (prog != null && prog.isShowing()) {
					prog.dismiss();
				}
			}
		}
		UploadTask runTaskUpload = new UploadTask();
		runTaskUpload.execute();
	}

	public class ImageProsessClass {

		public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();
            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
				//showMsg(e.toString());
            }
            return stringBuilder.toString();
        }
    }
    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

		StringBuilder stringBuilderObject;
		stringBuilderObject = new StringBuilder();

		for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

			if (check)
				check = false;
			else
				stringBuilderObject.append("&");

			stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
			stringBuilderObject.append("=");
			stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
		}

		return stringBuilderObject.toString();
	}

	private void showMsg(final String s) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Result")
		    .setMessage(Html.fromHtml(s))
			.setCancelable(false)

			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); 
					ClipData clip = ClipData.newPlainText("Copy", s);
					clipboard.setPrimaryClip(clip);
					builder.create().dismiss();
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}
}

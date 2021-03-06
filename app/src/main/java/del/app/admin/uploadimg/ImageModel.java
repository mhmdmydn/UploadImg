package del.app.admin.uploadimg;

import android.net.Uri;

public class ImageModel {
    private String name;
	private String path;
	private String fileName;
	private Uri uri;

	public ImageModel(){
		
	}
	
	public ImageModel(String name, String path, String fileName, Uri uri){
		this.name = name;
		this.path = path;
		this.fileName = fileName;
		this.uri = uri;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return path;
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public void setUri(Uri uri){
		this.uri = uri;
	}
	
	public Uri getUri(){
		return uri;
	}
}

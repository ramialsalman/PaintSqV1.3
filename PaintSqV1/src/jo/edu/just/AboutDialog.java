package jo.edu.just;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.graphics.Color;
import android.widget.TextView;
public class AboutDialog extends Dialog{
	private static Context mContext = null;
	
	private final  String Data = 
			"<h3>Android Sketching and Editing Tool (ASET)</h3>" +
			"Version 1.0<br>" +
			"People:<br>" +
			"<b>Rami Al-Salman</b><br>" +
			"University of Bremen<br>" +
			"<a href=\"mailto:rami@informatik.uni-bremen.com\">rami@informatik.uni-bremen.com</a><br> <br>" +
    
    		"<b>Dr.Mohammed Fraiwan</b><br>" +
    		"Jordan University of science and technology<br>" +
    		"<a href=\"mailto:mafraiwan@just.edu.jo\">mafraiwan@just.edu.jo</a><br> <br>" +
    
 			"<b>Hosam Ershedat</b><br>" +
 			"Jordan University of science and technology<br>" +
 			"<a href=\"mailto:powerhhr@gmail.com\">powerhhr@gmail.com</a>" ;
    
	
	
	private final String nfo = "ASET is a collaborative project between University of Bremen and Jordan University of Science and Technology to search the internet via sketching!";
	
	public AboutDialog(Context context) {
		super(context);
		mContext = context;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.about_dialog);
		TextView tv = (TextView)findViewById(R.id.legal_text);
		tv.setText(nfo);
		tv = (TextView)findViewById(R.id.info_text);
		tv.setText(Html.fromHtml(Data));
		//tv.setLinkTextColor(Color.WHITE);
		
		Linkify.addLinks(tv, Linkify.ALL);
	}
}
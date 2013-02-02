package jo.edu.just.Shapes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.text.format.Time;

public class joPath extends joShape{
	private List<Float>  pts = new ArrayList<Float>();
	
	
	private boolean finalized = false;
	private float[] Lines = null;
	private float[] ct = null;
	
	private Path path = new Path();
	
	public joPath(){
		Time x = new Time();
		x.setToNow();
		CreateTime  = x;
	}
	public joPath(int _LineColor,float Linewidth){
		this();
		LineColor  = _LineColor;
		StrokeWidth = Linewidth;
	}
	
	public JoPoint GetTrasformedPoint(int i,float ScreenWidth,float ScreenHeight){
		//1-translate
		//2-rotate
		//3-scale
		i = i*2;//  (i,i+1)==(x,y) point :P
		JoPoint pt = new JoPoint(pts.get(i),pts.get(i+1));
		float [] ct = GetCenterPoint();
		
		pt.x  += translateX;
		pt.y  += translateY;
		ct[0] += translateX;
		ct[1] += translateY;
		
		pt.x  -= ScreenWidth/2; //convert to cartesian coordinates 
		pt.y   = ScreenHeight/2 -pt.y;
		
		ct[0] -= ScreenWidth/2;
		ct[1]  = ScreenHeight/2 - ct[1]; 
		
		//translate points to the center
		pt.x =  pt.x -  ct[0];
		pt.y =  pt.y -  ct[1];
		
		//rotate around the center
		float xbar =  (float)( pt.x * Math.cos(Angle* (180/Math.PI)) + pt.y * Math.sin(Angle*(180/Math.PI)));
		float ybar =  (float)(-pt.x * Math.sin(Angle* (180/Math.PI)) + pt.y * Math.cos(Angle*(180/Math.PI)));
		
		//translate back
		pt.x = xbar + ct[0];
		pt.y = ybar + ct[1];
		
		pt.x *= scale;
		pt.y *= scale;
		
	//	convert to normalized coordinates   -1 .... 0 .... 1
		pt.x /= ScreenWidth ;
		pt.y /= ScreenHeight;
		
		return pt;
		
		
	}
	public int PointsCount(){
		return pts.size()/2;
	}
	
	public void AddLine (float x1,float y1,float x2 , float y2){
		pts.add(x1);
		pts.add(y1);
		pts.add(x2);
		pts.add(y2);
	}
	
	@Override
	public void EndShape(){
		ct = GetCenterPoint();
		//Lines = GetLines();
		finalized=true;
		
		path.reset();
		for(int i=0 ; i < pts.size()-4;i+=6){
			if (i==0 ) path.moveTo(pts.get(i), pts.get(i+1));
			else
				path.lineTo(pts.get(i),pts.get(i+1));
			
			path.lineTo(pts.get(i+2), pts.get(i+3));
			
			//if ((i+2)%4 ==0 ) i = i+2;//skip the two points 
			
		}
		
		
		super.EndShape();
	}
	
	public  float[]  GetLines (){
		float [] x = new float[pts.size() ];
		
		for (int i = 0; i < pts.size(); i++) {
		    x[i] = pts.get(i);
		}

		return x;
	}
	
	@Override
	public void Draw(Canvas cav,Paint paint){
		cav.save(Canvas.MATRIX_SAVE_FLAG);
		
		if(!finalized){
			ct = GetCenterPoint();
			//Lines = GetLines();
			
			
			path.reset();
			for(int i=0 ; i < pts.size()-4;i+=6){
				if (i==0 ) path.moveTo(pts.get(i), pts.get(i+1));
				else
					path.lineTo(pts.get(i),pts.get(i+1));
				
				path.lineTo(pts.get(i+2), pts.get(i+3));
				
				//if ((i+2)%4 ==0 ) i = i+2;//skip the two points 
				
			}
			
			
			
			
		}
		cav.translate(translateX, translateY);
		cav.rotate(Angle,ct[0],ct[1]);
		cav.scale(scale, scale,ct[0],ct[1]);
		
		//android.util.Log.d("translate nfo","trans x "+ String.valueOf(translateX));
		//android.util.Log.d("translate nfo","trans y "+ String.valueOf(translateX));
		//android.util.Log.d("translate nfo","angle " + String.valueOf(Angle));
		//android.util.Log.d("translate nfo", "centerpx: "+ String.valueOf(ct[0]));
		//android.util.Log.d("translate nfo", "cenmterpy: " + String.valueOf(ct[1]));
		//android.util.Log.d("translate nfo","scale: " + String.valueOf(scale));
		
		
		int tmpColor = paint.getColor();
		
		float tmpWidth = paint.getStrokeWidth();
		Paint.Style tmpS   = paint.getStyle();
		
		
		
		paint.setStyle(Paint.Style.STROKE);
 		paint.setColor(getColor());
 		paint.setStrokeWidth(StrokeWidth);
 		
 		paint.setStrokeCap(Cap.ROUND);   
		paint.setStrokeJoin(Join.ROUND);   
		paint.setStrokeMiter(2.0f); 
		paint.setStyle(Paint.Style.STROKE);   	
 		
 //-		
 		//cav.drawLines(Lines ,paint );
		cav.drawPath(path, paint);
 //-
 		//------------------------
		paint.setColor(tmpColor);
		paint.setStyle(tmpS);
		paint.setStrokeWidth(tmpWidth);
		cav.restore();
		
		
		
	
	

	}

	@Override
	public  float [] GetMinBoundBox(){
		float [] tmp   =  {0,0,0,0};
		tmp[2] = tmp[0] = pts.get(0); //MinX
		tmp[3] = tmp[1] = pts.get(1); //MinY
		
		
		
		for (int i=2; i< pts.size()-1 ;i+=2){
			if (tmp[0] > pts.get(i)  ) tmp[0] = pts.get(i);  //minX
			if (tmp[2] < pts.get(i)  ) tmp[2] = pts.get(i);  //MaxX
			
			if (tmp[1] > pts.get(i+1)) tmp[1] = pts.get(i+1);  //minY
			if (tmp[3] < pts.get(i+1)) tmp[3] = pts.get(i+1);  //minX

		}
		
		tmp[0] -= StrokeWidth/2.0;
		tmp[1] -= StrokeWidth/2.0;
		tmp[2] += StrokeWidth/2.0;
		tmp[3] += StrokeWidth/2.0;
		return tmp;
		
	}
	@Override
	public  float[] GetCenterPoint(){
		float []  MB = GetMinBoundBox();
		return new float[]{(MB[0]+MB[2])/2  ,(MB[1]+MB[3])/2 };
	}
}

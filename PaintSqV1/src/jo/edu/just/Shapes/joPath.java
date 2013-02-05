package jo.edu.just.Shapes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import android.R.bool;
import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.text.format.Time;

public class joPath extends joShape{
	private List<JoPoint>  pts = new ArrayList<JoPoint>();
	
	
	private boolean finalized = false;
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
	
	public JoPoint GetTrasformedPoint(JoPoint pt,float ScreenWidth,float ScreenHeight,Boolean withNormalization){
		//1-translate
		//2-rotate
		//3-scale
		
		
		float [] ctW  = this.ct;    //= GetCenterPoint();
		
		
	//	pt.x  = 0;
	//	pt.y = 0;
		
	//	Angle = (float) Math.PI;
		
		pt.x  += translateX;
		pt.y  += translateY;
		ctW[0] += translateX;
		ctW[1] += translateY;
		
		pt.x *= scale;
		pt.y *= scale;
		
		pt.x  -= ScreenWidth/2; //convert to cartesian coordinates 
		pt.y   = ScreenHeight/2 -pt.y;
		
		ctW[0] -= ScreenWidth/2;
		ctW[1]  = ScreenHeight/2 - ctW[1]; 
		
		//translate points to the center
		pt.x =  pt.x -  ctW[0];
		pt.y =  pt.y -  ctW[1];
		
		//rotate around the center
		
		
		float xbar =  (float)( pt.x * Math.cos(Angle* (180/Math.PI)) + pt.y * Math.sin(Angle*(180/Math.PI)));
		float ybar =  (float)(-pt.x * Math.sin(Angle* (180/Math.PI)) + pt.y * Math.cos(Angle*(180/Math.PI)));
		
		//translate back
		pt.x = xbar + ctW[0];
		pt.y = ybar + ctW[1];
		
		
		
	//	convert to normalized coordinates   -1 .... 0 .... 1
		if (withNormalization){
			pt.x /= ScreenWidth ;
			pt.y /= ScreenHeight;
		}else{//convert again to our mode not cartesian
			pt.x += ScreenWidth/2;
			pt.y  = - (pt.y - ScreenHeight/2);
		}
		return pt;
		
		
	}
	public int PointsCount(){
		return pts.size();
	}
	
	public JoPoint getPoint (int i){
		return pts.get(i);
	}
	public void setPoint(int i, JoPoint pt){
		pts.get(i).x = pt.x;
		pts.get(i).y = pt.y;
	}

	public void AddPoint(float x ,float y){
		pts.add(new JoPoint(x,y));
	}
	@Override
	public void EndShape(){
		ct = GetCenterPoint();
		//Lines = GetLines();
		finalized=true;
		
		UpdatePath(true);
		super.EndShape();
	}
	
	private void UpdatePath(Boolean withSmoothing ){
		//path.reset();
		//for(int i=0 ; i < pts.size()-4;i+=6){
				//	if (i==0 ) path.moveTo(pts.get(i), pts.get(i+1));
				//	else
				//		path.lineTo(pts.get(i),pts.get(i+1));
				//	path.lineTo(pts.get(i+2), pts.get(i+3));
				//}
		if (!withSmoothing ){
		//without smoothing 
			path.reset();
			for (int i=0 ; i< pts.size();i++){
				if (i==0 ) path.moveTo(pts.get(i).x, pts.get(i).y);
				else       path.lineTo(pts.get(i).x, pts.get(i).y);
			
			}
		//with smoothing 
		}else{
			/*-----------------------------------------------------------
			
			if(PointsCount() > 1){
		        for(int i = PointsCount() - 2; i <PointsCount(); i++){
		            if(i >= 0){
		                JoPoint point = pts.get(i);

		                if(i == 0){
		                	JoPoint next = pts.get(i + 1);
		                    point.dx = ((next.x - point.x) / 3);
		                    point.dy = ((next.y - point.y) / 3);
		                }
		                else if(i == PointsCount() - 1){
		                	JoPoint prev = pts.get(i - 1);
		                    point.dx = ((point.x - prev.x) / 3);
		                    point.dy = ((point.y - prev.y) / 3);
		                }
		                else{
		                	JoPoint next = pts.get(i + 1);
		                	JoPoint prev = pts.get(i - 1);
		                    point.dx = ((next.x - prev.x) / 3);
		                    point.dy = ((next.y - prev.y) / 3);
		                }
		            }
		        }
		    }
			path.reset();
		    boolean first = true;
		    for(int i = 0; i < PointsCount(); i++){
		    	JoPoint point = pts.get(i);
		        if(first){
		            first = false;
		            path.moveTo(point.x, point.y);
		        }
		        else{
		        	JoPoint prev = pts.get(i - 1);
		            path.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
		        }
		    }
			*/
			
			path.reset();
			if (pts.size() > 1) {
			    JoPoint prevPoint = null;
			    for (int i = 0; i < pts.size(); i++) {
			        JoPoint point = pts.get(i);

			        if (i == 0) {
			            path.moveTo(point.x, point.y);
			        } else {
			            float midX = (prevPoint.x + point.x) / 2;
			            float midY = (prevPoint.y + point.y) / 2;

			            if (i == 1) {
			                path.lineTo(midX, midY);
			            } else {
			                path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
			            }
			        }
			        prevPoint = point;
			    }
			    path.lineTo(prevPoint.x, prevPoint.y);
			}
		}
	
	
	}
	

	
	@Override
	public void Draw(Canvas cav,Paint paint){
		cav.save(Canvas.MATRIX_SAVE_FLAG);
		
		if(!finalized){
			ct = GetCenterPoint();
			UpdatePath(false);	
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
		tmp[2] = tmp[0] = pts.get(0).x; //MinX
		tmp[3] = tmp[1] = pts.get(1).y; //MinY
		
		
		
		for (int i=2; i< pts.size() ;i++){
			if (tmp[0] > pts.get(i).x  ) tmp[0] = pts.get(i).x;  //minX
			if (tmp[2] < pts.get(i).x  ) tmp[2] = pts.get(i).x;  //MaxX
			
			if (tmp[1] > pts.get(i).y) tmp[1] = pts.get(i).y;  //minY
			if (tmp[3] < pts.get(i).y) tmp[3] = pts.get(i).y;  //minX

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

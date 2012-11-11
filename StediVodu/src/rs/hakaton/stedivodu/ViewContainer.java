package rs.hakaton.stedivodu;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewContainer {
  private float x, y;
  public View view;
  
  public float getX() {
      return x;
  }
  
  public float getY() {
      return y;
  }

  public void setX(float x) {
      this.x = x;
      
      /**
       * ATTENTION FOR Android less than 3.0 do this.
       * Why Relative? Because our cointainer in RelativeContainer
       */
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
      params.leftMargin= (int)x;
      view.setLayoutParams(params);
      
      
      /**
       * IF FOR android 3.0 and more 
       */
      //view.setX(x);
     
  }

  public void setY(float y) {
      this.y = y;
     
      /**
       * ATTENTION FOR Android less than 3.0 do this.
       */
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
      params.topMargin = (int)y;
      view.setLayoutParams(params);
      
      
      /**
       * IF FOR android 3.0 and more 
       */
      //view.setX(x);
      
      //Povecavaj procente
      if (GraphicActivity.needToIncresePercentage) {
    	  TextView perc = (TextView) view.findViewById(R.id.textViewYourPercentage);
    	  if (perc != null) { //null je kada se pomera marker, ovde treba da se pomera voda
    		  float finalY = GraphicActivity.vodaContainerFinalPosY;
              float finalValue = GraphicActivity.prosekProcenat;
              
              float currentPercOfY = y / finalY * 100;
              float currentPercOfValue = finalValue * currentPercOfY / 100;
              
              
              perc.setText(Math.round(currentPercOfValue) + "%");
    	  }
          
      }
      
     
  }
}
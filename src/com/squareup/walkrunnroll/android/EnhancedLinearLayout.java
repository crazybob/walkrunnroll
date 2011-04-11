package com.squareup.walkrunnroll.android;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author Bob Lee (bob@squareup.com)
 */
public class EnhancedLinearLayout extends LinearLayout {

  public EnhancedLinearLayout(Context context) {
    super(context);
  }

  public void addView(View view, int width, int height, float weight) {
    LinearLayout.LayoutParams params = new LayoutParams(
        width, height, weight);
    addView(view, params);
  }  
}

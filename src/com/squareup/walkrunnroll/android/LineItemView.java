package com.squareup.walkrunnroll.android;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;

/**
 * @author Bob Lee (bob@squareup.com)
 */
public class LineItemView extends EnhancedLinearLayout
    implements LineItem.Listener {

  private final LineItem lineItem;
  private final TextView priceView;
  private final ImageView removeButton;

  private final float dp;

  public LineItemView(Context context, final LineItem lineItem) {
    super(context);
    this.lineItem = lineItem;

    setOrientation(HORIZONTAL);

    dp = context.getResources().getDimension(
        com.squareup.walkrunnroll.android.R.dimen.dp);

    int buttonSize = (int) (96 * dp);

    ImageView itemButton = new ImageView(context);
    itemButton.setImageResource(lineItem.product().iconId);
    itemButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        lineItem.add();
      }
    });
    itemButton.setScaleType(ImageView.ScaleType.FIT_XY);
    itemButton.setPadding((int) (4 * dp), (int) (2 * dp), (int) (8 * dp),
        (int) (2 * dp));

    addView(itemButton, buttonSize, buttonSize);

    priceView = new TextView(context);
    priceView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    priceView.setTypeface(Typeface.DEFAULT_BOLD);
    addView(priceView, FILL_PARENT, FILL_PARENT, 1);

    removeButton = new ImageView(context);
    removeButton.setImageResource(R.drawable.ic_delete);
    removeButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        lineItem.remove();
      }
    });
    removeButton.setScaleType(ImageView.ScaleType.CENTER);
    addView(removeButton, buttonSize >> 1, buttonSize);

    lineItem.add(this);

    onChange();
  }

  public void onChange() {
    if (lineItem.quantity() > 0) {
      priceView.setText(String.format("\u00D7%d = $%d",
          lineItem.quantity(), lineItem.price()));
      priceView.setTextSize(24);
      priceView.setTextColor(Color.DKGRAY);
      priceView.setShadowLayer(1, 1, 1, Color.rgb(0x99, 0x99, 0x99));

      removeButton.setVisibility(View.VISIBLE);
    } else {
      Product product = lineItem.product();
      priceView.setText(product.label + " - $" + product.price);
      priceView.setTextSize(16);
      priceView.setTextColor(Color.GRAY);
      priceView.setShadowLayer(1, 1, 1, Color.rgb(0xee, 0xee, 0xee));

      removeButton.setVisibility(View.GONE);
    }
  }
}

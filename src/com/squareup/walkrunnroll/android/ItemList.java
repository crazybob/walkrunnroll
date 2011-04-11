// Copyright 2011 Square, Inc.
package com.squareup.walkrunnroll.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author Bob Lee (bob@squareup.com)
 */
public class ItemList extends LinearLayout {

  /*
   * TODO: Support saving state.
   */

  private final List<LineItem> lineItems = new ArrayList<LineItem>();

  public ItemList(Context context, AttributeSet attrs) {
    super(context, attrs);

    setOrientation(LinearLayout.VERTICAL);

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        FILL_PARENT, WRAP_CONTENT);

    for (Item product : Item.values()) {
      LineItem lineItem = new LineItem(product);
      lineItems.add(lineItem);
      addView(new LineItemView(context, lineItem), params);
    }
  }

  public List<LineItem> items() {
    return lineItems;
  }

  public int computeTotal() {
    int total = 0;
    for (LineItem lineItem : lineItems) {
      total += lineItem.price();
    }
    return total;
  }
}

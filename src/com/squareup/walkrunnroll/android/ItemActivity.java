package com.squareup.walkrunnroll.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.squareup.android.*;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author Bob Lee (bob@squareup.com)
 */
public class ItemActivity extends Activity
    implements LineItem.Listener {

  private final List<LineItem> lineItems = new ArrayList<LineItem>();
  private TextView totalValue;
  private Button clear;
  private Button checkOut;

  float dp;

  @Override protected void onCreate(Bundle state) {
    super.onCreate(state);

    Window w = getWindow();
    w.setFormat(PixelFormat.RGBA_8888);

    dp = getResources().getDimension(
        com.squareup.walkrunnroll.android.R.dimen.dp);

    EnhancedLinearLayout layout = new EnhancedLinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);

    layout.addView(newItemList(), FILL_PARENT, 0, 1);
    layout.addView(newBorder(), FILL_PARENT, (int) dp);
    layout.addView(newTotalLayout(), FILL_PARENT, WRAP_CONTENT);
    layout.addView(newBorder(), FILL_PARENT, (int) dp);
    layout.addView(newButtonLayout(), FILL_PARENT, WRAP_CONTENT);

    setContentView(layout);

    onChange();
  }

  @Override protected void onResume() {
    super.onResume();
    inputEnabled = true;
  }

  public void onChange() {
    int total = computeTotal();
    totalValue.setText("$" + total);

    if (total == 0) {
      clear.setEnabled(false);
      checkOut.setEnabled(false);
    } else {
      clear.setEnabled(true);
      checkOut.setEnabled(true);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      clear();
    }
  }

  private int computeTotal() {
    int total = 0;
    for (LineItem lineItem : lineItems) {
      total += lineItem.price();
    }
    return total;
  }

  private EnhancedLinearLayout newTotalLayout() {
    EnhancedLinearLayout totalLayout = new EnhancedLinearLayout(this);

    totalLayout.setBackgroundColor(Color.rgb(0xCC, 0xCC, 0xCC));
    totalLayout.setPadding((int) (12 * dp), (int) (2 * dp), (int) (12 * dp),
        (int) (5 * dp));

    TextView totalLabel = newTotalTextView();
    totalLabel.setText("Total");
    totalLabel.setGravity(Gravity.LEFT);

    totalValue = newTotalTextView();
    totalValue.setText("$0");
    totalValue.setGravity(Gravity.RIGHT);

    totalLayout.addView(totalLabel, 0, WRAP_CONTENT, 1);
    totalLayout.addView(totalValue, 0, WRAP_CONTENT, 2);
    return totalLayout;
  }

  private TextView newTotalTextView() {
    TextView textView = new TextView(this);
    textView.setTextSize(26);
    textView.setTypeface(Typeface.DEFAULT_BOLD);
    textView.setShadowLayer(1, 1, 1, Color.WHITE);
    return textView;
  }

  protected View newBorder() {
    View border = new View(this);
    border.setBackgroundColor(Color.rgb(0x66, 0x66, 0x66));
    return border;
  }


  private ScrollView newItemList() {
    ScrollView scrollView = new ScrollView(this);
    scrollView.setBackgroundColor(Color.WHITE);

    LinearLayout itemList = new LinearLayout(this);

    itemList.setOrientation(LinearLayout.VERTICAL);

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        FILL_PARENT, WRAP_CONTENT);

    for (Product product : products()) {
      LineItem lineItem = new LineItem(product);
      lineItem.add(this);
      lineItems.add(lineItem);
      itemList.addView(new LineItemView(this, lineItem), params);
    }

    scrollView.addView(itemList);
    return scrollView;
  }

  private EnhancedLinearLayout newButtonLayout() {
    EnhancedLinearLayout buttonLayout = new EnhancedLinearLayout(this);

    clear = new Button(this);
    clear.setText("Clear");
    clear.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        clear();
      }
    });
    buttonLayout.addView(clear, 0, FILL_PARENT, 1);

    buttonLayout.addView(new View(this), 0, 0, 1); // spacer

    checkOut = new Button(this);
    checkOut.setText("Check Out");
    buttonLayout.addView(checkOut, 0, FILL_PARENT, 1);
    checkOut.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        inputEnabled = false;
        checkOut();
      }
    });


    buttonLayout.setBackgroundResource(android.R.drawable.bottom_bar);

    buttonLayout.setPadding((int) (4 * dp), (int) (5 * dp), (int) (4 * dp),
        (int) dp);

    return buttonLayout;
  }

  private void clear() {
    for (LineItem lineItem : lineItems) lineItem.clear();
  }

  private void checkOut() {
    int total = computeTotal();

    StringBuilder note = new StringBuilder();
    boolean first = true;
    for (LineItem lineItem : lineItems) {
      if (lineItem.quantity() > 0) {
        if (!first) {
          note.append(", ");
        }
        first = false;
        Product product = lineItem.product();
        note.append(lineItem.quantity()).append(' ').append(
            lineItem.quantity() == 1 ? product.label : product.pluralLabel);
      }
    }
    if (note.length() > 140) {
      note.setLength(140 - 3);
      note.append("...");
    }

    com.squareup.android.LineItem lineItem
        = new com.squareup.android.LineItem.Builder()
            .price(total * 100, Currency.USD)
            .description(note.toString())
            .build();

    Bill bill = Bill.containing(lineItem);

    new Square(this).squareUp(bill);
  }

  private boolean inputEnabled = true;

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    return !inputEnabled || super.dispatchTouchEvent(ev);
  }

  Product[] products() {
    throw new UnsupportedOperationException();
  }
}

package com.squareup.walkrunnroll.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.squareup.android.Bill;
import com.squareup.android.Currency;
import com.squareup.android.Square;

public class Main extends Activity {

  private static final int VALIDATION_DIALOG = 0;
  private static final int CLEAR_DIALOG = 2;
  private static final int EXIT_DIALOG = 3;

  private Spinner agency;
  private TextView name;
  private TextView amount;
  private ItemList items;

  private int dollars;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFormat(PixelFormat.RGBA_8888);

    setContentView(R.layout.main);

    name = findTextViewById(R.id.name);
    amount = findTextViewById(R.id.amount);

    items = (ItemList) findViewById(R.id.items);

    configureAgencyList();
    configureButtons();

    amount.setKeyListener(new AmountListener());
    amount.setCursorVisible(false);
  }

  @Override protected void onResume() {
    super.onResume();
    
    inputEnabled = true;
  }

  private void configureButtons() {
    findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        pay();
      }
    });

    findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(CLEAR_DIALOG);
      }
    });
  }

  private void configureAgencyList() {
    agency = (Spinner) findViewById(R.id.agency);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.agencies, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    agency.setAdapter(adapter);
  }

  private static final String AMOUNT_KEY = "amount";

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(AMOUNT_KEY, dollars);
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    dollars = savedInstanceState.getInt(AMOUNT_KEY);
  }

  /**
   * Updates the amount field after key events.
   */
  private void updateAmount() {
    if (dollars == 0) {
      amount.setText("");
    } else {
      amount.setText("$" + dollars);
    }
  }

  /** Handles pay button. */
  private void pay() {
    if (validate() != null) {
      showDialog(VALIDATION_DIALOG);
      return;
    }

    inputEnabled = false;

    int total = items.computeTotal() + dollars;

    StringBuilder note = new StringBuilder();

    note.append("From ").append(name.getText())
        .append(" benefiting ").append(agency.getSelectedItem())
        .append(": ");

    boolean first = true;
    for (LineItem lineItem : items.items()) {
      if (lineItem.quantity() > 0) {
        if (!first) {
          note.append(", ");
        }
        first = false;
        Item product = lineItem.product();
        note.append(lineItem.quantity()).append(' ').append(
            lineItem.quantity() == 1 ? product.label : product.pluralLabel);
      }
    }

    if (dollars > 0) {
      if (hasItems()) {
        note.append(", ");
      }

      note.append("$").append(dollars).append(" donation");
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

  @Override protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    if (resultCode == RESULT_OK) {
      startOver();
    }
  }

  @Override protected void onPrepareDialog(int id, Dialog dialog) {
    super.onPrepareDialog(id, dialog);

    if (id == VALIDATION_DIALOG) dialog.setTitle(validate());
  }

  @Override protected Dialog onCreateDialog(int id) {
    switch (id) {
      case VALIDATION_DIALOG:
        return new AlertDialog.Builder(this)
          .setCancelable(true)
          .setTitle(validate())
          .setMessage("Please try again.")
          .setNegativeButton("OK", null)
          .create();
      case CLEAR_DIALOG:
        return new AlertDialog.Builder(this)
          .setCancelable(true)
          .setTitle("Clear input?")
          .setPositiveButton("Dismiss", null)
          .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              startOver();
            }
          })
          .create();
      case EXIT_DIALOG:
        return new AlertDialog.Builder(this)
          .setCancelable(true)
          .setTitle("Confirm Exit")
          .setMessage("Existing input will be lost.")
          .setPositiveButton("Dismiss", null)
          .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
          })
          .create();
    }

    throw new AssertionError();
  }

  @Override public void onBackPressed() {
    showDialog(EXIT_DIALOG);
  }

  /**
   * Starts the activity over.
   */
  private void startOver() {
    Intent intent = new Intent(this, Main.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }

  private TextView findTextViewById(int id) {
    return (TextView) findViewById(id);
  }

  /**
   * Validates input. Returns null if all input is valid. Otherwise, switches
   * focus to first invalid field and returns an error message.
   */
  private String validate() {
    if (name.getText().toString().trim().length() == 0) {
      name.requestFocus();
      return "Missing Name";
    }

    if (agency.getSelectedItemPosition() == 0) {
      agency.requestFocus();
      return "Missing Agency";
    }

    if (!hasItems() && dollars == 0) {
      return "Missing Item or Donation";
    }

    return null;
  }

  boolean hasItems() {
    for (LineItem lineItem : items.items()) {
      if (lineItem.quantity() > 0) return true;
    }
    return false;
  }

  private class AmountListener implements KeyListener {
    public int getInputType() {
      return InputType.TYPE_CLASS_PHONE;
    }

    public boolean onKeyDown(View view, Editable text, int keyCode,
        KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_DEL) {
        dollars /= 10;
        updateAmount();
        return true;
      }

      if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9
          && dollars <= 999999) {
        dollars = dollars * 10 + keyCode - KeyEvent.KEYCODE_0;
        updateAmount();
        return true;
      }

      return true;
    }

    public boolean onKeyUp(View view, Editable text, int keyCode,
        KeyEvent event) {
      return true;
    }

    public boolean onKeyOther(View view, Editable text, KeyEvent event) {
      return true;
    }

    public void clearMetaKeyState(View view, Editable content, int states) {
    }
  }

  private boolean inputEnabled = true;

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    return !inputEnabled || super.dispatchTouchEvent(ev);
  }
}

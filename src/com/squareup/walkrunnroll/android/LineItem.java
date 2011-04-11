package com.squareup.walkrunnroll.android;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bob Lee (bob@squareup.com)
 */
public class LineItem {

  private final Product product;
  private int quantity;
  private final List<Listener> listeners = new ArrayList<Listener>();

  public void add(Listener listener) {
    listeners.add(listener);
  }

  public LineItem(Product product) {
    this.product = product;
  }

  public void add() {
    quantity++;
    notifyListeners();
  }

  private void notifyListeners() {
    for (Listener listener : listeners) listener.onChange();
  }

  public void remove() {
    if (quantity > 0) quantity--;
    notifyListeners();
  }

  /** Returns the total price of these items in dollars. */
  public int price() {
    return product.price(quantity);
  }

  /** Sets quantity back to 0. */
  public void clear() {
    quantity = 0;
    notifyListeners();
  }

  public Product product() {
    return product;
  }

  public int quantity() {
    return quantity;
  }

  /** Listens for quantity changes. */
  interface Listener {
    void onChange();
  }
}

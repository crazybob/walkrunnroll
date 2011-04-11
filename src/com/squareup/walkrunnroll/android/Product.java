package com.squareup.walkrunnroll.android;

/**
 * Product definitions.
 *
 * @author Bob Lee (bob@squareup.com)
 */
public enum Product {

  ;

  final int iconId;
  final String label;
  final String pluralLabel;
  final int price;

  Product(int iconId, String label, int price) {
    this(iconId, label, label + "s", price);
  }

  Product(int iconId, String label, String pluralLabel, int price) {
    this.iconId = iconId;
    this.label = label;
    this.pluralLabel = pluralLabel;
    this.price = price;
  }

  /** Price for the given quantity in dollars. */
  int price(int quantity) {
    return price * quantity;
  }
}

package com.squareup.walkrunnroll.android;

/**
 * Item definitions.
 *
 * @author Bob Lee (bob@squareup.com)
 */
public enum Item {

  ADULT("Adult", 30),
  CHILD("Child", "Children", 10),
  TRIBUTE("Tribute", 25),
  BIB("Bib", 1),
  BRACELET("Bracelet", 3)
  ;

  final String label;
  final String pluralLabel;
  final int price;

  Item(String label, int price) {
    this(label, label + "s", price);
  }

  Item(String label, String pluralLabel, int price) {
    this.label = label;
    this.pluralLabel = pluralLabel;
    this.price = price;
  }

  /** Price for the given quantity in dollars. */
  int price(int quantity) {
    return price * quantity;
  }
}

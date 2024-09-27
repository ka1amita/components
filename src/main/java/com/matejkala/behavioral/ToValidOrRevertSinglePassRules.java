package com.matejkala.behavioral;

import static com.matejkala.validations.Validations.requireNotEmpty;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

class ToValidOrRevertSinglePassRules<E> implements ToOneReducer<E> {
  
  private final Iterator<Predicate<E>> rules;
  
  @SafeVarargs
  ToValidOrRevertSinglePassRules(final Predicate<E>... rules) {
    this.rules = Arrays.asList(rules).iterator();
  }
  
  @Override
  public E reduce(final Iterable<? extends E> elements) throws UnreducableException {
    final List<E> remaining = new ArrayList<>();
    requireNonNull(elements).forEach(remaining::add);
    requireNotEmpty(remaining);
    
    final var reduced = reduceInt(remaining);
    if (reduced.size() == 1) {
      return reduced.getFirst();
    } else {
      throw UnreducableException.of(remaining.size());
    }
  }
  
  protected List<E> reduceInt(List<E> remaining) throws UnreducableException {
    while (rules.hasNext()) {
      final List<E> filtered = remaining.stream().filter(rules.next()).toList();
      if (filtered.size() == 1) {
        return filtered;
      }
      if (filtered.size() > 1) {
        remaining = filtered;
      }
    }
    if (remaining.size() == 1) {
      throw UnreducableException.of(1); // it is a single but invalid element
    }
    return remaining;
  }
}

package com.matejkala.behavioral.toonereducer;

import static com.matejkala.validations.Validations.requireSizeAtLeast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

class ToValidOrRevertSinglePassPredicateRules<E> implements ToOneReducer<E> {
  
  private final Iterator<Predicate<? super E>> rules;
  
  @SafeVarargs
  ToValidOrRevertSinglePassPredicateRules(final Predicate<? super E>... rules) {
    this.rules = Arrays.asList(rules).iterator();
  }
  
  @Override
  public E reduce(final Collection<? extends E> elements) throws UnreducableException {
    final var reduced = reduceInt(elements);
    if (reduced.size() == 1) {
      return reduced.getFirst();
    } else {
      throw UnreducableException.of(reduced.size());
    }
  }
  
  List<E> reduceInt(Collection<? extends E> elements) throws UnreducableException {
    List<E> remaining = new ArrayList<>(requireSizeAtLeast(2, elements));
    while (rules.hasNext()) {
      final List<E> filtered = remaining.stream().filter(rules.next()).toList();
      if (filtered.size() == 1) {
        return filtered;
      }
      if (filtered.size() > 1) {
        remaining = filtered;
      }
    }
    return remaining;
  }
}

package com.matejkala.behavioral.toonereducer;

import static com.matejkala.validations.Validations.requireSizeAtLeast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;

class ToValidOrRevertSinglePassCollectorRules<E> implements ToOneReducer<E> {
  
  private final Iterator<Collector<E, Collection<E>, Collection<E>>>
      rules;
  
  @SafeVarargs
  ToValidOrRevertSinglePassCollectorRules(
      final Collector<E, Collection<E>, Collection<E>>... rules) {
    this.rules = Arrays.asList(rules).iterator();
  }
  
  @Override
  public E reduce(final Collection<? extends E> elements) throws UnreducableException {
    final var reduced = reduceInt(elements);
    if (reduced.size() == 1) {
      return reduced.iterator().next();
    } else {
      throw UnreducableException.of(reduced.size());
    }
  }
  
  Collection<? extends E> reduceInt(Collection<? extends E> elements) {
    Collection<? extends E> remaining = new ArrayList<>(requireSizeAtLeast(2, elements));
    while (rules.hasNext()) {
      final Collection<? extends E> filtered = remaining.stream().collect(rules.next());
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

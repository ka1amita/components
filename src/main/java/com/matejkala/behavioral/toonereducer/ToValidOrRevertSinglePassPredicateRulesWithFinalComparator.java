package com.matejkala.behavioral.toonereducer;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

class ToValidOrRevertSinglePassPredicateRulesWithFinalComparator<E> extends
    ToValidOrRevertSinglePassPredicateRules<E> {
  
  private final Comparator<E> comparator;
  
  @SafeVarargs
  ToValidOrRevertSinglePassPredicateRulesWithFinalComparator(final Comparator<E> comparator,
                                                             final Predicate<? super E>... rules) {
    super(rules);
    this.comparator = comparator;
  }
  
  @Override
  List<E> reduceInt(Collection<? extends E> remaining) throws UnreducableException {
    return super.reduceInt(remaining).stream()
                .max(comparator)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
  }
}

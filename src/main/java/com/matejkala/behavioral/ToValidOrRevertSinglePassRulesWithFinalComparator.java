package com.matejkala.behavioral;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

class ToValidOrRevertSinglePassRulesWithFinalComparator<E> extends
    ToValidOrRevertSinglePassRules<E> {
  
  private final Comparator<E> comparator;
  private final Iterator<Predicate<E>> rules;
  
  @SafeVarargs
  ToValidOrRevertSinglePassRulesWithFinalComparator(final Comparator<E> comparator,
                                                    final Predicate<E>... rules) {
    this.comparator = comparator;
    this.rules = Arrays.asList(rules).iterator();
  }
  
  
  @Override
  protected List<E> reduceInt(List<E> remaining) throws UnreducableException {
    return super.reduceInt(remaining).stream()
                .max(comparator)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
  }
}

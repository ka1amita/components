package com.matejkala.behavioral.toonereducer;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collector;

interface ToOneReducer<E> {
  
  @SafeVarargs
  static <E> ToOneReducer<E> of(Predicate<? super E>... rules) {
    return new ToValidOrRevertSinglePassPredicateRules<>(rules);
  }
  
  @SafeVarargs
  static <E> ToOneReducer<E> of(Comparator<E> comparator, Predicate<? super E>... rules) {
    return new ToValidOrRevertSinglePassPredicateRulesWithFinalComparator<>(comparator, rules);
  }
  
  @SafeVarargs
  static <E> ToOneReducer<E> of(Collector<E, Collection<E>, Collection<E>>... rules) {
    return new ToValidOrRevertSinglePassCollectorRules<>(rules);
  }
  
  E reduce(final Collection<? extends E> elements) throws UnreducableException;
  
  class UnreducableException extends Exception {
    
    private final int remains;
    
    private UnreducableException(final int remains, final String message) {
      super(message);
      this.remains = remains;
    }
    
    static UnreducableException of(final int remains) {
      if (remains == 1) {
        return new UnreducableException(remains,
                                        """
                                            Unable to reduce to a single valid element. \
                                            Remains a single element but it is invalid.
                                            """);
      }
      return new UnreducableException(remains, String.format(
          """
              Unable to reduce to a single valid element. \
              Remains: `%s`.""",
          remains));
    }
    
    int remains() {
      return remains;
    }
  }
}

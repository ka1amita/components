package com.matejkala.behavioral;

import static com.matejkala.validations.Validations.requireNotEmpty;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

class ToOneValidOrRevertSinglePassReducer<E> {
  
  private final Iterator<Predicate<E>> rules;
  
  @SafeVarargs
  ToOneValidOrRevertSinglePassReducer(Predicate<E>... rules) {
    this.rules = Arrays.asList(rules).iterator();
  }
  
  public E reduce(final Iterable<? extends E> elements) throws UnreducableException {
    List<E> remaining = new ArrayList<>();
    requireNonNull(elements).forEach(remaining::add);
    requireNotEmpty(remaining);
    
    while (rules.hasNext()) {
      final List<E> filtered = remaining.stream().filter(rules.next()).toList();
      if (filtered.size() == 1) {
        return filtered.getFirst();
      }
      if (filtered.size() > 1) {
        remaining = filtered;
      }
    }
    
    throw UnreducableException.of(remaining.size());
  }
  
  static class UnreducableException extends Exception {
    
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

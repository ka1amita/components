package com.matejkala.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.matejkala.behavioral.ToOneValidOrRevertSinglePassReducer.UnreducableException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ToOneValidOrRevertSinglePassReducerTest {
  
  private record Fake() {
    
    public static final Fake TRUE = new Fake();
    
    public static final Fake FALSE = new Fake();
    public static final Fake SOME = new Fake();
    public static final Fake ANY = new Fake();
    
    private static Predicate<Object> passTrue() {
      return o -> o == Fake.TRUE;
    }
    
    private static Predicate<Object> passSome() {
      return o -> o == Fake.SOME;
    }
  }
  
  @Nested
  class ApiTest {
    
    private static Stream<List<Fake>> subjectCollections() {
      return Stream.of(List.of(Fake.TRUE, Fake.FALSE), List.of(Fake.FALSE, Fake.TRUE));
    }
    
    private static Stream<List<Fake>> variableSizeCollections() {
      return Stream.of(List.of(Fake.TRUE, Fake.TRUE), List.of(Fake.TRUE, Fake.TRUE));
    }
    
    @ParameterizedTest
    @MethodSource("subjectCollections")
    void given_a_single_valid_and_an_invalid_element_When_reduced_Then_returns_the_valid_one(
        final Iterable<Fake> collection) throws UnreducableException {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passTrue());
      
      var result = reducer.reduce(collection);
      
      assertSame(Fake.TRUE, result);
    }
    
    @Test
    void given_more_than_one_valid_element_remains_When_reduced_Then_throws() {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passTrue());
      
      assertThrows(UnreducableException.class, () -> reducer.reduce(List.of(Fake.TRUE, Fake.TRUE)));
    }
    
    @Test
    void given_empty_collection_When_reduced_Then_throws() {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passTrue());
      
      assertThrows(IllegalArgumentException.class, () -> reducer.reduce(Collections.emptyList()));
    }
    
    @ParameterizedTest
    @MethodSource("variableSizeCollections")
    void when_throws_Then_the_exception_contains_the_number_of_remaining_elements(
        final Collection<Fake> collection) {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passTrue());
      
      var exc = assertThrows(UnreducableException.class, () -> reducer.reduce(collection));
      
      assertEquals(exc.remains(), collection.size());
    }
    
    @Test
    void given_no_element_passes_when_throws_Then_the_exception_contains_the_number_of_remaining_elements() {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passTrue());
      
      var exc = assertThrows(UnreducableException.class, () -> reducer.reduce(List.of(Fake.FALSE)));
      
      assertEquals(1, exc.remains());
    }
  }
  
  @Nested
  class RuleTest {
    
    private static Predicate<Object> passAll() {
      return (e) -> true;
    }
    
    private static Predicate<Object> passNone() {
      return (e) -> false;
    }
    
    private static Predicate<Object> passFirst() {
      return new Predicate<>() {
        private int allowed = 1;
        
        @Override
        public boolean test(final Object o) {
          return allowed-- == 0;
        }
      };
    }
    
    @Test
    void given_a_single_valid_element_When_reduced_Then_returns_the_element()
        throws UnreducableException {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(passAll());
      
      var result = reducer.reduce(Set.of(Fake.ANY));
      
      assertNotNull(result);
    }
    
    @Test
    void given_a_single_invalid_element_When_reduced_Then_throws() {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passTrue());
      
      assertThrows(UnreducableException.class, () -> reducer.reduce(List.of(Fake.FALSE)));
    }
    
    @Test
    void given_a_rule_that_no_element_passes_when_reduced_Then_throws() {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(passNone());
      
      assertThrows(UnreducableException.class, () -> reducer.reduce(Set.of(Fake.ANY)));
    }
    
    @Test
    void given_rule_that_that_none_pass_then_passes_original_collection_to_the_next_rule()
        throws UnreducableException {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(passNone(), passFirst());
      
      var result = reducer.reduce(List.of(Fake.ANY, Fake.ANY, Fake.ANY));
      
      assertNotNull(result);
    }
    
    @Test
    void given_rule_that_only_some_pass_then_passes_the_reduced_collection_to_the_next_rule()
        throws UnreducableException {
      var reducer = new ToOneValidOrRevertSinglePassReducer<>(Fake.passSome(), passFirst());
      
      var result = reducer.reduce(List.of(Fake.ANY, Fake.SOME, Fake.SOME, Fake.ANY));
      
      assertSame(Fake.SOME, result);
    }
  }
}

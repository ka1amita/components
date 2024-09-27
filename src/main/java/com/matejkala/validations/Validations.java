package com.matejkala.validations;

import java.util.Collection;

/**
 * Validations convenience functions.
 */
public final class Validations {
  
  private Validations() {
  }
  
  /**
   * Checks that the specified collection is not empty and throws a customized
   * {@link IllegalArgumentException} if it is. This method is designed primarily for doing
   * parameter validation in methods and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(Collection bar) {
   *     this.bar = Validations.requireNotEmpty(bar);
   * }
   * </pre></blockquote>
   *
   * @param subject the collection reference to check for emptiness
   * @param <C>     the type of the collection
   * @return {@code subject} if not empty
   * @throws IllegalArgumentException if {@code subject} is empty
   */
  public static <C extends Collection<?>> C requireNotEmpty(final C subject) {
    return requireNotEmpty(subject, "must not be empty");
  }
  
  /**
   * Checks that the specified collection is not empty and throws a customized
   * {@link IllegalArgumentException} if it is. This method is designed primarily for doing
   * parameter validation in methods and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(Collection bar) {
   *     this.bar = Validations.requireNotEmpty(bar, "bar must not be empty");
   * }
   * </pre></blockquote>
   *
   * @param subject the collection reference to check for emptiness
   * @param message detail message to be used in the event that a {@code IllegalArgumentException}
   *                is thrown
   * @param <C>     the type of the collection
   * @return {@code subject} if not empty
   * @throws IllegalArgumentException if {@code subject} is empty
   */
  public static <C extends Collection<?>> C requireNotEmpty(final C subject, String message) {
    if (subject.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return subject;
  }
}

package org.symphonyoss.symphony.bots.utility.function;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helper class to execute a function using backoff exponential algorithm.
 * <p>
 * Created by rsanchez on 12/18/17.
 */
public class FunctionExecutor<T, R> {

  private static final int[] FIBONACCI = new int[] { 1, 1, 2, 3, 5, 8, 13 };

  private Function<T, R> function;

  private Consumer<Exception> errorListener;

  /**
   * Define function to be executed.
   *
   * @param function Function to be executed.
   * @return Helper class
   */
  public FunctionExecutor<T, R> function(Function<T, R> function) {
    this.function = function;
    return this;
  }

  /**
   * Define error listener. It'll be invoked on error during the function execution.
   *
   * @param errorListener Listener consumer.
   * @return Helper class
   */
  public FunctionExecutor<T, R> onError(Consumer<Exception> errorListener) {
    this.errorListener = errorListener;
    return this;
  }

  /**
   * Execute function using backoff exponential algorithm.
   *
   * @param input Function input
   * @return Function output
   * @throws InterruptedException Thread interrupted
   */
  public R executeBackoffExponential(T input) throws InterruptedException {
    int count = 0;

    do {
      if (count > 0) {
        int index = count % FIBONACCI.length;
        Thread.sleep(TimeUnit.SECONDS.toMillis(FIBONACCI[index]));
      }

      try {
        return function.apply(input);
      } catch (Exception e) {
        handleFailure(e);
      }

      count++;
    } while ( true );
  }

  /**
   * Invokes listener to handle failures.
   *
   * @param e Root cause exception
   */
  private void handleFailure(Exception e) {
    if (errorListener != null) {
      errorListener.accept(e);
    }
  }

}

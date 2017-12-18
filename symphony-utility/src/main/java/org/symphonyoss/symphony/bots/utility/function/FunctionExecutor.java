package org.symphonyoss.symphony.bots.utility.function;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by rsanchez on 18/12/17.
 */
public class FunctionExecutor<T, R> {

  private static final int[] FIBONACCI = new int[] { 1, 1, 2, 3, 5, 8, 13 };

  private Function<T, R> function;

  private Consumer<Exception> errorListener;

  public FunctionExecutor<T, R> function(Function<T, R> function) {
    this.function = function;
    return this;
  }

  public FunctionExecutor<T, R> onError(Consumer<Exception> errorListener) {
    this.errorListener = errorListener;
    return this;
  }

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

  private void handleFailure(Exception e) {
    if (errorListener != null) {
      errorListener.accept(e);
    }
  }

}

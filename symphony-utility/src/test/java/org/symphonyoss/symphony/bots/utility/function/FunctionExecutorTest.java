package org.symphonyoss.symphony.bots.utility.function;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by rsanchez on 18/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class FunctionExecutorTest {

  @Mock
  private Function<Object, Boolean> mockFunction;

  @Mock
  private Consumer<Exception> mockConsumer;

  @Test
  public void testErrorHandlingRetries() throws InterruptedException {
    Exception exception = new RuntimeException();
    doThrow(exception).doReturn(true).when(mockFunction).apply(any());

    FunctionExecutor<Object, Boolean> executor = new FunctionExecutor<>();
    executor.function(mockFunction);
    executor.onError(mockConsumer);

    Object input = new Object();

    executor.executeBackoffExponential(input);

    verify(mockFunction, times(2)).apply(input);
    verify(mockConsumer, times(1)).accept(exception);
  }
}

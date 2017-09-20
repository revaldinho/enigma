/**
* Allows timing of the execution of any block of code.
*/
import java.lang.reflect.*;
import java.lang.*;


public final class Stopwatch {

    /** Returns the time in milliseconds to execute a number of iterations of a a named method in a named class. The time taken to load the class is excluded from the execution time. 
</p>Example: long t = TimeMethodExecution( "MyClass", "main", new String[] { "arg1", "arg2"}, 100) </p>
calls the main method of MyClass 100 times and returns the total execution time.*/
    public static long TimeMethodExecution ( String className, String methodName, String[] arguments, int iterations) {
        Stopwatch stopwatch = new Stopwatch();
        try {
            Class c = Class.forName(className);
            //         Method m[] = c.getDeclaredMethods();
            //         for (int i = 0; i < m.length; i++)
            //             System.out.println(m[i].toString());
            
            Object argsObj[] = new Object[] { arguments};
            Method meth = c.getMethod(methodName, new Class[] { arguments.getClass()});

	    stopwatch.start();
            for ( int i=0 ; i< iterations ; i++ ) {
                meth.invoke(null, argsObj);
            }
	    stopwatch.stop();           

        }
        catch (Throwable e) {
            System.err.println(e);
        }
        return  stopwatch.toValue();
    }

  /**
  * An example of the use of this class to
  * time the execution of String manipulation code.
  */
  public static void main (String[] arguments) {

    String className=new String();
    String methodName = new String();
    String [] tmp = new String[ arguments.length ];
    String [] passArgs;
    int iterations = 1;
    int j=0;
    for ( int i=0 ; i< arguments.length ; i++ ) {
	if ( arguments[i].equalsIgnoreCase("-class")) {
	    className = new String(arguments[++i]);
	} else if ( arguments[i].equalsIgnoreCase("-method")) {
	    methodName = new String(arguments[++i]);
	} else if ( arguments[i].equalsIgnoreCase("-iter")) {
	    iterations = Integer.parseInt(arguments[++i]);
	} else {
	    tmp[j] = new String(arguments[i]);
	}
    }
    passArgs = new String[j];
    System.arraycopy( tmp, 0, passArgs, 0 , j );
    System.out.println( className + " " + methodName + " " + iterations + " " + arguments.toString());
    long t = TimeMethodExecution( className, methodName, passArgs , iterations ) ;
    System.out.println( "Timed Class took "+ t + " mS");
  }

  /**
  * Start the stopwatch.
  *
  * @throws IllegalStateException if the stopwatch is already running.
  */
  public void start(){
    if ( fIsRunning ) {
      throw new IllegalStateException("Must stop before calling start again.");
    }
    //reset both start and stop
    fStart = System.currentTimeMillis();
    fStop = 0;
    fIsRunning = true;
    fHasBeenUsedOnce = true;
  }

  /**
  * Stop the stopwatch.
  *
  * @throws IllegalStateException if the stopwatch is not already running.
  */
  public void stop() {
    if ( !fIsRunning ) {
      throw new IllegalStateException("Cannot stop if not currently running.");
    }
    fStop = System.currentTimeMillis();
    fIsRunning = false;
  }

  /**
  * Express the "reading" on the stopwatch.
  *
  * @throws IllegalStateException if the Stopwatch has never been used,
  * or if the stopwatch is still running.
  */
  public String toString() {
    validateIsReadable();
    StringBuilder result = new StringBuilder();
    result.append(fStop - fStart);
    result.append(" ms");
    return result.toString();
  }

  /**
  * Express the "reading" on the stopwatch as a numeric type.
  *
  * @throws IllegalStateException if the Stopwatch has never been used,
  * or if the stopwatch is still running.
  */
  public long toValue() {
    validateIsReadable();
    return fStop - fStart;
  }

  // PRIVATE ////
  private long fStart;
  private long fStop;

  private boolean fIsRunning;
  private boolean fHasBeenUsedOnce;

  /**
  * Throws IllegalStateException if the watch has never been started,
  * or if the watch is still running.
  */
  private void validateIsReadable() {
    if ( fIsRunning ) {
      String message = "Cannot read a stopwatch which is still running.";
      throw new IllegalStateException(message);
    }
    if ( !fHasBeenUsedOnce ) {
      String message = "Cannot read a stopwatch which has never been started.";
      throw new IllegalStateException(message);
    }
  }
}
 

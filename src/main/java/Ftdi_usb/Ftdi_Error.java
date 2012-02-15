package Ftdi_usb;
/**
* Created by jed
* User: jedartois@gmail.com
* Date: 11/02/12
*/
public class Ftdi_Error  extends Error
{
	private Throwable cause = null;
	  public Ftdi_Error() 
	  {
	    super();
	  }

	  public Ftdi_Error(String message) {
	    super(message);
	  }

	  public Ftdi_Error(String message, Throwable cause) {
	      super(message);
	      this.cause = cause;
	  }

	  public Throwable getCause() {
	    return cause;
	  }

	  public void printStackTrace() {
	    super.printStackTrace();
	    if (cause != null) {
	      System.out.println("Caused by:");
	      cause.printStackTrace();
	    }
	  }

	  public void printStackTrace(java.io.PrintStream ps)
	  {
	    super.printStackTrace(ps);
	    if (cause != null) {
	      ps.println("Caused by:");
	      cause.printStackTrace(ps);
	    }
	  }

	  public void printStackTrace(java.io.PrintWriter pw)
	  {
	    super.printStackTrace(pw);
	    if (cause != null) {
	      pw.println("Caused by:");
	      cause.printStackTrace(pw);
	    }
	  }
}

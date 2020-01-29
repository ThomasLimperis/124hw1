import java.util.*;
import java.io.*;

// Details of ini4j ini parser library
// http://ini4j.sourceforge.net/index.html
import org.ini4j.*;

// Logging related. Print statement might not be thread-safe.
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;


public class ServerMain {

	// System Exit codes reference
	// https://www.freebsd.org/cgi/man.cgi?sysexits(3)
	final static int EX_USAGE = 64;
	final static int EX_CONFIG = 78;

	// Initialization of global logger
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main (String[] args) throws IOException {

        LOGGER.log(Level.INFO, "Logger Initialized");

        if (args.length != 1) {
        	System.err.println("Usage: ./run-server [config_file]");
        	System.exit(EX_USAGE);
        }

        // Read out the ini file for configuration
        Wini server_config = new Wini(new File(args[0]));

        boolean enabled = server_config.get("httpd", "enabled", boolean.class);

        if (enabled) {
        	LOGGER.log(Level.INFO, "Web Server Enabled.");

        	// Create httpd_server instance the launch
        	HttpdServer httpd_server = new HttpdServer(server_config);
        	httpd_server.launch();
        } else {
        	LOGGER.log(Level.INFO, "Web Server Disabled.");
        }

	}
}

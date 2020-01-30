import org.ini4j.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.Files;

// Logging related. Print statement might not be thread-safe.
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;

public class HttpdServer {

	// Configuration error exit code
	final static int EX_CONFIG = 78;

	// You are free to change the fields if you want
	protected Wini server_config;
	protected int port;
	protected String doc_root;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public HttpdServer(Wini server_config) {
		this.server_config = server_config;

		this.port  = server_config.get("httpd", "port", int.class);
		if (this.port == 0) {
			LOGGER.log(Level.SEVERE, "Failed to read port number from config file");
			System.exit(EX_CONFIG);
		}

		this.doc_root = server_config.get("httpd", "doc_root", String.class);
		if (this.doc_root  == null) {
			LOGGER.log(Level.SEVERE, "Failed to read doc_root from config file");
			System.exit(EX_CONFIG);
		}
	}

	public void launch() throws UnknownHostException, IOException
	{
		LOGGER.log(Level.INFO, "Launching Web Server");
		LOGGER.log(Level.INFO, "Port: " + port);
		LOGGER.log(Level.INFO, "doc_root: " + doc_root);


		// Put code here that actually launches your webserver...

		//start http request
		ServerSocket server = new ServerSocket(port);
	  Socket socket = server.accept();
		Client client;

		while (true)
		{
		client = new Client(socket);
		Thread thread = new Client(socket);
		try
		{
		thread.sleep(5);
		//this.socket.setSoTimeout(5000);
	  }catch(InterruptedException e)
		{
        e.printStackTrace();
    }

		thread.start();
		LOGGER.log(Level.INFO, "Waiting response..");
		socket = server.accept();
		}

	}
	public String getMime(String fileName) throws IOException
	{

		//File file = new File(doc_root+"/src/mime.types");

		File file = new File("./src/mime.types");
		String mime = "";
		for (int i = fileName.length()-1; i >-1; i--)
		{
			if (fileName.charAt(i) =='.')
			{
				for (int j = i; j < fileName.length(); j++)
					mime += fileName.charAt(j);
				break;
			}
		}
		BufferedReader buff = new BufferedReader(new FileReader(file));
		StringTokenizer token;
		String end = "";
		String line = buff.readLine();
		while (line != null)
		{
				token = new StringTokenizer(line);
			  end = token.nextToken();
				if (end.equals(mime))
					return token.nextToken();
				line = buff.readLine();
		}
		return null;
	}

public class Client extends Thread
{
  public Socket socket;
  public InputStreamReader input;
  public DataOutputStream output;
  public Client(Socket socket) throws UnknownHostException, IOException
  {
    this.socket = socket;
    this.input = new InputStreamReader(this.socket.getInputStream());
    this.output = new DataOutputStream(this.socket.getOutputStream());
  }

	public void run()
	{
	try{
		BufferedReader buff = new BufferedReader(this.input);
		String fileName = "";
		String line = buff.readLine();
		if (line == null)
			return;
		StringTokenizer st = new StringTokenizer(line);
		st.nextToken();

		boolean dir = false;
		fileName = st.nextToken();
		fileName = fileName;
		System.out.println(fileName);

		//doc_root ="../project-1-java-red";
		File file = new File("."+fileName);

		if (file.isDirectory())
		{
			fileName = fileName + "index.html";
				System.out.println("TRUE");
			file = new File("." +fileName);
			System.out.println("TRUE");

		}
		String mime =getMime(fileName);
		System.out.println(fileName);
		if (mime.equals("image/x-icon"))
			return;
		if (mime == null)
		{
			LOGGER.log(Level.INFO, "404 Not Found");
			return;
		}
		if (!file.exists())
		{
				LOGGER.log(Level.INFO, "404 Not Found");
		}
		FileInputStream in  = new FileInputStream (file);

		line ="HTTP/1.1 200 OK\r\nContent-type: "  + mime + "\r\n\r\n";
		byte [] b = line.getBytes();
		this.output.writeBytes(line);

		int i = 0;
	  b = new byte [1024];
		while((i = in.read(b))!= -1)
    	this.output.write(b, 0, i);

		this.output.close();
		this.input.close();

	}catch(IOException e)
	{
  	e.printStackTrace();
	}

	}
}
}

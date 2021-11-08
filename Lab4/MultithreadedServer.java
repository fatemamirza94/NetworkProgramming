import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MultithreadedServer implements Runnable {

	Socket csocket;
	public static int noOfConnections = 0;

	/* Logging object declaration */

	private final static Logger LOGGER = Logger.getLogger(Logger.class.getName());

	final static int socket = 6400;


	MultithreadedServer(Socket csocket) {
		this.csocket = csocket;
	}
	

	public static void main(String args[]) throws Exception {

		// initialize server socket
		ServerSocket ssock = null;

		try {

			ssock = new ServerSocket(socket);
			
			LOGGER.info("Server Listening to port: 6400");

			while (true) {

				Socket sock = ssock.accept();
				
				noOfConnections++;

				LOGGER.info("New tcp connection established - Connection Number: " + noOfConnections + " , client address: " + sock.getInetAddress().getHostAddress());
				
				new Thread(new MultithreadedServer(sock)).start();
			}
		}
		catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (ssock != null) {
                try {
                    ssock.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

	}

	public void run() {

		BufferedReader clientInputReader = null;
		PrintStream pstream = null;

		try {
			// InputStream reader can read only characters
			// to read input line Buffered reader is needed
			String line;
			// for reading from input stream
			clientInputReader = new BufferedReader(new InputStreamReader(csocket.getInputStream()));

			// for writing to output stream
			pstream = new PrintStream(csocket.getOutputStream());

			ArrayList<Long> fibo;
			while ((line = clientInputReader.readLine()) != null) { // receives the n value of the fibo series from
																	// client
				fibo = new ArrayList<>();
				// writing to the output stream of the socket to which client is connected
				for (long i = Long.parseLong(line); i >= 0; i--)
					fibo.add(fibonacciGenerator(i));
				pstream.println(fibo);
				pstream.flush();
				System.out.println(fibo);

			}
			//csocket.close();
			//LOGGER.info("Connection Closed!");
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe("IO interruption detected!");
		}
		finally {
            try {
                if (pstream != null) {
                	pstream.close();
                }
                if (clientInputReader != null) {
                	clientInputReader.close();
                	csocket.close();
                	LOGGER.info("Connection Closed!");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	// returns the last fibonacci number in the series using recursion
	// if n = 5 returns (0 1 1 2 3 5)
	public long fibonacciGenerator(long n) {

		if (n == 0 || n == 1)
			return 1;

		return fibonacciGenerator(n - 1) + fibonacciGenerator(n - 2); // recursively generates the fibo
	}

}

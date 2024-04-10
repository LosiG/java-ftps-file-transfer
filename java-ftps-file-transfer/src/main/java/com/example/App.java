package com.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPSClient;

public class App {
    public static void main(String[] args) {
        Logger ftpLogger = Logger.getLogger("ftp"); // Used for debugging

        String username = "username";
        String host = "my.host";
        String password = "password";

        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);

        FTPSClient client = new FTPSClient("TLSv1.3");
        // Wasn't able to set the logger as Printer
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        client.configure(conf);
        try {
            client.connect(host, 21);
            client.login(username, password);
            // Set protection buffer size
            client.execPBSZ(0);
            // Set data channel protection to private
            client.execPROT("P");
            client.enterLocalPassiveMode();

            // Used to analyze delta time for the below options
            ftpLogger.log(Level.INFO, "Before: {0}", System.currentTimeMillis());

            // * Method for InputStream return, usable with small files */
            // InputStream inputStream = client.retrieveFileStream("dailyExport.csv");
            //
            // This is needed for method retrieveFileStream
            // client.completePendingCommand();

            // * Method for OutputStream return, generally good */
            OutputStream outputStream = new FileOutputStream("localFile.csv");
            client.retrieveFile("ftpFile.csv", outputStream);

            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

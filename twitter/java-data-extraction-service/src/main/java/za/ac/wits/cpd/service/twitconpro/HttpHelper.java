package za.ac.wits.cpd.service.twitconpro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.net.ssl.HttpsURLConnection;

/**
 *  HTTP Helper class for writing requests and reading responses.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
public class HttpHelper {

    
    public static boolean writeRequest(HttpsURLConnection connection, String textBody) {
        try {
            try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                wr.write(textBody);
                wr.flush();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static String readResponse(HttpsURLConnection connection) {
        try {
            StringBuilder str = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append(line).append(System.getProperty("line.separator"));
            }
            return str.toString();
        } catch (IOException e) {
            return new String();
        }
    }
}

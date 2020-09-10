package games.snake3000.network_tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class StreamUtils {

	public static String readFromInputStream(BufferedReader br) {
		String chaine=null;
		try {
			chaine= "";

			while(br.ready()){
				char c = (char)br.read();
				if(c=='%'){
					break;
				}
				else {
					chaine += c;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chaine;
	}

	public static void writeToOutputStream(BufferedWriter bw, String messageToSend) {

		try {
			bw.write(messageToSend+"%");
			bw.flush();
			//outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

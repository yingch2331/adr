package com.liyuan3210.adr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class Test {

	public static void main(String[] args) throws IOException {
		String line = "";
		String result = "";
		URL url = null;
		PrintWriter out = null;
		try {
			String target = "address=小榄镇杨集路口15618552721李四";
			url = new URL("http://127.0.0.1:8083/resolveAddress");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(target.toString());
			out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			System.out.print("结果:"+result);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

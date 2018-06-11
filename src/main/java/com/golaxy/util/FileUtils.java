package com.golaxy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
	public static String readFile(String path) throws IOException {
		File file = new File(path);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		String lineSeparator = System.getProperty("line.separator", "\n");
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			sb.append(line + lineSeparator);
		}
		fr.close();
		br.close();

		return sb.toString();
	}
	
    /**
     * 写入文件
     */
    public static void writeFile(String filename, Boolean append, String content) {
    	File file = new File(filename);
    	FileWriter fw = null;
    	BufferedWriter writer = null;
		try {
			fw = new FileWriter(file, append);
			writer = new BufferedWriter(fw);
			writer.write(content);
			writer.newLine();
			writer.flush(); //加不加此句都行，因为writer.close()时会刷新缓冲区的
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }
   
}

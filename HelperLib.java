/**
 * HelperLib.java
 *
 * A class that contains various helpful functions that are used throughout the program.
 *
 */

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.Dialog.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

class HelperLib{
	public static String getLines(String s){
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(int i=1; index != -1; i++){
			sb.append(i + "\n");
			index = s.indexOf("\n", index+1);
		}
		return sb.toString();
	}
	
	public static JTextArea getTextArea(String s){
		JTextArea ret = new JTextArea(s);
		ret.setFont(new Font("Courier", Font.PLAIN, 12));
		ret.setTabSize(4);
		return ret;
	}
	
	public static String inputStreamReaderToString(InputStreamReader in)throws Exception{
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[1025];
		while(in.ready()){
			sb.append( (new String(buf, 0, in.read(buf, 0, 1024))).replaceAll("\r\n", "\n") );
		}
		in.close();
		String tmp = sb.toString();
		while(tmp.contains("\r\n")){
			tmp = tmp.replaceAll("\r\n", "\n");
		}
		return tmp;
	}
	
	public static String getSaveFileString(String language, String code, String folder, String fileName){
		switch(language){
			case "C++":
				if(fileName == null){
					return folder+"\\a.cpp";
				}else{
					return folder+"\\"+fileName+".cpp";
				}
			case "Java":
				int sindex = code.indexOf("public");
				sindex = code.indexOf("class", sindex+1)+5;
				int eindex = code.indexOf("{");
				String className = code.substring(sindex, eindex).trim();
				return folder+"\\"+className+".java";
			case "Python":
				if(fileName == null){
					return folder+"\\a.py";
				}else{
					return folder+"\\"+fileName+".py";
				}
		}
		return null;
	}
	
	public static String getSaveFileString(String language, String code, String folder){
		return getSaveFileString(language, code, folder, null);
	}
	
	public static String getCompileString(String language, String code, String folder, String fileName){
		switch(language){
			case "C++":
				if(fileName == null){
					return "g++ -o "+folder+"\\a "+folder+"\\a.cpp";
				}else{
					return "g++ -o "+folder+"\\"+fileName+" "+folder+"\\"+fileName+".cpp";
				}
			case "Java":
				int sindex = code.indexOf("public");
				sindex = code.indexOf("class", sindex+1)+5;
				int eindex = code.indexOf("{");
				String className = code.substring(sindex, eindex).trim();
				return "javac "+folder+"\\"+className+".java";
			case "Python":
				return null;
		}
		return null;
	}
	
	public static String getCompileString(String language, String code, String folder){
		return getCompileString(language, code, folder, null);
	}
	
	public static String getRunString(String language, String code, String folder, String fileName){
		switch(language){
			case "C++":
				if(fileName == null){
					return folder+"\\a.exe";
				}else{
					return folder+"\\"+fileName+".exe";
				}
			case "Java":
				int sindex = code.indexOf("public");
				sindex = code.indexOf("class", sindex+1)+5;
				int eindex = code.indexOf("{");
				String className = code.substring(sindex, eindex).trim();
				return "java -cp "+folder+" "+className;
			case "Python":
				if(fileName == null){
					return "python " + folder + "\\a.py";
				}else{
					return "python " + folder + "\\"+fileName+".py";
				}
		}
		return null;
	}
	
	public static String getRunString(String language, String code, String folder){
		return getRunString(language, code, folder, null);
	}
	
	public static String fileToString(String file){
		try{
			FileReader fr = new FileReader(new File(file));
			return inputStreamReaderToString(fr);
		}catch(Exception e){
			System.out.println("Failed to read file, returning empty string instead. (" + file + ")");
		}
		return "";
	}

	public static void stringToFile(String file, String string){
		try{
			if(file.lastIndexOf('\\') != -1){
				File par = new File(file.substring(0, file.lastIndexOf('\\')));
				par.mkdirs();
			}
			
			FileWriter fw = new FileWriter(file);
			fw.write(string);
			fw.flush();
			fw.close();
		}catch(IOException e){
			System.out.println("Failed to save file.");
		}
	}
	
	public static String problemNumbertoLetters(int i) {
        int i1 = i/26, i2 = i%26;
        String s = "";
        if (i>=26) s += (char)(i1+'A'-1);
        s += (char)(i2+'A');
        return s;
    }
	
	public static Color getVerdictColor(String verdict){
		switch (verdict){
			case "Accepted": 			return new Color(0,170,50);
			case "Wrong Answer": 		return new Color(255,0,0);
			case "Time Limit Exceeded": return new Color(0,0,255);
			case "Runtime Error":		return new Color(0,170,183);
			default:					return new Color(0,0,0);
		}
	}
}

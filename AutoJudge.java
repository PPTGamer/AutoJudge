/**
 * AutoJudge.java
 *
 * This is the main class and the launching point of the auto-judging program.
 * It creates all of the program's main components and starts up the GUI.
 * It also serves as the access point of all other classes to the main components where necessary.
 *
 */

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class AutoJudge{
	public static ProblemHandler problemHandler;
	public static RunWindow runWindow;
	public static Judge judge;
	public static GUI gui;
	public static final String VERSION = "7";

	public static void main(String[] args){
		problemHandler = new ProblemHandler();
		problemHandler.setup();
		
		judge = new Judge();
		runWindow = new RunWindow();
	
		gui = new GUI(VERSION);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
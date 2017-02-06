import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.Dialog.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.imageio.*;
import java.text.*;


class PieChart extends JComponent {
	class Slice{
		int value;
		Color color;
		public Slice(int value, Color color) {  
			this.value = value;
			this.color = color;
		}
	}
	private HashMap<String,Slice> slices;
	public PieChart(int n) {
		slices=new HashMap<String,Slice>();
		slices.put("No Verdict", new Slice(n,new Color(0,0,0)));
		setPreferredSize(new Dimension(100, 150));
		repaint();
	}
	public void reset(int n){
		slices=new HashMap<String,Slice>();
		slices.put("No Verdict", new Slice(n,new Color(0,0,0)));
		repaint();
	}
	public void addEntry(String key){
		slices.put("No Verdict",new Slice(slices.get("No Verdict").value - 1, new Color(0,0,0)));
		int currval = 0;
		if(slices.containsKey(key)){
			currval = slices.get(key).value;
		}
		slices.put(key,new Slice(currval+1,HelperLib.getVerdictColor(key)));	
		repaint();
	}
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHints(new RenderingHints(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON));
		drawPie((Graphics2D) g, new Rectangle(100,100));
		drawLegend((Graphics2D) g, 35,115);
	}
	void drawPie(Graphics2D g, Rectangle area) {
		int total = 0;
		String[] verdicts = new String[]{"Accepted","Wrong Answer","Time Limit Exceeded","Runtime Error","Output Format Error","No Verdict"};
		for (int i = 0; i < verdicts.length; i++) {
			if(slices.get(verdicts[i])!=null){	//containsKey does not work for some reason
				total += slices.get(verdicts[i]).value;
			}
		}
		int curValue = 0;
		for (int i = 0; i < verdicts.length; i++) {
			if(slices.get(verdicts[i])!=null){
				int valuei = slices.get(verdicts[i]).value;
				Color colori = slices.get(verdicts[i]).color;
				double startAngle = curValue * 360.0D / total;
				double arcAngle = valuei * 360.0D / total;
				Arc2D.Double arc = new Arc2D.Double(area, startAngle, arcAngle, Arc2D.PIE);
				g.setPaint(new GradientPaint(area.x, area.y,colori.brighter(), area.width, area.height,colori.darker()));
				g.fill(arc);
				curValue += valuei;
			}
		}
	}
	void drawLegend(Graphics2D g, int x, int y){
		String[] verdicts = new String[]{"Accepted","Wrong Answer","Time Limit Exceeded","Runtime Error","Output Format Error"};
		int buffer = 0;
		for (int i = 0; i < verdicts.length; i++) {
			if(slices.get(verdicts[i])!=null){	//containsKey does not work for some reason
				String abbreviation = "";
				switch(verdicts[i]){
					case "Accepted": abbreviation = "AC"; break;
					case "Wrong Answer": abbreviation = "WA"; break;
					case "Time Limit Exceeded": abbreviation = "TLE"; break;
					case "Runtime Error": abbreviation = "RE"; break;
					case "Output Format Error": abbreviation = "OFE"; break;
				}
				g.drawString(abbreviation + ":" + slices.get(verdicts[i]).value, x, y + buffer);
				buffer+=15;
			}
		}
	}
}
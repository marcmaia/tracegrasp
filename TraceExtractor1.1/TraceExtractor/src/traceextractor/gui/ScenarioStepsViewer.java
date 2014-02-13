package traceextractor.gui;

import java.io.*;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;


public class ScenarioStepsViewer extends JFrame {
	
	private JList stepsView;
	
	public static void main(String[] args) {
		final ScenarioSteps scenario = new ScenarioSteps();
		final ScenarioStepsViewer viewer = new ScenarioStepsViewer(scenario);
		
		Thread t = new Thread() {
			public void run() {
				try {
					sleep(2000);
				
					scenario.loadFrom(new File("C:\\Users\\Victor\\Desktop\\experimentos\\Cenário - Eye of the Tjger - Game Playing.txt"));
					
					Iterator<String> it = scenario.getStepsIterator();
					while (it.hasNext()) {
						sleep(1000);
						viewer.setSelected(it.next());
					}
					
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				
			}
		};
		
		t.start();
		
	}
	
	public ScenarioStepsViewer(ScenarioSteps scenario) {
		super();
		this.setTitle("Scenario Viewer");
		this.setSize(300, 400);
		this.stepsView = new JList();
		this.stepsView.setModel(scenario);
		this.stepsView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.add(new JScrollPane(stepsView));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setSelected(String label) {
		this.stepsView.setSelectedValue(label, true);
	}
}

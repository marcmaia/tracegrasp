package traceextractor.gui;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

public class ScenarioSteps implements ListModel {
	private List<String> stepsLabels = new ArrayList<String>();
	private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	public static void main(String[] args) {
		new ScenarioSteps().loadFrom(new File("cenario-steps.txt"));
	}
	
	public void loadFrom(File file) throws IllegalArgumentException {
		stepsLabels = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String line = null;
			int line_count = 0;
			
			while (null != (line = br.readLine())) {
				line_count++;
				String label = extractLabelFromLine(line);
				
				if (!isValidLabel(label)) {
					new IllegalArgumentException("Invalid label at line: " + line_count);
				}
				
				if (!isUniqueLabel(label)) {
					new IllegalArgumentException("Duplicated label at line: " + line_count + " - Already exists at: " + getStepFor(label));
				}
				
				stepsLabels.add(label);
			}
		
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		notifyStepsLoadToListeners();
	}
	
	public boolean isValidLabel(String label) {
		return null != label
			&& "".equals(label)
			&& 0 != label.length();
	}
	
	public boolean isUniqueLabel(String label) {
		return !stepsLabels.contains(label);
	}
	
	public int getStepFor(String label) {
		if (!stepsLabels.contains(label)) {
			return -1;
		}
		return stepsLabels.indexOf(label)+1;
	}
	
	public String extractLabelFromLine(String line) {
		return line.trim();
	}
	
	public Iterator<String> getStepsIterator() {
		return stepsLabels.iterator();
	}
	
	public void notifyStepsLoadToListeners() {
		for (ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, stepsLabels.size()));
		}
	}
	
	public void addListDataListener(ListDataListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListDataListener(ListDataListener observer) {
		this.listeners.remove(observer);
	}
	
	public int getSize() {
		return stepsLabels.size();
	}
	
	public String getElementAt(int index) {
		return stepsLabels.get(index);
	}
}

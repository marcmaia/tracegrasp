package traceextractor.gui;

import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.Toolkit;

import traceextractor.feature.FeatureModelWriter;
import traceextractor.mark.EndMark;
import traceextractor.mark.Mark;
import traceextractor.mark.StartMark;
import traceextractor.register.SimpleMarkRegister;



public class ExtractorControlGui {
	//private static Pattern p = Pattern.compile("[a-c]*");
	private enum ControlButtonState { Start, End };
	private JFrame mainContainer;
	private JTextField markLabel;
	private JButton markControlButton;
	private JTable markTable;
	private JLabel status;
	private List<Mark> marks;
	private FeatureModelWriter featureModelWriter;
	private SimpleMarkRegister register;
	private File outputDir;
	private Iterator<String> stepsIt;
	private ScenarioSteps scenario;
	private ScenarioStepsViewer stepsViewer;
	private String lastLabel = null;
	
	public static void main(String[] args) {
		try{
		new ExtractorControlGui();
		}catch(SecurityException ex){
			ex.printStackTrace();
		}
	}
	
	public ExtractorControlGui() throws SecurityException{
		this.marks = new ArrayList<Mark>();
		buildGui();
		this.register = new SimpleMarkRegister(getOutputDir());
		this.featureModelWriter = new FeatureModelWriter(getOutputDir(), mainContainer);
		
		scenario = new ScenarioSteps();
		stepsViewer = new ScenarioStepsViewer(scenario);
		scenario.loadFrom(chooseScenarioInputFile());
		Point p = mainContainer.getLocation();
		stepsViewer.setLocation(
				p.x + mainContainer.getWidth() - stepsViewer.getWidth(), 
				p.y - stepsViewer.getHeight());
		stepsIt = scenario.getStepsIterator();
		
		preInitializationMarkOption();
		
		mainContainer.setVisible(true);
		stepsViewer.setVisible(true);
	}
	
	public File getOutputDir() {
		if (null == outputDir) {
			outputDir = getValidOutputDir();
		}
		
		return outputDir;
	}
	
	public void buildGui() throws SecurityException{
		mainContainer = new JFrame();
		mainContainer.setSize(200,100);
		mainContainer.setTitle("Trace Mark Control");
		mainContainer.setAlwaysOnTop(false);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		mainContainer.setLocation(
				screensize.width - mainContainer.getWidth(),
				screensize.height - mainContainer.getHeight());
		
		JPanel top_panel = new JPanel();
		top_panel.add(new JLabel("Mark Label:"));
		markLabel = new JTextField();
		Dimension d = new Dimension(400, 25);
		markLabel.setMinimumSize(d);
		markLabel.setPreferredSize(d);
		top_panel.add(markLabel);
		markControlButton = new JButton(ControlButtonState.Start.toString());
		markControlButton.addActionListener(new ControlButtonListener());
		top_panel.add(markControlButton);
		
		mainContainer.add(top_panel, BorderLayout.NORTH);
		
		markTable = new JTable();
		markTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] { },
				new String [] {"Feature","Start", "Stop"}));
		markTable.getColumn("Feature").setPreferredWidth(300);
		markTable.getColumn("Start").setPreferredWidth(50);
		markTable.getColumn("Stop").setPreferredWidth(50);
		markTable.setPreferredSize(new Dimension(400, 600));
		
		mainContainer.add(new JScrollPane(markTable), BorderLayout.CENTER);
		
		status = new JLabel("Write a label name and press start!");
		
		mainContainer.add(status, BorderLayout.SOUTH);
		//mainContainer.pack();
		mainContainer.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		mainContainer.addWindowListener(new WindowCloseListener());
	}
	
	public void preInitializationMarkOption() {
		if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(mainContainer, 
				"If you want to insert any mark before initialize the application, \n" +
				"use the Control Gui to insert marks before closing this dialog. \n" +
				"Do you want to insert a mark before proceed?", 
				"Before proceed...", 
				JOptionPane.OK_OPTION)) {
			
			//String label = JOptionPane.showInputDialog(mainContainer, "Enter the initial mark label:");
			String label = stepsIt.next();
			insertStartMark(label, System.currentTimeMillis());
		}
	}
	
	private boolean isValidMarkLabel(String label) {
		if ("".equals(label)
			|| null == label
			|| 0 == label.length()) { 
			return false;
		}
		
		if (marks.size() > 0) { 
			Mark last_mark = marks.get(marks.size()-1);
			
			if ("Start".equalsIgnoreCase(last_mark.type())) {
				if (!label.equalsIgnoreCase(last_mark.getName())) {
					return false;
				}
				
			} else {
				for (Mark mark : marks) {
					if (mark.getName().equals(label)) {
						JOptionPane.showMessageDialog(mainContainer, "Duplicated mark label: " + label);
						return false;
					}
				}
			}
		}
		
		/*
		 * TODO: Implement a RegEx checker, where any non alpha numeric character
		 * invalidate the label.
		 * 
		Matcher m = p.matcher(label);
		
		if (!m.find()) {
			return true;
		}
		
		markLabel.setSelectionStart(m.start());
		markLabel.setSelectionEnd(m.end());
		
		return false;*/
		return true;
	}
	
	private boolean isStartMarkCommand() {
		if (markControlButton.getText().equals(ControlButtonState.Start.toString())) {
			return true;
		}
		
		return false;
	}
	
	private void insertStartMark(String label, long time) {
		if (marks.size() == 0
				|| ControlButtonState.End.toString().equalsIgnoreCase(marks.get(marks.size()-1).type())) {
			Mark mark = new StartMark(label, time);
			register.registerMark(mark);
			marks.add(mark);
			writeToHistoryColumn(mark.getName(), 0);
			writeToHistoryColumn(getFormattedTime(mark.getTime()), 1);
			
			status.setText("Start Mark inserted!");
			markControlButton.setText(ControlButtonState.End.toString());
			markLabel.setText(label);
			markLabel.setEditable(false);
			
			stepsViewer.setSelected(label);
			lastLabel = label;
		} else {
			status.setText("Could not insert start mark!");
		}
	}
	
	private void insertEndMark(long time) {
		if (marks.size() != 0
				&& ControlButtonState.Start.toString().equalsIgnoreCase(marks.get(marks.size()-1).type())) {
			Mark mark = new EndMark(lastLabel, time);
			register.registerMark(mark);
			marks.add(mark);
			writeToHistoryColumn(getFormattedTime(mark.getTime()), 2);
			
			status.setText("End Mark inserted!");
			markControlButton.setText(ControlButtonState.Start.toString());
			markLabel.setText(lastLabel);
			markLabel.setEditable(true);
		} else {
			status.setText("Could not insert end mark!");
		}
	}
	
	private String getFormattedTime(long time) {
		DateFormat df = DateFormat.getTimeInstance();
		return df.format(new Date(time));
	}
	
	private void writeToHistoryColumn(String message, int column) {
		javax.swing.table.DefaultTableModel dtm =
			(javax.swing.table.DefaultTableModel)markTable.getModel();
		
		switch(column) {
			case 0: 
				dtm.addRow(new Object[]{message,"",""});
				this.featureModelWriter.addFeature(message);
				break;
				
			case 1:
			case 2:
				dtm.setValueAt(message, dtm.getRowCount()-1, column);
				break;
		}
	}
	
	private File getValidOutputDir() {
		boolean valid_output_dir = false;
		do {
			if (null != outputDir
				&& outputDir.exists() 
				//&& outputDir.isDirectory()
				//&& outputDir.canWrite()
				) {
				valid_output_dir = true;
			
			} else {
				outputDir = chooseOutputDir();
			}
			
		} while (!valid_output_dir);
		
		return outputDir;
	}
	
	private File chooseOutputDir() {
    	JFileChooser fc = new JFileChooser();
        
        // Restricts the visualization of directories only
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select the Output Directory for generated data");
        fc.setToolTipText("Select the Output Directory for generated data");
        
        File dir = null;
        do {
        	int res = fc.showOpenDialog(mainContainer);
        	
        	if(res == JFileChooser.APPROVE_OPTION){
        		dir = fc.getSelectedFile();
        	}
        	else {
        		if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(mainContainer, "No output directory selected!", "Finish execution?",
        				JOptionPane.YES_NO_OPTION) ) {
        			System.exit(0);
        		}
        		dir = null;
        	}
        } while (dir == null);
        
        return dir;
    }
	
	private File chooseScenarioInputFile() {
    	JFileChooser fc = new JFileChooser();
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileNameExtensionFilter("Scenario Steps File", "txt"));
        fc.setDialogTitle("Inform the scenario input file");
        fc.setToolTipText("Inform the scenario input file");
        
        File dir = null;
        do {
        	int res = fc.showOpenDialog(mainContainer);
        	
        	if(res == JFileChooser.APPROVE_OPTION){
        		dir = fc.getSelectedFile();
        	}
        	else {
        		if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(mainContainer, "No scenario steps file selected!", "Finish execution?",
        				JOptionPane.YES_NO_OPTION) ) {
        			System.exit(0);
        		}
        		dir = null;
        	}
        } while (dir == null);
        
        return dir;
    }
	
	class ControlButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			//String label = markLabel.getText().trim();
			
			if (!stepsIt.hasNext()
					&& isStartMarkCommand()) {
				return;
			}
			
			if (isStartMarkCommand()) {
				String label = stepsIt.next();
				
				if (!isValidMarkLabel(label)) {
					return;
				}
				
				insertStartMark(label, System.currentTimeMillis());
				lastLabel = label;
			
			} else {
				insertEndMark(System.currentTimeMillis());
			}
		}
	}
	
	class WindowCloseListener extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			register.terminate();
		}
	}
}

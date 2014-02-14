package sequence.alignment;

import java.io.*;
import java.util.Vector;

public class FeatureLoader {
	private String path;
	private Vector<Feature> list;
	
	FeatureLoader(String path) {
		this.path = path;
		this.list = new Vector<Feature>();
		load();
	}
	
	public void load() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.path));
			while(reader.ready()) {
				String START = reader.readLine();
				String[] argsS = START.split(",");
				
				String END = reader.readLine();
				String[] argsE = END.split(",");
				
				long inicio = Long.parseLong(argsS[2]);
				long fim = Long.parseLong(argsE[2]);
				
				Feature f = new Feature(argsS[1], inicio, fim);
				list.add(f);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int size() {
		return list.size();
	}
	
	public Feature get(int i) {
		return list.get(i);
	}
}

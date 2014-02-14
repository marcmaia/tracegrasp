package sequence.alignment;

import java.io.*;

public class BlocoLoader {
	private ObjectInputStream input;
	private String path;
	private Bloco loaded;
	
	BlocoLoader(String path) {
		this.path = path;
		File dir = new File(this.path);
		if (!dir.isDirectory()) {
			System.out.println("Diretorio invalido!");
		}
	}
	
	public Bloco load(int i) {
		File f = new File(this.path + "/block-" + i + ".dat");
		try {
			input = new ObjectInputStream(new FileInputStream(f));
			loaded = (Bloco) input.readObject();
			
		} catch (IOException e) {
			loaded = null;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			loaded = null;
			e.printStackTrace();
		}
		return loaded;
	}
}

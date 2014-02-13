package sequence.alignment;

import java.io.*;

public class BlocoWriter {
	private String path;
	private ObjectOutputStream output;
	
	BlocoWriter(String path) {
		this.path = path;
		File dir = new File(this.path);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
	}
	
	public void write(Bloco b) {
		File f = new File(this.path + "/block-" + b.getId() + ".dat");
		try {
            output = new ObjectOutputStream(new FileOutputStream(f));
            output.writeObject(b);
            output.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}

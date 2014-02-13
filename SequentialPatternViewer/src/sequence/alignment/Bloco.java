package sequence.alignment;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class Bloco implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -307866459957755875L;
	private int id;
	private boolean aligned;
	private Vector<Integer> list;
	
	Bloco(int id, boolean aligned) {
		this.id = id;
		this.aligned = aligned;
		this.list = new Vector<Integer>();
	}
	
	public void add(int code) {
		list.add(code);
	}
	
	public int get(int i) {
		return list.get(i);
	}
	
	public int size() {
		return list.size();
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isAligned() {
		return this.aligned;
	}
	
	public String toString() {
		String out = "Segmento " + this.id + ": ";
		for (int i = 0; i < this.size(); i++)
			out += this.get(i) + " ";
		return out;
	}
	
	public int first() {
		return list.firstElement();
	}
	
	public int last() {
		return list.lastElement();
	}
}

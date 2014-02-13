package sequence.alignment;


import java.util.ArrayList;
import java.util.List;


public class Metodo {
    private List[] lista;
    private int id;
    private String name;
    
    private int[] current;
    private int[] lastIndex;

    public Metodo(int id, String  name) {
        this.lista = new List[2];
        this.lista[0] = new ArrayList<Long>();
        this.lista[1] = new ArrayList<Long>();
        this.id = id;
        this.name = name;
        
        current = new int[2];
        current[0] = 0;
        current[1] = 0;
        
        lastIndex = new int[2];
        lastIndex[0] = 0;
        lastIndex[1] = 0;
    }

    public void addTimestamp(int seq, long value) {
        this.lista[seq].add(value);
    }

    public long getTimestamp(int seq, int index) {
        long tmp = (Long) this.lista[seq].get(index);

        return tmp;
    }

    public int size(int seq) {
        return this.lista[seq].size();
    }
    
    public long getNextTimestamp(int sequencia) {
    	long timestamp = (Long) this.lista[sequencia].get(current[sequencia]);
    	this.current[sequencia] = this.current[sequencia] + 1;
    	return timestamp;
    }
    
    public void resetIterator(int sequencia) {
    	this.current[sequencia] = lastIndex[sequencia];
    }
    
    public int getCurrentIndex(int sequencia) {
    	return this.current[sequencia];
    }
    
    public void reset(int sequencia) { 
    	this.current[sequencia] = 0;
    	this.lastIndex[sequencia] = 0;;
    }
    
    public void update(int sequencia) {
    	this.lastIndex[sequencia] = this.current[sequencia]; 
    }
}

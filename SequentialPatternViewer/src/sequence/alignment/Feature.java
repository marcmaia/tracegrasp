package sequence.alignment;


public class Feature {
	private String nome;
	private long ini;
	private long end;
	
	Feature(String nome, long ini, long end) {
		this.nome = nome;
		this.ini = ini;
		this.end = end;
	}
	
	public String getNome() {
		return this.nome;
	}
	
	public long getInicio() {
		return this.ini;
	}
	
	public long getFim() {
		return this.end;
	}
	
	public long getIntervalo() {
		return this.end - this.ini;
	}
}

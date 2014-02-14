package sequence.alignment;

import java.io.*;

public class MatrizEmDisco {
    
    private RandomAccessFile matriz;
    private int m;
    private int n;
    private long filePointer;
    private long filePointer_old;
    
    MatrizEmDisco(int m, int n, String root) {
        this.m = m;
        this.n = n;
        filePointer = 0;
        filePointer_old = 0;
        try {
//            File f = new File("C:\\vdiffsimilar\\matriz-Compressed.txt"); //Pequeno porte
//       	File f = new File("C:\\vdiffigual\\matriz-Compressed.txt"); //VdifFgigual
//        	File f = new File("C:\\vigualfsimilar\\matriz-Compressed.txt"); //Vers�o igual, fun��o similar
//        	File f = new File("C:\\SanityTest\\matriz-Compressed.txt"); //Sanity Test
        	File f = new File(root + "\\Matrix-Compressed.txt"); //Pequeno porte
                //File f = new File("C:\\Luciana\\Matrix\\Matrix-Compressed2.txt"); //Pequeno porte
//        	File f = new File("C:\\EditorTest\\matriz.txt"); //Pequeno porte
            if (f.exists()) {
                f.delete();
            }
            //f.mkdir();
            matriz = new RandomAccessFile(f, "rw");
        }
        catch (FileNotFoundException ex) {
            System.err.println("Erro ao abrir arquivo matriz.txt");
        }
    }
    
    MatrizEmDisco() {
        this.m = m;
        this.n = n;
        try {
            File f = new File("matriz.txt");
            matriz = new RandomAccessFile(f, "r");
        }
        catch (FileNotFoundException ex) {
            System.err.println("Erro ao abrir arquivo matriz.txt");
        }
    }
    
    public void writeLine (byte line[]) {
		try {
                    matriz.seek(filePointer);
                       // System.out.println("Antes  "+matriz.getFilePointer());
                    matriz.write(line);
                    filePointer_old = filePointer;
                    filePointer = matriz.getFilePointer();
                    //System.out.println(filePointer);
                       // System.out.println("FilePointer atual  "+matriz.getFilePointer());
//                        matriz.seek(matriz.getFilePointer()+4);
//                        System.out.println("Depois  "+matriz.getFilePointer());
		} catch (IOException e) {
		}
	}
    
    public void seek(int linha, int coluna) {
		try {
			int pos = (this.m * linha + coluna) * 4;
			matriz.seek(pos);
		} 
		catch (IOException ex) {

		}
	}

    
    public void set(int linha, int coluna, int valor) {
        try {
            int pos = (this.m * linha + coluna) * 4;
            matriz.seek(pos);
            matriz.writeInt(valor);
        } catch (IOException ex) {
            
        }
    }
    
    public int get(int linha, int coluna) throws IOException {
        int val = 0;
       // if (coluna == 0) return 0;
       
            //long pos = (this.m * linha + coluna) * 4;
//            long linhaLong = linha;
//            long colunaLong = coluna;
            //long pos = (linhaLong*n)*4 + colunaLong * 4;
            long pos = ((long)linha*n)*4 + (long)coluna * 4;
            //long pos2 = (linha*n)*4 + coluna * 4;
            //long pos = (m*4+4)*linha;
            //long pos = linha * (4*this.m+System.getProperty("line.separator").getBytes().length);
           matriz.seek(pos);
           val = matriz.readInt();

            
            //val = matriz.readByte();
        
        return val;
    }
    
    public int linhas() {
        return this.m;
    }
    
    public int colunas() {
        return this.n;
    }
    
    public void close() {
        try {
            matriz.close();
        }
        catch (IOException exp) {
        }
    }
    /*
    public static void main(String[] args) {
        MatrizEmDisco m = new MatrizEmDisco(3, 3);
        
        int c = 0;
        
        
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 3; j++)
                m.set(i, j, c++);
        
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 3; j++)
                System.out.println(m.get(i, j));
    }
    */
}

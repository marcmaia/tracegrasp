package sequence.alignment;

import data.handler.CarryFileMemory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeedlemanWunsch {
    static private int contadorMetodos = 1;

    static private HashMap<Integer, Metodo> m_MetodosList = new HashMap<Integer, Metodo>();

    public static final int A=0, G=1, C=2, T=3;
    public static final int d = 0;
    
    public static Integer[] seqA, seqB;
    private static Vector<Integer> alA;// = new Vector<Integer>();
    private static Vector<Integer> alB;// = new Vector<Integer>();
    
    //public static int[][] ar;
    private static MatrizEmDisco matriz;
    private static int numBlocks = 0;
    
    public static String run(String sequence1, String sequence2, String root) throws IOException
    {
        alA = new Vector<Integer>();
        alB = new Vector<Integer>();

        File temp = new File(root);
        temp.mkdir();
  
         readFile(sequence1+"\\data.trace");
         Vector seqAVector = readFileMethodCalls(0);
	        
	 readFile(sequence2+"\\data.trace"); //Pequeno porte
         Vector seqBVector = readFileMethodCalls(1);
           
            seqA = new Integer[seqAVector.size()];
            seqB = new Integer[seqBVector.size()];
            seqAVector.copyInto(seqA);
            seqBVector.copyInto(seqB);
            
            System.out.println("Calculando Matriz...");
            matriz = NeedlemanWunsch.calculateMatrix(seqA, seqB, root);
            //matriz = new MatrizEmDisco();
            System.out.println("Matriz Calculada!");
            writeSeqsBeforeAlignment(root);
            System.out.println("Pegando alinhamento...");
            NeedlemanWunsch.getIntAlignments(matriz, seqA, seqB);
            System.out.println("\nAlinhamento completo!");
            writeSeqsAligned(root);
           

            matriz.close();            
            //======================= FIM TESTE =============================//
            return "Completed Alignment";
    }
    
    static void printlnIntArray(Integer x[]) {
    	for (int i = 0; i<x.length; i++)
    		System.out.printf("%2d ",x[i]);
    	System.out.println();
    }
    
    public static void getAlignments(int[][] ar, String sA, String sB)
    {
    	Integer[] A = convertStringToArr(sA);
    	Integer[] B = convertStringToArr(sB);
        String alA = "";
        String alB = "";        
        int i = sA.length();
        int j = sB.length();
        while (i > 0 && j > 0)
        {
            int score = ar[i][j];
            int scorediag = ar[i-1][j-1];
            int scoreup = ar[i][j-1];
            int scoreleft = ar[i-1][j];
            if (score == scorediag + similar(A[i-1], B[j-1]))
              {
                alA = sA.charAt(i-1) + alA;
                alB = sB.charAt(j-1) + alB;
                i--;j--;                
              }
            else if (score == scoreleft + d)
            {
                alA = sA.charAt(i-1) + alA;
                alB = "-" + alB;
                i--;
            }
            else if(score == scoreup + d)
            {
                alA = "-" + alA;
                alB = sB.charAt(j-1) + alB;
                j--;
            }
        }
        while(i > 0)
        {
            alA = sA.charAt(i - 1) + alA;
            alB = "-" + alB;
            i--;            
        }
        while(j > 0)
        {
            alA = "-" + alA;
            alB = sB.charAt(j - 1) + alB;
            j--;            
        }
        System.out.println(alA+"\n");
        System.out.println(alB+"\n");
    }

    public static void getIntAlignments(MatrizEmDisco mt, Integer A[], Integer B[]) throws IOException
    {

        int i = A.length;
        int j = B.length;
//        for(int p=0; p<mt.linhas(); p++){
//            for(int q=0; q<mt.colunas(); q++){
//                System.out.print(mt.get(p,q)+" ");
//            }
//            System.out.println("");
//        }
        while (i > 0 && j > 0)
        {
            int score = mt.get(i, j);
            int scorediag = mt.get(i-1, j-1);
            int scoreleft = mt.get(i, j-1);
            int scoreup = mt.get(i-1, j);
            if (score == scorediag + similar((Integer)(A[i-1]), (Integer)(B[j-1])))
              {
                alA.add(0, A[i-1]);
                alB.add(0, B[j-1]);
                i--;j--;                
              }
            else if (score == scoreleft + d)
            {
                alA.add(0,0); // '-'
                alB.add(0,B[j-1]);
                j--;
            }
            else if(score == scoreup + d)
            {
                alA.add(0, A[i-1]);
                alB.add(0, 0); // '-'
                i--;
            }
        }
        while(i > 0)
        {
            alA.add(0, A[i-1]);
            alB.add(0, 0);
            i--;            
        }
        while(j > 0)
        {
            alA.add(0,0);
            alB.add(0,B[j-1]);
            j--;            
        }
//        System.out.println("Seq1:");
//        for (i=0; i<alA.size();i++) {
//        	System.out.printf("%04d ",alA.elementAt(i));
//        }
//        System.out.println("\nSeq2:");
//        for (i=0; i<alB.size();i++) {
//        	System.out.printf("%04d ",alB.elementAt(i));
//        }
        
    }
    
    public static void appendInt(byte buffer[], int pos, int value) {
		for (int i = pos; i < pos+4; i++) {
			int offset = (4 - 1 - i) * 8;
			buffer[i] = (byte) ((value >>> offset) & 0xFF); //Converting an unsigned byte array to an integer
		}
	}
    
    public static MatrizEmDisco calculateMatrix(Integer[] source, Integer[] dest, String root)
    {
        MatrizEmDisco mt = new MatrizEmDisco(source.length+1, dest.length+1, root);
        byte buffer[] = new byte[4 * (dest.length+1)]; //Quantidades de coluna
        
        int line[] = new int[dest.length+1];
        int linePrevious[] = new int[dest.length+1];
        
        //first line
        for (int j = 0; j <= dest.length; j++){
            appendInt(buffer, j*4, 0);
            linePrevious[j] = 0;
        }
        mt.seek(0,0);
        mt.writeLine(buffer);
//        for(int position : line){
//            System.out.print(position + " ");
//        }
	
		//buffer = new byte[4 * (source.length - 1)];
		for (int i = 1; i <= source.length; i++) {
			int j = 0;
			line[0] = 0;
                        //linePrevious[0] = 0;
			appendInt(buffer,j*4,line[j]);
			//linePrevious[0] = 0;
			for (j = 1; j <= dest.length; j++) {
                            
				//int k = mt.get(i-1, j-1) + similar(source[i-1] , dest[j-1]);
				//diagonal
				int k = linePrevious[j-1] + similar(source[i-1] , dest[j-1]);
				
	            //int l = mt.get(i-1, j) + d;
				//acima
				int l = linePrevious[j] + d;
				
				//int m = mt.get(i, j-1) + d;
				//esquerda
				int m = line[j-1];		
				
				k = Math.max(k,l);
	            k = Math.max(k,m);
	            line[j] = k;
	            linePrevious[j-1] = line[j-1];
	            appendInt(buffer,j*4,line[j]);
			}
			linePrevious[j-1] = line[j-1];			
			mt.seek(i, 0);
			mt.writeLine(buffer);
//                        System.out.print("\n");
//                        for(int position : line){
//                            System.out.print(position + " ");
//                        }
			//System.out.println(source.length + " " + i);
		}
     return mt;
    }
    
   /*     
    public static MatrizEmDisco calculateMatrix(Integer[] source, Integer[] dest)
    {
        MatrizEmDisco mt = new MatrizEmDisco(source.length+1, dest.length+1);
        //int[][] res = new int[source.length+1][dest.length+1];
        for (int y = 0; y < source.length; y++)
            mt.set(y, 0, d * y);   
        
        for (int x = 0; x < dest.length; x++)
            mt.set(0, x, d * x);
        
        for (int y = 1; y < source.length + 1; y++){
            for (int x = 1; x < dest.length +1; x++)
                {                    
                    int k = mt.get(y-1, x-1) + similar(source[y-1] , dest[x-1]);
                    int l = mt.get(y-1, x) + d;
                    int m = mt.get(y, x-1) + d;
                    k = Math.max(k,l);
                    mt.set(y, x, Math.max(k,m));
                }
            System.out.println(source.length + " " + y);
        }
        return mt;
    }
    */
    
    public static Integer[] convertStringToArr(String str)
    {
        ArrayList l = new ArrayList();
        for (int i = 0; i < str.length(); i++)
        {
            int n = -1;
            if(str.charAt(i) == 'A')
                n= 1;
            if(str.charAt(i) == 'G')
                n= 2;
            if(str.charAt(i) == 'C')
                n= 3;
            if(str.charAt(i) == 'T')
                n= 4;
            
            l.add(new Integer(n));
        }
        Integer[] arr = new Integer[l.size()];
        for (int i = 0; i < l.size();i++)
            arr[i] = (Integer)l.get(i);
        return arr;    
    }
    public static int similar(int first, int second)
    {
        //return simularity[first * 4 + second];
    	return (first == second) ? 2 : -2;
    }

    //////////////////// LER ARQUIVO DE TRACE /////////////////
    static private Scanner input;
    static private HashMap tabelaMetodos = new HashMap();
    static private HashMap tabelaMetodosInvertida = new HashMap();
    static private CarryFileMemory fullFile;
    // enable user to open file
    public static void openFile(File f)
    {
       try
       {
          input = new Scanner( f); //new File( "c:\\projetos\\ExemplosTrace\\src\\graphicaleditor\\Trace1\\Thread-1\\data.trace" ) );        
       } // end try
       catch ( FileNotFoundException fileNotFoundException )
       {
          System.err.println( "Error opening file." );
          System.exit( 1 );
       } // end catch
    } // end method openFile

    public static void readFile(String f){
        fullFile = new CarryFileMemory(f);
    }

    public static Vector readFileMethodCalls(int seqNum)
    {
        String[] file = null;
        try {
            file = fullFile.carryCompleteFile();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NeedlemanWunsch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NeedlemanWunsch.class.getName()).log(Level.SEVERE, null, ex);
        }
       Vector seq = new Vector();
       // object to be written to screen
       try // read records from file using Scanner object
       {
          for(String line : file)
          {
             StringTokenizer st = new StringTokenizer(line, ",");
             String classe = st.nextToken();
             if(!classe.startsWith("<SEQ")){
                String metodo = classe + "." + st.nextToken();
            // System.out.print(metodo);
             if (tabelaMetodos.containsKey(metodo)){
            	// System.out.print(" - ja existia.");

            	 st.nextToken(); st.nextToken();
                 long timeStamp = Long.parseLong(st.nextToken().split("\r")[0]);

                 int key = (Integer)tabelaMetodos.get(metodo);
                 Metodo _metodo = m_MetodosList.get(key);
                 _metodo.addTimestamp(seqNum, timeStamp);
             }
             else {
            	 tabelaMetodos.put(metodo, contadorMetodos);
            	 tabelaMetodosInvertida.put(contadorMetodos, metodo);

            	 st.nextToken(); st.nextToken();
                 long timeStamp = Long.parseLong(st.nextToken().split("\r")[0]);

                 Metodo tmp = new Metodo(contadorMetodos, metodo);
                 tmp.addTimestamp(seqNum, timeStamp);
                 m_MetodosList.put(contadorMetodos, tmp);

            	 contadorMetodos++;
            	// System.out.print(" - inserido.");
             }

             int idMetodo = (Integer)tabelaMetodos.get(metodo);
          //   System.out.println(" - Id: " + idMetodo);
             seq.addElement(idMetodo);
             }
          } // end while
       } // end try
       catch ( NoSuchElementException elementException )
       {
          System.err.println( "File improperly formed." );
          input.close();
          System.exit( 1 );
       } // end catch
       catch ( IllegalStateException stateException )
       {
          System.err.println( "Error reading from file." );
          System.exit( 1 );
       } // end catch
       return seq;
    } // end method readRecords

    // read record from file
    public static Vector readMethodCalls(int seqNum)
    {
       Vector seq = new Vector();
       // object to be written to screen
       try // read records from file using Scanner object
       {
          while ( input.hasNext() )
          {
             String line = input.nextLine(); 
             StringTokenizer st = new StringTokenizer(line, ",");
//             System.out.println(line);
             String classe = st.nextToken();
             if(!classe.startsWith("<SEQ")){
                String metodo = classe + "." + st.nextToken();
             System.out.print(metodo);
             if (tabelaMetodos.containsKey(metodo)){
            	 System.out.print(" - ja existia.");

            	 st.nextToken(); st.nextToken();
                 long timeStamp = Long.parseLong(st.nextToken());

                 int key = (Integer)tabelaMetodos.get(metodo);
                 Metodo _metodo = m_MetodosList.get(key);
                 _metodo.addTimestamp(seqNum, timeStamp);
             }
             else {
            	 tabelaMetodos.put(metodo, contadorMetodos);
            	 tabelaMetodosInvertida.put(contadorMetodos, metodo);

            	 st.nextToken(); st.nextToken();
                 long timeStamp = Long.parseLong(st.nextToken());
                 //timestamp.put(contadorMetodos, timeStamp);
                 //System.out.print(" - Timestamp: " + timestamp.get(contadorMetodos));

                 Metodo tmp = new Metodo(contadorMetodos, metodo);
                 tmp.addTimestamp(seqNum, timeStamp);
                 m_MetodosList.put(contadorMetodos, tmp);

            	 contadorMetodos++;
            	 System.out.print(" - inserido.");
             }

             int idMetodo = (Integer)tabelaMetodos.get(metodo);
             System.out.println(" - Id: " + idMetodo);
             seq.addElement(idMetodo);
             }
          } // end while
       } // end try
       catch ( NoSuchElementException elementException )
       {
          System.err.println( "File improperly formed." );
          input.close();
          System.exit( 1 );
       } // end catch
       catch ( IllegalStateException stateException )
       {
          System.err.println( "Error reading from file." );
          System.exit( 1 );
       } // end catch
       return seq;
    } // end method readRecords

    // close file and terminate application
    public static void closeFile()
    {
       if ( input != null )
          input.close(); // close file
    } // end method closeFile
    

    private static void writeSeqsAligned(String root) {
       Formatter outputA=null; // object used to output text to file
       Formatter outputB=null; // object used to output text to file

       try
       {
//    	   outputA = new Formatter("C:\\vdiffsimilar\\ClassDiagram-Compressed\\Thread-2\\dataAlignment.trace"); //vdiffsimilar
//    	   outputB = new Formatter("C:\\vdiffsimilar\\UseCase-Compressed\\Thread-2\\dataAlignment2.trace");//

//    	   outputA = new Formatter("C:\\vdiffigual\\ClassDiagram-Compressed\\Thread-2\\dataAlignment.trace"); //vdifFigual
//    	   outputB = new Formatter("C:\\vdiffigual\\ClassDiagram2-Compressed\\Thread-2\\dataAlignment2.trace");//

//   	   outputA = new Formatter("C:\\vigualfsimilar\\ClassDiagram-Compressed\\Thread-2\\dataAlignment.trace"); //VigualFsimilar
//   	   outputB = new Formatter("C:\\vigualfsimilar\\UseCase-Compressed\\Thread-2\\dataAlignment2.trace");//

    	   //    	   outputA = new Formatter("C:\\SanityTest\\ClassDiagram-Compressed\\Thread-2\\dataAlignment.trace"); //Sanity Test
//    	   outputB = new Formatter("C:\\SanityTest\\ClassDiagram2-Compressed\\Thread-2\\dataAlignment2.trace");//Sanity test

    	       	   outputA = new Formatter(root + "\\dataAlignment.trace"); //Pequeno porte
    	   outputB = new Formatter(root + "\\dataAlignment2.trace");//Pequeno porte

    	   //    	   outputA = new Formatter("C:\\EditorTest\\reta\\Thread-2\\dataAlignment.trace"); //Pequeno porte
//    	   outputB = new Formatter("C:\\EditorTest\\circulo\\Thread-2\\dataAlignment2.trace");//Pequeno porte
    	  
       } // end try
       catch ( SecurityException securityException )
       {
          System.err.println("You do not have write access to this file." );
          System.exit( 1 );
       } // end catch
       catch ( FileNotFoundException filesNotFoundException )
       {
          System.err.println("WriteSeqsAligned: Error creating file." );
          System.exit( 1 );
       } // end catch
       
       for (int i=0; i<alA.size();i++) {
       	  outputA.format("%d\n",alA.elementAt(i));
       }

       for (int i=0; i<alB.size();i++) {
       	outputB.format("%d\n",alB.elementAt(i));
       }

       if ( outputA != null )
          outputA.close();
       if ( outputB != null )
           outputB.close();
    } // end method 

    private static void writeSeqsBeforeAlignment(String root) {
        Formatter outputA=null; // object used to output text to file
        Formatter outputB=null; // object used to output text to file

        try
        {
//        	outputA = new Formatter( "C:\\vdiffsimilar\\ClassDiagram-Compressed\\Thread-2\\dataBeforeAlignment.trace"  ); //vdiffsimilar
//      	   	outputB = new Formatter( "C:\\vdiffsimilar\\UseCase-Compressed\\Thread-2\\dataBeforeAlignment2.trace"  ); //

//        	outputA = new Formatter( "C:\\vdiffigual\\ClassDiagram-Compressed\\Thread-2\\dataBeforeAlignment.trace"  ); //vdiffigual
//      	   	outputB = new Formatter( "C:\\vdiffigual\\ClassDiagram2-Compressed\\Thread-2\\dataBeforeAlignment2.trace"  ); 
      	   	
        //	outputA = new Formatter( "C:\\vigualfsimilar\\ClassDiagram-Compressed\\Thread-2\\dataBeforeAlignment.trace"  ); //VigualFsimilar
      	 //  	outputB = new Formatter( "C:\\vigualfsimilar\\UseCase-Compressed\\Thread-2\\dataBeforeAlignment2.trace"  ); //

  //      	        	outputA = new Formatter( "C:\\SanityTest\\ClassDiagram-Compressed\\Thread-2\\dataBeforeAlignment.trace"  ); //Sanity Test
//      	   	outputB = new Formatter( "C:\\SanityTest\\ClassDiagram2-Compressed\\Thread-2\\dataBeforeAlignment2.trace"  ); //Sanity Test

        	        	outputA = new Formatter( root + "\\dataBeforeAlignment.trace"  ); //Pequeno porte
      	   	outputB = new Formatter( root + "\\dataBeforeAlignment2.trace"  ); //Pequeno porte

        	//        	outputA = new Formatter( "C:\\EditorTest\\reta\\Thread-2\\dataBeforeAlignment.trace"  ); //Pequeno porte
//        	outputB = new Formatter( "C:\\EditorTest\\circulo\\Thread-2\\dataBeforeAlignment2.trace"  ); //Pequeno porte
     	   
        } // end try
        catch ( SecurityException securityException )
        {
           System.err.println("You do not have write access to this file." );
           System.exit( 1 );
        } // end catch
        catch ( FileNotFoundException filesNotFoundException )
        {
           System.err.println("writeSeqsBeforeAlignment: Error creating file.");
           System.exit( 1 );
        } // end catch

        for (int i=0; i<seqA.length;i++) {
        	  outputA.format("%3d %s\n",seqA[i], tabelaMetodosInvertida.get(seqA[i]));
        }

        for (int i=0; i<seqB.length;i++) {
       	    outputB.format("%3d %s\n",seqB[i], tabelaMetodosInvertida.get(seqB[i]));
        }

        if ( outputA != null )
           outputA.close();
        if ( outputB != null )
            outputB.close();
     } // end method 

    
    private static void writeCommonalitiesVariabilities() {
        Formatter outputCommon=null; // object used to output text to file
        Formatter outputA=null; // object used to output text to file
        Formatter outputB=null; // object used to output text to file
        HashSet common = new HashSet();
        HashSet specificA = new HashSet();
        HashSet specificB = new HashSet();        
        
        try
        {
//        	outputCommon = new Formatter( "C:\\vdiffsimilar\\relComum-Compressed.txt"  ); //Vdiffsimilar
//      	   	outputA = new Formatter( "C:\\vdiffsimilar\\relEspecA-Compressed.txt"  ); //
//      	   	outputB = new Formatter( "C:\\vdiffsimilar\\relEspecB-Compressed.txt"  ); //
      	  
//      	   	outputCommon = new Formatter( "C:\\vdiffigual\\relComum-Compressed.txt"); //vdiffsimilar
//    	   	outputB = new Formatter( "C:\\vdiffigual\\relEspecB-Compressed.txt"  ); 
//    	   	outputA = new Formatter( "C:\\vdiffigual\\relEspecA-Compressed.txt"  ); 
    	   	
    	  // 	outputCommon = new Formatter( "C:\\vigualfsimilar\\relComum-Compressed.txt"  ); //Vers�o igual, fun��o similar
      	  // 	outputA = new Formatter( "C:\\vigualfsimilar\\relEspecA-Compressed.txt"  ); //
      	   //	outputB = new Formatter( "C:\\vigualfsimilar\\relEspecB-Compressed.txt"  ); //
      	   	
      	    //outputCommon = new Formatter( "C:\\SanityTest\\relComum-Compressed.txt"  ); //Sanity test
    	   	//outputA = new Formatter( "C:\\SanityTest\\relEspecA-Compressed.txt"  ); //Sanity Test
    	   	//outputB = new Formatter( "C:\\SanityTest\\relEspecB-Compressed.txt"  ); //Pequeno porte
    	   	
    	   	outputCommon = new Formatter( "C:\\Luciana\\Matrix\\relComum-Compressed.txt"  ); //Pequeno porte
      	   	outputA = new Formatter( "C:\\Luciana\\Matrix\\relEspecA-Compressed.txt"  ); //Pequeno porte
      	   	outputB = new Formatter( "C:\\Luciana\\Matrix\\relEspecB-Compressed.txt"  ); //Pequeno porte
        	
      //  	outputCommon = new Formatter( "C:\\EditorTest\\relComum.txt"  ); //Pequeno porte
      //   	outputA = new Formatter( "C:\\EditorTest\\relEspecA.txt"  ); //Pequeno porte
      //   	outputB = new Formatter( "C:\\EditorTest\\relEspecB.txt"  ); //Pequeno porte
        } // end try
        catch ( SecurityException securityException )
        {
           System.err.println("You do not have write access to this file." );
           System.exit( 1 );
        } // end catch
        catch ( FileNotFoundException filesNotFoundException )
        {
           System.err.println("writeCommonalitiesVariabilities: Error creating file.");
           System.exit( 1 );
        } // end catch


        for (int i =0; i < alA.size(); i++) {
        	if (alA.elementAt(i) == alB.elementAt(i)) 
        		common.add(alA.elementAt(i));
        	else if (alA.elementAt(i) == 0)
        		specificB.add(alB.elementAt(i));
        	else if (alB.elementAt(i) == 0)
        		specificA.add(alA.elementAt(i));
        	else 
        		System.out.println("Erro!!");
        }        		
        System.out.println("\n Tams: " + common.size() + ", " + specificA.size() + ", " + specificB.size()) ;       
        Object aux[] = common.toArray();
        for (int i=0; i < aux.length; i ++)
        	outputCommon.format("%s\n", tabelaMetodosInvertida.get((Integer)aux[i]));

        aux = specificA.toArray();
        for (int i=0; i < aux.length; i ++)
        	outputA.format("%s\n", tabelaMetodosInvertida.get((Integer)aux[i]));

        aux = specificB.toArray();
        for (int i=0; i < aux.length; i ++)
        	outputB.format("%s\n", tabelaMetodosInvertida.get((Integer)aux[i]));


        if ( outputCommon != null )
            outputCommon.close();
        if ( outputA != null )
            outputA.close();
        if ( outputB != null )
            outputB.close();
     } // end method 
    
    private static void writeBlocks(){
    	//block blocks[] = new block();
    	int blockID = 0;
    	
    	System.out.println("SeqA: " + alA.size());
        for (int i = 0; i < alA.size(); i++)
            System.out.print(String.format("%2d", alA.get(i)) + " ");
        System.out.println();
        
        System.out.println("SeqB: " + alB.size());
        for (int i = 0; i < alB.size(); i++)
            System.out.print(String.format("%2d", alB.get(i)) + " ");
        System.out.println();
        
//        BlocoWriter writer1 = new BlocoWriter("C:\\vdiffsimilar\\ClassDiagram-Compressed\\Thread-2\\blocks"); //vdiffsimilar
//        BlocoWriter writer2 = new BlocoWriter("C:\\vdiffsimilar\\UseCase-Compressed\\Thread-2\\blocks"); 
                
//        BlocoWriter writer1 = new BlocoWriter("C:\\vdiffigual\\ClassDiagram-Compressed\\Thread-2\\blocks"); //VdifFigual
//        BlocoWriter writer2 = new BlocoWriter("C:\\vdiffigual\\ClassDiagram2-Compressed\\Thread-2\\blocks"); //vdifFigual
        
       // BlocoWriter writer1 = new BlocoWriter("C:\\vigualfsimilar\\ClassDiagram-Compressed\\Thread-2\\blocks"); //Vers�o igual, fun��o similar
       // BlocoWriter writer2 = new BlocoWriter("C:\\vigualfsimilar\\UseCase-Compressed\\Thread-2\\blocks"); //Vers�o igual, fun��o similar
        
//        BlocoWriter writer1 = new BlocoWriter("C:\\SanityTest\\ClassDiagram-Compressed\\Thread-2\\blocks"); //Sanity test
//        BlocoWriter writer2 = new BlocoWriter("C:\\SanityTest\\ClassDiagram2-Compressed\\Thread-2\\blocks"); //Sanity Test
        
        BlocoWriter writer1 = new BlocoWriter("C:\\EditorTest\\reta-Compressed\\Thread-2\\blocks"); //Pequeno porte
        BlocoWriter writer2 = new BlocoWriter("C:\\EditorTest\\circulo-Compressed\\Thread-2\\blocks"); //Pequeno porte
        
//        BlocoWriter writer1 = new BlocoWriter("C:\\EditorTest\\reta\\Thread-2\\blocks"); //Pequeno porte
//        BlocoWriter writer2 = new BlocoWriter("C:\\EditorTest\\circulo\\Thread-2\\blocks"); //Pequeno porte
      
        
        Bloco bloco1;
        Bloco bloco2;
        for (int i = 0; i < alA.size(); ) {
        	if ( (alA.get(i) == 0) || (alB.get(i) == 0) ) {
        		bloco1 = new Bloco(blockID, false);
        		bloco2 = new Bloco(blockID++, false);
        		
        		while( i < alA.size() && (alA.get(i) == 0 || alB.get(i) == 0) ) {
        			bloco1.add(alA.get(i));
        			bloco2.add(alB.get(i));
        			
        			i++;
        		}
        		writer1.write(bloco1);
        		writer2.write(bloco2);
        	}
        	else {
        		bloco1 = new Bloco(blockID, true);
        		bloco2 = new Bloco(blockID++, true);
        		
        		while( i < alA.size() && ((alA.get(i) != 0) && (alB.get(i) != 0)) ) {
        			bloco1.add(alA.get(i));
        			bloco2.add(alB.get(i));
        			
        			i++;
        		}
        		writer1.write(bloco1);
        		writer2.write(bloco2);
        	}
        }
        numBlocks = blockID;
    }
   
    public static void relatorio(int sequencia, String pathBlock, String pathFeature, int n, String pathReport) {
        int pos = 0;
        Formatter outputReport = null;
        String temp;
        int nalignerCount = 0;
        File file = new File(pathBlock);
        file.mkdir();
        
      

        try
        {
           outputReport = new Formatter(pathReport);
        } // end try
        catch ( SecurityException securityException )
        {
           System.err.println("You do not have write access to this file.");
           System.exit( 1 );
        } // end catch
        catch ( FileNotFoundException filesNotFoundException )
        {
           System.err.println("Error creating report file.");
           System.exit( 1 );
        } // end catch
        
        FeatureLoader floader = new FeatureLoader(pathFeature);
        BlocoLoader loader = new BlocoLoader(pathBlock);
        for (int k = 0; k < n; k++) {
            Bloco bloco = loader.load(k);
            System.out.println("\n*** Analisando " + bloco);
            
            temp = "*** Analisando " + bloco;
            outputReport.format("\n%s", temp);
            outputReport.flush();
            
            System.out.println("-=> Sequencia: de " + pos + " a " + (pos + bloco.size() - 1));
            
            temp = "Sequencia: de " + pos + " a " + (pos + bloco.size() - 1 + ".");
            outputReport.format("\n%s\n", temp);
            outputReport.flush();
            
            pos += bloco.size();
            if (!bloco.isAligned()) {
            	System.out.println("---> Nao esta alinhada.");
            	
            	nalignerCount++;
            	
            	temp = "---> Nao esta alinhada.";
            	outputReport.format("%s\n", temp);
                outputReport.flush();
               // if(bloco.first() == 0){
               // 	continue;
               // }
            }
            /*
            if (bloco.isAligned()) {
                for (int j = 0; j < bloco.size(); j++) {
                    Metodo metodo = m_MetodosList.get(bloco.get(j));
                
                    outputReport.format("** O Metodo %d tem %d ocorrencias na sequencia %d.\n", bloco.get(j), metodo.size(sequencia), sequencia);
                    outputReport.flush();
                }
            }
            */
            float totalPorFeature = 0;
            //PARA CADA FEATURE, FAÇA:
            for (int i = 0; i < floader.size(); i++) {
                Feature feature = floader.get(i);
                System.out.println("--------> Caracter�stica " + feature.getNome() + ": ");
                
              try {
                int freq = 0;
                //VERIFICA QUANTOS METODOS ESTAO CONTIDOS NA FEATURE
                for (int metodoID = 1; metodoID < contadorMetodos; metodoID++) {
                    Metodo _metodo = m_MetodosList.get(metodoID);
                    //_metodo.reset(sequencia);
                    _metodo.resetIterator(sequencia);
                    for (int timestampCount = 0; timestampCount < _metodo.size(sequencia); timestampCount++) {
                        long _timestamp = _metodo.getTimestamp(sequencia, timestampCount);
                        if (_timestamp >= feature.getInicio() && _timestamp <= feature.getFim()) {
                            freq++;
                        }
                    }
                }
                outputReport.format("--------> Caracter�stica %s: %d metodos.\n", feature.getNome(), freq);
                outputReport.flush();
                
                int qt = 0;
                for (int j = 0; j < bloco.size(); j++) {
                    //VERIFICA QUANTAS VEZES O METODO DENTRO DO BLOCO OCORREU NA FEATURE
                    Metodo metodo = m_MetodosList.get(bloco.get(j));
                    
                    int ind = metodo.getCurrentIndex(sequencia);
                    long occ = metodo.getNextTimestamp(sequencia);
                    //outputReport.format("*** Testando metodo %d com timestamp[%d]: %d.\n", bloco.get(j), ind, occ);
                    //outputReport.flush();
                    if (occ >= feature.getInicio() && occ <= feature.getFim()) {
                    	qt++;
                    }
                }
                float porcentagemFeature;
                if (freq == 0)
                    porcentagemFeature = 0;
                else
                    porcentagemFeature = (float) qt / freq;
                System.out.println("---> " + String.format("%f", porcentagemFeature * 100) + "% da Caracter�stica " + feature.getNome() + " foi implementada no segmento " + bloco.getId());
                
                temp = "---> " + String.format("%f", porcentagemFeature * 100) + "% da Caracter�stica " + feature.getNome() + " foi implementada no segmento " + bloco.getId();
                outputReport.format("%s\n", temp);
                outputReport.flush();

                float porcentagemB = (float) qt / bloco.size();
                totalPorFeature += porcentagemB;
                System.out.println("---> " + String.format("%f", porcentagemB * 100) + "% do segmento " + bloco.getId() + " esta na Caracter�stica " + feature.getNome());
                
                temp = "---> " + String.format("%f", porcentagemB * 100) + "% do segmento " + bloco.getId() + " implementa a Caracter�stica " + feature.getNome();
                outputReport.format("%s\n", temp);
                outputReport.flush();
              }catch (NullPointerException ex) {
            	  continue;
              }
            }
            float rest = 1 - totalPorFeature;
            System.out.println("---> " + String.format("%f", rest * 100) + "% do segmento " + bloco.getId() + " nao se encontra em nenhuma Caracter�stica.");
                
            temp = "---===> " + String.format("%f", rest * 100) + "% do segmento " + bloco.getId() + " n�o implementou alguma Caracter�stica.";
            outputReport.format("\n%s\n", temp);
            outputReport.flush();
			//ATUALIZA LISTA DE TIMESTAMP PARA GARANTIR A RELACAO COM O METODO NA SEQUENCIA
            for (int metodoID = 1; metodoID < contadorMetodos; metodoID++) {
                Metodo _metodo = m_MetodosList.get(metodoID);
                _metodo.update(sequencia);
            }
        }
        
        temp = "Total de segmentos: " + String.format("%d", (n));
        System.out.println(temp);
        outputReport.format("\n%s\n", temp);
        outputReport.flush();
        
        temp = "Quantidade de segmentos alinhados: " + String.format("%d", (n - nalignerCount));
        System.out.println(temp);
        outputReport.format("\n%s\n", temp);
        outputReport.flush();
        
        temp = "Quantidade de segmentos n�o alinhados: " + String.format("%d", nalignerCount);
        System.out.println(temp);
        outputReport.format("\n%s\n", temp);
        outputReport.flush();
        
        outputReport.close();
    }
    
    public static void relatorio(String pathBlock, String pathFeature, int n) {
        int pos = 0;        
/*
        FeatureLoader floader = new FeatureLoader(pathFeature);
        BlocoLoader loader = new BlocoLoader(pathBlock);
        for (int k = 0; k < n; k++) {
            Bloco bloco = loader.load(k);
            System.out.println("\n*** Analisando " + bloco);
            System.out.println("-=> Sequencia: de " + pos + " a " + (pos + bloco.size() - 1));
            pos += bloco.size();
            if (!bloco.isAligned()) {
            	System.out.println("---> N�o est� alinhada.");
                continue;
            }

            for (int i = 0; i < floader.size(); i++) {
                Feature feature = floader.get(i);
                System.out.println("--------> Feature " + feature.getNome() + ": ");
                //System.out.println(timestamp.get(bloco.first()) + " >= " + feature.getInicio() + " && " + timestamp.get(bloco.last()) + " <= " + feature.getFim());

                int lim_inf = (int) Math.max(timestamp.get(bloco.first()), feature.getInicio());
                int lim_sup = (int) Math.min(timestamp.get(bloco.last()), feature.getFim());
                int intervalo = lim_sup - lim_inf;
                float porcentagem = 0;
                if (intervalo < 0)
                        intervalo = 0;
                else if (intervalo == 0)
                        intervalo = 1;

                porcentagem = (float) intervalo / feature.getIntervalo();
                System.out.println("---> " + String.format("%f", porcentagem * 100) + "% da feature " + feature.getNome() + " ocorreu no bloco " + bloco.getId());

                int qt = 0;
                for (int j = 0; j < bloco.size(); j++) {
                        long occ = timestamp.get(bloco.get(j));
                        if (occ >= feature.getInicio() && occ <= feature.getFim())
                                qt++;
                }
                float porcentagemB = (float) qt / bloco.size();
                System.out.println("---> " + String.format("%f", porcentagemB * 100) + "% do bloco " + bloco.getId() + " est� na feature " + feature.getNome());
            }
        }
    */
    }
}

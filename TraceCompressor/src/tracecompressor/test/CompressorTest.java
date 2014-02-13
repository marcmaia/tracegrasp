package tracecompressor.test;

import java.io.*;

import tracecompressor.Compressor;

import junit.framework.TestCase;

public class CompressorTest extends TestCase {
	
	private static String base_path = "tracecompressor\\test\\";
	
	public void testNoCompression() {
		File trace_file = new File(base_path + "compressed-reference.trace");
		Compressor compressor = new Compressor();
		
		File compressed = compressor.buildCompressedFrom(trace_file);
		
		assertTrue(checkCompressedFileCreated(compressed, base_path + "compressed-compressed-reference.trace"));
		
		File reference = new File(base_path + "compressed-reference.trace");
		
		assertTrue(checkEqualFiles(compressed, reference));
	}
	
	public void testCallRepetitionCompression() {
		File trace_file = new File(base_path + "call-repetition.trace");
		Compressor compressor = new Compressor();
		
		File compressed = compressor.buildCompressedFrom(trace_file);
		
		assertTrue(checkCompressedFileCreated(compressed, base_path + "compressed-call-repetition.trace"));
		
		File reference = new File(base_path + "compressed-call-repetition-reference.trace");
		
		assertTrue(checkEqualFiles(compressed, reference));
	}
	
	public void testCallRecursionCompression() {
		File trace_file = new File(base_path + "call-recursion.trace");
		Compressor compressor = new Compressor();
		
		File compressed = compressor.buildCompressedFrom(trace_file);
		
		assertTrue(checkCompressedFileCreated(compressed, base_path + "compressed-call-recursion.trace"));
		
		File reference = new File(base_path + "compressed-call-recursion-reference.trace");
		
		assertTrue(checkEqualFiles(compressed, reference));
	}
	
	public void testCallSequentialCallCompression() {
		File trace_file = new File(base_path + "call-sequence.trace");
		Compressor compressor = new Compressor();
		
		File compressed = compressor.buildCompressedFrom(trace_file);
		
		assertTrue(checkCompressedFileCreated(compressed, base_path + "compressed-call-sequence.trace"));
		
		File reference = new File(base_path + "compressed-call-sequence-reference.trace");
		
		assertTrue(checkEqualFiles(compressed, reference));
	}
	
	public void testCallSequentialCallCompression2() {
		File trace_file = new File(base_path + "call-sequence-2.trace");
		Compressor compressor = new Compressor();
		
		File compressed = compressor.buildCompressedFrom(trace_file);
		
		assertTrue(checkCompressedFileCreated(compressed, base_path + "compressed-call-sequence-2.trace"));
		
		File reference = new File(base_path + "compressed-call-sequence-reference-2.trace");
		
		assertTrue(checkEqualFiles(compressed, reference));
	}
	
	public void testCallSequentialCallCompression3() {
		File trace_file = new File(base_path + "call-sequence-3.trace");
		Compressor compressor = new Compressor();
		
		File compressed = compressor.buildCompressedFrom(trace_file);
		
		assertTrue(checkCompressedFileCreated(compressed, base_path + "compressed-call-sequence-3.trace"));
		
		File reference = new File(base_path + "compressed-call-sequence-reference-3.trace");
		
		assertTrue(checkEqualFiles(compressed, reference));
	}
	
	private boolean checkCompressedFileCreated(File compressed, String referencePath) {
		return compressed != null
			&& compressed.exists()
			&& compressed.getPath().equals(referencePath);
	}
	
	private boolean checkEqualFiles(File a, File b) {
		try {
			BufferedReader br_reference = new BufferedReader(new FileReader(a));
			BufferedReader br_compressed = new BufferedReader(new FileReader(b));
		
			String r_line = null;
			String c_line = null;
			while (null != (r_line = br_reference.readLine())) {
				c_line = br_compressed.readLine();
				
				if (null == c_line) {
					assertTrue(false);
					return false;
				}
				
				assertEquals(r_line, c_line);
			}
			
			br_reference.close();
			br_compressed.close();
			
		} catch (IOException ex) {
			assertTrue(false);
			return false;
			
		}
		
		return true;
	}
}

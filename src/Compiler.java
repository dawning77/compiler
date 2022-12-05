import backend.*;
import frontend.Lexer;
import frontend.Logger;
import frontend.Parser;
import middle.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Compiler{
	private static Lexer lexer;
	private static Parser parser;
	private static Logger logger;
	private static ICodeManager iCodeManager;
	private static MipsManager mipsManager;

	public static final boolean OUTPUT_AST = false;
	public static final boolean OUTPUT_ERROR = false;
	public static final boolean OUTPUT_ICODE = false;
	public static final boolean OUTPUT_MIPS = true;

	private static String readFile(String inputFilePath){
		File inputFile = new File(inputFilePath);
		try{
			FileReader fr = new FileReader(inputFile);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
		catch(IOException e){ throw new RuntimeException(e); }
	}

	private static void writeFile(String outputFilePath, String content){
		File outputFile = new File(outputFilePath);
		try{
			if(!outputFile.exists()){ outputFile.createNewFile(); }
			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		}
		catch(IOException e){ throw new RuntimeException(e); }
	}

	public static void main(String[] args){
		lexer = new Lexer(readFile("testfile.txt"));
		lexer.tokenize();
		logger = new Logger();
		parser = new Parser(lexer.tokens, logger);
		parser.parseCompUnit();
		if(OUTPUT_AST){ writeFile("output.txt", parser.getAST()); }
		if(OUTPUT_ERROR){ writeFile("error.txt", logger.getLog()); }
		if(logger.getLog().length() != 0){
			System.out.println("ERROR OCCURS!!!!!");
			return;
		}

		iCodeManager = new ICodeManager();
		iCodeManager.analyseCompUnit(parser.compUnit);
		if(OUTPUT_ICODE){ writeFile("ir.txt", iCodeManager.getICodes()); }

		mipsManager = new MipsManager(iCodeManager);
		mipsManager.genMips();
		if(OUTPUT_MIPS){ writeFile("mips.txt", mipsManager.getMips()); }
	}
}

package luceneCode;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.StringTokenizer; 

public class Index {

	public static void main(String[] args) {
		String indexPath = "Index";
		String docsPath = "Documents";
		
		Path indexDir = Paths.get(indexPath); 									//path for index
		Path docsDir = Paths.get(docsPath); 									//path with Restaurant's files
		
		try {
			Directory directory = FSDirectory.open(indexDir); 					//open the directory for index
			Analyzer analyzer = new StandardAnalyzer(); 						//create a Standard Analyzer
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(OpenMode.CREATE);								//create new index in the directory	
			IndexWriter writer = new IndexWriter(directory, config); 			//create an IndexWriter, for writing in index
			indexDocs(writer, docsDir); 										//call method indexDocs, which read the files in the Directory
			writer.close(); 													//close the IndexWriter
		}
		catch(IOException e) {
			System.out.println("Error");
		}
	}
	
	public static void indexDocs(IndexWriter writer, Path file) throws IOException { //this method read the files in directory one by one
		Files.walkFileTree(file, new SimpleFileVisitor<Path>() {					 //and call for each one the method createDocuments
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try {
					createDocuments(writer, file);
				} catch (IOException ignore) {}
				return FileVisitResult.CONTINUE;
				}
			});
	}
	
	public static void createDocuments(IndexWriter writer, Path file) throws IOException { //this method create a Document with fields for a file
																						   //and write it to index
		try (InputStream stream = Files.newInputStream(file)) { //open file for reading
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			
			String id = "";
			StringTokenizer tokenizer = new StringTokenizer(""+file, "\\");
			String token = "";
			while(tokenizer.hasMoreTokens())
				token = tokenizer.nextToken();
			for(int i = 0; i<token.length()-4;i++)
				id += token.charAt(i);
			
			String max_useful = "0";
			String max_date = "0000-00-00";
			
			//collect Restaurant data from file and add them as fields in Document
			Document doc = new Document();
			doc.add(new Field("filename",id,TextField.TYPE_STORED));
			doc.add(new Field("name",getFieldAndJumpOneLine(br),TextField.TYPE_STORED));
			doc.add(new Field("address",getFieldAndJumpOneLine(br),TextField.TYPE_STORED));
			doc.add(new Field("city",getFieldAndJumpOneLine(br),TextField.TYPE_STORED));
			doc.add(new Field("state",getFieldAndJumpOneLine(br),TextField.TYPE_STORED));
			doc.add(new SortedDocValuesField ("stars", new BytesRef(getFieldAndJumpOneLine(br))));
			doc.add(new SortedDocValuesField ("number_of_reviews", new BytesRef(getFieldAndJumpOneLine(br))));
			
			String line = br.readLine();
			
			//collect also from file data about Reviews for fields in Document
			while(line != null) {
				if(line.equals("*")) {
					br.readLine();
					String text = "";
					String line2 =  br.readLine();
					while(!line2.equals("*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*")) {
						text += line2;
						line2 =  br.readLine();
					}
					
					//add fields to the Document
					doc.add(new Field("review",text,TextField.TYPE_STORED));
					doc.add(new Field("user",getFieldAndJumpOneLine(br),TextField.TYPE_STORED));
					String date = getFieldAndJumpOneLine(br);
					doc.add(new Field("stars_review",getFieldAndJumpOneLine(br),TextField.TYPE_STORED));
					String useful = getFieldAndJumpOneLine(br);

					int linesToJump = 5;
					for(int i=0; i<linesToJump; i++)
						br.readLine();
					
					if(max_useful.compareTo(useful) == -1) //search for the most useful Restaurant's review in file
						max_useful = useful;
					
					if(max_date.compareTo(date) == -1) //search for the most recent Restaurant's review in file
						max_date = date;
					
				}
				line = br.readLine();
			}
			//add fields to the Document
			doc.add(new SortedDocValuesField ("date", new BytesRef(max_date)));
			doc.add(new SortedDocValuesField ("useful", new BytesRef(max_useful)));
			
			//write the Document to the index
			writer.addDocument(doc);
		}
	}
	
	private static String getFieldAndJumpOneLine(BufferedReader br) throws IOException {
			String field = br.readLine();
			br.readLine();
			return field;
	}
}
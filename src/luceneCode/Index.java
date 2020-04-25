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
		
		Path indexDir = Paths.get(indexPath); //path for index
		Path docsDir = Paths.get(docsPath); //path with Restaurant's files
		
		try {
			Directory directory = FSDirectory.open(indexDir); //open the directory for index
			Analyzer analyzer = new StandardAnalyzer(); //create a Standard Analyzer
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(OpenMode.CREATE);	//create new index in the directory	
			IndexWriter writer = new IndexWriter(directory, config); //create an IndexWriter, for writing in index
			indexDocs(writer, docsDir); //call method indexDocs, which read the files in the Directory
			writer.close(); //close the IndexWriter
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
			
			String f = ""+file; //from the file's path, get the file's name
			StringTokenizer t = new StringTokenizer(f, "\\");
			String tok = "";
			while(t.hasMoreTokens()) {
				tok = t.nextToken();
			}
			String id = "";
			for(int i = 0; i<tok.length()-4;i++) {
				id += tok.charAt(i);
			}
			
			String max_useful = "0";
			String max_date = "0000-00-00";
			
			//collect from file data about Restaurant for fields in Document
			String name = br.readLine();
			br.readLine();
			String address = br.readLine();
			br.readLine();
			String city = br.readLine();
			br.readLine();
			String state = br.readLine();
			br.readLine();
			String stars = br.readLine();
			br.readLine();
			String number_of_reviews = br.readLine();
			br.readLine();
			
			//add fields to the Document
			Document doc = new Document();
			doc.add(new Field("filename",id,TextField.TYPE_STORED));
			doc.add(new Field("name",name,TextField.TYPE_STORED));
			doc.add(new Field("address",address,TextField.TYPE_STORED));
			doc.add(new Field("city",city,TextField.TYPE_STORED));
			doc.add(new Field("state",state,TextField.TYPE_STORED));
			doc.add(new SortedDocValuesField ("stars", new BytesRef(stars)));
			doc.add(new SortedDocValuesField ("number_of_reviews", new BytesRef(number_of_reviews)));
			
			String line = br.readLine();
			
			//collect also from file data about Reviews for fields in Document
			while( line != null) {
				if(line.equals("*")) {
					br.readLine();
					String text = "";
					String line2 =  br.readLine();
					while(!line2.equals("*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*")) {
						text += line2;
						line2 =  br.readLine();
					}
					
				    String user = br.readLine();
				    br.readLine();
					String date = br.readLine();
					br.readLine();
					String stars_review = br.readLine();
					br.readLine();
					String useful = br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					
					if(max_useful.compareTo(useful) == -1) { //search for the most useful Restaurant's review in file
						max_useful = useful;
					}
					
					if(max_date.compareTo(date) == -1) { //search for the most recent Restaurant's review in file
						max_date = date;
					}
					
					//add fields to the Document
					doc.add(new Field("review",text,TextField.TYPE_STORED));
					doc.add(new Field("user",user,TextField.TYPE_STORED));
					doc.add(new Field("stars_review",stars_review,TextField.TYPE_STORED));
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
}
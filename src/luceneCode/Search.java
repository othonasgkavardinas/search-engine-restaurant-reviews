package luceneCode;

import GUI.Menu;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;

public class Search {
	
	private static String indexPath = "Index";
	private Path indexDir = Paths.get(indexPath);
	
	public Search() {} //empty constractor
	
	//doSearch is the method for search, 
	//					 				userQuery has user's request,
	//					 				field has the Field for search,
	//					 				kindOfSearch has "Restaurants", if user's search is for Restaurants 
	//									and "Reviews", if user's search is for Reviews,
	//									sortBy has the Field for sort
	public void doSearch(String userQuery, String field, String kindOfSearch, String sortBy) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir)); //create an IndexReader
	    IndexSearcher searcher = new IndexSearcher(reader); //create an IndexSearcher
	    Analyzer analyzer = new StandardAnalyzer(); //create a StandardAnalyzer
	    
	    QueryParser parser;
	    Query query;   
	    if(field.equals("location and review")) { //if search is for Restaurants and for more than one Fields
	    	String fields[] = new String[4];
	    	fields[0] = "city";
	    	fields[1] = "state";
	    	fields[2] = "address";
	    	fields[3] = "review";
	    	MultiFieldQueryParser mparser = new MultiFieldQueryParser(fields, analyzer); //create a MultiFieldQueryParser for these Fields
	    	query = mparser.parse(userQuery); //create the query for search
	    }
	    else if(field.equals("name and review")) { //if search is for Reviews and for more than one Fields 
	    	String fields[] = new String[2];
	    	fields[0] = "name";
	    	fields[1] = "review";
	    	MultiFieldQueryParser mparser = new MultiFieldQueryParser(fields, analyzer); //create a MultiFieldQueryParser for these Fields
	    	query = mparser.parse(userQuery); //create the query for search
	    }
	    else {
	    	parser = new QueryParser(field, analyzer); //create a QueryParser for one Field
	    	query = parser.parse(userQuery); //create the query for search
	    }
	    
	    //Depending on the type of search and layout, 
	    //we store the 1000 most relevant search results, according to the query, appropriately arranged
	    Sort sort = null;
	    TopDocs topDocs = null;
	    if(kindOfSearch.equals("Restaurants")) {
	    	if (sortBy.equals("Sort by: text (default)")) {
	    		 topDocs = searcher.search(query, 1000);
	    	}
	    	else if (sortBy.equals("Sort by: number of reviews")) {
	    		sort = new Sort(SortField.FIELD_SCORE, new SortField("number_of_reviews", Type.STRING));
	    		topDocs = searcher.search(query, 1000, sort, true, true);
	    	}
	    	else if (sortBy.equals("Sort by: number of stars")) {
	    		sort = new Sort(new SortField("stars", SortField.Type.STRING));
	    		topDocs = searcher.search(new TermQuery(new Term("stars")),1000,sort);
	    	}
	    }
	    else if(kindOfSearch.equals("Reviews")) {
	    	if (sortBy.equals("Sort by: text (default)")) {
	    		topDocs = searcher.search(query, 1000);
	    	}
	    	else if (sortBy.equals("Sort by: useful count")) {
	    		sort = new Sort(SortField.FIELD_SCORE, new SortField("useful", Type.STRING));
	    		topDocs = searcher.search(query, 1000, sort, true, true);
	    	}
	    	else if (sortBy.equals("Sort by: more recent")) {
	    		sort = new Sort(SortField.FIELD_SCORE, new SortField("date", Type.STRING));
	    		topDocs = searcher.search(query, 1000, sort, true, true);
	    	}
	    }
	
	    ScoreDoc[] hits = topDocs.scoreDocs; //store results in a ScoreDoc list
	    
	    //from the results, we collect only them in different city for Restaurants,
	    //and them from different user for Reviews
	    HashMap<String,String> newHits = new HashMap<String,String>();
	    HashSet<String> location = new HashSet<String>();
	    HashSet<String> users = new HashSet<String>();
	    int docId;
	    Document d;
	    for (int i = 0; i < hits.length; i++) {
	    	if(kindOfSearch.equals("Restaurants")) {
		    	docId = hits[i].doc;
	            d = searcher.doc(docId);
		    	if(!location.contains(d.get("city"))) {
		    		location.add(d.get("city"));
		    		newHits.put(d.get("name"), d.get("filename"));
		    	}
		    	else if(newHits.size()<100) {
		    		newHits.put(d.get("name"), d.get("filename"));
		    	}
	    	}
	    	else if(kindOfSearch.equals("Reviews")) {
	    		docId = hits[i].doc;
	            d = searcher.doc(docId);
	            if(!users.contains(d.get("user"))) {
		    		users.add(d.get("user"));
		    		newHits.put(d.get("name"), d.get("filename"));
		    	}
	            else if(newHits.size()<100) {
	            	newHits.put(d.get("name"), d.get("filename"));
		    	}
	    	}
	    }
	    
	    Menu menu = new Menu();  //create a Menu object
	    menu.results(newHits, userQuery); //call results method in Menu class
	}
}

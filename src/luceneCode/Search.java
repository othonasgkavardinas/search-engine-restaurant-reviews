package luceneCode;

import GUI.Menu;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	//doSearch is the method for search, 
	//					 				userQuery has user's request,
	//					 				field has the Field for search,
	//					 				kindOfSearch has "Restaurants", if user's search is for Restaurants 
	//									and "Reviews", if user's search is for Reviews,
	//									sortBy has the Field for sort
	public static void doSearch(String userQuery, String field, String kindOfSearch, String sortBy) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
		Path indexDir = Paths.get("Index");
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir)); //create an IndexReader
	    IndexSearcher searcher = new IndexSearcher(reader); //create an IndexSearcher
	    Analyzer analyzer = new StandardAnalyzer(); //create a StandardAnalyzer
	    
	    String[] fields;
	    Query query;   
	    QueryParser parser;
	    switch(field) {
	    	case "location and review":
	    		fields = new String[] { "city", "state", "address", "review" };
	    		parser = new MultiFieldQueryParser(fields, analyzer); //create a MultiFieldQueryParser for these Fields
	    		break;
	    	case "name and review" ://if search is for Reviews and for more than one Fields 
	    		fields = new String[] { "name", "review" };
	    		parser = new MultiFieldQueryParser(fields, analyzer); //create a MultiFieldQueryParser for these Fields
	    		break;
	    	default:
	    		parser = new QueryParser(field, analyzer); //create a QueryParser for one Field
	    }
	    query = parser.parse(userQuery); //create the query for search
	    
	    //Depending on the type of search and layout, 
	    //we store the 1000 most relevant search results, according to the query, appropriately arranged
	    Sort sort = null;
	    TopDocs topDocs = null;
	    switch(kindOfSearch) {
	    	case "Restaurants":
	    		switch(sortBy) {
	    			case "Sort by: text (default)":
	    				topDocs = searcher.search(query, 1000);
	    				break;
	    			case "Sort by: number of reviews":
	    				sort = new Sort(SortField.FIELD_SCORE, new SortField("number_of_reviews", Type.STRING));
	    				topDocs = searcher.search(query, 1000, sort, true, true);
	    				break;
	    			case "Sort by: number of stars":
	    				sort = new Sort(new SortField("stars", SortField.Type.STRING));
	    				topDocs = searcher.search(new TermQuery(new Term("stars")),1000,sort);
	    		}
	    		break;
	    	case "Reviews":
	    		switch(sortBy) {
	    			case "Sort by: text (default)":
	    				topDocs = searcher.search(query, 1000);
	    				break;
	    			case "Sort by: useful count":
	    				sort = new Sort(SortField.FIELD_SCORE, new SortField("useful", Type.STRING));
	    				topDocs = searcher.search(query, 1000, sort, true, true);
	    				break;
	    			case "Sort by: more recent" :
	    				sort = new Sort(SortField.FIELD_SCORE, new SortField("date", Type.STRING));
	    				topDocs = searcher.search(query, 1000, sort, true, true);
	    		}
	    }
	
	    ScoreDoc[] hits = topDocs.scoreDocs; //store results in a ScoreDoc list
	    
	    //from the results, we collect only them in different city for Restaurants,
	    //and them from different user for Reviews
	    HashMap<String,String> newHits = new HashMap<String,String>();
	    int docId;
	    Document d;
	    String[] kindOfSearchOptions = { "Restaurants", "Reviews" };
	    String[] kindOfSearchFields = { "city", "user" };
	    ArrayList<HashSet<String>> kindOfSearchWhere = new ArrayList<HashSet<String>>();
	    kindOfSearchWhere.addAll(Arrays.asList(new HashSet<String>(), new HashSet<String>()));
	    int kindOfSearchOptionsNo = 2;
	    for (int i=0; i<hits.length; i++)
	    	for (int j=0; j<kindOfSearchOptionsNo; j++)
	    		if(kindOfSearch.equals(kindOfSearchOptions[j])) {
	    			docId = hits[i].doc;
	    			d = searcher.doc(docId);
	    			if(!kindOfSearchWhere.get(j).contains(d.get(kindOfSearchFields[j]))) {
	    				kindOfSearchWhere.get(j).add(d.get(kindOfSearchFields[j]));
	    				newHits.put(d.get("name"), d.get("filename"));
	    			}
	    			else if(newHits.size()<100)
	    				newHits.put(d.get("name"), d.get("filename"));
	    			break;
	    		}	
	    
	    Menu menu = new Menu();  //create a Menu object
	    menu.results(newHits, userQuery); //call results method in Menu class
	}
}

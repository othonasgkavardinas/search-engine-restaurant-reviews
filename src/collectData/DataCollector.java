package collectData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Iterator;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.File;


public class DataCollector {
	static HashSet<String> setOfIds = new HashSet<String>();
	static ArrayList<String> restaurantContents;
	static HashMap<String,ArrayList<String>> restaurants = new HashMap<String,ArrayList<String>>();
	static String businessFilePath = "business.json";
	static String reviewFilePath = "review.json";
	
    public static void main(String[] args) {
        readJsonFile();
    }

	public static void readJsonFile() { //method for Restaurants

        BufferedReader br = null;
        JSONParser parser = new JSONParser();
        boolean flag = false;
        boolean flag2 = false;
        int counter = 0;
        long review_number = 0;
        long min_review = 1000000;
        long max_review = 0;
        
        String business_id;
        String name;
        String address;
        String city;
        String state;
        double stars;
        long review_count;

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(businessFilePath)); //read file with businesses

            while ((sCurrentLine = br.readLine()) != null) {
               Object obj;
                try {
                	flag = false;
                	flag2 = false;
                    obj = parser.parse(sCurrentLine);
                    JSONObject jsonObject = (JSONObject) obj;
                    
                    review_count = (long) jsonObject.get("review_count");
                    if(review_count > 50) //check if business has more than 50 reviews
                    	flag2 = true;
                   
                    JSONArray categories = (JSONArray) jsonObject.get("categories");
                    @SuppressWarnings("rawtypes")
					Iterator iterator = categories.iterator();
                    while (iterator.hasNext())
                    	if(iterator.next().equals("Restaurants")) //check if business is Restaurant
                    		flag = true;
                    
                    if(flag == true && flag2 == true) {
	                    business_id = (String) jsonObject.get("business_id");
	                    name = (String) jsonObject.get("name");
	                    address = (String) jsonObject.get("address");
	                    city = (String) jsonObject.get("city");
	                    state = (String) jsonObject.get("state");
	                    stars = (double) jsonObject.get("stars");
	                    
	                    review_number += review_count;
	                    if(review_count > max_review)
	                    	max_review = review_count;
	                    if(review_count < min_review)
	                    	min_review = review_count;
	                    restaurantContents = new ArrayList<String>(); //add information about Restaurant in an ArrayList
	                    restaurantContents.addAll(Arrays.asList(name, address, city, state, ""+stars, ""+review_count));
	                	restaurants.put(business_id, restaurantContents);
	                	counter += 1;
                		setOfIds.add(business_id);
                    }
                    
                    if(counter == 14000) {
                    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
                    						+ "restaurant_number: " + counter + "\n"
                    						+ "review_number: " + review_number + "\n"
                    						+ "avg_review: " + review_number/counter + "\n"
                    						+ "min_review: " + min_review + "\n"
                    						+ "max_review: "+ max_review + "\n"
                    						+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    	createDocuments();  
                    	readJsonFile2();
                    	break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
	public static void readJsonFile2() { //method for reviews
    	BufferedReader br = null;
        JSONParser parser = new JSONParser();
        String business_id;
        String user_id;
        long stars;
        String date;
        String text;
        long useful;
        long funny;
        long cool;
        BufferedWriter bw = null;
		FileWriter fw = null;
        
        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(reviewFilePath)); //read file with reviews

            while ((sCurrentLine = br.readLine()) != null) {
            	Object obj;
                try {
                	obj = parser.parse(sCurrentLine);
                	JSONObject jsonObject = (JSONObject) obj;
                       
                	business_id = (String) jsonObject.get("business_id");
                       
                	if(setOfIds.contains(business_id)) { //if review is for one of our Restaurants
                		//collect information about review
   	                    user_id = (String) jsonObject.get("user_id");
   	                    stars = (long) jsonObject.get("stars");
   	                    date = (String) jsonObject.get("date");
   	                    text = (String) jsonObject.get("text");
   	                    useful = (long) jsonObject.get("useful");
   	                    funny = (long) jsonObject.get("funny");
   	                    cool = (long) jsonObject.get("cool");
                        
                       	try { //store in Restaurant's file the information
                       		File file = new File("Documents/"+business_id+".txt");
                       		fw = new FileWriter(file.getAbsoluteFile(), true);
                       		bw = new BufferedWriter(fw);
                       		
                       		bw.write("*\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                    		bw.write(text);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.write(user_id);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.write(date);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.write(""+stars);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.write(""+useful);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.write(""+funny);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.write(""+cool);
                            bw.write("\n*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
                            bw.close();
                            fw.close();
                       		
                       	} catch (IOException e) {
                			e.printStackTrace();
                       	}
   					}
   				}
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
           
            System.out.println("Done!"); 
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        	try {
        		if (br != null)br.close();
        	} catch (IOException ex) {
        		ex.printStackTrace();
        	}
        }
    }
    
    public static void createDocuments() { //store information about business in text file
    	FileOutputStream outputStream = null;
    	for(String id : restaurants.keySet()) {
    		try {
            	outputStream = new FileOutputStream("Documents/"+id+".txt");
        	}
        	catch(FileNotFoundException e) {
        		System.out.println("Error opening the file.");
        		System.exit(0);
        	}
    		PrintWriter outputWriter = new PrintWriter(outputStream);
    		for(String restaurant : restaurants.get(id)) {
    			outputWriter.println(restaurant);
                outputWriter.print("*-*_*-*_*-*_*-*_*-*_*-*_*-*_*-*_*\n");
    		}
    		outputWriter.close();
    	}
    	System.out.println("Restaurants ok!");
    }
}
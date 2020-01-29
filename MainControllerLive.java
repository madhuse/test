package com.ssis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.index.reindex.ReindexRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class MainController {

	/*@RequestMapping("/")
	public String showForm() {
	return "index";
		
	}*/
	
	
	
	@RequestMapping("/")
	public String userLogin() {
		
		return "userlogin";
		
	}
	
	
	@RequestMapping("/data")
	public String jspview(HttpServletRequest request,@RequestParam(name = "email") String email) {
		System.out.println(email);
		if(email.startsWith("admin")) {
			return "test";
		}
		return "userlogin";
	}
	@SuppressWarnings("unchecked")
	//@ResponseBody
	@RequestMapping("/formdata")
	public String indexData() throws  Exception{
		System.out.println("Button is submitted");
		Client client = ESConnection.getConnection();
		
		
		
		BulkByScrollResponse response1 =
				  new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
				    .filter(QueryBuilders.matchAllQuery()) 
				    .source("forecasteddata")                         
				    .get();                                             
				long deleted1 = response1.getDeleted();  
				System.out.println(deleted1);
				Thread.sleep(3000);
				
				SearchRequestBuilder searchbuilder=client.prepareSearch("xyz_new_backupdata");
			    MaxAggregationBuilder aggregation =
					        AggregationBuilders
					        .max("maxdate").field("source_start_date");
					        
			    
				searchbuilder.addAggregation(aggregation);
		        SearchResponse searchResponse=searchbuilder.execute().actionGet();
		        Max agg = searchResponse.getAggregations().get("maxdate");
		        String name = agg.getValueAsString();
		        System.out.println(name);
		        
		        Date presentdate = new Date();
		    	Date daysAgo14 = new DateTime(presentdate).minusDays(1).toDate();
		    	//Date extra30 = new DateTime(date).plusDays(extradate).toDate();
		    	DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-YYYY");
		    	Date parse = dateFormat1.parse(name);
		    	System.out.println("nnn"+dateFormat1.parse(name));
		    	//System.out.println("Changed Date"+dateFormat1.format(daysAgo14));
		    	System.out.println("Changed to"+dateFormat1.format(presentdate));
		    	
				 if (name.compareTo(dateFormat1.format(presentdate)) == 0)  {
		    	    System.out.println(" same day");
		    	    
		    	    
		    	    BulkByScrollResponse response =
		  				  new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
		  				    .filter(QueryBuilders.matchAllQuery()) 
		  				    .source("currentdaydata")                         
		  				    .get();                                             
		  				long deleted = response.getDeleted();  
		  				System.out.println(deleted);
		  		
		  				
		  				
		  				BulkByScrollResponse finalres =
		  					  new ReindexRequestBuilder(client, ReindexAction.INSTANCE)
		  					    .source("xyz_new_backupdata")
		  					    .destination("currentdaydata")
		  					    .filter(QueryBuilders.matchQuery("source_start_date", dateFormat1.format(daysAgo14))) 
		  					    .get();
		  			System.out.println(finalres);
		    	}
		//here i need to wait the thread to 1 minThread.sleep(60000);
		String recordsFile="D:\\elastic latest versions\\elk\\log files\\SSRS and CPU log Files\\Datarecords\\temp\\datafile15.txt";
		String cpuFile="D:\\elastic latest versions\\elk\\log files\\SSRS and CPU log Files\\Datarecords\\temp\\cpu_mem_log.txt";
		
		
		
		
		 DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
	    	Date date = new Date();
	    	System.out.println(dateFormat.format(date));
	        
	    	BulkByScrollResponse response11 =
					  new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
					    .filter(QueryBuilders.matchQuery("source_start_date", dateFormat.format(date))) 
					    .source("xyz_new_backupdata")                         
					    .get();                                             
					long deleted11 = response11.getDeleted();  
					System.out.println(deleted11);
	    	try {
		File folder=new File(recordsFile);
		//File tempcpufile=new File(recordsFile);
		Path path = Paths.get(cpuFile);
		List<String> allLines = Files.readAllLines(path);
		String cpurecords= allLines.get(allLines.size()-1);
		String []arrnew=cpurecords.split(",");
		String cpudata=null;
		String memorydata=null;
		Double memorydatanew=0.0;
		Double cpudatanew=0.0;
		for (int i = 0; i < arrnew.length; i++) {
			cpudata=arrnew[1].replaceAll("", "");
			cpudata = cpudata.replaceAll("^\"|\"$", "");
			cpudatanew=(Double.valueOf(cpudata));
			memorydata=arrnew[2];
			memorydata = memorydata.replaceAll("^\"|\"$", "");
			memorydatanew=(Double.valueOf(memorydata));
			
		}
			
		
		System.out.println(folder.getName());
		String nameOfFile=folder.getName();
		String[] filename=nameOfFile.split(".txt");
		
		String day="Day"+filename[0].replaceAll("datafile", "");
			System.out.println(day+"jjjjjjjjjj");
		System.out.println(folder.getAbsolutePath());
		
		BufferedReader br1=new BufferedReader(new FileReader(folder));
		String inputrecords=null;
		String []records=null;
		String table_name=null;
		String table_name_new=null;
		int recordscount=0;
		while ((inputrecords = br1.readLine()) != null) {
			if(!inputrecords.isEmpty()) {
			records=inputrecords.split(": ");
			table_name=records[0];
			recordscount=Integer.parseInt(records[1]);
			table_name=table_name.replaceAll("_DATASET.CSV", "");
			table_name=table_name.replaceAll("_DATA.CSV", "");
			table_name=table_name.replaceAll("---------- ", "");
			table_name=table_name.replaceAll("OLIST_", "");
			table_name=table_name.replaceAll("---------- OLIST_", "").toLowerCase();
			//table_name=table_name.replaceAll("s", "");
			if(table_name.equals("customers")) {
				table_name="customer";
			}
			if(table_name.equals("orders")) {
				table_name="order";
			}
			if(table_name.equals("order_payments")) {
				table_name="order_payment";
			}
			if(table_name.equals("products")) {
				table_name="product";
			}
			table_name_new="'"+table_name+"'";
			System.out.println("table_name_new"+table_name_new);
			table_name_new=table_name_new.replaceAll("_", "%20");
			table_name=table_name.replaceAll("_", " ");
			System.out.println("table_name"+table_name);
			JSONObject slatime=new JSONObject();
			slatime.put("slatime", 43);
			slatime.put("source_start_date",dateFormat.format(date));
			slatime.put("elapsed_time",0.0);
			//List<Object>inputdata=new ArrayList<Object>(Arrays.asList(table_name_new,recordscount,cpudata,memorydata));
			JSONObject json = null;
			//String urlnew="http://10.80.2.9:1234/elapsed_time/"+URLEncoder.encode(inputdata.toString(), "UTF-8");
			String urlnew="http://10.80.2.214:8800/elapsed_time/["+table_name_new+","+recordscount+","+cpudata+","+memorydata+"]";
			
			//System.out.println(URLEncoder.encode(inputdata.toString(), "UTF-8"));
				
		        URL url = new URL(urlnew);//your url i.e fetch data from .
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        
		        conn.setRequestMethod("GET");
		        
		        conn.setDoOutput(true);
		        
		        InputStreamReader in = new InputStreamReader(conn.getInputStream());
		      
		        BufferedReader br = new BufferedReader(in);
		        String output;
		        String [] arr=null;
		        String elapsed=null;
		        Float elapsednew=0.0F;
		       while ((output = br.readLine()) != null) {
		        	arr=output.split(" : ");
		        	elapsed=arr[1];
		        	elapsed = elapsed.replaceAll("\\[", "").replaceAll("\\]","");
		            json = new JSONObject();
		            elapsednew=Float.valueOf(elapsed);
		            json.put("day", day);
		            json.put("source_start_date",dateFormat.format(date));
		            json.put("table", table_name);
		            json.put("forecastednoofrecords", recordscount);
		            json.put("cpu_usage", cpudatanew);
		            json.put("memory_usage", memorydatanew); //daynumber
		            json.put("forcasted_elapsed_time", elapsednew);
		           
		           /* Double slatimefromindex=0.0;
		            SearchRequestBuilder searchbuilder=client.prepareSearch("slanumbers");
		            searchbuilder.setQuery(QueryBuilders.matchQuery("table.keyword", table_name));
		            searchbuilder.setSize(1);
		            SearchResponse searchResponse=searchbuilder.execute().actionGet();

		            SearchHit[] searchHits = searchResponse.getHits().getHits();
		            for (SearchHit searchHit : searchHits) {
		                  String hitJson = searchHit.getSourceAsString();
		                  JSONParser parser = new JSONParser(); 
		                  JSONObject jsondatafro = (JSONObject) parser.parse(hitJson);
		                  slatimefromindex  =(Double) jsondatafro.get("slatime");
		                  System.out.println(slatimefromindex);
		           // json.put("slatime", slatimefromindex);
		            
		            }
		            //System.out.println(json);
				    IndexResponse response11 = client.prepareIndex(indexname, "doc")
		        	        .setSource(json, XContentType.JSON)
		        	        .get();
		            System.out.println(response11.getId());
		            
		            IndexResponse response2=client.prepareIndex("forecasteddata", "doc")
		            		 .setSource(json, XContentType.JSON)
			        	        .get();
			            System.out.println(response2.getId());
			           
			            if(table_name.equals("customer")) {
			            IndexResponse response3=client.prepareIndex("xyz_new_backupdata", "doc")
			            		 .setSource(slatime, XContentType.JSON)
				        	        .get();
				            System.out.println(response3.getId());
			            }
		            */
		            
		            
		            IndexResponse response2=client.prepareIndex("forecasteddata", "doc")
  		            		 .setSource(json, XContentType.JSON)
  			        	        .get();
  			            System.out.println(response2.getId());
  			           
  			            if(table_name.equals("customer")) {
  			            IndexResponse response3=client.prepareIndex("xyz_new_backupdata", "doc")
  			            		 .setSource(slatime, XContentType.JSON)
  				        	        .get();
  				            System.out.println(response3.getId());
  			            }
		             }
		        
		        conn.disconnect();

			
			}
			}
	
		br1.close();
	    	}catch(Exception e) {
	    		System.out.println("Exception occured");
	    	}
		////second part
		Thread.sleep(3000);
     Calendar c = Calendar.getInstance();
		
		
		File f=new File("D:\\elastic latest versions\\elk\\log files\\SSRS and CPU log Files\\Day15");
		File[] listFiles = f.listFiles();
		for(File onefile:listFiles) {
		if(onefile.isFile()) {
		File  dir=new File("D:\\new\\logstash-7.1.1\\"+c.getTimeInMillis()+"_ssis1");
		
		dir.mkdir();
		
		Process processes = null;
		 try
	    {
	    
	            String s = "";
	            String[] cmd=null;
	             cmd = new String[] { "D:\\new\\logstash-7.1.1\\bin\\logstash.bat","--path.data",dir.getAbsolutePath(),"--pipeline.workers","1","--pipeline.batch.size","1","-f", "D:\\new\\logstash-7.1.1\\bin\\update_new_1.conf", "<", onefile.getAbsolutePath()};
	            processes = Runtime.getRuntime().exec(cmd);
	           	            BufferedReader stdInput = new BufferedReader(new InputStreamReader(processes.getInputStream()));
	            while ((s = stdInput.readLine()) != null)
	            {
	                  // System.out.println(s);
	            }
	   
	            System.out.println("processing completed for"+onefile.getAbsolutePath());
	            //onefile.deleteOnExit();
	    }catch(Exception e ) {
	    	System.out.println("error");
	    }
		// dir.
			} //if condition
		
			} //for condition
		System.out.println("Processed all the SSIS log files");
		
		
		///Third part
		Thread.sleep(3000);
		
		File f1=new File("D:\\elastic latest versions\\elk\\log files\\SSRS and CPU log Files\\Datarecords\\temp\\cpu_mem_log.txt");
		File  dir1=new File("D:\\new\\logstash-7.1.1\\"+c.getTimeInMillis()+"_cpu110");
		
		dir1.mkdir();
		
		Process processes1 = null;
		 try
	    {
	    
	            String s1 = "";
	            String[] cmd1=null;
	             cmd1 = new String[] { "D:\\new\\logstash-7.1.1\\bin\\logstash.bat","--path.data",dir1.getAbsolutePath(),"--pipeline.workers","1","--pipeline.batch.size","1","-f", "D:\\new\\logstash-7.1.1\\bin\\cpu_usage_1.conf", "<", f1.getAbsolutePath()};
	            processes1 = Runtime.getRuntime().exec(cmd1);
	           	            BufferedReader stdInput1 = new BufferedReader(new InputStreamReader(processes1.getInputStream()));
	           	         System.out.println("Starting Cpu log file");
	            while ((s1 = stdInput1.readLine()) != null)
	            {
	                  // System.out.println(s1);
	            }
	   
	           // f1.deleteOnExit();
	           // dir1.delete();
	            System.out.println("Processed cpu log file");
	    }catch(Exception e ) {
	    	System.out.println("error");
	    }
			
		//folder.deleteOnExit();
		//tempcpufile.deleteOnExit();
		 System.out.println("Total Batch was completed");
		 
		 BulkByScrollResponse response =
				  new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
				    .filter(QueryBuilders.matchAllQuery()) 
				    .source("currentdaydata")                         
				    .get();                                             
				long deleted = response.getDeleted();  
				System.out.println(deleted);
		
			Thread.sleep(3000);
		
		 
		 
		 
		 BulkByScrollResponse finalres =
				  new ReindexRequestBuilder(client, ReindexAction.INSTANCE)
				    .source("xyz_new_backupdata")
				    .destination("currentdaydata")
				    .filter(QueryBuilders.matchQuery("source_start_date", dateFormat.format(date))) 
				    .get();
		System.out.println(finalres);
		Thread.sleep(30000);
		
		 return "userlogin";
		 
		 
		 
		 
	}
}


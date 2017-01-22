package com.tasktwo.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String filename = "C:\\Users\\xom\\Desktop\\report.csv"; //location and filename of report.
		String url = "https://issues.apache.org/jira/browse/CAMEL-10597"; //HTML source.
		
		//All properties required.
		String title = "Type,Priority,Affects Version/s,Component/s,Labels,Patch Info,Estimated Complexity,Status,Resolution,Fix Version/s,Assignee,Reporter,Votes,Watchers,Created Date,Created Epoch,Updated Date,Updated Epoch,Resolved Date,Resolved Epoch,Description,Comments";

		File file = new File(filename);
		
		// if file does not exist, then create it
		if (!file.exists()) {						
			try {
				file.createNewFile();
				writeCsv(title, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		
		if (file.renameTo(file)) { //Check if the file is opening
			String summary = getParse(url);
			writeCsv(summary, filename); //If the file can be written, then write report details into file.
		} else {
			System.out.println("Report file is open, close it first !"); //If the file is opening, then pop a warning.
		}

	}

	//Analyse content from HTML source code.
	static String getParse(String url) {
		String summary = "";
		Document doc;
		try {
			
			//Get content section of HTML page.
			doc = Jsoup.connect(url).get();
			Element content = doc.getElementById("content");
			
			//Extract all properties required.
			String type = textFormatter(content.getElementById("type-val")); //Type of this issue.
			String priority = textFormatter(content.getElementById("priority-val")); //Priority of this issue.
			String affectsVersion = textFormatter(content.getElementById("versions-field")); //Which version is impacted by this issue.
			String components = textFormatter(content.getElementById("components-field")); //Which components is impacted by this issue.
			String labels = textFormatter(content.getElementById("labels-13028113-value")); //Labels of this issue.
			String patchAvailable = textFormatter(content.getElementById("customfield_12310041-field")); //If patch can fix this issue.
			String estimatedComplexity = textFormatter(content.getElementById("customfield_12310060-val")); //Estimated complexity of this issue.
			String status = textFormatter(content.getElementById("status-val")); //Status of this issue.
			String resolution = textFormatter(content.getElementById("resolution-val")); //Resolution of this issue.
			String fixVersions = textFormatter(content.getElementById("fixVersions-field")); //The version which this issue is fixed in.
			String votesData = textFormatter(content.getElementById("vote-data")); //Number of users who vote for this issue.
			String votesLabel = textFormatter(content.getElementById("vote-label")); //Text "Vote for this issue"
			String votes = votesData + " " + votesLabel; //Put number and text of Vote property together.
			String watcherData = textFormatter(content.getElementById("watcher-data")); //Number of users who watch this issue.
			String watcherLabel = textFormatter(content.getElementById("watch-label")); //Text "Start watching this issue"
			String watchers = watcherData + " " + watcherLabel; //Put number and text of watcher property together.
			String assignee = textFormatter(content.getElementById("assignee-val")); //Assignee of this issue
			String reporter = textFormatter(content.getElementById("reporter-val")); //Reporter of this issue.
			String createdDate = textFormatter(content.getElementById("create-date")); //Create date of this issue.
			long createdEpoch = getTime(createdDate); //Get Epoch of created date.
			String updatedDate = textFormatter(content.getElementById("updated-date")); //Last update date of this issue.
			long updatedEpoch = getTime(updatedDate); //Get Epoch of last update date.
			String resolvedDate = textFormatter(content.getElementById("resolved-date")); //Resolve date of this issue.
			long resolvedEpoch = getTime(resolvedDate); //Get Epoch of resolve date.
			String description = textFormatter(content.getElementById("description-val")); //Description of this issue.
			
			//Analyse comments and format text.
			Elements comments = content.getElementsByClass("action-details flooded");
			String detailedComments = "";
			for (Element comment : comments) {
				String time = comment.getElementsByClass("livestamp").text(); //Date of comment.
				String name = comment.getElementsByClass("user-hover user-avatar").text(); //Author of comment.
				String body = comment.ownText().replace("added a comment - ", ""); //Content of comment.
				detailedComments += name + ":" + getTime(time) + ":" + time + ":" + body + ";"; //Format contents of all comments.

			}
			detailedComments = detailedComments.replace(",", " "); //Remove all commas.
			
			//Add all text into one line.
			summary = type + "," + priority + "," + affectsVersion + "," + components + "," + labels + "," + patchAvailable + "," + estimatedComplexity + "," + status + "," + resolution + "," + fixVersions + "," + assignee + "," + reporter + "," + votes + "," + watchers + "," + createdDate + "," + createdEpoch + "," + updatedDate + "," + updatedEpoch + "," + resolvedDate + "," + resolvedEpoch + "," + description + "," + detailedComments;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summary;
	}

	//Format time to seconds from 1970-01-01;
	static long getTime(String create) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy HH:mm", java.util.Locale.ENGLISH);
		long createDate = 0;
		try {
			createDate = sdf.parse(create).getTime() / 1000;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return createDate;
	}

	//Write report into csv file.
	static void writeCsv(String details, String filename) {

		try {
		
			FileWriter fw = new FileWriter(filename, true);
			fw.write(details + "\r\n"); //Write report details into file.
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//There is no content or ID for some elements at all. Need to judge if it is null then format the text.
	static String textFormatter(Element rawElement){
		
		String formattedText = "";
		
		if(rawElement!=null){
			formattedText=rawElement.text().replace("\r\n", " ").replace(","," "); //Remove Enter and comma
			return formattedText;
		}
		
		return "";
	}
}

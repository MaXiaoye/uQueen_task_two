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

		String filename = "C:\\Users\\xom\\Desktop\\report.csv";
		String url = "https://issues.apache.org/jira/browse/CAMEL-10597";
		String title = "Type,Priority,Affects Version/s,Component/s,Labels,Patch Info,Estimated Complexity,Status,Resolution,Fix Version/s,Assignee,Reporter,Votes,Watchers,Created Date,Created Epoch,Updated Date,Updated Epoch,Resolved Date,Resolved Epoch,Description,Comments";

		File file = new File(filename);
		if (!file.exists()) {						// if file does not exist, then create it
			try {
				file.createNewFile();
				writeCsv(title, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		//If the file is opening
		if (file.renameTo(file)) {
			String summary = getParse(url);
			writeCsv(summary, filename);
		} else {
			System.out.println("Report file is open, close it first !");
		}

	}

	//Analyse content from HTML source code.
	static String getParse(String url) {
		String summary = "";
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Element content = doc.getElementById("content");
			String type = textFormatter(content.getElementById("type-val"));
			String priority = textFormatter(content.getElementById("priority-val"));
			String affectsVersion = textFormatter(content.getElementById("versions-field"));
			String components = textFormatter(content.getElementById("components-field"));
			String labels = textFormatter(content.getElementById("labels-13028113-value"));
			String patchAvailable = textFormatter(content.getElementById("customfield_12310041-field"));
			String estimatedComplexity = textFormatter(content.getElementById("customfield_12310060-val"));
			String status = textFormatter(content.getElementById("status-val"));
			String resolution = textFormatter(content.getElementById("resolution-val"));
			String fixVersions = textFormatter(content.getElementById("fixVersions-field"));
			String votesData = textFormatter(content.getElementById("vote-data"));
			String votesLabel = textFormatter(content.getElementById("vote-label"));
			String votes = votesData + " " + votesLabel;
			String watcherData = textFormatter(content.getElementById("watcher-data"));
			String watcherLabel = textFormatter(content.getElementById("watch-label"));
			String watchers = watcherData + " " + watcherLabel;
			String assignee = textFormatter(content.getElementById("assignee-val"));
			String reporter = textFormatter(content.getElementById("reporter-val"));
			String createdDate = textFormatter(content.getElementById("create-date"));
			long createdEpoch = getTime(createdDate);
			String updatedDate = textFormatter(content.getElementById("updated-date"));
			long updatedEpoch = getTime(updatedDate);
			String resolvedDate = textFormatter(content.getElementById("resolved-date"));
			long resolvedEpoch = getTime(resolvedDate);
			System.out.println(resolvedEpoch);
			String description = textFormatter(content.getElementById("description-val"));
			
			//Analyse comments and format text.
			Elements comments = content.getElementsByClass("action-details flooded");
			String detailedComments = "";
			for (Element comment : comments) {
				String time = comment.getElementsByClass("livestamp").text();
				String name = comment.getElementsByClass("user-hover user-avatar").text();
				String body = comment.ownText().replace("added a comment - ", "");
				detailedComments += name + ":" + getTime(time) + ":" + time + ":" + body + ";";

			}
			detailedComments = detailedComments.replace(",", " ");
			
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
			fw.write(details + "\r\n");
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
			formattedText=rawElement.text().replace("\r\n", " ").replace(","," ");
			return formattedText;
		}
		
		return "";
	}
}

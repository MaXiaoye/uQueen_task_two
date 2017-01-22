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
		String title = "Type,Priority,Assignee,Reporter,Created,Created Epoch,Description,Comments";

		File file = new File(filename);
		if (!file.exists()) {						// if file does not exist, then create it
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		//If the file is opening
		if (file.renameTo(file)) {
			writeCsv(title, filename);
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
			//System.out.println(priority);
			String affectsVersion = textFormatter(content.getElementById("version-val"));
			System.out.println(affectsVersion);
			String assignee = textFormatter(content.getElementById("assignee-val"));
			String reporter = textFormatter(content.getElementById("reporter-val"));
			String create = textFormatter(content.getElementById("create-date"));
			long createTime = getTime(create);
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
			summary = type + "," + priority + "," + assignee + "," + reporter + "," + create + "," + createTime + "," + description + ","
					+ detailedComments;

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

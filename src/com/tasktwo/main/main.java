package com.tasktwo.main;

import java.io.BufferedWriter;
import java.io.File;
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
		String title = "Type,Assignee,Reporter,Created,Created Epoch,Description,Comments";

		File file = new File(filename);
		if (!file.exists()) {						// if file does not exist, then create it
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writeCsv(title, filename);
		}

		String summary = getParse(url);
		writeCsv(summary, filename);

	}

	static String getParse(String url) {
		String summary = "";
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Element content = doc.getElementById("content");
			String type = content.getElementById("type-val").text().replace(",", " ");
			String assignee = content.getElementById("assignee-val").text().replace(",", " ");
			String reporter = content.getElementById("reporter-val").text().replace(",", " ");
			String create = content.getElementById("create-date").text().replace(",", " ");
			long createTime = getTime(create);
			String description = content.getElementById("description-val").text().replace("\r\n", " ").replace(",",
					" ");
			Elements comments = content.getElementsByClass("action-details flooded");
			String details = "";
			
			for (Element comment : comments) {
				String time = comment.getElementsByClass("livestamp").text();
				String name = comment.getElementsByClass("user-hover user-avatar").text();
				String body = comment.ownText().replace("added a comment - ", "");
				details += name + ":" + getTime(time) + ":" + time + ":" + body + ";";

			}
			details = details.replace(",", " ");
			summary = type + "," + assignee + "," + reporter + "," + create + "," + createTime + "," + description + ","
					+ details;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summary;
	}

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
}

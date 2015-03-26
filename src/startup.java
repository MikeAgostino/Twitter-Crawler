import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class startup extends Frame {
	public static String webStatus = "";
	public static String UserStatus = "";

	@SuppressWarnings("deprecation")
	public static void startup() {
		JFrame startFrame = new JFrame();
		// This is the Frame that is the container of everything,

		startFrame.setSize(750, 500);
		startFrame.setTitle("Twitter Crawler");
		startFrame.setLocation(500, 200);
		startFrame.setResizable(false);
		

		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BorderLayout());
		JPanel subPanel = new JPanel(new BorderLayout());
		
		// This panel contains a portion of the space
		startFrame.add(startPanel);

		JLabel title = new JLabel(
				"Tweet reciever                                "
						+ "                                            "
						+ "                                            "
						+ "                                            "
						+ "          By: Michael Agostino");
		startPanel.add(title, BorderLayout.NORTH);

		JButton startButton = new JButton("Collect Tweets");
		JButton clearButton = new JButton("Clear log");
		clearButton.setPreferredSize(new Dimension(40,40));

		
		// This is the button to start Collecting Tweets

		subPanel.add(startButton);
		subPanel.add(clearButton,BorderLayout.SOUTH);
		startPanel.add(subPanel, BorderLayout.WEST);

		final TextArea status = new TextArea(
				webStatus
						+ "\nPlease Enter a valid Username below and click collect tweets to begin\n");
		status.setEditable(false);
		startPanel.add(status, BorderLayout.CENTER);

		final TextField username = new TextField("Enter Username here");
		username.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				if (username.getText().equals("Enter Username here")) {
					username.setText("");
				}
				// changes username text to nothing when the box is clicked if
				// it is the default value..

			}

			@Override
			public void focusLost(FocusEvent arg0) {
				// Nothing is Focus is lost

			}
		});

		
		clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				status.setText(webStatus);
				
			}
			
		});
		
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConnectToUser(username.getText());
				status.setText(status.getText() + UserStatus);

			}

		});

		startPanel.add(username, BorderLayout.AFTER_LAST_LINE);
		startFrame.show();
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void main(String[] args) {
		ConnectToTwitter();
		startup();

	}

	private static void ConnectToTwitter() {
		String TwitterURL = "https://twitter.com/";
		try {
			webStatus = "Attempting to connect...";
			URL twitterURL = new URL(TwitterURL);
			URLConnection TwitterConnection = twitterURL.openConnection();
			TwitterConnection.connect();
			webStatus += "\nConnection Established";
		} catch (Exception e) {
			webStatus += "\nCould not connect to Twitter";
		}
	}

	private static void ConnectToUser(String User) {
		String TwitterURL = "https://twitter.com/" + User;
		try {
			URL twitterURL = new URL(TwitterURL);
			URLConnection TwitterConnection = twitterURL.openConnection();
			TwitterConnection.connect();
			String pageContent = readWebPage(TwitterURL);
			if (!pageContent.equals("bad")) {
				UserStatus = "\nConnection Established to User " + User + "\n";
				UserStatus += findText(readWebPage(TwitterURL),
						"ProfileTweet-contents");

			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			UserStatus = "\nCould not connect to User (User doesn't exist) ";
		}

	}

	@SuppressWarnings("unused")
	private static String readWebPage(String url) {
		URL mypage;
		try {
			mypage = new URL(url);
			String content = "";
			@SuppressWarnings("resource")
			Scanner pageScanner = new Scanner(mypage.openStream());
			while (pageScanner.hasNext()) {
				content += pageScanner.nextLine();
			}
			return content;
		} catch (MalformedURLException e) {
			
		} catch (IOException e) {
		
		}
		return "bad";
	}

	public static String findText(String content, String segment) {
		String timestamp =  findFirstTimeStamp(content);
		String total_tweets = "";
		String tagOpen = segment;
		String tagClose = "</p>";
		int numtweet = 1;
		int begin = content.indexOf(tagOpen) + 157;
		String leftstrip = content.substring(begin);
		int end = leftstrip.indexOf(tagClose);
		String singletweet = leftstrip.substring(0, end);
		if( singletweet.contains("<a")){
			end = singletweet.indexOf("</a");
			singletweet =  singletweet.substring(0,end);
		}
		String modifiedtweet = singletweet.replace("&#39;", "'");
		total_tweets += Integer.toString(numtweet) + "\t"
				+ modifiedtweet + "\n" + "\t\t" + timestamp + "\n";
		while (leftstrip.contains(segment)) {
			String timestamp2 =  findFirstTimeStamp(leftstrip);
			numtweet += 1;
			begin = leftstrip.indexOf(tagOpen) + 157;
			leftstrip = leftstrip.substring(begin);
			end = leftstrip.indexOf(tagClose);
			String singletweet2 = leftstrip.substring(0, end);
			if( singletweet2.contains("<a")){
				end = singletweet2.indexOf("</a");
				singletweet2 =  singletweet2.substring(0,end);
			}
			String modifiedtweet2 = singletweet2.replace("&#39;", "'");
			
			total_tweets += Integer.toString(numtweet) + "\t"
					+ modifiedtweet2 + "\n" + "\t\t" + timestamp2 + "\n";
		}

		return total_tweets;

	}
	
	public static String findFirstTimeStamp(String content){
		String tagOpen = "ProfileTweet-timestamp";
		String tagClose = ">";
		int begin = content.indexOf(tagOpen) + 110;
		String leftStrip = content.substring(begin);
		System.out.println(leftStrip);
		int end = leftStrip.indexOf(tagClose) -1 ;
		String stamp = leftStrip.substring(0,end);
		//System.out.println(stamp);
		return stamp;
	}

}

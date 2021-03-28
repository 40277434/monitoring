package com.example.demo.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component
@EnableScheduling
//@Async
public class AlarmTask {

	private String url = "http://proxy.40277434.qpc.hal.davecutting.uk/";
	String[] methodArr = {"add", "subtract", "multiply", "division", "power", "modulo"};
	SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	@Scheduled(cron = "0/5 * * * * *")
    public void run() throws InterruptedException {
    	int x = (int)(Math.random()*100+1);
    	int y = (int)(Math.random()*100+1);
    	int i = (int)(Math.random()*6);
        String method =  methodArr[i];
        Float temp = 0f;
        switch (i) {
			case 0: temp = (float) (x + y);break;
			case 1: temp = (float) (x - y);break;
			case 2: temp = (float) (x * y);break;
			case 3: temp = (float) (x / y);break;
			case 4: temp = (float) (Math.pow(x, y));break;
			case 5: temp = (float) (x % y);break;
		}
        try {
        	String urlTemp = url+"?x="+x+"&y="+y+"&method="+method;
			System.out.println("Testing: " + SimpleDateFormat.format(new Date())+": "+urlTemp);
        	String result = doGet(urlTemp);
        	JSONObject jsonObject = JSON.parseObject(result);
        	Boolean error = jsonObject.getBoolean("error");
        	if(null !=  error && !error) {
        		String res = jsonObject.getString("answer");
        		if(res != null && !"".equals(res)) {
        			if(Float.parseFloat(res) == temp) {
                		System.out.println("Scheduled: " + SimpleDateFormat.format(new Date())+": "+urlTemp+" : Calculation correct!");
                	}else {
                		System.out.println("Scheduled: " + SimpleDateFormat.format(new Date())+": "+urlTemp+" : Calculation NOT correct!");
                	}
        		}else{
        			System.out.println("Scheduled: " + SimpleDateFormat.format(new Date())+": "+urlTemp+" : param: answer is true");
        		}
        	}else {
        		System.out.println("Scheduled: " + SimpleDateFormat.format(new Date())+": "+urlTemp+" :  param: error is true");
        	}
        }catch (Exception e) {
        	System.out.println("Scheduled: " + SimpleDateFormat.format(new Date())+": "+e.toString());
		}
    }
	
	private String doGet(String httpurl) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		try {
			URL url = new URL(httpurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(60000);
			connection.connect();
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuffer sbf = new StringBuffer();
				String temp = null;
				while ((temp = br.readLine()) != null) {
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result = sbf.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			connection.disconnect();
		}
		return result;
	}
}

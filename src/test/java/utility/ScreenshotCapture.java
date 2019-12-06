package utility;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import config.Config;
import executionEngine.DriverScript;
import library.ActionKeywords;

public class ScreenshotCapture {
	public static WebDriver driver;
	public static String testcaseID;
	public static String teststepID;
	
	public static void setTestDetails(String testcasenumber, String teststepnumber) {
		testcaseID = testcasenumber;
		teststepID = teststepnumber;
	}
	
	public static void takeScreenShot(WebDriver driver) {
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(source, new File(Config.Base_Dir+"/Screenshots/Output/" + (String) ActionKeywords.localTOTest.get("Browser") 
								+ "/" + DriverScript.screenShotFolderName + "/" + testcaseID + "_" + teststepID + ".png"));
		}
		catch (Exception e) {
			System.out.println("Exception while taking screenshot " + e.getMessage());
			Log.error("Exception while taking screenshot --- " + e.getMessage());
		}
	}
}
package executionEngine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import config.Config;
import library.ActionKeywords;

public class GenerateReport
{
	public static String testcasename_Report;
	public static String testcaseID_Report;
    public static ExtentReports extent = new ExtentReports("./TestReports/" + "TestReport_" + new SimpleDateFormat("MMddHHmm").format(new Date())+ ".html" , true);
	public static ExtentTest logger;        
    public static String username;
    public static String password;
      
    
    public static void startReport() throws NoSuchMethodException, SecurityException
    {   
    	//extent.addSystemInfo("Environment", DriverScript.sEnvironment);
    	File reportFile = new File(Config.Path_ReportProp);
    	extent.loadConfig(reportFile);
    }
    
    public static void startTest(String testcaseID, String testcasename) {
    	System.out.println(testcaseID+testcasename);
    	logger = extent.startTest(testcaseID, testcasename);
 
    }
    
    public static void logTestStepResult(String teststepresult, String teststepID, String teststepdescription, String testcaseID, String testcasename, String exception) {
    	System.out.println("Exception: "+ exception);
    	if (teststepresult == "PASS") {    		
    		logger.log(LogStatus.PASS, teststepID + " - " + teststepdescription);
    		// Else if statement is added for image compare keyword condition only
    	} else if(teststepresult == "WARNING"){
    		String imagepath = "../Screenshots/Output/" + (String) ActionKeywords.localTOTest.get("Browser") + "/" + exception + "/" + exception + "_" + teststepID + ".png";
    		logger.log(LogStatus.FAIL, teststepID + "-" + teststepdescription + "<br/>" + ActionKeywords.driver.getCurrentUrl() + logger.addScreenCapture(imagepath) + exception);
    	} else{
    		String imagepath = "../Screenshots/Output/" + (String) ActionKeywords.localTOTest.get("Browser") + "/" + DriverScript.screenShotFolderName + "/" + testcaseID + "_" + teststepID + ".png";
    		logger.log(LogStatus.FAIL, teststepID + "-" + teststepdescription + "<br/>" + ActionKeywords.driver.getCurrentUrl() + logger.addScreenCapture(imagepath) + exception);   		
    	}
    	extent.flush();
    }
    
    public static void endTest() {
    	extent.endTest(logger);
    }
    
    public static void endReport() {
        extent.flush();
        //extent.close();
    }
}
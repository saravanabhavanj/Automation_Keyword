package executionEngine;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import config.Config;
import library.ActionKeywords;
import utility.ExcelUtils;
import utility.Log;
import utility.ScreenshotCapture;

import com.relevantcodes.extentreports.ExtentReports;

public class DriverScript {
	public static Properties OR;
	public static ActionKeywords<Object> actionKeywords;
	public static GenerateReport generateReports;
	public static String Auto_Url;
	public static String sActionKeyword;
	public static String sPageObject;
	public static Method method[];
	
	public static int iTestStep;
	public static int iTestLastStep;
	public static String sTestCaseID;
	public static String sTestStepID;
	public static String sTestCaseDescription;
	public static String sTestStepDescription;
	public static String sTestStepRunMode;
	public static String sRunMode;
	public static String sData;
	public static String sSheetName;
	public static String sDataProvider;
	public static String sEnvironment;
	public static String sDataSet;
	public static String screenShotFolderName;
	
	// New variable
	public static String testSuite;
	
	public static boolean bResult;
	public static String failedException;
	public static ExtentReports extent;
	public static int i;
	public static java.util.Properties p;
	public static String env = System.getProperty("env");
		
	public DriverScript() throws NoSuchMethodException, SecurityException{
		actionKeywords = new ActionKeywords<Object>();
		method = actionKeywords.getClass().getMethods();
	}
	
	public static void getProp(String prop) throws Exception{
		System.out.println(Config.Base_Dir);
		DOMConfigurator.configure("log4j.xml");
		FileInputStream fs1 = new FileInputStream(prop);
		java.util.Properties p = new Properties();
		p.load(fs1);
		System.out.println("NVF: "+p.getProperty("Path_TestData"));
		testSuite = p.getProperty("Path_TestData");
		//ExcelUtils.setExcelFile(p.getProperty("Path_TestData"));
		String Path_OR = p.getProperty("Path_OR");
		Auto_Url = p.getProperty("Auto_Url");
		FileInputStream fs2 = new FileInputStream(Path_OR);
		OR= new Properties(System.getProperties());
		OR.load(fs2);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Main method started");
		screenShotFolderName = new SimpleDateFormat("MMddHHmm").format(new Date());
		GenerateReport.startReport();
		DriverScript startEngine = new DriverScript();
		
		if(env.equals("SF_Web")){
			DriverScript.getProp(Config.Base_Dir+"/target/classes/SF/config.properties");
			startEngine.executeTest();
			DriverScript.getProp(Config.Base_Dir+"/target/classes/Web/config.properties");
			startEngine.executeTest(); 
				
		} else {
			DriverScript.getProp(Config.Base_Dir + "/target/classes/config.properties");
			String[] testSuiteArr = testSuite.split(",");
			
			for (String var : testSuiteArr) {
				ExcelUtils.setExcelFile(Config.Base_Dir + "/src/test/resources/" + var);
				startEngine.executeTest();
			}
			
		}
		ActionKeywords.globalTOTest.clear();
		GenerateReport.endReport();
		
	}
	
	private void executeTest() throws Exception {
		System.out.println("Test execution started");
		//This will return the total number of test cases mentioned in the Test cases sheet
		int iTotalTestCases = ExcelUtils.getRowCount(Config.Sheet_TestCases);
		Log.info("Total number of test cases: " + iTotalTestCases);

		//This loop will execute number of times equal to Total number of test cases
		for(int iTestcase=1;iTestcase<iTotalTestCases;iTestcase++){
			//This is to get the Test case ID from the Test Cases sheet
			sTestCaseID = ExcelUtils.getCellData(iTestcase, Config.Col_TestCaseID, Config.Sheet_TestCases).trim();
			sTestCaseDescription = ExcelUtils.getCellData(iTestcase, Config.Col_TestDescription, Config.Sheet_TestCases).trim();
			
			//This is to get the value of the Environment column for the current test case 
			sEnvironment = ExcelUtils.getCellData(iTestcase, Config.Col_Environment, Config.Sheet_TestCases).trim();
			Log.info("Run environment for test case: " + sTestCaseID + "is " + sEnvironment);
			
			//This is to get the value of the RunMode column for the current test case
			sRunMode = ExcelUtils.getCellData(iTestcase, Config.Col_RunMode,Config.Sheet_TestCases).trim();
			Log.info("Run mode for test case: " + sTestCaseID + "is " + sRunMode);
			
			//This is to get the value of the Data set column for the current test case
			sDataSet = ExcelUtils.getCellData(iTestcase, Config.Col_DataSet,Config.Sheet_TestCases).trim();
			Log.info("Data set value for test case: " + sTestCaseID + "is " + sDataSet);
			
			//This is the condition statement on RunMode value
			if (sRunMode.equals("Yes")){
				Log.startTestCase(sTestCaseID);

				//Starting the report
				GenerateReport.startTest(sTestCaseID + " - "+ sEnvironment, sTestCaseDescription);
				Log.info("Current test case ID: " + sTestCaseID);
				iTestStep = ExcelUtils.getRowContains(sTestCaseID, Config.Col_TestCaseID, sTestCaseID);
				Log.info("Current iTestStep: " + iTestStep);
				iTestLastStep = ExcelUtils.getTestStepsCount(sTestCaseID, sTestCaseID, iTestStep);
				bResult=true;

				test_Data(sTestCaseID,iTestcase);					

				if(bResult==true){
					Log.endTestCase(sTestCaseID);
				}
				
				GenerateReport.endTest();
			}
		}				
	}	

	public static void execute_Actions() throws Exception {
		String nameMatch = "No";
		for(int i=0;i<method.length;i++){			
			if(method[i].getName().equals(sActionKeyword)){
				nameMatch = "Yes";
				method[i].invoke(actionKeywords,sPageObject,sData);
				if(bResult==true){
					GenerateReport.logTestStepResult(Config.KEYWORD_PASS, sTestStepID, sTestStepDescription, sTestCaseID, sTestCaseDescription, failedException);
				}
				if(bResult==false)
				{
					// This condition is added to set the compared image in report
					if(sActionKeyword.equalsIgnoreCase("imageCompare")){
						GenerateReport.logTestStepResult("WARNING", sTestStepID, sTestStepDescription, sTestCaseID, sTestCaseDescription, sData);
					} else {
						GenerateReport.logTestStepResult(Config.KEYWORD_FAIL, sTestStepID, sTestStepDescription, sTestCaseID, sTestCaseDescription, failedException);
					}
					break;
					/*Log.endTestCase(sTestCaseID);
					ActionKeywords.closeBrowser("","");
					DriverScript.bResult=false;
					break;*/
				}
				if(DriverScript.failedException.equals("false")){
					GenerateReport.logTestStepResult(Config.KEYWORD_FAIL, sTestStepID, sTestStepDescription, sTestCaseID, sTestCaseDescription, failedException);
				}
			}
		}
		if(nameMatch.equals("No")){
			System.out.println("Class DriverScript | Method execute_Actions | Exception desc : Keyword doesn't match the Method name");
			Log.error("Class DriverScript | Method execute_Actions | Exception desc : Keyword doesn't match the Method name");
		}
	}

	public static void test_Data(String sheetname, int iTestcase) throws Exception{
		int colval = ExcelUtils.getColCount(sheetname);
		System.out.println("colval :" + colval);
		int startValue = 7;
		String[] sRange = null;
		// sDataSet is either empty, integer[3] or range[1-2]
		// If empty entire dataset will run
		// If integer [Eg: 2] then test script will run for first two data set
		// If range [Eg: 3-5] then test script will run from 3 to 5 data set
		
		if(!(sDataSet.isEmpty()) && sDataSet.contains("-")){
			sRange = sDataSet.split("-");
			startValue = (Integer.parseInt(sRange[0]) + 6);
			colval = Integer.parseInt(sRange[1]) + 7;
		} else if (!(sDataSet.isEmpty()) && colval > (Integer.parseInt(sDataSet) + 7)){
			colval = Integer.parseInt(sDataSet) + 7;
		}
		
		for(i=startValue;i<colval;i++){
			ActionKeywords.localTOTest.clear();
			for(iTestStep = 1;iTestStep<iTestLastStep;iTestStep++){
				bResult=true;
				DriverScript.failedException = "true";
				sTestStepRunMode = ExcelUtils.getCellData(iTestStep, Config.Col_TestStepRunMode, sheetname).trim();
				sTestStepID = ExcelUtils.getCellData(iTestStep, Config.Col_TestStepID, sheetname).trim();
				sTestStepDescription = ExcelUtils.getCellData(iTestStep, Config.Col_TestStepDescription, sheetname).trim();
				sPageObject = ExcelUtils.getCellData(iTestStep, Config.Col_PageObject, sheetname).trim();
				sActionKeyword = ExcelUtils.getCellData(iTestStep, Config.Col_ActionKeyword,sheetname).trim();
				sData = ExcelUtils.getCellData(iTestStep,i, sheetname).trim();
				
				//Set test case name and test step id for screen capture
				ScreenshotCapture.setTestDetails(sTestCaseID, sTestStepID);
				String sPO = null;
				if(sPageObject.contains("(var")){
					String[] sobj = sPageObject.split("\\(");
					String replaceChar = sobj[1].replace(")", "").trim();
					String rChar = (String) ActionKeywords.localTOTest.get(replaceChar);
					sPO = sPageObject.replace(replaceChar,rChar.trim());
				} else {
					sPO = sPageObject;
				}
				
				if(sTestStepRunMode.equalsIgnoreCase("No") || (sPO.toLowerCase().contains("(skip)"))){
						System.out.println(sTestStepID+"||"+sTestStepDescription+"|| is commented");
				} else {
					System.out.println(sTestStepID+"||"+sTestStepDescription+"||"+sPageObject+"||"+sActionKeyword+"||"+sData);
					execute_Actions();
				}
				// Using the below code will end the test case and move to the next test case  
				/*if(bResult==false){
					Log.endTestCase(sTestCaseID);					
					break;
				}*/	
			}
			//if(bResult==true){			
				Log.endTestCase(sTestCaseID);
			//}
			//Config.Col_DataSet=Config.Col_DataSet+1;
		}
	}
}
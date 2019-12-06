package config;

public class Config {
	//System Variables
	public static final String Base_Dir = System.getProperty("user.dir");
	public static final String Path_Log4j = Base_Dir+"/lib/apache-log4j-1.2.17/log4j-1.2.17.jar";
	public static final String Path_ReportProp = Base_Dir+"/src/test/java/executionEngine/ReportProp.xml";
	public static final String KEYWORD_FAIL = "FAIL";
	public static final String KEYWORD_PASS = "PASS";
	
	//Data Sheet Column Numbers
	public static final int Col_DataSet = 4;
	public static final int Col_Environment =3;
	public static final int Col_RunMode =2;
	public static final int Col_TestDescription = 1;
	public static final int Col_TestCaseID = 0;
	
	public static final int Col_TestStepID =1;
	public static final int Col_TestStepRunMode =2;
	public static final int Col_TestStepDescription =3;
	public static final int Col_Result =4;
	public static final int Col_PageObject =5;
	public static final int Col_ActionKeyword =6;
	public static final int Col_Sheetname =8;
	public static final int Col_Dataprovider =9;
	
	// Data Engine Excel sheets
	public static final String Sheet_TestSteps = "Test Steps";
	public static final String Sheet_TestCases = "Test Cases";
	public static final String Sheet_Login = "Login";
}
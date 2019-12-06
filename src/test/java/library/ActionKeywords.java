package library;

import static executionEngine.DriverScript.OR;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;

import com.testautomationguru.ocular.Ocular;
import com.testautomationguru.ocular.comparator.OcularResult;

import config.Config;
import executionEngine.DriverScript;
import utility.Log;
import utility.ScreenshotCapture;

public class ActionKeywords<IWebElement> {
	public static WebDriver driver;
	public static WebDriverWait wait;
	private static int defaultWait = 10;
	public static HashMap localTOTest = new HashMap();
	public static HashMap globalTOTest = new HashMap();
	
	public static WebElement objectLocator(String object) {
		if (object.endsWith("_id"))
			return driver.findElement(By.id(OR.getProperty(object)));
		if (object.endsWith("_class"))
			return driver.findElement(By.className(OR.getProperty(object)));
		if (object.endsWith("_xpath"))
			return driver.findElement(By.xpath(OR.getProperty(object)));
		if (object.endsWith("_css"))
			return driver.findElement(By.cssSelector(OR.getProperty(object)));
		if (object.endsWith("_link"))
			return driver.findElement(By.linkText(OR.getProperty(object)));
		if (object.endsWith("_name"))
			return driver.findElement(By.name(OR.getProperty(object)));
		if (object.endsWith("_tag"))
			return driver.findElement(By.tagName(OR.getProperty(object)));
		if (object.endsWith(")")) {
			String[] obj = object.split("\\(");
			String replaceChar = obj[1].replace(")", "").trim();
			String rObj = OR.getProperty(obj[0].trim()).trim();
			String replacedObj = null;
			if(replaceChar.contains(",")){
				String[] repChar = replaceChar.split(",");
				if (repChar.length == 2){
					replacedObj = modifyLocator(rObj,repChar[0],repChar[1]);}
				else if (repChar.length == 3){
					replacedObj = modifyLocator(rObj,repChar[0],repChar[1],repChar[2]); }
			}else{
				replacedObj = modifyLocator(rObj,replaceChar);
			}
			return driver.findElement(By.xpath(replacedObj));
		}
		return null;
	}
	
	public static void clear(String objLocator, String data) {
		Log.info("Entering the text in " + objLocator);
		objectLocator(objLocator).clear();
		if(data != null) {
			waitFor(objLocator,data);
		}
	}
	
	public static String modifyLocator(String objLocator, String replaceString) {
		if (objLocator != null && replaceString != null) {
			String rString = modifyString(replaceString);
			String Str = new String(objLocator);
			String mStr = Str.replace("ReplaceString", rString);
			return mStr;
		} else {
			Log.error("Replacing string in xpath is failed");
			return null;
		}
	}
	
	public static String modifyLocator(String objLocator, String replaceString1, String replaceString2) {
		if (objLocator != null && replaceString1 != null && replaceString2 != null) {
			replaceString1=modifyString(replaceString1);
			replaceString2=modifyString(replaceString2);
			String Str = new String(objLocator);
			String mStr1 = Str.replace("ReplaceString1", replaceString1);
			String mStr2 = mStr1.replace("ReplaceString2", replaceString2);
			return mStr2;
		} else {
			Log.error("Replacing string in xpath is failed");
			return null;
		}
	}
	
	public static String modifyLocator(String objLocator, String replaceString1, String replaceString2, String replaceString3) {
		if (objLocator != null && replaceString1 != null && replaceString2 != null) {
			replaceString1=modifyString(replaceString1);
			replaceString2=modifyString(replaceString2);
			replaceString3=modifyString(replaceString3);
			String Str = new String(objLocator);
			String mStr1 = Str.replace("ReplaceString1", replaceString1);
			String mStr2 = mStr1.replace("ReplaceString2", replaceString2);
			String mStr3 = mStr2.replace("ReplaceString3", replaceString3);
			return mStr3;
		} else {
			Log.error("Replacing string in xpath is failed");
			return null;
		}
	}
	
	public static String modifyString(String replaceString) {
		String rString = "";
		if (replaceString.trim().toLowerCase().contains("globalvar")) {
			rString = (String) globalTOTest.get(replaceString);
			return rString;
		} else if (replaceString.trim().toLowerCase().contains("variable")) {
			rString = (String) localTOTest.get(replaceString);
			return rString;
		} else {
			rString = replaceString;
			return rString;
		}
	}
	
 	public static void openBrowser(String object,String data){
		try{	
			// For browser stack execution
			String brwsStackURL = "https://sureshkumar12:1hn4ewTz1hhZbsQUZkhp@hub-cloud.browserstack.com/wd/hub";
			String data2=null;
			String data3=null;
			if(data.contains(",")){
				String[] parArray = data.split(",");
				data=parArray[0];
				data2 = parArray[1];
				if(parArray.length > 2){
					data3 = parArray[2];
				}
			}
			if (data.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", Config.Base_Dir + "\\lib\\chromedriver.exe");
				DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
				chromeCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
				driver = new ChromeDriver(chromeCapabilities);
				driver.manage().window().maximize();
				// driver.manage().deleteAllCookies();
				// This is set the browser value which is used during
				// screenshot capture
				localTOTest.put("Browser", "Chrome");
				Log.info("Chrome browser started");
			}
			if(data.equalsIgnoreCase("firefox")){
				System.setProperty("webdriver.gecko.driver", Config.Base_Dir+"\\lib\\geckodriver.exe");
				DesiredCapabilities ffCapabilities = DesiredCapabilities.firefox();
				ffCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);				
				driver = new FirefoxDriver(ffCapabilities);
				driver.manage().window().setSize(new Dimension(1366,768));
				// This is set the browser value which is used during screenshot capture
				localTOTest.put("Browser","Firefox");
				Log.info("Firefox browser started");
			}
			if(data.equalsIgnoreCase("ie")){
				System.setProperty("webdriver.ie.driver", Config.Base_Dir+"\\lib\\IEDriverServer.exe");
				DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
				ieCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
				ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
				ieCapabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,true);
				ieCapabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "http://www.google.com/");
				driver = new InternetExplorerDriver(ieCapabilities);
				localTOTest.put("Browser","IE");
				Log.info("IE browser started");
			}
			if(data.equalsIgnoreCase("opera")){
				System.setProperty("webdriver.chrome.driver", Config.Base_Dir+"\\lib\\operadriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.setBinary("C:\\Program Files\\Opera\\52.0.2871.30\\opera.exe");
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				driver=new ChromeDriver(capabilities);
				driver.manage().window().maximize();
				Log.info("Opera browser started");
			}
			if(data.equalsIgnoreCase("windows")){
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("os", "Windows");
				caps.setCapability("os_version", "10");
				caps.setCapability("browser",data2);
				if(!(data3 == null)){
					caps.setCapability("browser_version",data3);
				}
				caps.setCapability("browserstack.local", "true");
				caps.setCapability("browserstack.debug", "true");
				java.net.URL myURL = new java.net.URL(brwsStackURL);
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
				driver = new RemoteWebDriver(myURL,caps);
				driver.manage().window().maximize();
			}
			if(data.equalsIgnoreCase("mac")){
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("os", "OS X");
				caps.setCapability("os_version", "High Sierra");
				caps.setCapability("browser",data2);
				if(!(data3 == null)){
					caps.setCapability("browser_version",data3);
				}
				caps.setCapability("browserstack.local", "true");
				caps.setCapability("browserstack.debug", "true");
				java.net.URL myURL = new java.net.URL(brwsStackURL);
				driver = new RemoteWebDriver(myURL,caps);
				driver.manage().window().maximize();
			}
			if(data.equalsIgnoreCase("andriod")){
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("realMobile", "true");
				caps.setCapability("platform", "ANDROID");
				caps.setCapability("device",data2);
				if(!(data3 == null)){
					caps.setCapability("os_version",data3);
				}
				caps.setCapability("browserstack.local", "true");
				caps.setCapability("browserstack.debug", "true");
				java.net.URL myURL = new java.net.URL(brwsStackURL);
				driver = new RemoteWebDriver(myURL,caps);
			}
			if(data.equalsIgnoreCase("andriod tab")){
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("realMobile", "true");
				caps.setCapability("platform", "ANDROID");
				caps.setCapability("device",data2);
				if(!(data3 == null)){
					caps.setCapability("os_version",data3);
				}
				caps.setCapability("browserstack.local", "true");
				caps.setCapability("browserstack.debug", "true");
				java.net.URL myURL = new java.net.URL(brwsStackURL);
				driver = new RemoteWebDriver(myURL,caps);
			}
			if(data.equalsIgnoreCase("ios")){
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("realMobile", "true");
				caps.setCapability("platform", "MAC");
				caps.setCapability("device",data2);
				if(!(data3 == null)){
					caps.setCapability("os_version",data3);
				}
				caps.setCapability("browserstack.local", "true");
				caps.setCapability("browserstack.debug", "true");
				java.net.URL myURL = new java.net.URL(brwsStackURL);
				driver = new RemoteWebDriver(myURL,caps);
			}if(data.equalsIgnoreCase("ios tab")){
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("realMobile", "true");
				caps.setCapability("platform", "MAC");
				caps.setCapability("device",data2);
				if(!(data3 == null)){
					caps.setCapability("os_version",data3);
				}
				caps.setCapability("browserstack.local", "true");
				caps.setCapability("browserstack.debug", "true");
				java.net.URL myURL = new java.net.URL(brwsStackURL);
				driver = new RemoteWebDriver(myURL,caps);
			}
			int implicitWaitTime=(10);
			driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
		}catch (Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.info("Not able to open the Browser --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	public static void navigateURL(String object, String data){
		try{
			Log.info("Navigating to URL");
			System.out.println("Value = " + DriverScript.Auto_Url);
			if (!DriverScript.Auto_Url.equalsIgnoreCase("$Auto_Url")){
				driver.get(DriverScript.Auto_Url);
				Log.info("Test script URL is overidden by Octopus variable");
				System.out.println("Test script URL is overidden by Octopus variable");
			} else {
				driver.get(modifyString(data));
				Log.info("URL present in test script used");
				System.out.println("URL present in test script used");
			}
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.info("Not able to navigate --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	public static void clickElement(String object, String data){		
		try{
			Log.info("Clicking on Webelement "+ object);
			//waitUntil(object,"clickable");
			objectLocator(object).click();
			waitFor(object, data);
			DriverScript.bResult = true;
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Notlnk_ able to click --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
    public static void doubleClickElement(String object, String data){
    	try{
			Log.info("Double clicking on Webelement "+ object);
			waitUntil(object,"clickable");
			Actions action = new Actions(driver);
			action.moveToElement(objectLocator(object)).doubleClick().perform();
		}
		catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Notlnk_ able to double click --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
    
    public static void clearField(String object, String data){        
        try{
              Log.info("Clearing the text in " + object);
              if (data.equals("")){
            	  objectLocator(object).clear();
              } else if(data.equalsIgnoreCase("s")) {
            	  objectLocator(object).sendKeys(Keys.CONTROL + "a");
            	  Thread.sleep(500);
            	  objectLocator(object).sendKeys(Keys.DELETE);
            	  Thread.sleep(500);
              }
              Thread.sleep(2000);
              DriverScript.bResult = true;
        }catch(Exception e){
              ScreenshotCapture.takeScreenShot(driver);
              Log.error("Not able to clear data --- " + e.getMessage());
              DriverScript.bResult = false;
              DriverScript.failedException = e.getMessage();
        }
    }
    
    public static void waitUntil(String object, String data){        
        try{
              Log.info("Wait for element presence : " + object);
              WebDriverWait wait = new WebDriverWait(driver,60);
              if(data.equalsIgnoreCase("presence")){
            	  wait.until(ExpectedConditions.presenceOfElementLocated(getLocator(object)));  
              }if(data.equalsIgnoreCase("clickable")){
            	  wait.until(ExpectedConditions.elementToBeClickable(getLocator(object)));
              }if(data.equalsIgnoreCase("visible")){
            	  wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(object)));
              }if(data.equalsIgnoreCase("invisible")){
            	  wait.until(ExpectedConditions.invisibilityOfElementLocated(getLocator(object)));
              }
              DriverScript.bResult = true;
        }catch(Exception e){
              ScreenshotCapture.takeScreenShot(driver);
              Log.error("Not able to clear data --- " + e.getMessage());
              DriverScript.bResult = false;
              DriverScript.failedException = e.getMessage();
        }
    }
    
	public static By getLocator(String object){
		Log.info("Get all elements within a dom "+object);
		if (object.endsWith("_id"))
			return By.id(OR.getProperty(object));
		if (object.endsWith("_class"))
			return By.className(OR.getProperty(object));
		if (object.endsWith("_xpath"))
			return By.xpath(OR.getProperty(object));
		if (object.endsWith("_css"))
			return By.cssSelector(OR.getProperty(object));
		if (object.endsWith("_link"))
			return By.linkText(OR.getProperty(object));
		if (object.endsWith("_name"))
			return By.name(OR.getProperty(object));
		if (object.endsWith("_tag"))
			return By.tagName(OR.getProperty(object));
		if (object.endsWith(")")){
			String[] obj = object.split("\\(");
			String replaceChar = obj[1].replace(")", "").trim();
			String rObj = OR.getProperty(obj[0].trim()).trim();
			String replacedObj = null;
			if(replaceChar.contains(",")){
				String[] repChar = replaceChar.split(",");
				if (repChar.length == 2){
					replacedObj = modifyLocator(rObj,repChar[0],repChar[1]);}
				else if (repChar.length == 3){
					replacedObj = modifyLocator(rObj,repChar[0],repChar[1],repChar[2]); }
			}else{
				replacedObj = modifyLocator(rObj,replaceChar);
			}
			return By.xpath(replacedObj);
		}
		return null;
	}
    
	public static List<WebElement> getElements(String object, String data){
			Log.info("Get all elements within a dom "+object);
			if(object.endsWith("_css"))
				{ return driver.findElements(By.cssSelector(OR.getProperty(object))); }				
			if(object.endsWith("_xpath"))
				{ return driver.findElements(By.xpath(OR.getProperty(object))); }		
			if(object.endsWith("_id"))
				{ return driver.findElements(By.id(OR.getProperty(object))); }		
			if(object.endsWith("_name"))
				{ return driver.findElements(By.name(OR.getProperty(object))); }		
			if(object.endsWith("_class"))
				{ return driver.findElements(By.className(OR.getProperty(object))); }		
			if(object.endsWith("_linktext"))
				{ return driver.findElements(By.linkText(OR.getProperty(object))); }		
			if(object.endsWith("_tag"))
				{ return driver.findElements(By.tagName(OR.getProperty(object))); }
			if (object.endsWith(")")) {
				String[] obj = object.split("\\(");
				String replaceChar = obj[1].replace(")", "").trim();
				String rObj = OR.getProperty(obj[0].trim()).trim();
				String replacedObj = null;
				if(replaceChar.contains(",")){
					String[] repChar = replaceChar.split(",");
					if (repChar.length == 2){
						replacedObj = modifyLocator(rObj,repChar[0],repChar[1]);}
					else if (repChar.length == 3){
						replacedObj = modifyLocator(rObj,repChar[0],repChar[1],repChar[2]); }
				}else{
					replacedObj = modifyLocator(rObj,replaceChar);
				}
				return driver.findElements(By.xpath(replacedObj));
			}
			return null;
	}
	
	public void exeScript(String exeName, String parameters) {
		try {
			Runtime.getRuntime().exec(Config.Base_Dir + "\\bin\\" + exeName +".exe" + " "+ parameters);
			System.out.println(Config.Base_Dir + "\\bin\\" + exeName +".exe" + " "+ parameters);
		} catch (Exception e) {
			Log.error("Not able to upload document --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	public void dropFile(String target, String filename) {
		try {			
			// open upload window
			objectLocator(target).click();
			Thread.sleep(3000);
			Runtime.getRuntime().exec(Config.Base_Dir + "\\bin\\SetFilePath.exe" + " " + Config.Base_Dir + "\\uploadFiles");
			Thread.sleep(2000);
			Runtime.getRuntime().exec(Config.Base_Dir + "\\bin\\FileUpload.exe" + " "+ filename);
			Thread.sleep(7000);
		} catch (Exception e) {
			Log.error("Not able to upload document --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}

	public static void matchElements(String object, String data) {
		try {
			String[] NewData = data.split("\\n");
			Log.info("Expected data : " + Arrays.toString(NewData));
			Log.info("Get all elements within a dom " + OR.getProperty(object));
			List<WebElement> elements = getElements(object, data);
			List<Integer> indexCnt = new ArrayList<Integer>();
			List<String> webList = new ArrayList<String>();
			int cnt = 0;
			for (WebElement elemText : elements) {
				webList.add(elemText.getText());
			}
			for (int i = 0; i < NewData.length; i++) {
				if (webList.toString().contains(NewData[i])) {
					cnt = cnt + 1;
				} else {
					indexCnt.add(i);
				}
			}
			if (cnt == NewData.length) {
				DriverScript.bResult = true;
			} else {
				DriverScript.failedException = "false";
				for(int j=0;j<indexCnt.size();j++){
					Log.error("Expected text --- " + NewData[indexCnt.get(j)]);
					Log.error("Expected is not in actual string --- " + webList.toString());
			}
				indexCnt.clear();
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able find elements with text --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void verifyAttributesText(String object, String data) {
		try {
			String[] NewData = data.split("\\n");
			Log.info("Expected data : " + Arrays.toString(NewData));
			Log.info("Get all elements within a dom " + OR.getProperty(object));
			List<WebElement> elements = getElements(object, data);
			List<Integer> indexCnt = new ArrayList<Integer>();
			List<String> webList = new ArrayList<String>();
			int cnt = 0;
			// First value in the array should be attribute for which values is required
			// Collect all the values in an array
			for (WebElement elemText : elements) {
				webList.add(elemText.getAttribute(NewData[0].trim()).trim());
			}
			// Check the expected value is present weblist
			for (int i = 1; i < NewData.length; i++) {
				if (webList.toString().contains(NewData[i])) {
					cnt = cnt + 1;
				} else {
					indexCnt.add(i);
				}
			}
			if (cnt == NewData.length) {
				DriverScript.bResult = true;
			} else {
				DriverScript.failedException = "false";
				for (int j = 0; j < indexCnt.size(); j++) {
					Log.error("Expected text --- " + NewData[indexCnt.get(j)]);
					Log.error("Expected is not in actual string --- " + webList.toString());
				}
				indexCnt.clear();
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able find elements with text --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	public static void enterInput(String object, String data){		
		try{
			Log.info("Entering the text in " + object);
			String expectedText = modifyString(data);
			System.out.print("expectedText : " + expectedText +"\n");
			waitUntil(object,"visible");
			objectLocator(object).sendKeys(expectedText);
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to Enter UserName --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	public static void selectDropdown(String object, String data){
		try{
			Log.info("Selecting the dropdown value in " + object);
			WebElement element = null;
			element = objectLocator(object);
			Select se = new Select(element);
			Log.info(data);
			se.selectByVisibleText(data);
			DriverScript.bResult = true;
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to select dropdown value --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	public static void selectListBox(String object, String data){	
		try{
			Log.info("Selecting the list value in " + object);
			WebElement element = null;
			element = objectLocator(object);	
			Select listBox = new Select(element);
			System.out.println(data);
			Log.info(data);
			listBox.selectByValue(data);
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to select dropdown value --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	public static void checkElement(String object, String data){
		try{
			Log.info("check element: "+ object);
			Boolean isChecked = null;
			isChecked = objectLocator(object).isSelected();
			if(isChecked.equals(false))
				objectLocator(object).click();			
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to check element --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void unCheckElement(String object, String data){
		try{
			Log.info("uncheck element: "+ object);
			Boolean isChecked = null;
			isChecked = objectLocator(object).isSelected();
			if(isChecked.equals(true))
				objectLocator(object).click();			
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to uncheck element --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void waitFor(String object, String data) {
		try{
			Log.info("Wait for 3 seconds");
			Thread.sleep(Integer.parseInt(data));
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to Wait --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
		
	public static void ScreenshotCapture(String object, String data){
			ScreenshotCapture.takeScreenShot(driver);
	}
	
	public static void closeBrowser(String object, String data){
		try{
			Log.info("Closing the browser");
			//driver.close();
			driver.quit();
			DriverScript.bResult = true;
		}catch(Exception e){
			Log.error("Not able to Close the Browser --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void verifyPageTitle(String object, String data){
		try{
			if (driver.getTitle().contains(data) == true) {
				DriverScript.bResult = true;
			}else{
	        	ScreenshotCapture.takeScreenShot(driver);
	        	Log.error("Expected text: " + data + " Actual text: "+ driver.getTitle());
	           	DriverScript.failedException = "false";
			}
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to get page title --- " + e.getMessage());
			DriverScript.failedException = "false";
		}
	}
	
	public static void verifyTextPresent(String object, String data) {
		try {
			String expectedText = modifyString(data);
			String txtElement = objectLocator(object).getText();
			if (txtElement.trim().toUpperCase().contains(expectedText.trim().toUpperCase()) && !expectedText.equals("")){
				DriverScript.bResult = true;
				Log.info("Expected text: " + expectedText + " ; Actual text: " + txtElement);
			} else {
				highLightElementAndScreenCapture(object, data);
				Log.error("Expected text: " + expectedText + " ; Actual text: " + txtElement);
				DriverScript.failedException = "false";
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to get text of the element --- " + e.getMessage());
			DriverScript.failedException = "false";
		}
	}

	public static void verifyElementPresent(String object, String data) {
		try {
			Log.info("Verify Element present " + object);
			boolean exists = getElements(object, data).size() != 0;

			if (data.isEmpty() || data.equalsIgnoreCase("true")) {
				if (exists) {
					DriverScript.bResult = true;
				} else {
					ScreenshotCapture.takeScreenShot(driver);
					Log.error("Element not present in the screen");
					DriverScript.bResult = false;
					DriverScript.failedException = "Element not present in the screen";
				}
			} else if (data.equalsIgnoreCase("false")) {
				if (!exists) {
					DriverScript.bResult = true;
				} else {
					ScreenshotCapture.takeScreenShot(driver);
					Log.error("Element is present in the screen");
					DriverScript.bResult = false;
					DriverScript.failedException = "Element present in the screen";
				}
			}

		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Not able to locate element --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
		
	public static int pickRandomValue(int maxValue) {
		int r = 1;
		int[] numbers = new int[maxValue];
		for (int i = 0; i < numbers.length; i++) {
			r = (int) (Math.random() * maxValue);
			return r;
		}
		return r;
	}

	public static int getTableRowCount(String object, String data){
		int count = 0;
		try{
			Log.info("Get total row count "+ object);
			count = driver.findElements(By.xpath(object)).size();
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Element not present --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
		return count;		 
	}
	
	public static void getTableColNumber(String object, String data){
		try{
			//No. of Columns
	        List<WebElement> col = getElements(object,data);
	        int colNumb, i = 0;
	        
	        for(WebElement element : col) {
	        	if (element.getAttribute("scope").equals("col")){
	        		 if (element.findElement(By.xpath("//*[text()='" + data +"']")).getText().contains(data)){
	        			 colNumb = i;
	        			 // Set this as column number
	        		 } else {
	        			 // Increase the column number by 1
	        		 }
	        	} else {
	        		// Increase the column number by 1
	        	}
	        }
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Element not present --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}		 
	}

	public static String getText(String object, String data){
		String elemText = "";
		try{
			Log.info("Get text of: "+ object);
			elemText = objectLocator(object).getText();
			if(data.toLowerCase().contains("globalvar")){
				globalTOTest.put(data, elemText);
			} else {
				localTOTest.put(data, elemText);
			}
			Log.info("Text of the object is: " + elemText);
		}
		catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable get text --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
		return elemText;
	}
	
	public static void getDate(String object, String data){
		// Parameter format
		// Object = 
		// data = variableHan (Should have variable in the string)
		try{
			LocalDate today = LocalDate.now();
			String[] days;
			String date = "";
			if (object.contains(",") && (!object.toLowerCase().contains("startofweek"))) {
				days = object.split(",");
				date = today.plusDays(Integer.parseInt(days[1])).format(DateTimeFormatter.ofPattern(days[0]));
			}else 
				if(object.contains(",")&& (object.toLowerCase().contains("startofweek"))) {
					//StartOfWeek-0
					days = object.split(",");
					String[] sDay = days[1].split(":");
					// Go backward to get Monday
				    LocalDate monday = today;
				    while (monday.getDayOfWeek() != DayOfWeek.MONDAY)
				    {
				      monday = monday.minusDays(1);
				    }
				    date = monday.plusDays(Integer.parseInt(sDay[1])).format(DateTimeFormatter.ofPattern(days[0]));
			}
			else 
				if (object.isEmpty()){
					date = today.format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
				} else {
					date = today.format(DateTimeFormatter.ofPattern(object));
				}
				if(data.toLowerCase().contains("global")){
					globalTOTest.put(data,date);
				} else {
					localTOTest.put(data,date);
				}
			
			Log.info("Returned Date is : " + date);
		}
		catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable get today's date --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void randomNumber(String len,String data) {
		String genString = "123456789";
		int length = Integer.parseInt(len);

		StringBuilder randString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int character = (int) (Math.random() * genString.length());
			randString.append(genString.charAt(character));
		}
		System.out.print("Random String : " + randString.toString() + "\n");
		if(data.toLowerCase().contains("globalvar")){
			globalTOTest.put(data,randString.toString());
		} else {
			localTOTest.put(data,randString.toString());
		}
	}
	
	public static void generateString(String len,String varName) {
		if(varName.contains(",")){
			String[] varPart = varName.split(",");
			String modifiedString = "";
			if (!len.isEmpty()){
				randomNumber(len,"randPart");
				String randString = (String) localTOTest.get("randPart");
				modifiedString = varPart[1].replaceAll("(?i)random", randString);
			} else {
				modifiedString = varPart[1];
			}
			if(varPart[0].toLowerCase().contains("globalvar")){
				globalTOTest.put(varPart[0],modifiedString);
			} else {
				localTOTest.put(varPart[0],modifiedString);
			}
		} else {
			Log.error("Provide porper parameters to the keyword");
			System.out.println("Provide porper parameters to the keyword");
			DriverScript.bResult = false;
		}	
	}
		
	public static void pressEnter(String object, String data) {
		try {
			objectLocator(object).sendKeys(Keys.ENTER);
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to send enter key to the object --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void pageRefresh(String object, String data){
			driver.navigate().refresh();
	}
	
	public static void setDateByTextEntry(String object, String data){
		try{
			Log.info("Get Date: "+object);
			objectLocator(object).sendKeys(data);
			objectLocator(object).sendKeys(Keys.ENTER);
			String dateval = getText(object,data);
			if(dateval.equals(data)){
				Log.info("Date in field -- "+dateval);
				DriverScript.bResult = true;				
			}
		}
		catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to set date --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}		
	}
	
	public static boolean columnSortingoption(String columntosort, String data) {

		ArrayList<String> beforesort = new ArrayList<String>();
		ArrayList<String> sortedvalue = new ArrayList<String>();

		List<WebElement> columnlisttochksort = driver.findElements(By.cssSelector(columntosort));
		for (WebElement sortingColvalue : columnlisttochksort) {
			beforesort.add(sortingColvalue.getText().trim().toUpperCase());
			sortedvalue.add(sortingColvalue.getText().trim().toUpperCase());
		}
		if (data.equalsIgnoreCase("ascending")){
			Collections.sort(sortedvalue, String.CASE_INSENSITIVE_ORDER);	
		}else if (data.contains("descending")) {
			Collections.sort(sortedvalue, String.CASE_INSENSITIVE_ORDER);
			Collections.reverse(sortedvalue);
		}
		
		System.out.println("before sorting:" + beforesort);
		System.out.println("sortedvalue:" + sortedvalue);
		if (sortedvalue.equals(beforesort)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean columndateASCsortingoption(String columntosort, String data) {

		ArrayList<String> beforesort = new ArrayList<String>();
		ArrayList<String> sortedvalue = new ArrayList<String>();

		List<WebElement> columnlisttochksort = driver.findElements(By.cssSelector(columntosort));
		for (WebElement sortingColvalue : columnlisttochksort)

		{
			if (sortingColvalue.getText().matches("") || sortingColvalue.getText().matches("--")) {
				beforesort.remove(sortingColvalue.getText().trim().toUpperCase());
				sortedvalue.remove(sortingColvalue.getText().trim().toUpperCase());
			} else {
				beforesort.add(sortingColvalue.getText().trim().toUpperCase());
				sortedvalue.add(sortingColvalue.getText().trim().toUpperCase());

			}
		}

		Collections.sort(sortedvalue, new Comparator<String>() {
			DateFormat f = new SimpleDateFormat("MM/dd/yyyy");

			public int compare(String o1, String o2) {
				try {
					return f.parse(o1).compareTo(f.parse(o2));
				} catch (java.text.ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});

		System.out.println("before sorting:" + beforesort);
		System.out.println("revesrted date:" + sortedvalue);
		if (beforesort.equals(sortedvalue)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean columndateDSCsortingoption(String columntosort, String data) {

		ArrayList<String> beforesort = new ArrayList<String>();
		ArrayList<String> sortedvalue = new ArrayList<String>();

		List<WebElement> columnlisttochksort = driver.findElements(By.cssSelector(columntosort));
		for (WebElement sortingColvalue : columnlisttochksort)

		{
			if (sortingColvalue.getText().matches("") || sortingColvalue.getText().matches("--")) {
				beforesort.remove(sortingColvalue.getText().trim().toUpperCase());
				sortedvalue.remove(sortingColvalue.getText().trim().toUpperCase());
			} else {
				beforesort.add(sortingColvalue.getText().trim().toUpperCase());
				sortedvalue.add(sortingColvalue.getText().trim().toUpperCase());

			}
		}

		Collections.sort(sortedvalue, new Comparator<String>() {
			DateFormat f = new SimpleDateFormat("MM/dd/yyyy");

			public int compare(String o1, String o2) {
				try {
					return f.parse(o1).compareTo(f.parse(o2));
				} catch (java.text.ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		System.out.println("intermediate sort: " + sortedvalue);
		Collections.reverse(sortedvalue);
		System.out.println("before sorting:" + beforesort);
		System.out.println("revesrted date:" + sortedvalue);

		if (beforesort.equals(sortedvalue)) {
			return true;
		} else {
			return false;
		}

	}
	
	public static boolean validateDropdown(String object, String... text) {

		Select selectObject = new Select(objectLocator(object));
		List<String> options = new ArrayList<String>();
		for (int i = 0; i < selectObject.getOptions().size(); i++) {
			options.add(selectObject.getOptions().get(i).getText().toLowerCase());
		}
		if (options.size() == text.length) {
			for (String temp : text)
				if (!options.contains(temp.toLowerCase()))
				{
					System.out.println("Option " + temp + " is not displayed in the options " + options);
					return false;
				}

		} else {
			System.out.println("Options count Mismatch.\n Expected values: " + text + " \n Actual values: " + options);
			return false;
		}
		System.out.println("Options are displayed .\n Expected values: " + text + " \n Actual values: " + options);
		return true;
	}
	
	public static String validateText(String object, String data){
		String expectedText = "";
		if(data.trim().toLowerCase().contains("variable")){
			expectedText = (String) localTOTest.get(data);
		}else{
			expectedText=data;
		}
		WebElement element = objectLocator(object); 			
		if (element.getAttribute("value").trim().equals(expectedText.trim()) && !expectedText.equals("")) {
        	DriverScript.bResult = true;
        	Log.info("Expected text: " + expectedText.trim() + " Actual text: "+ element.getAttribute("value").trim());
		} else{
			highLightElementAndScreenCapture(object,data);
        	Log.error("Expected text: " + expectedText.trim() + " Actual text: "+ element.getAttribute("value").trim());
           	DriverScript.failedException = "false";
		}
		return expectedText;
	}
	
	public static int getInt(String object, String text) {
		int count = 0;
		if (text != null)
			try {
				text = text.replaceAll("(\\D+)(.)*", "").trim();
				if (!"".equals(text))
					count = Integer.parseInt(text.trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
		return count;
	}
	
	public static boolean isValidURL(String object, String pageUrl) {
		try {
			new URL(pageUrl);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	
	public boolean isAlertDisplayedWithinTimeout(int timeout) throws InterruptedException{
		for (int counter = 1; counter <= (timeout) / 500; counter++) {
			try {
				Alert alert = driver.switchTo().alert();
				return true;
			} catch (Exception e) {
				Thread.sleep(500);
			}
		}
		return false;
	}
	
	public static boolean isElementByCSSPresent(String selector) {
		int count = driver.findElements(By.cssSelector(selector)).size();
		if (count > 0)
			return true;
		else
			return false;

	}
	
	public static WebElement FindByCssSelector(String selector,String data) {
		try {
			WebElement element = driver.findElement(By.cssSelector(selector));
			return element;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isDateValid(String actualDate, String expectedDateFormat){
		try {
			SimpleDateFormat format = new SimpleDateFormat(expectedDateFormat);
			format.parse(actualDate);
		} catch (ParseException e) {
			Log.error("Date is not valid --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
		return true;
	}
		
	public static void SelectFromDateInCalendar(String object, String data) {
		try{
			//month-0;date-1;year-2
			String expectedText = modifyString(data);
			String[] formatDate = expectedText.split("/");
			String fromMonth=formatDate[0],date=formatDate[1],fromYear=formatDate[2];
			System.out.println("cal: "+fromMonth+date+fromYear);
			objectLocator(object).click();
			Select selectYear = new Select(driver.findElement(By.cssSelector("#calYearPicker")));
			selectYear.selectByVisibleText(fromYear);
			Select selectMonth = new Select(driver.findElement(By.cssSelector("#calMonthPicker")));
			if (fromMonth.matches("\\d*")) {
				// since in calendar values for months are from '0' to '11'
				fromMonth = String.valueOf(Integer.valueOf(fromMonth) - 1);
				selectMonth.selectByValue(fromMonth);
			} else if (fromMonth.matches("\\D*")) {
				selectMonth.selectByVisibleText(fromMonth);
			}
			//driver.findElement(By.xpath(".//*[@id='datePicker']//td[@class='weekday' and text()="+date+"]")).click();
			driver.findElement(By.xpath(".//*[@id='datePicker']//td[contains(@class,'weekday') or @class='selectedDate'] [text()="+date+"]")).click();
			String datetext = driver.findElement(By.xpath(OR.getProperty(object))).getText();
			if(datetext.equals(data)){
				Log.info("Date in field -- "+datetext);
				DriverScript.bResult = true;				
			}
		}
		catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to set date --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public String selectOptionFromDropDown(String selector, String selectText) {
		try {

			driver.findElement(By.cssSelector(selector)).click();
			new Select(driver.findElement(By.cssSelector(selector))).selectByVisibleText(selectText);

		} catch (Exception e) {
			System.out.println("Exception in selectOptionFromDropDown:" + e);
		}
		return null;
	}
	
	public static void switchToNewTab(String object, String data) {
		try {
			String currentTab = "";
			if (data.trim().toLowerCase().contains("variable")) {
				currentTab = (String) localTOTest.get(data);
			} else {
				currentTab = data;
			}
			ArrayList<String> newTabs = new ArrayList<String>(driver.getWindowHandles());
			// To get total tabs
			int totalTabs = newTabs.size();
			int navigateToTab = 0;
			if (totalTabs >= 1) {
				int tabCount = 0;
				for (String newTab : newTabs) {
					if (newTab.equals(currentTab)) {
						navigateToTab = tabCount;
						break;
					}
					tabCount = tabCount + 1;
				}
				if (tabCount == 0 && totalTabs > 1) {
					driver.switchTo().window(newTabs.get(1));
					DriverScript.bResult = true;
				} else {
					driver.switchTo().window(newTabs.get(0));
					DriverScript.bResult = true;
				}

				/*
				 * newTabs.remove(currentTab); totalTabs = newTabs.size();
				 * System.out.println("totalTabs : " + totalTabs);
				 * driver.switchTo().window(newTabs.get(navigateToTab));
				 */
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to close new browser --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
		
	public static void switchAndCloseLatestTab(String object, String data) {
		try {
			String parentWindow = getCurrentWindow(object,data);
			ArrayList<String> newTabs = new ArrayList<String>(driver.getWindowHandles());
			int totalTabs = newTabs.size();
			System.out.println("totalTabs : " + totalTabs);
			int navigateToTab = 0;
			if (totalTabs > 1) {
				int tabCount = 0;
				for (String newTab : newTabs) {
					if (newTab.equals(parentWindow)) {
						navigateToTab = tabCount;
						break;
					}
					tabCount = tabCount + 1;
				}
				if (navigateToTab == 0) {
					driver.switchTo().window(newTabs.get(1));
					driver.close();
					driver.switchTo().window(newTabs.get(0));
				} else {
					driver.switchTo().window(newTabs.get(navigateToTab));
					driver.close();
					driver.switchTo().window(newTabs.get(0));
				}
			}
		} catch (Exception e) {
			Log.error("Unable to close new browser --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	public static void navigateBack(String object, String data){
		try {
			Log.info("Browser navigate");
			driver.navigate().back();
			waitFor(object, data);
			DriverScript.bResult = true;
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to navigate back --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	//hover over related to web

	public static void hoverAndClick(String object, String data){
		try {
			Log.info("Hover on menu : "+ data + "; click on object : " + object);
			Actions action = new Actions(driver);
			WebElement we = objectLocator(data);
			action.moveToElement(we).moveToElement(objectLocator(object)).click().build().perform();
			/*WebDriverWait wait = new WebDriverWait(driver, 30);
		    wait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));*/
			waitFor(object,"5000");
			DriverScript.bResult = true;
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to click --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
		
	public static void scrollAndClick(String object, String data){
		try{
			Log.info("Clicking on Element "+ object);
			WebElement element = objectLocator(object);
			JavascriptExecutor executor = (JavascriptExecutor)driver;
			executor.executeScript("arguments[0].click();", element);				
			waitFor(object, data);
			DriverScript.bResult = true;
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to click --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
		
	public static void scrollToElement(String object, String data){		
		try{
			Log.info("X and Y axis are : "+ data);
			String axis[] = data.split(",");
			int x = Integer.parseInt(axis[0]);
			int y = Integer.parseInt(axis[1]) - 200;
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			jse.executeScript("window.scrollTo(" + x + "," + y +")", "");
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to scroll --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	public static void scrollToView(String object, String data){		
		try{
			Log.info("Scroll to the element : "+ object);
			WebElement element = objectLocator(object);
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			jse.executeScript("arguments[0].scrollIntoView(true);",element);
			jse.executeScript("window.scrollBy(0,-200)");
			
		}catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to scroll --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
			
	public static void switchToAlert(String object, String data) {
		try {
			Alert alert = driver.switchTo().alert();
			Thread.sleep(1000);
			if (data.trim().toLowerCase().contains("ok")) {
				alert.accept();
			} else if (data.trim().toLowerCase().contains("cancel")){
				alert.dismiss();
			} else {
				Log.info("Switch to alert is successful");
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to identify alert --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
		
	public static String getCurrentWindow(String object,String data){
		String parentWinHandle = null;
		try {
			Log.info("Get window handle of current window");
			parentWinHandle = driver.getWindowHandle();
			System.out.println("handle : " + parentWinHandle);
			if (data.toLowerCase().contains("global")) {
				globalTOTest.put(data,parentWinHandle);
			} else {
				localTOTest.put(data,parentWinHandle);
			}
			DriverScript.bResult = true;
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to get current window --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
		return parentWinHandle;
	}
	
	public static void switchToNewWindow(String object,String data){
		try {
				driver.switchTo().window(object);
		} catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to get window --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void closeWindow(String object, String data){
		try {
			driver.close();
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to close window --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}
	
	public static void dragAndDropFile(String object, String filePath) {
		WebElement target = objectLocator(object);
		int offsetX = 0; int offsetY = 0;
	    
	    WebDriver driver = ((RemoteWebElement)target).getWrappedDriver();
	    JavascriptExecutor js = (JavascriptExecutor)driver;
	    WebDriverWait wait = new WebDriverWait(driver,30);

	    String JS_DROP_FILE = "var target = arguments[0],offsetX = arguments[1],offsetY = arguments[2]," +
	    		"document = target.ownerDocument || document," +
	    		"window = document.defaultView || window;"+
	    		"var input = document.createElement('INPUT');"+
	    		"input.type = 'file';"+
	    		"input.style.display = 'none';"+
	    		"input.onchange = function () {"+
	    		"target.scrollIntoView(true);"+
	          "var rect = target.getBoundingClientRect(),"+
	              "x = rect.left + (offsetX || (rect.width >> 1)),"+
	              "y = rect.top + (offsetY || (rect.height >> 1)),"+
	              "dataTransfer = {files: this.files};"+
	              "['dragenter', 'dragover', 'drop'].forEach(function (name) {"+
	            "var evt = document.createEvent('MouseEvent');"+
	            "evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);"+
	            "evt.dataTransfer = dataTransfer;"+
	            "target.dispatchEvent(evt);" +
	            "});" +
	            "setTimeout(function() {document.body.removeChild(input);}, 25);" +
	            "}; " +	            
	            "document.body.appendChild(input);" +
	            "return input;";	    	    
	    WebElement input =  (WebElement)js.executeScript(JS_DROP_FILE, target, offsetX, offsetY);
	    input.sendKeys(filePath);
	    wait.until(ExpectedConditions.stalenessOf(input));
	}
	
	public void sliderControl(String object1, String object2) throws InvocationTargetException{
		try{
			WebElement slidebar = objectLocator(object1);
			int widthSlideBar = slidebar.getSize().width;
			System.out.println("widthSlideBar Length: "+widthSlideBar);
			WebElement slider =  objectLocator(object2);
			Actions sliderAction = new Actions(driver);
			sliderAction.clickAndHold(slider);
			sliderAction.moveByOffset(40,0).build().perform();
		}
		catch(Exception e){
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to get window --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();			
		}
	 }
	
	public void switchToFrame(String object, String data) {
		try {
			if(data.toLowerCase().trim().contains("frame")){
				WebElement iFrame = objectLocator(object);
				driver.switchTo().frame(iFrame);
				DriverScript.bResult = true;
			}else if (data.toLowerCase().trim().contains("default")){
				driver.switchTo().defaultContent();
				DriverScript.bResult = true;
			}else{
				System.out.println("Frame name doesn't match");
				Log.error("Frame name is not valid");
				DriverScript.bResult = false;
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Unable to switch frame  --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getMessage();
		}
	}

	// This a new method where Current URL is stored in a variable
	// Old getPageUrl is now renamed to verifyPageUrl where comparison of expected URL is to current URL is done
	public static void getPageUrl(String object, String data) {
		try {
			String URL = driver.getCurrentUrl();
			System.out.println("Current URL: " + URL);
			if(data.toLowerCase().contains("globalvar")){
				globalTOTest.put(data, URL);
			} else {
				localTOTest.put(data, URL);
			}
		} catch (Exception e) {
			Log.error("Not able to get page url --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	
	// getPageURL method name is change to verifyPageUrl and new getPageURL method is created 
	public static void verifyPageUrl(String object, String data) {
		try {
			String expectedText = modifyString(data);
			String URL = driver.getCurrentUrl();
			System.out.println("Current URL: " + URL);
			if (URL.trim().toUpperCase().contains(expectedText.trim().toUpperCase()) && !expectedText.equals(""))
				DriverScript.bResult = true;
			else {
				Log.error("Expected text: " + data + " ; Actual text: " + URL);
				DriverScript.failedException = "false";
			}
		} catch (Exception e) {
			Log.error("Invalid page url --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}

	
	// Method to Highlight Element

	public static void highLightElementAndScreenCapture(String object, String data){
		try {
			WebElement element = objectLocator(object);
			JavascriptExecutor executor = (JavascriptExecutor)driver;
			executor.executeScript("arguments[0].scrollIntoView(true);", element);
			executor.executeScript("window.scrollBy(0,-25)", "");
			Thread.sleep(1000);
			executor.executeScript("arguments[0].setAttribute('style',arguments[1]);", element, "border: 2px solid red;");
			ScreenshotCapture.takeScreenShot(driver);
			executor.executeScript("arguments[0].setAttribute('style',arguments[1]);", element, "");
		} catch (InterruptedException e) {
			Log.error("Unable to highlight the element" + e.getMessage());
		} 
	}

	public static void extractDigits(String object, String data) {
		String dataText="";
		if(data.trim().toLowerCase().contains("variable")){
			dataText=(String) localTOTest.get(data);
			System.out.println("dataText : " + dataText);
			final StringBuilder sb = new StringBuilder(dataText.length());
			for (int i = 0; i < dataText.length(); i++) {
				final char c = dataText.charAt(i);
				if (c > 47 && c < 58) {
					sb.append(c);
				}
			}
			// Check if it adds duplicate
			if(data.toLowerCase().contains("global")){
				globalTOTest.put(data, sb.toString());
			} else {
				localTOTest.put(data, sb.toString());
			}
			DriverScript.bResult = true;
			System.out.println("extractDigits : " + sb.toString());
			//hm.put(data + "Int", sb.toString());
		}else{
			DriverScript.failedException = "false";
			Log.error("Variable name is expected (Eg: VariabelVal1)");
		}
	}
	
	public static void compareString(String object, String data) {
		String[] dataText = data.split("=");
		String strCmp1 = dataText[0];
		String strCmp2 = dataText[1];

		System.out.println("strCmp1 : " + strCmp1);
		System.out.println("strCmp2 : " + strCmp2);
		
		// Check arithmetic operator is present in string 1
		if (Pattern.compile("[-+*/]").matcher(strCmp1.trim()).find()) {
			// split with arithmetic operator, and operator will also be
			// considered in array Eg: array will have [a, +, b, -, c, *, d, /, e]
			String[] result1 = strCmp1.split("(?<=[-+*/])|(?=[-+*/])");	
			// check if it has a variable name and get value
			if (result1[0].trim().toLowerCase().contains("variable")) {
				result1[0] = (String) localTOTest.get(result1[0]);
			}
			if (result1[2].trim().toLowerCase().contains("variable")) {
				result1[2] = (String) localTOTest.get(result1[2]);
			}
			
			Double str1 = Double.parseDouble(result1[0]);
			Double str2 = Double.parseDouble(result1[2]);
			Double str3 = 0.00;
			// perform operation accordingly and store the value in string
			if (result1[1].equals("+")) {
				str3 = str1 + str2;
				System.out.println("str3 : " + str3);
			} else if (result1[1].equals("-")) {
				str3 = str1 - str2;
			} else if (result1[1].equals("*")) {
				str3 = str1 * str2;
			}
			strCmp1 = String.valueOf(Math.round(str3));
			System.out.println(strCmp1);

		}
		if (Pattern.compile("[-+*/]").matcher(strCmp2.trim()).find()) {
			String[] result2 = strCmp2.split("(?<=[-+*/])|(?=[-+*/])");
			if (result2[0].trim().toLowerCase().contains("variable")) {
				result2[0] = (String) localTOTest.get(result2[0]);
			}
			if (result2[2].trim().toLowerCase().contains("variable")) {
				result2[2] = (String) localTOTest.get(result2[2]);
			}
			
			Double str1 = Double.parseDouble(result2[0]);
			Double str2 = Double.parseDouble(result2[2]);
			Double str3 = 0.00;
			if (result2[1].equals("+")) {
				str3 = str1 + str2;
				System.out.println("str3 : " + str3);
			} else if (result2[1].equals("-")) {
				str3 = str1 - str2;
			} else if (result2[1].equals("*")) {
				str3 = str1 * str2;
			}
			strCmp2 = String.valueOf(Math.round(str3));
			System.out.println(strCmp2);
		}

		if (strCmp1.trim().toLowerCase().contains("variable")) {
			strCmp1 = (String) localTOTest.get(strCmp1);
		}
		if (strCmp2.trim().toLowerCase().contains("variable")) {
			strCmp2 = (String) localTOTest.get(strCmp2);
		}
		
		System.out.println("cmp strCmp1 : " + strCmp1);
		System.out.println("cmp strCmp2 : " + strCmp2);
		
		if (strCmp1.equals(strCmp2)) {
			System.out.println("Scenario1 passed");
			DriverScript.bResult = true;
		} else {
			Log.error("String1 : " + strCmp1 + " ; String2 : " + strCmp2);
			DriverScript.failedException = "false";
		}
	}

	public void verifyAttributeText(String object, String data){
		try{
			String[] dataText = data.split("=");
			String attribute = dataText[0].trim();
			String attributeExpValue = dataText[1].trim();
			String attributeActValue = objectLocator(object).getAttribute(attribute).trim();
			
			if (attributeExpValue.equalsIgnoreCase("blank")){
				if (attributeActValue.isEmpty()){
					DriverScript.bResult = true;
				}else{
		        	highLightElementAndScreenCapture(object,data);
		        	Log.error("Attribute value is not blank");
		           	DriverScript.failedException = "false";
				}
			}else{
				if (attributeActValue.trim().toUpperCase().contains(attributeExpValue.trim().toUpperCase()) && !attributeExpValue.equals("")){
					DriverScript.bResult = true;	
					Log.info("Expected " + attribute + " value: " + attributeExpValue + " ; Actual " + attribute + " value: "+ attributeActValue);
				}else{
		        	highLightElementAndScreenCapture(object,data);
		        	Log.error("Expected " + attribute + " value: " + attributeExpValue + " ; Actual " + attribute + " value: "+ attributeActValue);
		           	DriverScript.failedException = "false";
				}	
			}
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Object attribute not found --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	public static void dragAndDropElement(String Fromobject, String ToObject) {         
        WebElement From = objectLocator(Fromobject);
        WebElement To = objectLocator(ToObject);
        Actions builder = new Actions(driver);
        Action dragAndDrop = builder.clickAndHold(From)
        .moveToElement(To)
        .release(To)
        .build();
        dragAndDrop.perform();
    }
	
	public static void rightClick(String object, String data) {
        try {
        	WebElement element = objectLocator(object);
            Actions action = new Actions(driver).contextClick(element);
            action.build().perform();
            DriverScript.bResult = true;
            waitFor(object,data);
        } catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Object not found to perform right click --- " + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
        }
	}
	
	public static void createScreenshot(String object, String data){
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			String path = Config.Base_Dir + "/Screenshots/Expected/" + (String) ActionKeywords.localTOTest.get("Browser") + "/"
					+ data;
			File f = new File(path);
			if (!f.isDirectory()) {
				boolean success = f.mkdirs();
				if (success) {
					System.out.println("Created path: " + f.getPath());
				} else {
					System.out.println("Could not create path: " + f.getPath());
				}
			} else {
				System.out.println("Path exists: " + f.getPath());
			}
			FileUtils.copyFile(source, new File(path + "/" + data + "_" + DriverScript.sTestStepID + ".png"));
			
		} catch (Exception e) {
			System.out.println("Exception while taking screenshot " + e.getMessage());
			Log.error("Exception while taking screenshot --- " + e.getMessage());
		}
	}
	
	public void imageCompare(String object, String data) {
		try {
			// Image compare using Ocular
			String exepectedSrceenShot = Config.Base_Dir + "\\Screenshots\\Expected\\"
					+ (String) ActionKeywords.localTOTest.get("Browser") + "\\" + data + "\\"
					+ data + "_" + DriverScript.sTestStepID + ".png";
			// snapshotPath- location of the baseline images
			// resultPath- location where the results with differences highlighted should be stored
			// globalSimilarity- % of pixels should match for the visual validation to pass
			// saveSnapshot- flag to save the snapshots automatically if they are not present. 
							// (useful for the very first time test run)
			String path = Config.Base_Dir + "/Screenshots/Output/" + (String) ActionKeywords.localTOTest.get("Browser") + "/"
					+ data;
			File f = new File(path);
			if (!f.isDirectory()) {
				boolean success = f.mkdirs();
				if (success) {
					System.out.println("Created path: " + f.getPath());
				} else {
					System.out.println("Could not create path: " + f.getPath());
				}
			} else {
				System.out.println("Path exists: " + f.getPath());
			}
			Ocular.config()
					.resultPath(Paths.get(path))
					.snapshotPath(Paths.get(path))
					.globalSimilarity(90).saveSnapshot(true);
			String status = cmpImage(exepectedSrceenShot, "");
			
			if (status.equalsIgnoreCase("same")) {
				DriverScript.bResult = true;
			} else {
				Log.error("Image compard is different");
				DriverScript.bResult = false;
			}
		} catch (Exception e) {
			Log.error("Image compare failed" + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
	
	public String cmpImage(String fileName, String extra) {
		Path expectedFilePath = Paths.get(fileName);
		OcularResult result = Ocular.snapshot().from(expectedFilePath).sample().using(driver).compare();
		result.getDiffImage();
		System.out.println("Result : " + result.getComparisonStatus());
		System.out.println("Result : " + result.getSimilarity());
		return result.getComparisonStatus();
	}
	
	public void cmpElement(String object, String data) {
		// Image compare using Ocular
					String exepectedSrceenShot = Config.Base_Dir + "\\Screenshots\\Expected\\"
							+ (String) ActionKeywords.localTOTest.get("Browser") + "\\" + data + "\\"
							+ data + "_" + DriverScript.sTestStepID + ".png";
					
		OcularResult result = Ocular.snapshot().from(exepectedSrceenShot).sample().using(driver).element(objectLocator(object)).compare();
		result.getDiffImage();
		System.out.println("Result : " + result.getComparisonStatus());
		System.out.println("Result : " + result.getSimilarity());
		if (result.getComparisonStatus().equalsIgnoreCase("same")) {
			DriverScript.bResult = true;
		} else {
			Log.error("Image compard is different");
			DriverScript.bResult = false;
		}
	}
	 
	public static void verifyImagePresent(String object, String sectionName) {
		try {
			// Image compare using Sikuli
			// Create and initialize an instance of Screen object
			org.sikuli.script.Pattern imagepattern = new org.sikuli.script.Pattern(
					Config.Base_Dir + "/Image/" + (String) ActionKeywords.localTOTest.get("Browser") + "/" + sectionName);
			imagepattern.similar((float) 0.90);
			// imagepattern.exact();
			Screen screen = new Screen();
			screen.find(imagepattern);
			// Below code will scroll the page until image is found (It is going to infinite loop currently)
			/*while(!clipExist(screen,Config.Base_Dir + "/Image/" + (String) ActionKeywords.hm.get("Browser") + "/" + sectionName)) {
				screen.wheel(1,3);
			}*/
			DriverScript.bResult = true;
			System.out.println("Sikuli : Screen comparision is done");
		} catch (Exception e) {
			ScreenshotCapture.takeScreenShot(driver);
			Log.error("Expected Image not found on the screen" + e.getMessage());
			DriverScript.bResult = false;
			DriverScript.failedException = e.getLocalizedMessage();
		}
	}
			  
	public static boolean clipExist(Screen screen, String clip) {
		Match m = screen.exists(clip);
		if (m != null) {
			return true;
		} else {
			return false;
		}
	}			  
}
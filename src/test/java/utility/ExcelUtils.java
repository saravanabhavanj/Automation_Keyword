package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;

import config.Config;
import executionEngine.DriverScript;

public class ExcelUtils {
	public static XSSFSheet ExcelWSheet;
	public static XSSFWorkbook ExcelWBook;
	public static org.apache.poi.ss.usermodel.Cell Cell;
	public static XSSFRow Row;
	public static int SheetCount;

	//This method is to set the File path and to open the Excel file
	//Pass Excel Path and SheetName as Arguments to this method
	public static void setExcelFile(String Path) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
			System.out.println("Excel file path set");
		} catch (Exception e){
			Log.error("Class Utils | Method setExcelFile | Exception desc : "+e.getMessage());
			DriverScript.bResult = false;
		}
	}
	//This method is to read the test data from the Excel cell
	//In this we are passing Arguments as Row Num, Col Num & Sheet Name
	public static String getCellData(int RowNum, int ColNum, String SheetName ) throws Exception{
		try{
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			String CellData = Cell.getStringCellValue();
			return CellData;
		}catch (Exception e){
			Log.error("Class Utils | Method getCellData | Exception desc : "+e.getMessage());
			DriverScript.bResult = false;
			return"";
		}
	}

	//This method is to get the row count used of the excel sheet
	public static int getRowCount(String SheetName){
		int iRowCount=0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			iRowCount=ExcelWSheet.getLastRowNum()+1;
		} catch (Exception e){
			Log.error("Class Utils | Method getRowCount | Exception desc : "+e.getMessage());
			DriverScript.bResult = false;
		}
		return iRowCount;
	}

	//This method is to get the column count used of the excel sheet        	
	public static int getColCount(String SheetName){
		int icolCount=0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			icolCount = ExcelWSheet.getRow(0).getLastCellNum();        			
		} catch (Exception e){
			Log.error("Class Utils | Method getColCount | Exception desc : "+e.getMessage());
			DriverScript.bResult = false;
		}
		return icolCount;
	}

	//This method is to get the Row number of the test case
	//This methods takes three arguments(Test Case name , Column Number & Sheet name)
	public static int getRowContains(String sTestCaseName, int colNum,String SheetName) throws Exception{
		int iRowNum=0;	
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			int rowCount = ExcelUtils.getRowCount(SheetName);
			System.out.println("getrowcount: "+rowCount);
			for (; iRowNum<rowCount; iRowNum++){
				if  (ExcelUtils.getCellData(iRowNum,colNum,SheetName).equalsIgnoreCase(sTestCaseName)){
					break;
				}
			}       			
		} catch (Exception e){
			Log.error("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
			DriverScript.bResult = false;
		}
		return iRowNum;
	}

	//This method is to get the count of the test steps of test case
	//This method takes three arguments (Sheet name, Test Case Id & Test case row number)
	public static int getTestStepsCount(String SheetName, String sTestCaseID, int iTestCaseStart) throws Exception{
		try {
			for(int i=iTestCaseStart;i<=ExcelUtils.getRowCount(SheetName);i++){
				if(!sTestCaseID.equals(ExcelUtils.getCellData(i, Config.Col_TestCaseID, SheetName))){
					int number = i;
					return number;      				
				}
			}
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			int number=ExcelWSheet.getLastRowNum()+1;
			return number;
		} catch (Exception e){
			Log.error("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
			DriverScript.bResult = false;
			return 0;
		}
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	public static void setCellData(String Result,  int RowNum, int ColNum, String SheetName) throws Exception    {
		try{

			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			Row  = ExcelWSheet.getRow(RowNum);
			Cell = Row.getCell(ColNum, Row.RETURN_BLANK_AS_NULL);
			if (Cell == null) {
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(Result);
			} else {
				Cell.setCellValue(Result);
			}
			FileOutputStream fileOut = new FileOutputStream(DriverScript.OR.getProperty("Path_TestData"));
			ExcelWBook.write(fileOut);
			//fileOut.flush();
			fileOut.close();
			ExcelWBook = new XSSFWorkbook(new FileInputStream(DriverScript.OR.getProperty("Path_TestData")));
		}catch(Exception e){
			DriverScript.bResult = false;

		}
	}

}
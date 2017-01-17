package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.*;


public class Test {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@org.junit.Test
	public void testFileName(){
		String aString="";
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd HH:hh:ss");
		aString=simpleDateFormat.format(Calendar.getInstance().getTime());
		System.out.println(aString);
		
		System.out.println(aString.matches(".*/.*"));
		System.out.println(aString.indexOf("/"));
		aString=aString.replaceAll("/", "_");
		System.out.println(aString);
	}
	
	
	@org.junit.Test
	public void testAngleToDecimal(){
		StringBuffer transAngle = new StringBuffer("110��30'50\"");

		// get the degree, minute and second in the format of String
		String degreeString = transAngle.substring(0, transAngle.indexOf("��")).trim();
		String minuteString = transAngle.substring(transAngle.indexOf("��") + 1, transAngle.indexOf("'")).trim();
		String secondString = transAngle.substring(transAngle.indexOf("'") + 1,transAngle.indexOf("\"")).trim();

		// convert the strings into numbers in Double
		double degree = 0;
		double minute = 0;
		double second = 0;

		if (Pattern.compile("^[-+]?(\\d)+((\\.)?(\\d)+)$").matcher(degreeString).matches()) {
			degree = Double.parseDouble(degreeString);
		}
		if (Pattern.compile("^[-+]?(\\d)+((\\.)?(\\d)+)$*").matcher(minuteString).matches()) {
			minute = Double.parseDouble(minuteString);
		}
		if (Pattern.compile("^[-+]?(\\d)+((\\.)?(\\d)+)$*").matcher(degreeString).matches()) {
			second = Double.parseDouble(secondString);
		}
		
		System.out.println(degree + (minute * 60 + second) / 3600.0);
	}
	
	
	@org.junit.Test
	public void testCalendar() {
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.toString());
		// System.out.println(calendar.);

		SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EE MM dd hh:mm:ss X yyyy", Locale.CHINESE);
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, new Locale("CN", "CHINA"));
		Date currentDate = new Date();

		System.out.println("dateFormat.getDateInstance(DateFormat.LONG,Locale.CHINA)");
		System.out.println(DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA).format(new java.util.Date()));

		System.out.println(dateFormat.format(currentDate));

		System.out.println(currentDate.toLocaleString());

		System.out.println("simpleDateFormat1.getDateInstance()");
		System.out.println(simpleDateFormat1.format(currentDate));

		Date fileModifiedDate = null;
		String calendarString = null;
		calendarString = simpleDateFormat1.format(new java.util.Date());
		System.out.println(calendarString);
		/*
		 * try { fileModifiedDate = (Date) simpleDateFormat1.parse(
		 * "������ ���� 24 15:55:06 +08:00 2016"); } catch (ParseException e) {
		 * e.printStackTrace(); } Calendar
		 * fileModifiedCalendar=Calendar.getInstance();
		 * fileModifiedCalendar.setTime(fileModifiedDate);
		 */

	}

	@org.junit.Test
	public void testRegularExpress() {
		System.out.println("abcd".matches(".*B.*"));
		System.out.println("Exifab".matches("^Exif.{2}"));
		System.out.println("Date/Time Original".matches("(.*Date/Time Original.*)|(.*Exif Image.*)"));
		System.out.println("Exif Image Width".matches(".*Date/Time Original.*|.*Exif.*Image.*"));
		System.out.println("abcd\"".matches(".*b.*\".*"));
		System.out.println("abcd".matches(".*B.*"));

		// match a number, including the integer and float number.
		System.out.println(Pattern.compile("^[-+]?(\\d)+((\\.)?(\\d)+)$").matcher("36.37").matches());
		System.out.println(Pattern.compile(".*(.)?.*").matcher("").matches());
		System.out.println(Pattern.compile("(\\d)*(\\.)?(\\d)*").matcher("36.37").matches());
		System.out.println(Pattern.compile("(.*Make.*)", Pattern.CASE_INSENSITIVE).matcher("Make").matches());

		// extract the number from a string
		System.out.println("1972 pixels".replaceAll("(\\D)*", ""));
	}

	@org.junit.Test
	public void testHSSF() {

		// 1. to create a workbook
		Workbook workbook1 = null;
		workbook1 = new HSSFWorkbook();

		// 2. to create a sheet
		Sheet sheet1 = workbook1.createSheet();

		// 3. to create a row in the sheet
		Row row1 = sheet1.createRow(0);

		// 4. to create a cell in the row
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue("hello this is the first cell on the row in the sheet of the workbook.");

		// 5. to output the data into a file, which means the all following
		// operations are temporarily stored in the memory
		// 5.1 to create a file for ouput
		File outputFile = new File("testHSSF.xls");
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e1) {
				// to debug
				System.out.println("the file for output data failed to create.");
				e1.printStackTrace();
			}
		}

		// 5.2 to create and open a ouputStream
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			// to debug
			System.out.println("the file for output failed to create.");
			e.printStackTrace();
		}

		try {
			workbook1.write(fileOutputStream);
			fileOutputStream.close();
		} catch (IOException e) {
			// to debug
			System.out.println("Failed to write into the file.");
			e.printStackTrace();
		}
		try {
			workbook1.close();
		} catch (IOException e) {
			// to debug
			System.out.println("the workbook failed to close.");
			e.printStackTrace();
		}
		System.out.println("");

	}

	@org.junit.Test
	public void testXSSF() {

		// 1. to create a workbook
		XSSFWorkbook workbook1 = null;
		workbook1 = new XSSFWorkbook();

		// 2. to create a sheet
		XSSFSheet sheet1 = workbook1.createSheet();

		// 3. to create a row in the sheet
		XSSFRow row1 = sheet1.createRow(0);

		// 4. to create a cell in the row
		XSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue("hello this is the first cell on the row in the sheet of the workbook.");

		// 5. to output the data into a file, which means the all following
		// operations are temporarily stored in the memory
		// 5.1 to create a file for ouput
		File outputFile = new File("testXSSF.xlsx");
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				// to debug
				System.out.println("the file for ouput failed to create.");
				e.printStackTrace();
			}
		}

		// 5.2 to create and open a ouputStream
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			// to debug
			System.out.println("the file for output failed to create.");
			e.printStackTrace();
		}

		try {
			workbook1.write(fileOutputStream);
			fileOutputStream.close();
		} catch (IOException e) {
			// to debug
			System.out.println("Failed to write into the file.");
			e.printStackTrace();
		}
		try {
			workbook1.close();
		} catch (IOException e) {
			// to debug
			System.out.println("the workbook failed to close.");
			e.printStackTrace();
		}
		System.out.println("");

	}

}

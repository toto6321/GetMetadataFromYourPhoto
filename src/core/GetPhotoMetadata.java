package core;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

public class GetPhotoMetadata {

	private ArrayList<String> inputFiles = null;

	public GetPhotoMetadata() {

	}

	public ArrayList<String> getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(ArrayList<String> inputFiles) {
		this.inputFiles = inputFiles;
	}

	/**
	 * this method allow user to select some file in jpeg,jpg,pnd to manipulate
	 * 
	 * @return
	 */
	public boolean setInputFilesByDialog() {
		boolean status = false;
		inputFiles = new ArrayList<String>();
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setMultiSelectionEnabled(true);

		// to set filter so that only specific type of files will be allowed to
		// selected.
		FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(
				"Only picture will be allowed(Jpg,Jpeg,png)", "jpeg", "jpg", "png");
		jFileChooser.setFileFilter(fileNameExtensionFilter);
		if (jFileChooser.showOpenDialog(jFileChooser) == JFileChooser.APPROVE_OPTION) {
			File[] input = jFileChooser.getSelectedFiles();
			for (File file : input) {
				inputFiles.add(file.getAbsolutePath());
			}
			status = true;
		}
		return status;
	}

	/**
	 * this method allows user to customize their filter
	 * 
	 * @param filter
	 * @return if everything is OK, true will be returned
	 */
	public boolean setInputFilesByDialog(FileNameExtensionFilter filter) {
		boolean status = false;
		inputFiles = new ArrayList<String>();
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setMultiSelectionEnabled(true);
		jFileChooser.setFileFilter(filter);
		if (jFileChooser.showOpenDialog(jFileChooser) == JFileChooser.APPROVE_OPTION) {
			File[] input = jFileChooser.getSelectedFiles();
			for (File file : input) {
				inputFiles.add(file.getAbsolutePath());
			}
			status = true;
		}
		return status;
	}

	private String readMeatadataFromPhoto(String fileName, RegularExpression directoryFileter,
			RegularExpression tagFilter) {
		String result = "";
		File file = new File(fileName);
		Metadata metadata = null;
		try {
			metadata = ImageMetadataReader.readMetadata(file);
		} catch (ImageProcessingException | IOException e) {
			e.printStackTrace();
		}
		for (Directory directory : metadata.getDirectories()) {
			if (directoryFileter.matches(directory.getName()))
				for (Tag tag : directory.getTags()) {
					if (tagFilter.matches(tag.getTagName())) {
						result = tag.getDescription();
						break;
					}
				}
		}
		return result;
	}

	private String readMeatadataFromPhoto(String fileName, RegularExpression tagFilter) {
		RegularExpression directoryFileter = new RegularExpression(".*");
		return readMeatadataFromPhoto(fileName, directoryFileter, tagFilter);
	}

	/**
	 * 
	 * @param fileName
	 * @return the maker of the photo
	 */
	public String getMaker(String fileName) {
		RegularExpression regularExpression = new RegularExpression("^Make$");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	/**
	 * 
	 * @param fileName
	 * @return model of the photo
	 */
	public String getModel(String fileName) {
		RegularExpression regularExpression = new RegularExpression("^Model$");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	/**
	 * it is for strings describing a date like this: "2016-10-17T20:24:24"
	 * 
	 * @param dateInString
	 * @return
	 */
	private Date parseStringToDate1(String dateInString) {
		Calendar calendar = Calendar.getInstance();
		// to extract data from the string to comprise a Date
		int year = Integer.parseInt(dateInString.substring(0, 4));
		int month = Integer.parseInt(dateInString.substring(5, 7));
		int dayOfMonth = Integer.parseInt(dateInString.substring(8, 10));
		int hourOfDay = Integer.parseInt(dateInString.substring(11, 13));
		int minute = Integer.parseInt(dateInString.substring(14, 16));
		int second = Integer.parseInt(dateInString.substring(17));
		calendar.set(year, month, dayOfMonth, hourOfDay, minute, second);
		System.out.println(year + "\t" + month + "\t" + dayOfMonth + "\t" + hourOfDay + "\t" + minute + "\t" + second);
		;
		return calendar.getTime();
	}

	/**
	 * it is for strings describing a date like this: "2016:10:17 20:24:24"
	 * 
	 * @param dateInString
	 * @return
	 */
	private Date parseStringToDate2(String dateInString) {
		// Calendar calendar = Calendar.getInstance();
		String pattern = "yyyy:MM:dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateInString);
			// calendar.setTime(date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
		// return calendar.getTime();
	}

	public Date getDateOrTime(String fileName) {
		String result = "";
		RegularExpression regularExpression = new RegularExpression("(^Date/Time Original$)|(^Date$)|(^Create Date$)");
		result = readMeatadataFromPhoto(fileName, regularExpression);
		if (result.isEmpty()) {
			result = getFileModifiedDateInString(fileName);
		}
		Date date = null;
		// strings like "2016:10:17 20:24:24"
		if (result.matches("(\\d){4}:(\\d){2}:(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}")) {
			date = parseStringToDate2(result);
		} else if (result.matches("(\\d){4}-(\\d){2}-(\\d){2}(.)(\\d){2}:(\\d){2}:(\\d){2}")) {
			date = parseStringToDate1(result);
		}
		return date;

	}

	public String getDateOrTimeInString(String fileName) {
		String result = "";
		RegularExpression regularExpression = new RegularExpression("(^Date/Time Original$)|(^Date$)|(^Create Date$)");
		result = readMeatadataFromPhoto(fileName, regularExpression);
		if (result.isEmpty()) {
			result = getFileModifiedDateInString(fileName);
		}
		return result;

	}

	public String getImageWidth(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*Image.*Width.*)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	public String getImageHeight(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*Image.*Height.*)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	public String getFileName(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*File.*Name.*)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	public String getFileSize(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*File.*Size.*)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	/**
	 * it is for calendarString like "Fri Jan 13 23:24:14 -05:00 2017"
	 * @param calendarString
	 * @return
	 */
	private Calendar parseFileModifiedDateToCalendar(String calendarString) {
		Calendar calendar = Calendar.getInstance();
		String pattern="EEE MMM dd HH:mm:ss XXX yyyy";
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		try {
			calendar.setTime(format.parse(calendarString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return calendar;
	}

	public Calendar getFileModifiedDate(String fileName) {
		Calendar calendar = null;
		RegularExpression regularExpression = new RegularExpression("(.*File.*Modified.*Date.*)");
		String fileModifiedDateInString = readMeatadataFromPhoto(fileName, regularExpression);
		// "EEE MMM dd HH:mm:ss ZZZZ yyyy"
		if(fileModifiedDateInString.matches("^(\\w){3}(.)(\\w){3}(.)(\\d){2}(.)(\\d){2}:(\\d){2}:(\\d){2}(.){7}(\\d){4}$")){
			calendar=parseFileModifiedDateToCalendar(fileModifiedDateInString);
		}
		return calendar;
	}

	public String getFileModifiedDateInString(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*File.*Modified.*Date.*)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	public String getGPSLatitude(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*Latitude$)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	public String getGPSLongitude(String fileName) {
		RegularExpression regularExpression = new RegularExpression("(.*Longitude$)");
		return readMeatadataFromPhoto(fileName, regularExpression);
	}

	public void printAllInformation() {
		String fileName = inputFiles.get(0);
		File file = new File(fileName);
		Metadata metadata = null;
		try {
			metadata = ImageMetadataReader.readMetadata(file);
		} catch (ImageProcessingException | IOException e) {
			e.printStackTrace();
		}
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				System.out.println(directory.getName() + "\t" + tag.getTagName() + "\t" + tag.getDescription());
			}
		}
	}
}

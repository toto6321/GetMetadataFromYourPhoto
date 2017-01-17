package core;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RenamePictureWithTakenDate extends GetPhotoMetadata {

	public RenamePictureWithTakenDate() {

	}

	/**
	 * this is only for the photo taken by Apple besides the photo in the same
	 * kind. for example: "2016-10-17T20:24:24".
	 * 
	 * @param dateInString
	 * @return
	 */
	private Date parseStringToDate(String dateInString) {
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

	public String formatDateToString(Date date) {
		SimpleDateFormat formatDateToAString = new SimpleDateFormat("yyyyMMdd_HHmmss");
		if (date != null) {
			return formatDateToAString.format(date);
		} else {
			return "";
		}

	}

	/**
	 * this method is to rename the photo. If it is "IMG_0010.png", after itrun,
	 * it will be "IMG_20161117_202424_-0500.png"
	 * 
	 * @param fileName
	 */
	public void rename(String fileName) {
		File original = new File(fileName);

		// show original
		System.out.println(original.getAbsolutePath());

		String dateLable = null;
		String dateStringRead = getDateOrTimeInString(getInputFiles().get(0));
		// String temp="2016-10-17T20:24:24";
		System.out.println(dateStringRead);

		// format the date to comprise new name
		dateLable = formatDateToString(parseStringToDate(dateStringRead));

		// to rename the file keeping it on the original directory
		String destinationPath = (String) original.getAbsolutePath().subSequence(0,
				original.getAbsolutePath().lastIndexOf(File.separatorChar));
		destinationPath += File.separatorChar + "IMG_" + dateLable;
		destinationPath += original.getName().substring(original.getName().lastIndexOf('.'));
		System.out.println(destinationPath);
		File dest = new File(destinationPath);
		System.out.println(original.renameTo(dest));

		// show destination file after renaming
		System.out.println(original.getName());
		System.out.println(dest.getName());
	}

	public void renameAll() {
		ArrayList<String> photos = getInputFiles();
		if (!photos.isEmpty()) {
			for (String string : photos) {
				rename(string);
			}
		}
	}

/*
	public static void main(String[] args) {
		RenamePictureWithTakenDate renaming = new RenamePictureWithTakenDate();
		renaming.setInputFilesByDialog();
		renaming.renameAll();

	}
*/
}

package test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import core.GetPhotoMetadata;
import core.RenamePictureWithTakenDate;

public class TestRenamePictureWithTakenDate {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	
	@Test
	public void testGetPhotoMetadataClass(){
		GetPhotoMetadata getPhotoMetadata=new GetPhotoMetadata();
//		String inputFile="/Volumes/Toto/totosweet/Pictures/IMG_1028_ps2 copy.png";
		String inputFile="/Volumes/Toto/totosweet/Pictures/IMG_0010.png";
		ArrayList<String> aList=new ArrayList<String>();
		aList.add(inputFile);
		getPhotoMetadata.setInputFiles(aList);
//		System.out.println(getPhotoMetadata.getDateOrTime(getPhotoMetadata.getInputFiles().get(0)).toString());
		System.out.println(getPhotoMetadata.getFileModifiedDate(getPhotoMetadata.getInputFiles().get(0)));
//		System.out.println(getPhotoMetadata.getFileSize(getPhotoMetadata.getInputFiles().get(0)));
		getPhotoMetadata.printAllInformation();
	}
	
	@Test
	public void testRename() {
		String inputFile="/Volumes/Toto/totosweet/Pictures/IMG_0010.png";
		ArrayList<String> aList=new ArrayList<String>();
		aList.add(inputFile);
		RenamePictureWithTakenDate renaming = new RenamePictureWithTakenDate();
		renaming.setInputFiles(aList);
//		renaming.setInputFilesByDialog();
		renaming.rename(renaming.getInputFiles().get(0));
	}

	@Test
	public void testFormatForFileModifiedDate(){
		String calendarString="Fri Jan 13 23:24:14 -05:00 2017";
		Calendar calendar = Calendar.getInstance();
		String pattern="EEE MMM dd HH:mm:ss XXX yyyy";
		SimpleDateFormat parse=new SimpleDateFormat(pattern);
		try {
			calendar.setTime(parse.parse(calendarString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd_HHmmss_ZZZ");
		System.out.println(format.format(calendar.getTime()));
	}
	
	
}

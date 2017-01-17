package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;



/**
 * @author toto
 *
 */
public class GetPhotoMetadata_main {

	public GetPhotoMetadata_main() {

	}

	public static void main(String[] args) throws IOException {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
			// File photo = new File(photoAbsolunatePath);
			File[] photos = fileChooser.getSelectedFiles();

			// to decide a file to output the data
			File ouputTextFile = new File("fullMetadata_and_log.txt");
			try {
				if (!ouputTextFile.exists())
					ouputTextFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// to create a file writer instance to log.
			FileWriter exportToTextFile = new FileWriter(ouputTextFile, true);
			
			// to get a unique name of the output file
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String fileName="PhotoMetadata_";
			fileName=fileName+simpleDateFormat.format(Calendar.getInstance().getTime())+".xlsx";
			fileName=fileName.replaceAll("/","_");
			fileName=fileName.replaceAll(":", "_");
//			System.out.println(fileName);
			File ouputExcel = new File(fileName);
			try {
				if (!ouputExcel.exists())
					ouputExcel.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			XSSFWorkbook workbook = null;
			workbook = new XSSFWorkbook();
			XSSFSheet sheet1 = workbook.createSheet();

			// to create the header to store the title
			XSSFRow header = sheet1.createRow(0);
			XSSFRow row = null;
			XSSFCell[] heads = new XSSFCell[15];
			XSSFCell activeCell = null;
			for (int i = 0; i < heads.length; i++) {
				heads[i] = header.createCell(i);
			}
			heads[0].setCellValue("File Name");
			heads[1].setCellValue("Make");
			heads[2].setCellValue("Model");
			heads[3].setCellValue("Image Width/pixels");
			heads[4].setCellValue("Image Height/pixels");
			heads[5].setCellValue("File Size/bytes");
			heads[6].setCellValue("Date/Time Original");
			heads[7].setCellValue("File Modified Date");
			heads[8].setCellValue("GPS Longitude in degree");
			heads[9].setCellValue("GPS Latitude in degreee");
			heads[10].setCellValue("Address");
			heads[11].setCellValue("X");
			heads[12].setCellValue("Y");
			heads[13].setCellValue("Name");
			heads[14].setCellValue("Telephone");

			// to start to read metadata from the photos
			for (int i = 0; i < photos.length; i++) {
				if (photos[i].exists()) {
					Metadata metadata = null;
					try {
						metadata = ImageMetadataReader.readMetadata(photos[i]);
					} catch (ImageProcessingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// to create a ouput stream
					FileOutputStream exportToExcelFile = new FileOutputStream(ouputExcel);

					// here is a seperator on the screen
					System.out.println("\n------photo  " + i + "-------Start------------\n");

					exportToTextFile.write("\n------photo  " + i + "-------Start------------\n");
					exportToTextFile.flush();
					
					// read and print the EXIF Data and store them in the
					// meanwhile
					row = sheet1.createRow(i + 1);
					for (Directory directory : metadata.getDirectories()) {
						/**
						 * "Exif IFDO","Make" "Exif IFDO","Model" "Exif IFDO"
						 * ,"Date/Time" or "Exif SubIFD","Date/Time Original"
						 */

						if (Pattern.compile("^Exif.*", Pattern.CASE_INSENSITIVE).matcher(directory.getName())
								.matches()) {
							// here are the tag in directory of
												// "Exif XXX"
							for (Tag tag : directory.getTags()) {
								// write the maker,model,original date of the
								// image into the file

								// to log full of metadata into the txt file
								exportToTextFile.write(String.format(tag.getDirectoryName() + "," + tag.getTagName()
										+ "," + tag.getDescription() + "\n"));
								exportToTextFile.flush();

								if (Pattern.compile(
										"(^Make$)|(^Model$)|(.*Date/Time$)|(.*Date/Time Original)|(.*Exif Image.*)",
										Pattern.CASE_INSENSITIVE).matcher(tag.getTagName()).matches()) {
									System.out.format("[%s] - %s = %s\n", directory.getName(), tag.getTagName(),
											tag.getDescription());
								}

								// output int to the excel file
								if (Pattern.compile("(^Make$)", Pattern.CASE_INSENSITIVE).matcher(tag.getTagName())
										.matches()) {
									activeCell = row.createCell(1);
									activeCell.setCellValue(tag.getDescription());
								} else if (Pattern.compile("(^Model$)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(2);
									activeCell.setCellValue(tag.getDescription());
								} else if (Pattern.compile("(.*Image.*Width.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(3);
									activeCell.setCellValue(tag.getDescription().replaceAll("(\\D)*", ""));
								} else if (Pattern.compile("(.*Image.*Height.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(4);
									activeCell.setCellValue(tag.getDescription().replaceAll("(\\D)*", ""));
								} else if (Pattern
										.compile("(.*Date/Time$)|(.*Date/Time.*Original.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(6);
									SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
									Date dateOriginal = null;
									try {
										dateOriginal = simpleDateFormat2.parse(tag.getDescription());
										activeCell.setCellValue(dateOriginal);
									} catch (ParseException e) {
										// to debug
										System.out.println(
												"the string of Date/Time original failed to be converted to a Date kind.");
										activeCell.setCellValue(tag.getDescription());
										e.printStackTrace();
									}
								}
							}

							if (directory.hasErrors()) {
								for (String error : directory.getErrors()) {
									System.err.format("ERROR: %s", error);
								}
							}
						}

						/**
						 * "Image Width" "Image Hight"
						 */
						else if (Pattern.compile("^JPEG.*", Pattern.CASE_INSENSITIVE).matcher(directory.getName())
								.matches()) {
							for (Tag tag : directory.getTags()) {

								// to log full of metadata into the txt file
								exportToTextFile.write(String.format(tag.getDirectoryName() + "," + tag.getTagName()
										+ "," + tag.getDescription() + "\n"));
								exportToTextFile.flush();

								// write the width and height of the image into
								// the file
								if (Pattern.compile("(.*Image.*Width.*)|(.*Image.*Height.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									System.out.format("[%s] - %s = %s\n", directory.getName(), tag.getTagName(),
											tag.getDescription());
								}

								if (Pattern.compile("(.*Image.*Width.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(3);
									activeCell.setCellValue(tag.getDescription().replaceAll("(\\D)*", ""));
								} else if (Pattern.compile(".*Image.*Height.*", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(4);
									activeCell.setCellValue(tag.getDescription().replaceAll("(\\D)*", ""));
								}
							}

							if (directory.hasErrors()) {
								for (String error : directory.getErrors()) {
									System.err.format("ERROR: %s", error);
								}
							}
						}

						/**
						 * "File","File Name" "File","File Size" "File",
						 * "File Modified Date"
						 */
						else if (Pattern.compile("^File.*", Pattern.CASE_INSENSITIVE).matcher(directory.getName())
								.matches()) {
							for (Tag tag : directory.getTags()) {

								// to log full of metadata into the txt file
								exportToTextFile.write(String.format(tag.getDirectoryName() + "," + tag.getTagName()
										+ "," + tag.getDescription() + "\n"));
								exportToTextFile.flush();

								// write the name, size and modified date of the
								// image file into the file
								if (Pattern.compile("(.*File.*Name.*)|(.*File.*Size.*)|(.*File.*Modified.*Date.*)",
										Pattern.CASE_INSENSITIVE).matcher(tag.getTagName()).matches()) {
									System.out.format("[%s] - %s = %s\n", directory.getName(), tag.getTagName(),
											tag.getDescription());
								}

								if (Pattern.compile("(.*File.*Name.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(0);
									activeCell.setCellValue(tag.getDescription());
								} else if (Pattern.compile("(.*File.*Size.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(5);
									activeCell.setCellValue(tag.getDescription().replaceAll("(\\D)*", ""));
								} else if (Pattern.compile("(.*File.*Modified.*Date.*)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(7);
									activeCell.setCellValue(tag.getDescription());
									
								}
							}

							if (directory.hasErrors()) {
								for (String error : directory.getErrors()) {
									System.err.format("ERROR: %s", error);
								}
							}
						}

						/**
						 * "GPS","Latitude" "GPS" ,"Longitude"
						 */
						else {
							for (Tag tag : directory.getTags()) {
								// to log full of metadata into the txt file
								exportToTextFile.write(String.format(tag.getDirectoryName() + "," + tag.getTagName()
										+ "," + tag.getDescription() + "\n"));
								exportToTextFile.flush();

								// write the
								// coordination(longititude,lagititude) into the
								// file
								if (Pattern.compile("(.*Latitude$)|(.*Longitude$)").matcher(tag.getTagName())
										.matches()) {
									System.out.format("[%s] - %s = %s\n", directory.getName(), tag.getTagName(),
											tag.getDescription());
								}

								if (Pattern.compile("(.*Latitude$)", Pattern.CASE_INSENSITIVE).matcher(tag.getTagName())
										.matches()) {
									activeCell = row.createCell(9);
									activeCell.setCellValue(tag.getDescription());
									activeCell = row.createCell(12);
									activeCell.setCellValue(angleToDecimal(tag.getDescription()));
								} else if (Pattern.compile("(.*Longitude$)", Pattern.CASE_INSENSITIVE)
										.matcher(tag.getTagName()).matches()) {
									activeCell = row.createCell(8);
									activeCell.setCellValue(tag.getDescription());
									activeCell = row.createCell(11);
									activeCell.setCellValue(angleToDecimal(tag.getDescription()));
								}

							}

							if (directory.hasErrors()) {
								for (String error : directory.getErrors()) {
									System.err.format("ERROR: %s", error);
								}
							}
						}
					}

					row.createCell(10).setCellValue("");
					//output to the excel workbook
					workbook.write(exportToExcelFile);
					exportToExcelFile.close();
					
					//ouput to the screen (standard output)
					System.out.println("\nrecord time: " + Calendar.getInstance().getTime().toString() + "\n"
							+ "---------------End-----------------" + "\n");
					
					//output to the text file
//					SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					exportToTextFile.write("\nrecord time: " + simpleDateFormat.format(Calendar.getInstance().getTime()) + "\n"
							+ "---------------End-----------------" + "\n");
					exportToTextFile.flush();
				}

			}
			exportToTextFile.close();
			workbook.close();
		}

	}

	/**
	 * to convert angle degree in the format of String into decimal degree in
	 * Double, for example, 110°30'50' then it will return 110.51388888888889
	 * 
	 * @param angle
	 * @return
	 */
	public static double angleToDecimal(String angle) {
		StringBuffer transAngle = new StringBuffer(angle);

		// get the degree, minute and second in the format of String
		String degreeString = transAngle.substring(0, transAngle.indexOf("°")).trim();
		String minuteString = transAngle.substring(transAngle.indexOf("°") + 1, transAngle.indexOf("'")).trim();
		String secondString = transAngle.substring(transAngle.indexOf("'") + 1, transAngle.indexOf("\"")).trim();

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
		return degree + (minute * 60 + second) / 3600.0;
	}

}

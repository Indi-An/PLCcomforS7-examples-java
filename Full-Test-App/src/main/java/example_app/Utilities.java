package example_app;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import com.indian.plccom.fors7.eDataType;

class Utilities {

	/**
	 * extract writeable values from rawdata string
	 * 
	 * @param ValueString rawdata in string format
	 * @param ValueType   desired data type
	 * @return a sValues_to_Write Object
	 */
	static sValues_to_Write CheckValues(String ValueString, eDataType ValueType) {
		sValues_to_Write Result = new sValues_to_Write();

		try {

			if (ValueString.equals("") || ValueType == null) {
				Result.ParseError = true;
				return Result;
			}

			String Separator = "\n";
			ValueString = ValueString.replace("\r\n", "\n").trim();
			String[] rawValues = ValueString.split(Separator);
			// TypeConverter tc = new System.ComponentModel.TypeConverter();
			for (String ValuePart : rawValues) {
				try {
					switch (ValueType) {
					case BIT:
						Result.values.add(Boolean.valueOf(ValuePart));
						break;
					case BYTE:
						Result.values.add(Byte.valueOf(ValuePart));
						break;
					case INT:
						Result.values.add(Short.valueOf(ValuePart));
						break;
					case DINT:
						Result.values.add(Integer.valueOf(ValuePart));
						break;
					case WORD:
						Result.values.add(Integer.valueOf(ValuePart));
						break;
					case DWORD:
						Result.values.add(Long.valueOf(ValuePart));
						break;
					case REAL:
						Result.values.add(Float.valueOf(ValuePart));
						break;
					case RAWDATA:
						Result.values.add(Byte.valueOf(ValuePart));
						break;
					case BCD8:
						Result.values.add(Byte.valueOf(ValuePart));
						break;
					case BCD16:
						Result.values.add(Short.valueOf(ValuePart));
						break;
					case BCD32:
						Result.values.add(Integer.valueOf(ValuePart));
						break;
					case BCD64:
						Result.values.add(Long.valueOf(ValuePart));
						break;
					case DATETIME:
					case DATE_AND_TIME:
					case DTL:
				        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
				        LocalDateTime ldt = LocalDateTime.parse(ValuePart, formatter);		        
				        Instant instant = ldt.atZone(ZoneId.of("UTC")).toInstant();
						Result.values.add(instant);
						break;
					case S5TIME:
						Result.values.add(Long.valueOf(ValuePart));
						break;
					case TIME_OF_DAY:
						Result.values.add(Long.valueOf(ValuePart));
						break;
					case TIME:
						Result.values.add(Long.valueOf(ValuePart));
						break;
					case DATE:
						Result.values.add(Integer.valueOf(ValuePart));
						break;
					case STRING:
						Result.values.add(String.valueOf(ValuePart));
						break;
					case S7STRING:
						Result.values.add(String.valueOf(ValuePart));
						break;
					case S7WSTRING:
						Result.values.add(String.valueOf(ValuePart));
						break;
					default:
						break;

					}

				} catch (Exception ex) {
					Result.ParseError = true;
					return Result;
				}
			}
			return Result;
		} catch (Exception ex) {
			Result.ParseError = true;
			return Result;

		}
	}

	static class sValues_to_Write {

		// List<object> values = new List<object>();
		ArrayList<Object> values = new ArrayList<Object>();
		boolean ParseError;
	}

}

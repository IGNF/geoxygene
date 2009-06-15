/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.conversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * @author Julien Perret
 *
 */
public class IntegerList2VarCharFieldConversion implements FieldConversion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object javaToSql(Object source) throws ConversionException {
		if (source instanceof List) {
			String s = "";
			if (((List) source).isEmpty()) return s;
			List<Integer> dates=(List<Integer>)source;
			s+=dates.get(0);
			for (int i = 1 ; i<dates.size(); i++) {
				s+="-"+dates.get(i);
			}
			return s;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
	 */
	public Object sqlToJava(Object source) throws ConversionException {
		if (source instanceof String) {
			String s = (String) source;
			String[] values = s.split("-");
			List<Integer> result = new ArrayList<Integer>();
			for (String val:values) {
				if (!val.isEmpty()) result.add(Integer.parseInt(val));
			}
			return result;
		}
		return null;
	}

}

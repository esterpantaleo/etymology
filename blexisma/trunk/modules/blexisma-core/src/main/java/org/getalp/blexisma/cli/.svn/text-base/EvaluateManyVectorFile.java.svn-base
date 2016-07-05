package org.getalp.blexisma.cli;

import java.io.File;
import java.io.IOException;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.utils.BaseFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class EvaluateManyVectorFile {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: java ... " + EvaluateManyVectorFile.class.getName() +  " vector_directory number_of_file_tested");
			System.exit(-1);
		}
		
		File dir = new File(args[0]);
		String_RAM_VectorialBase base = null;
		ConceptualVector cv = null;
		StringBuffer sbuff = new StringBuffer();
		int nbTest = Integer.parseInt(args[1]);
		
		if (!dir.isDirectory()) {
			System.err.println("Argument should be a directory");
			System.exit(-1);
		}
		
		for (File f : dir.listFiles()){
			cv = fileToVector(f);
			if (cv != null) {
				if (base==null)
					base = new String_RAM_VectorialBase(cv.getCodeLength(),cv.getDimension());
				base.addVector(f.getName(), cv);
			}
		}
		
		sbuff.append("Statitics on texts");
		sbuff.append("\n");
		sbuff.append("Number of texts: ");
		sbuff.append(base.size());
		sbuff.append("\n");
		sbuff.append("Absolute distance mean on "+nbTest+" texts: ");
		sbuff.append(base.getAbsoluteDistanceMean(nbTest, BaseFilter.createYesFilter()));
		sbuff.append("\n");
		sbuff.append("Average distance for "+nbTest+" pairs of texts: ");
		sbuff.append(base.getPairDistanceMean(nbTest, BaseFilter.createYesFilter()));
		
		System.out.println(sbuff.toString());
	}
	
	private static ConceptualVector fileToVector(File f) {
		ConceptualVector cv = null;
		SAXBuilder builder = new SAXBuilder();
		int dim = 0;
		int norm = 0;
		
		Document doc;
		try {
			doc = builder.build(f);
			Element root = doc.getRootElement();
			if (root.getChild("vect")!=null) {
				dim = Integer.parseInt(root.getChildText("dim"));
				norm = Integer.parseInt(root.getChildText("norm"));
				cv = new ConceptualVector(root.getChildText("vect"),dim,norm);
			}
		} catch (JDOMException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return cv;
	}
}

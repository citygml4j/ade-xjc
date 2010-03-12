package org.citygml4j.ade_xjc;

import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.api.ErrorListener;

public class XJCErrorListener implements ErrorListener {

	public void error(SAXParseException ex) {
		ex.printStackTrace();
	}

	public void fatalError(SAXParseException ex) {
		ex.printStackTrace();
	}

	public void info(SAXParseException ex) {
		ex.printStackTrace();
	}

	public void warning(SAXParseException ex) {
		ex.printStackTrace();
	}

}

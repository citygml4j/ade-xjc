package org.citygml4j.ade_xjc;

import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.api.ErrorListener;

public class XJCErrorListener implements ErrorListener {

	@Override
	public void error(SAXParseException ex) {
		ex.printStackTrace();
	}

	@Override
	public void fatalError(SAXParseException ex) {
		ex.printStackTrace();
	}

	@Override
	public void info(SAXParseException ex) {
		ex.printStackTrace();
	}

	@Override
	public void warning(SAXParseException ex) {
		ex.printStackTrace();
	}

}

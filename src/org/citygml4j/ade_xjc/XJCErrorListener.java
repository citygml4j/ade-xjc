/*
 * ade-xjc - XML Schema binding compiler for CityGML ADEs
 * https://github.com/citygml4j/ade-xjc
 * 
 * ade-xjc is part of the citygml4j project
 * 
 * Copyright (C) 2013 - 2015,
 * Claus Nagel <claus.nagel@gmail.com>
 *
 * The ade-xjc program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
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

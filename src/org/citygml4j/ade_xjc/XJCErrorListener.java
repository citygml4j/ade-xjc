/*
 * ade-xjc - XML Schema binding compiler for CityGML ADEs
 * https://github.com/citygml4j/ade-xjc
 * 
 * ade-xjc is part of the citygml4j project
 * 
 * Copyright 2013-2017 Claus Nagel <claus.nagel@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

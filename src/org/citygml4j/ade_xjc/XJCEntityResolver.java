package org.citygml4j.ade_xjc;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XJCEntityResolver implements EntityResolver {

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		URL url = null;
		
		//if (systemId.endsWith("CityGML.xsd"))
			//url = XJCEntityResolver.class.getResource("/schemas/CityGML/0.4.0/CityGML.xsd");
			
		/*
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/gml.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/dynamicFeature.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/feature.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/geometryBasic2d.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/geometryBasic0d1d.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/measures.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/units.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/dictionary.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/gmlBase.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/basicTypes.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/xlink/xlinks.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/temporal.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/direction.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/topology.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/geometryComplexes.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/geometryAggregates.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/geometryPrimitives.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/coverage.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/valueObjects.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/grids.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/coordinateReferenceSystems.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/coordinateSystems.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/referenceSystems.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/datums.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/coordinateOperations.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/dataQuality.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/observation.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/defaultStyle.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/smil/smil20.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/smil/smil20-language.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/smil/xml-mod.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/temporalReferenceSystems.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/GML/3.1.1/base/temporalTopology.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/xAL/2.0/xAL.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/appearance.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/cityGMLBase.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/generics.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/building.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/cityFurniture.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/cityObjectGroup.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/landUse.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/relief.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/transportation.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/vegetation.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/waterBody.xsd
		FAILED: null file:/C:/java/ade-xjc/resources/xml/schemas/CityGML/1.0.0/texturedSurface.xsd
		return null;*/
		
		return (url != null) ? new InputSource(url.toString()) : null;
	}

}

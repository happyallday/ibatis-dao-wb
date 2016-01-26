package com.wb.ibatis.dao.engine.builder.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * XML文档解析时的实体解析器，将公共DTD声明替换为本地DTD文件
 * 
 */

public class DaoClasspathEntityResolver implements EntityResolver {

	// 本地DTD文件的位置
	private static final String DTD_PATH_DAO = "com/wb/ibatis/dao/engine/builder/xml/dao-2.dtd";
	
	// 默认替换为本地DTD文件的公共DTD声明
	private static final Map<String, String> doctypeMap = new HashMap<>();
	
	static {
		doctypeMap.put("http://www.ibatis.com/dtd/dao-2.dtd", DTD_PATH_DAO);
	    doctypeMap.put("http://ibatis.apache.org/dtd/dao-2.dtd", DTD_PATH_DAO);
	    doctypeMap.put("-//iBATIS.com//DTD DAO Configuration 2.0", DTD_PATH_DAO);
	    doctypeMap.put("-//iBATIS.com//DTD DAO Config 2.0", DTD_PATH_DAO);
	}
	
	/**
	 * 替换一个公共DTD声明为本地DTD文件
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		InputSource source = null;
		try {
			String path = doctypeMap.get(publicId);
			source = getInputSource(path);
			if (source == null) {
				path = doctypeMap.get(systemId);
				source = getInputSource(path);
			}
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
		return source;
	}

	/**
	 * 从path所指的文件中生成InputSource
	 */
	private InputSource getInputSource(String path) {
		InputSource source = null;
		if (path != null) {
			InputStream in = null;
			try {
				in = Resources.getResourceAsStream(path);
				source = new InputSource(in);
			} catch (IOException e) {
				// 不处理
			}
		}
		return source;
	}
}




























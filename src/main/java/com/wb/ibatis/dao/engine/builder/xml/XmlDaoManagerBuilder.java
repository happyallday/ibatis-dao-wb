package com.wb.ibatis.dao.engine.builder.xml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ibatis.common.io.ReaderInputStream;
import com.ibatis.common.resources.Resources;
import com.wb.ibatis.dao.client.Dao;
import com.wb.ibatis.dao.client.DaoException;
import com.wb.ibatis.dao.client.DaoManager;
import com.wb.ibatis.dao.engine.impl.DaoContext;
import com.wb.ibatis.dao.engine.impl.DaoImpl;
import com.wb.ibatis.dao.engine.impl.StandardDaoManager;
import com.wb.ibatis.dao.engine.transaction.DaoTransactionManager;
import com.wb.ibatis.dao.engine.transaction.jdbc.JdbcDaoTransactionManager;
import com.wb.ibatis.dao.engine.transaction.sqlmap.SqlMapDaoTransactionManager;

/**
 * @author www
 * @date 2016年1月23日
 * 
 * ibatis.xml的解析类，生成DaoManager实例。
 * 非线程安全，多线程每个线程需使用单独地实例类解析。
 * 
 */

public class XmlDaoManagerBuilder {

	private static final String DAO_CONFIG_ELEMENT = "daoConfig";
	private static final String PROPERTIES_ELEMENT = "properties";
	private static final String CONTEXT_ELEMENT = "context";
	private static final String TRANSACTION_MANAGER_ELEMENT = "transactionManager";
	private static final String PROPERTY_ELEMENT = "property";
	private static final String DAO_ELEMENT = "dao";
	
	private Properties properties = null;
	private boolean validationEnabled = true;
	private Map<String, String> typeAliases =  new HashMap<>(); // 别名、事务管理器类的对应关系
	
	public XmlDaoManagerBuilder() {
//		typeAliases.put("EXTERNAL", ExternalDaoTransactionManager.class.getName());
//		typeAliases.put("HIBERNATE", HibernateDaoTransactionManager.class.getName());
		typeAliases.put("JDBC", JdbcDaoTransactionManager.class.getName());
//		typeAliases.put("JTA", JtaDaoTransactionManager.class.getName());
//		typeAliases.put("OJB", "com.ibatis.dao.engine.transaction.ojb.OjbBrokerTransactionManager");
		typeAliases.put("SQLMAP", SqlMapDaoTransactionManager.class.getName());
//		typeAliases.put("TOPLINK", "com.ibatis.dao.engine.transaction.toplink.ToplinkDaoTransactionManager");
	}
	
	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	/**
	 * 生成DaoManager实例
	 * @param reader
	 * @param properties
	 * @return
	 * @throws DaoException
	 */
	public DaoManager buildDaoManager(Reader reader, Properties properties) throws DaoException {
		this.properties = properties;
		return buildDaoManager(reader);
	}
	
	/**
	 * 生成DaoManager实例
	 * @param reader
	 * @return
	 * @throws DaoException
	 */
	public DaoManager buildDaoManager(Reader reader) throws DaoException {
		StandardDaoManager daoManager = new StandardDaoManager();
		try {
			Document document = getDocument(reader);
			
			// 解析根节点
			Element root = (Element) document.getLastChild();
			String rootName = root.getNodeName();
			if (!DAO_CONFIG_ELEMENT.equals(rootName)) {
				throw new IOException("Error while configuring DaoManager. The root tag of the DAO" +
						" configuration XML document must be '" + DAO_CONFIG_ELEMENT + "'.");
			}
			
			// 解析第一层节点信息
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (CONTEXT_ELEMENT.equals(child.getNodeName())) {
						// 解析第一层节点中的context节点的信息，首先创建一个DaoContext对象，然后加到StandardDaoManager对象中
						DaoContext daoContext = parseContext((Element) child, daoManager);
						daoManager.addContext(daoContext);
					}
					else if (PROPERTIES_ELEMENT.equals(child.getNodeName())) {
						// 解析第一层节点中的property节点的信息
						Properties attributes = parseAttributes(child);
						if (attributes.containsKey("resource")) {
							String resource = attributes.getProperty("resource");
							Properties tempProperties = Resources.getResourceAsProperties(resource);
							if (properties != null) {
								tempProperties.putAll(properties);
							}
							properties = tempProperties;
						}
						else if (attributes.containsKey("url")) {
							String url = attributes.getProperty("url");
							Properties tempProperties = Resources.getUrlAsProperties(url);
							if (properties != null) {
								tempProperties.putAll(properties);
							}
							properties = tempProperties;
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw new DaoException("Error while configuring DaoManage. Cause: " + e.toString(), e);
		}
		return daoManager;
	}
	
	/**
	 * 解析第一层节点中的context节点的信息
	 */
	private DaoContext parseContext(Element contextElement, StandardDaoManager daoManager) throws DaoException {
		DaoContext daoContext = new DaoContext();
		
		daoContext.setDaoManager(daoManager);
		String id = contextElement.getAttribute("id");
		if (id != null && id.length() > 0) {
			daoContext.setId(id);
		}
		
		NodeList children = contextElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (TRANSACTION_MANAGER_ELEMENT.equals(child.getNodeName())) {
					// 解析context节点中transactionManager节点的信息
					DaoTransactionManager daoTransactionManager = parseTransactionManager((Element) child);
					daoContext.setTransactionManager(daoTransactionManager);
				}
				else if (DAO_ELEMENT.equals(child.getNodeName())) {
					// 解析context节点中dao节点的信息，首先创建一个Dao对象，然后加到DaoContext对象中
					DaoImpl daoImpl = parseDao((Element) child, daoManager, daoContext);
					daoContext.addDao(daoImpl);
				}
			}
		}
		return daoContext;
	}
	
	/**
	 * 解析context节点中transactionManager节点的信息
	 */
	private DaoTransactionManager parseTransactionManager(Element transactionManagerElement)
		throws DaoException {
		DaoTransactionManager daoTransactionManager = null;
		
		Properties attributes = parseAttributes(transactionManagerElement);
		
		String implementation = attributes.getProperty("type");
		implementation = resolveAlias(implementation);
		
		// 根据字符名称动态实例化一个DaoTransactionManager对象
		try {
			daoTransactionManager = (DaoTransactionManager) Resources.classForName(implementation).newInstance();
		} catch (Exception e) {
			throw new DaoException("Error while configuring DaoManager. Cause: " + e.toString(), e);
		}
		
		Properties props = properties;
		if (props == null) {
			props = parsePropertyElements(transactionManagerElement);
		} else {
			props.putAll(parsePropertyElements(transactionManagerElement));
		}
		
		// 初始化DaoTransactionManager对象
		daoTransactionManager.configure(props);
		
		return daoTransactionManager;
	}
	
	/**
	 * 解析context节点中dao节点的信息
	 */
	private DaoImpl parseDao(Element daoElement, StandardDaoManager daoManager,
			DaoContext daoContext) {
		DaoImpl daoImpl = new DaoImpl();
		
		Properties attributes = parseAttributes(daoElement);
		
		try {
			String iface = attributes.getProperty("interface");
			String impl = attributes.getProperty("implementation");
			daoImpl.setDaoManager(daoManager);
			daoImpl.setDaoContext(daoContext);
			daoImpl.setDaoInterface(Resources.classForName(iface));
			daoImpl.setDaoImplementation(Resources.classForName(impl));
			
			Class<? extends Dao> daoClass = daoImpl.getDaoImplementation();
			Dao dao = null;
			
			// 根据字符名称动态实例化一个Dao对象，先是生成构造方法对象，再用构造方法对象实例化对象
			try {
				Constructor<? extends Dao> constructor = daoClass.getConstructor(new Class<?>[] {DaoManager.class});
				dao = constructor.newInstance(new Object[] {daoManager});
			} catch (Exception e) {
				dao = daoClass.newInstance();
			}
			
			daoImpl.setDaoInstance(dao);
			daoImpl.initProxy(); // 生成dao代理
		} catch (Exception e) {
			throw new DaoException("Error configuring DAO. Cause: " + e, e);
		}
		return daoImpl;
	}
	
	
	/**
	 * 解析XML文档
	 * @param reader
	 * @return
	 */
	private Document getDocument(Reader reader) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// 指定此工厂生成的解析器将提供对XML名称工具的支持
			factory.setNamespaceAware(false);
			// 开启验证属性，开启此属性会验证XML文档是否符合DTD
			factory.setValidating(true);
			// 指定此工厂生成的解析器将忽略注释
			factory.setIgnoringComments(true);
			// 指定此工厂生成的解析器将在解析XML文档时，必须删除元素内容中的空格
			factory.setIgnoringElementContentWhitespace(true);
			// 指定此工厂生成的解析器将把CDATA节点转换为Text节点，并将其附加到相邻的Text节点
			factory.setCoalescing(false);
			// 指定此工厂生成的解析器将扩展实体引用节点
			factory.setExpandEntityReferences(false);
			
			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);
			
			// 使用工厂生成XML文档解析器
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 给解析器注册错误事件处理程序
			builder.setErrorHandler(new SimpleErrorHandler(new PrintWriter(errorWriter, true)));
			// 给解析器注册自定义的实体解析器
			builder.setEntityResolver(new DaoClasspathEntityResolver());
			
			// 解析器解析XML文档
			Document document = builder.parse(new ReaderInputStream(reader));
			return document;
		} catch (Exception e) {
			throw new RuntimeException("XML Parser Error. Cause: " + e);
		}
	}
	
	/**
	 * 通过别名返回事务管理器类的完整路径名称
	 */
	private String resolveAlias(String str) {
		String newString = null;
		if (typeAliases.containsKey(str)) {
			newString = typeAliases.get(str);
		}
		if (newString == null) {
			newString = str;
		}
		return newString;
	}
	
	/**
	 * 解析节点中的所有属性(attribute)，并把value中的"${}"部分替换后，组成Properties返回
	 */
	private Properties parseAttributes(Node node) {
		Properties attributes = new Properties();
		NamedNodeMap attributeNodes = node.getAttributes();
		for (int i = 0; i < attributeNodes.getLength(); i++) {
			Node attribute = attributeNodes.item(i);
			String value = parsePropertyTokens(attribute.getNodeValue());
			attributes.put(attribute.getNodeName(), value);
		}
		return attributes;
	}
	
	/**
	 * 处理字符串中带有"${}"的内容， 到一个properties文件中去读取并替换
	 */
	private String parsePropertyTokens(String str) {
		final String OPEN = "${";
		final String CLOSE = "}";
		String value = str;
		if (value != null && properties != null) {
			int start = value.indexOf(OPEN);
			int end = value.indexOf(CLOSE);
			while (start > -1 && end > start) {
				String prepend = value.substring(0, start);
				String append = value.substring(end + CLOSE.length());
				String propName = value.substring(start + OPEN.length(), end);
				String propValue = properties.getProperty(propName);
				if (propValue == null) {
					value = prepend + append;
				} else {
					value = prepend + propValue + append;
				}
				start = value.indexOf(OPEN);
				end = value.indexOf(CLOSE);
			}
		}
		return value;
	}
	
	/**
	 * 解析节点中的所有"property"子节点，组成Properties返回
	 */
	private Properties parsePropertyElements(Element propertiesParentElement) {
		Properties properties = new Properties();
		
		NodeList children = propertiesParentElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (PROPERTY_ELEMENT.equals(child.getNodeName())) {
					Properties attributes = parseAttributes(child);
					String name = attributes.getProperty("name");
					String value = attributes.getProperty("value");
					properties.setProperty(name, value);
				}
			}
		}
		return properties;
	}
	
	
	/**
	 * **********************************
	 * ******* SimpleErrorHandler *******
	 * **********************************
	 */	

	/**
	 * XML文档解析时的error处理器，用来报告errors和warnings
	 */
	private static class SimpleErrorHandler implements ErrorHandler {
		
		private PrintWriter out; // 处理器使用的输出流
		
		public SimpleErrorHandler(PrintWriter out) {
			this.out = out;
		}
		
		@Override
		public void warning(SAXParseException exception) throws SAXException {
			out.println("Warning: " + getParseExceptionInfo(exception));;
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(exception);
			throw new SAXException(message);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(exception);
			throw new SAXException(message);
		}
		
		/**
		 * 返回对SAXParseException的详细描述
		 */
		private String getParseExceptionInfo(SAXParseException e) {
			String systemId = e.getSystemId();
			if (systemId == null) {
				systemId = "null";
			}
			String info = "URL=" + systemId +
					" Line=" + e.getLineNumber() +
					": " + e.getMessage();
			return info;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}

package jdepend.parse.impl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import jdepend.metadata.JavaClass;
import jdepend.parse.ParseConfigurator;
import jdepend.parse.ParseListener;

/**
 * The <code>AbstractParser</code> class is the base class for classes capable
 * of parsing files to create a <code>JavaClass</code> instance.
 * 
 * @author <b>Abner</b>
 * 
 */

public abstract class AbstractParser {

	public static final int CONSTANT_UTF8 = 1;
	public static final int CONSTANT_UNICODE = 2;
	public static final int CONSTANT_INTEGER = 3;
	public static final int CONSTANT_FLOAT = 4;
	public static final int CONSTANT_LONG = 5;
	public static final int CONSTANT_DOUBLE = 6;
	public static final int CONSTANT_CLASS = 7;
	public static final int CONSTANT_STRING = 8;
	public static final int CONSTANT_FIELD = 9;
	public static final int CONSTANT_METHOD = 10;
	public static final int CONSTANT_INTERFACEMETHOD = 11;
	public static final int CONSTANT_NAMEANDTYPE = 12;

	private ArrayList<ParseListener> parseListeners;
	private boolean DEBUG = false;

	private PrintWriter writer = new PrintWriter(System.err);

	private ParseConfigurator conf;

	private String model = Model_Big;

	public final static String Model_Small = "small";
	public final static String Model_Big = "big";

	/**
	 * 设置日志输出的对象
	 * 
	 * @param writer
	 */
	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public AbstractParser(ParseConfigurator conf) {
		this.conf = conf;
		parseListeners = new ArrayList<ParseListener>();
		DEBUG = conf.getParseDebug();
		String analyzeModel = conf.getAnalyzeModel();
		if (analyzeModel != null) {
			model = analyzeModel;
		}
	}

	public void addParseListener(ParseListener listener) {
		parseListeners.add(listener);
	}

	/**
	 * Registered parser listeners are informed that the resulting
	 * <code>JavaClass</code> was parsed.
	 */
	public final JavaClass parse(String place, InputStream is) throws ParseClassException {
		JavaClass jClass = null;
		try {
			return this.doParse(place, is, model);
		} finally {
			this.onParsedJavaClass(jClass);
		}
	}

	protected abstract JavaClass doParse(String place, InputStream is, String model) throws ParseClassException;

	/**
	 * Informs registered parser listeners that the specified
	 * <code>JavaClass</code> was parsed.
	 * 
	 * @param jClass
	 *            Parsed Java class.
	 */
	protected void onParsedJavaClass(JavaClass jClass) {
		for (ParseListener listener : parseListeners) {
			listener.onParsedJavaClass(jClass, 1);
		}
	}

	public ParseConfigurator getConf() {
		return conf;
	}

	public void setConf(ParseConfigurator conf) {
		this.conf = conf;
	}

	public void setModel(String model) {
		this.model = model;
	}

	protected void debug(String message) {
		if (DEBUG) {
			this.writer.println(message);
		}
	}

	public boolean isDebug() {
		return DEBUG;
	}
}

package jdepend.parse.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jdepend.metadata.JavaClass;
import jdepend.metadata.TableInfo;
import jdepend.metadata.annotation.Autowired;
import jdepend.metadata.annotation.Controller;
import jdepend.metadata.annotation.Qualifier;
import jdepend.metadata.annotation.RequestMapping;
import jdepend.metadata.annotation.Service;
import jdepend.metadata.annotation.Transactional;
import jdepend.metadata.util.ParseUtil;
import jdepend.parse.util.ParseTool;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.SimpleElementValue;

public class JDependClassFileVisitor extends EmptyVisitor {

	private JavaClass jClass;

	private AbstractParser parser;

	private ConstantPool cp;

	public JDependClassFileVisitor(JavaClass jClass) {
		this.jClass = jClass;
	}

	@Override
	public void visitConstantString(ConstantString obj) {
		String name = obj.getBytes(this.cp);
		this.parser.debug("visitConstantString: obj.getBytes(this.cp) = " + name);
		List<TableInfo> tables = this.ParseTable(name);
		if (tables != null) {
			for (TableInfo table : tables) {
				this.jClass.getDetail().addTable(table);
				this.parser.debug("visitConstantString: variable type = " + table);
			}
		}
	}

	@Override
	public void visitConstantClass(ConstantClass obj) {
		String name1 = obj.getBytes(this.cp);
		this.parser.debug("visitConstantClass: obj.getBytes(this.cp) = " + name1);
		String name = ParseTool.slashesToDots(name1);
		name = ParseTool.getType(name);
		if (name != null && name.length() > 0 && !jClass.getDetail().getSupers().contains(name)) {
			jClass.getDetail().addVariableType(name);
			this.parser.debug("visitConstantClass: type = " + name);
		}
	}

	@Override
	public void visitConstantFieldref(ConstantFieldref obj) {
		String name = obj.getClass(this.cp);
		jClass.getDetail().addVariableType(name);
		this.parser.debug("visitConstantFieldref: variable type = " + name);
	}

	@Override
	public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
		String name = obj.getClass(this.cp);
		jClass.getDetail().addVariableType(name);
		this.parser.debug("visitConstantInterfaceMethodref: variable type = " + name);
	}

	@Override
	public void visitConstantMethodref(ConstantMethodref obj) {
		String name = obj.getClass(this.cp);
		String variableType = null;
		if (name.startsWith("[")) {
			if (name.startsWith("[L")) {
				variableType = name.substring(2, name.length() - 1);
			} else {
				return;
			}
		} else {
			variableType = name;
		}
		if (!jClass.getDetail().getSupers().contains(variableType)) {
			jClass.getDetail().addVariableType(variableType);
			this.parser.debug("visitConstantMethodref: variable type = " + variableType);
		}
	}

	@Override
	public void visitConstantPool(ConstantPool obj) {
		this.cp = obj;
	}

	public ConstantPool getConstantPool() {
		return cp;
	}

	@Override
	public void visitField(Field obj) {
		jdepend.metadata.Attribute attribute = new jdepend.metadata.Attribute(this.jClass, obj);

		// 处理Annotation
		for (AnnotationEntry annotationEntry : obj.getAnnotationEntries()) {
			if (annotationEntry.getAnnotationType().equals("Lorg/springframework/beans/factory/annotation/Autowired;")) {
				attribute.setAutowired(this.parseAutowired(annotationEntry));
			} else if (annotationEntry.getAnnotationType().equals(
					"Lorg/springframework/beans/factory/annotation/Qualifier;")) {
				attribute.setQualifier(this.parseQualifier(annotationEntry));
			}
		}

		this.jClass.getDetail().addAttribute(attribute);
		this.parser.debug("visitField: obj.getSignature() = " + attribute.getSignature());
	}

	private Qualifier parseQualifier(AnnotationEntry annotationEntry) {
		Qualifier qualifier = new Qualifier();
		for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
			if (elementValuePair.getNameString().equals("value")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				qualifier.setValue(simpleElementValue.toShortString());
			}
		}

		return qualifier;
	}

	private Autowired parseAutowired(AnnotationEntry annotationEntry) {
		Autowired autowired = new Autowired();
		for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
			if (elementValuePair.getNameString().equals("required")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				autowired.setRequired(Boolean.valueOf(simpleElementValue.toShortString()));
			}
		}

		return autowired;
	}

	@Override
	public void visitJavaClass(org.apache.bcel.classfile.JavaClass obj) {

		// 基本信息
		jClass.setName(obj.getClassName());
		String packageName = ParseTool.getPackageName(obj.getClassName());
		if (packageName != null) {
			jClass.setPackageName(packageName);
		}

		this.parser.debug("Parser: class name = " + jClass.getName());
		this.parser.debug("Parser: abstract = " + jClass.isAbstract());
		this.parser.debug("Parser: package name = " + jClass.getPackageName());

		// 处理父类
		if (!obj.getSuperclassName().equals("java.lang.Object")) {
			this.jClass.getDetail().setSuperClassName(obj.getSuperclassName());
			this.parser
					.debug("ParserSuperClassName: super class type = " + this.jClass.getDetail().getSuperClassName());
		}
		// 处理接口
		for (String interfaceName : obj.getInterfaceNames()) {
			this.jClass.getDetail().addInterfaceName(interfaceName);
			this.parser.debug("ParserInterfaceNames: interface type = " + interfaceName);
		}

		// 处理表信息
		Map<String, List<TableInfo>> tables = ConfigParseMgr.getInstance().getTheTables(TableInfoItem.ClassNameType);
		if (tables.containsKey(jClass.getName())) {
			for (TableInfo tableInfo : tables.get(jClass.getName())) {
				jClass.getDetail().addTable(tableInfo);
			}
		}

		// 处理Annotation
		for (AnnotationEntry annotationEntry : obj.getAnnotationEntries()) {
			if (annotationEntry.getAnnotationType().equals("Ljavax/persistence/Table;")) {
				L: for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
					if (elementValuePair.getNameString().equals("name")) {
						this.jClass.getDetail().addTable(
								new TableInfo(elementValuePair.getValue().toShortString(), TableInfo.Define));
						break L;
					}
				}
			} else if (annotationEntry.getAnnotationType().equals(
					"Lorg/springframework/transaction/annotation/Transactional;")) {
				jClass.setTransactional(this.parseTransactional(annotationEntry));
			} else if (annotationEntry.getAnnotationType().equals(
					"Lorg/springframework/web/bind/annotation/RequestMapping;")) {
				this.jClass.getDetail().setRequestMapping(this.parseRequestMapping(annotationEntry));
			} else if (annotationEntry.getAnnotationType().equals("Lorg/springframework/stereotype/Controller;")) {
				this.jClass.getDetail().setController(this.parseController(annotationEntry));
			} else if (annotationEntry.getAnnotationType().equals("Lorg/springframework/stereotype/Service;")) {
				this.jClass.getDetail().setService(this.parseService(annotationEntry));
			}
		}

	}

	@Override
	public void visitLineNumber(LineNumber obj) {
		if (obj.getLineNumber() > this.jClass.getLineCount()) {
			this.jClass.setLineCount(obj.getLineNumber());
		}
	}

	@Override
	public void visitLocalVariable(LocalVariable obj) {
		String name1 = obj.getSignature();
		this.parser.debug("visitLocalVariable: obj.getSignature() = " + name1);
		Collection<String> types = ParseUtil.signatureToTypes(name1);
		for (String name : types) {
			this.jClass.getDetail().addVariableType(name);
			this.parser.debug("visitLocalVariable: variable type = " + name);
		}

	}

	@Override
	public void visitMethod(Method obj) {
		if (!obj.isSynthetic()) {// 不采集编译器生成的Method
			jdepend.metadata.Method method = new jdepend.metadata.Method(this.jClass, obj);
			if (!obj.getName().equals("<clinit>")) {

				new GeneralMethodReader(method, parser.getConf().getPackageFilter()).read(obj);
				new HttpInvokeMethodReader(method, parser.getConf()).read(obj);
				
				method.setSelfLineCount(this.calLineCount(obj));

				this.jClass.getDetail().addMethod(method);

				// 处理Annotation
				for (AnnotationEntry annotationEntry : obj.getAnnotationEntries()) {
					if (annotationEntry.getAnnotationType().equals(
							"Lorg/springframework/transaction/annotation/Transactional;")) {
						method.setTransactional(this.parseTransactional(annotationEntry));
					} else if (annotationEntry.getAnnotationType().equals(
							"Lorg/springframework/web/bind/annotation/RequestMapping;")) {
						method.setRequestMapping(this.parseRequestMapping(annotationEntry));
					}
				}
				this.parser.debug("visitMethod: method type = " + obj);
			} else {
				new ClInitMethodReader(method).read(obj);
			}
		}
	}

	@Override
	public void visitConstantUtf8(ConstantUtf8 obj) {
		String name = obj.getBytes();
		this.parser.debug("visitConstantUtf8: obj.getBytes(this.cp) = " + name);
		if (SqlParserUtil.isSQL(name)) {
			List<TableInfo> tables = SqlParserUtil.parserSql(name);
			if (tables != null) {
				for (TableInfo table : tables) {
					this.jClass.getDetail().addTable(table);
				}
			}
		}
	}

	public void setParser(AbstractParser parser) {
		this.parser = parser;
	}

	private RequestMapping parseRequestMapping(AnnotationEntry annotationEntry) {
		RequestMapping requestMapping = new RequestMapping();
		for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
			if (elementValuePair.getNameString().equals("value")) {
				ArrayElementValue arrayElementValue = (ArrayElementValue) elementValuePair.getValue();
				for (ElementValue elementValue : arrayElementValue.getElementValuesArray()) {
					requestMapping.setValue(elementValue.toShortString());
				}
			} else if (elementValuePair.getNameString().equals("method")) {
				ArrayElementValue arrayElementValue = (ArrayElementValue) elementValuePair.getValue();
				for (ElementValue elementValue : arrayElementValue.getElementValuesArray()) {
					requestMapping.setMethod(elementValue.toShortString());
				}
			}
		}
		if (requestMapping.getValue() == null) {
			requestMapping.setValue("");
		}

		return requestMapping;
	}

	private Transactional parseTransactional(AnnotationEntry annotationEntry) {
		Transactional transactional = new Transactional();
		for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
			if (elementValuePair.getNameString().equals("value")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				transactional.setValue(simpleElementValue.toShortString());
			} else if (elementValuePair.getNameString().equals("readOnly")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				transactional.setReadOnly(Boolean.valueOf(simpleElementValue.toShortString()));
			} else if (elementValuePair.getNameString().equals("propagation")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				transactional.setPropagation(simpleElementValue.toShortString());
			}
		}

		return transactional;
	}

	private Controller parseController(AnnotationEntry annotationEntry) {
		Controller controller = new Controller();
		for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
			if (elementValuePair.getNameString().equals("value")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				controller.setValue(simpleElementValue.toShortString());
			}
		}

		return controller;
	}

	private Service parseService(AnnotationEntry annotationEntry) {
		Service service = new Service();
		for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {
			if (elementValuePair.getNameString().equals("value")) {
				SimpleElementValue simpleElementValue = (SimpleElementValue) elementValuePair.getValue();
				service.setValue(simpleElementValue.toShortString());
			}
		}

		return service;
	}

	private List<TableInfo> ParseTable(String constant) {
		if (constant != null) {
			if (SqlParserUtil.isSQL(constant)) {
				return SqlParserUtil.parserSql(constant);
			} else {
				Map<String, List<TableInfo>> tables = ConfigParseMgr.getInstance().getTheTables(TableInfoItem.KeyType);
				if (tables != null && tables.containsKey(constant)) {
					return tables.get(constant);
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private int calLineCount(org.apache.bcel.classfile.Method method) {
		if (method.getCode() != null && method.getCode().getCode().length == 1) {
			return 0;
		} else {
			LineNumberTable lt = method.getLineNumberTable();
			if (lt != null) {
				if (lt.getTableLength() == 1) {
					return 1;
				} else {
					int length = lt.getLineNumberTable().length;
					if (length > 1) {
						List<Integer> lineNumbers = new ArrayList<Integer>(lt.getLineNumberTable().length);
						for (LineNumber lineNumber : lt.getLineNumberTable()) {
							lineNumbers.add(lineNumber.getLineNumber());
						}
						Collections.sort(lineNumbers);
						return lineNumbers.get(lineNumbers.size() - 1) - lineNumbers.get(0);
					} else {
						return 0;
					}
				}
			} else {
				return 0;
			}
		}
	}
}

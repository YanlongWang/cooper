package jdepend.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;

import jdepend.framework.util.StringUtil;
import jdepend.framework.util.ThreadPool;
import jdepend.model.Component;
import jdepend.model.JavaClass;
import jdepend.model.JavaClassRelationItem;
import jdepend.model.JavaPackage;
import jdepend.model.Method;
import jdepend.model.relationtype.ParamRelation;

public class JavaClassUtil {

	public static Collection<JavaPackage> getJavaPackages(Collection<Component> components) {
		Collection<JavaPackage> javaPackages = new HashSet<JavaPackage>();
		for (Component component : components) {
			for (JavaPackage javaPackage : component.getJavaPackages()) {
				javaPackages.add(javaPackage);
			}
		}
		return javaPackages;

	}

	public static Collection<JavaClass> getClasses(Collection<Component> components) {
		Collection<JavaClass> javaClasses = new HashSet<JavaClass>();
		for (Component component : components) {
			javaClasses.addAll(component.getClasses());
		}
		return javaClasses;
	}

	public static Collection<JavaClass> getAllClasses(Collection<Component> components) {
		Collection<JavaClass> javaClasses = new HashSet<JavaClass>();
		for (Component component : components) {
			javaClasses.addAll(component.getClasses());
			for (JavaClass javaClass : component.getClasses()) {
				javaClasses.addAll(javaClass.getInnerClasses());
			}
		}
		return javaClasses;
	}

	public static Collection<JavaClass> getClassesForJavaPackages(Collection<JavaPackage> javaPackages) {
		Collection<JavaClass> javaClasses = new ArrayList<JavaClass>();
		for (JavaPackage javaPackage : javaPackages) {
			javaClasses.addAll(javaPackage.getClasses());
		}
		return javaClasses;
	}

	public static ClassTypeInfo getType(JavaClass javaClass) {

		String type;
		boolean ensure_VO_TYPE;
		String path;

		type = JavaClass.Unensure_TYPE;
		ensure_VO_TYPE = false;
		path = "";
		if (javaClass.isInner()) {
			if (!javaClass.isState()) {
				if (javaClass.getMethods().size() == 1 && javaClass.getMethods().iterator().next().isConstruction()) {
					type = JavaClass.VO_TYPE;
					path = "1";
					ensure_VO_TYPE = true;
				}
				if (type.equals(JavaClass.Unensure_TYPE)) {
					L1: for (JavaClass subClass : javaClass.getSubClasses()) {
						if (subClass.isState()) {
							type = JavaClass.VO_TYPE;
							path = "2";
							break L1;
						}
					}
				}
				if (type.equals(JavaClass.Unensure_TYPE)) {
					L2: for (JavaClass superClass : javaClass.getSupers()) {
						if (superClass.isState()) {
							type = JavaClass.VO_TYPE;
							path = "3";
							break L2;
						}
					}
				}
				if (type.equals(JavaClass.Unensure_TYPE)) {
					type = JavaClass.Service_TYPE;
					path = "4";
				}
			} else {
				type = JavaClass.VO_TYPE;
				path = "5";
			}

			if (type.equals(JavaClass.VO_TYPE) && !ensure_VO_TYPE) {

				boolean haveBusinessMethod = false;
				O: for (Method method : javaClass.getMethods()) {
					if (!method.isConstruction() && !method.getName().startsWith("get")
							&& !method.getName().startsWith("set")) {
						haveBusinessMethod = true;
						break O;
					}
				}
				if (!haveBusinessMethod) {
					type = JavaClass.VO_TYPE;
					path += "6";
					ensure_VO_TYPE = true;
				}

				if (!ensure_VO_TYPE) {
					L1: for (JavaClass superClass : javaClass.getSupers()) {
						if (superClass.isState()) {
							type = JavaClass.VO_TYPE;
							path += "7";
							ensure_VO_TYPE = true;
							break L1;
						}
					}
				}

				if (!ensure_VO_TYPE) {
					boolean isParamRelation = false;
					M: for (JavaClassRelationItem item : javaClass.getCaItems()) {
						if (item.getType() instanceof ParamRelation) {
							isParamRelation = true;
							break M;
						}
					}
					if (!isParamRelation) {
						type = JavaClass.Service_TYPE;
						path += "9";
					} else {
						type = JavaClass.VO_TYPE;
						path += "10";
						ensure_VO_TYPE = true;
					}
				}
			}
		}
		return new ClassTypeInfo(type, path);

	}

	public static void supplyJavaClassRelationItem(final JavaClassCollection javaClasses) {

		ExecutorService pool = ThreadPool.getPool();

		for (final String unit : javaClasses.getUnitJavaClasses().keySet()) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					for (JavaClass javaClass : javaClasses.getUnitJavaClasses().get(unit)) {
						javaClass.supplyJavaClassRelationItem(javaClasses);
					}
				}
			});
		}

		ThreadPool.awaitTermination(pool);
	}

	/**
	 * 将JavaClassDetail中的字符串信息填充为对象引用
	 * 
	 * @param javaClasses
	 */
	public static void supplyJavaClassDetail(final JavaClassCollection javaClasses) {
		supplyJavaClassDetailSimple(javaClasses);
		supplyJavaClassDetailMethodInvoke(javaClasses);
	}

	/**
	 * 匹配JavaClass
	 * 
	 * @param pattern
	 * @param javaClass
	 * @return
	 */
	public static boolean match(String pattern, JavaClass javaClass) {
		return match(pattern, javaClass.getName());
	}

	/**
	 * 匹配JavaClass
	 * 
	 * @param pattern
	 * @param className
	 * @return
	 */
	public static boolean match(String pattern, String className) {
		String newPattern = null;
		if (!pattern.endsWith("*")) {
			newPattern = pattern + "*";
		}
		boolean rtn = StringUtil.match(newPattern.toUpperCase(), className.substring(className.lastIndexOf('.') + 1)
				.toUpperCase());
		if (rtn) {
			return true;
		} else {
			return StringUtil.match(pattern.toUpperCase(), className.substring(className.lastIndexOf('.') + 1)
					.toUpperCase());
		}
	}

	/**
	 * 将JavaClassDetail中的字符串信息填充为对象引用
	 * 
	 * @param javaClasses
	 */
	private static void supplyJavaClassDetailSimple(final JavaClassCollection javaClasses) {

		ExecutorService pool = ThreadPool.getPool();

		for (final String unit : javaClasses.getUnitJavaClasses().keySet()) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					for (JavaClass javaClass : javaClasses.getUnitJavaClasses().get(unit)) {
						javaClass.supplyDetail(javaClasses);
					}
				}
			});
		}

		ThreadPool.awaitTermination(pool);
	}

	/**
	 * 将Method.InvokeItem中的字符串信息填充为对象引用
	 * 
	 * @param javaClasses
	 */
	private static void supplyJavaClassDetailMethodInvoke(final JavaClassCollection javaClasses) {

		ExecutorService pool = ThreadPool.getPool();

		for (final String unit : javaClasses.getUnitJavaClasses().keySet()) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					for (JavaClass javaClass : javaClasses.getUnitJavaClasses().get(unit)) {
						// 填充Method中的InvokeItem
						for (Method method : javaClass.getSelfMethods()) {
							method.supplyInvokeItem(javaClasses);
						}
					}
				}
			});
		}

		ThreadPool.awaitTermination(pool);
	}

	public static void main(String[] args) {
		String pattern = "*controller";

		String className = "com.neusoft.saca.snap.portal.comment.CommentController";

		System.out.print(JavaClassUtil.match(pattern, className));
	}

}

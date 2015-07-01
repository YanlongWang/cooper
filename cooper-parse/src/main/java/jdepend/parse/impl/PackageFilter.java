package jdepend.parse.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>PackageFilter</code> class is used to filter imported package
 * names.
 * <p>
 * The default filter contains any packages declared in the
 * <code>jdepend.properties</code> file, if such a file exists either in the
 * user's home directory or somewhere in the classpath.
 * 
 * @author <b>Abner</b>
 * 
 */

public class PackageFilter implements Serializable {

	private static final long serialVersionUID = 3937389131772119374L;

	private Collection<String> filtered = new ArrayList<String>();
	private Collection<String> notFiltered = new ArrayList<String>();

	private transient Collection<String> classNames;// 本次分析的类列表

	private transient Map<String, Boolean> histroy = new HashMap<String, Boolean>();

	public PackageFilter(Collection<String> filteredPackages, Collection<String> notFilteredPackages) {
		this.filtered = filteredPackages;
		this.notFiltered = notFilteredPackages;
	}

	public boolean acceptClass(String className) {
		if (this.classNames == null) {
			return true;
		} else {
			return this.classNames.contains(className);
		}
	}

	/**
	 * Indicates whether the specified package name passes this package filter.
	 * 
	 * @param packageName
	 *            Package name.
	 * @return <code>true</code> if the package name should be included;
	 *         <code>false</code> otherwise.
	 */
	public boolean accept(String packageName) {
		if (packageName == null) {
			return false;
		}
		synchronized (histroy) {
			if (histroy.containsKey(packageName)) {
				return histroy.get(packageName);
			}
			for (String nameNotFilter : this.notFiltered) {
				if (nameNotFilter.endsWith("*")) {
					nameNotFilter = nameNotFilter.substring(0, nameNotFilter.length() - 2);
					if (packageName.startsWith(nameNotFilter)) {
						histroy.put(packageName, true);
						return true;
					}
				} else {
					if (packageName.equals(nameNotFilter)) {
						histroy.put(packageName, true);
						return true;
					}
				}
			}
			for (String nameToFilter : this.filtered) {
				if (nameToFilter.endsWith("*")) {
					nameToFilter = nameToFilter.substring(0, nameToFilter.length() - 2);
					if (packageName.startsWith(nameToFilter)) {
						histroy.put(packageName, false);
						return false;
					}
				} else {
					if (packageName.equals(nameToFilter)) {
						histroy.put(packageName, false);
						return false;
					}
				}
			}

			histroy.put(packageName, true);
			return true;
		}

	}

	public void addFilters(Collection<String> filteredPackages) {

		for (String filteredPackage : filteredPackages) {
			if (!this.filtered.contains(filteredPackage)) {
				this.filtered.add(filteredPackage);
			}
		}
	}

	public void setClassNames(Collection<String> classNames) {
		this.classNames = classNames;
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		histroy = new HashMap<String, Boolean>();
	}
}

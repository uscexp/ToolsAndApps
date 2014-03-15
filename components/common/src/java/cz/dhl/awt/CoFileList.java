/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import cz.dhl.io.CoFile;
import cz.dhl.io.CoSort;

/**
 * @version  0.72 08/10/2003
 * @author  Bea Petrovicova <beapetrovicova@yahoo.com>
 */
public class CoFileList extends MultiList {
	String filterS[] = {
	};
	int orderI = CoSort.ORDER_BY_NAME;

	/**
   * @uml.property  name="files"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
	CoFile files[] = new CoFile [ 0 ] ;
	/**
   * @uml.property  name="origfiles"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
	CoFile origfiles[] = new CoFile [ 0 ] ;

	CoFileList() {
		setMultipleMode(true);
	}

	/**
   * @param files  The files to set.
   * @uml.property  name="files"
   */
	void setFiles(CoFile files[]) {
		if (files == null)
			origfiles = new CoFile[0];
		else origfiles = files;
		this.files = CoSort.listSplit( CoSort.listOrder( 
			CoSort.listFilter(origfiles, filterS), orderI));
		setEnabled((this.files.length > 0));
		removeAll();
		for (int i = 0; i < this.files.length; i++)
			if (this.files[i].isDirectory())
				add("[" + this.files[i].getName() + "]");
			else if (this.files[i].isLink())
				add("<" + this.files[i].getName() + ">");
			else
				add(this.files[i].getName());
	}

	/**
   * @return  Returns the files.
   * @uml.property  name="files"
   */
	CoFile[] getFiles() {
		return origfiles;
	}

	void setOrder(int order) {
		this.orderI = order;
		setFiles(origfiles);
	}

	void setFilter(String filter[]) {
		this.filterS = filter;
		setFiles(origfiles);
	}

	CoFile getSelectedFile() {
		int i = getSelectedIndex();
		if (i >= 0 && i < files.length)
			return files[i];
		else
			return null;
	}

	CoFile[] getSelectedFiles() {
		int is[] = getSelectedIndexes();
		CoFile fs[] = new CoFile[is.length];
		for (int i = 0; i < is.length; i++)
			fs[i] = files[is[i]];
		return fs;
	}

	void deselectAllFiles() {
		int is[] = getSelectedIndexes();
		for (int j = 0; j < is.length; j++)
			deselect(is[j]);
	}
}
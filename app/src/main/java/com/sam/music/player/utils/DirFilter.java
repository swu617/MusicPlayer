package com.sam.music.player.utils;

import java.io.File;
import java.io.FilenameFilter;


public class DirFilter implements FilenameFilter {
	private String mStrType = null;

	public DirFilter(String strType) {
		this.mStrType = strType;
	}

	public boolean accept(File fl, String strName) {
		return strName.endsWith(mStrType);
	}
}

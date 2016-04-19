package shared.heiliuer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.text.TextUtils;

public class FileHelper {

	/**
	 * 刪除文件或者文件夹（包括子目录和子文件）
	 * 
	 * @param path
	 *            文件夹或者文件路径
	 */
	public static boolean delFolderOrFile(String path) {
		File myFilePath = new File(path);
		if (myFilePath.isFile() || myFilePath.list() == null) {
			return myFilePath.delete();
		}
		try {
			return delAllFileInFolder(path) & myFilePath.delete();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * such as: mp3 and no the '.'
	 * 
	 * @return maybe null
	 */
	public static String getSuffix(String fileName) {
		if (TextUtils.isEmpty(fileName))
			return null;
		int fileNameLength = fileName.length();
		int i = fileName.lastIndexOf(".");
		if (i != -1 && i != fileNameLength - 1)
			return fileName.substring(i + 1, fileNameLength);
		return null;
	}

	public static String getNameWithoutSuffix(File file) {
		String fileName = file.getName();
		if (file.isDirectory()) {
			return fileName;
		}
		int fileNameLength = fileName.length();
		int i = fileName.lastIndexOf(".");
		if (i > 0 && i != fileNameLength - 1)
			return fileName.substring(0, i);
		return fileName;
	}

	/**
	 * 删除对应路径所有文件
	 * 
	 * @param path
	 *            只能是路径
	 * @return 成功与否
	 */
	public static boolean delAllFileInFolder(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFileInFolder(path + "/" + tempList[i]);
				delFolderOrFile(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 检查文件是否不存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFileNotExist(String path) {
		if (TextUtils.isEmpty(path))
			return true;
		return isFileNotExist(new File(path));
	}

	/**
	 * 检查文件是否不存在
	 * 
	 * @param f
	 * @return
	 */
	public static boolean isFileNotExist(File f) {
		if (f == null)
			return true;
		return !f.exists();
	}

	/**
	 * 复制文件，目标路径不存在会自动创建
	 * 
	 * @param fileFrom
	 *            源路径
	 * @param fileTo
	 *            目标路径
	 * @return
	 */
	public static boolean copyFile(String fileFrom, String fileTo) {
		File from = new File(fileFrom);
		if (!from.isFile())
			return false;
		File to = null;
		if (fileTo.contains("/")) {
			to = new File(fileTo.substring(0, fileTo.lastIndexOf('/')));
		} else if (fileTo.contains("\\")) {
			to = new File(fileTo.substring(0, fileTo.lastIndexOf('\\')));
		}
		if (to != null && !to.exists() && !to.mkdirs()) {
			return false;
		}
		to = new File(fileTo);
		try {
			FileInputStream in = new FileInputStream(from);
			FileOutputStream out = new FileOutputStream(to);
			byte[] bs = new byte[1024];
			int count;
			while ((count = in.read(bs)) != -1) {
				out.write(bs, 0, count);
			}
			out.flush();
			out.close();
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyFile(InputStream in, String fileToPath,
			String fileName) {
		if (in == null || TextUtils.isEmpty(fileToPath)
				|| TextUtils.isEmpty(fileName)) {
			return false;
		}
		File to = new File(fileToPath);
		if (!to.exists() && !to.mkdirs()) {
			return false;
		}
		to = new File(fileToPath + File.separatorChar + fileName);
		try {
			FileOutputStream out = new FileOutputStream(to);
			byte[] bs = new byte[1024];
			int count;
			while ((count = in.read(bs)) != -1) {
				out.write(bs, 0, count);
			}
			out.flush();
			out.close();
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 移动文件
	 * 
	 * @param fileFrom
	 * @param fileTo
	 * @return
	 */
	public static boolean moveFile(String fileFrom, String fileTo) {
		File from = new File(fileFrom);
		return from.renameTo(new File(fileTo));
	}

	/**
	 * 扫描文件
	 * 
	 * @param path
	 * @param baseProgress
	 * @param onGetFileListener
	 */
	public static void scanFiles(File path, OnGetFileListener onGetFileListener) {
		isScan = true;
		progress = 0;
		getFileList(path, 1, onGetFileListener);
		onGetFileListener.onFileScanfinish();
	}

	public static void stopScanFiles() {
		isScan = false;
	}

	private static double progress = 0;

	private static boolean isScan;

	private static void getFileList(File path, double baseProgress,
			OnGetFileListener onGetFileListener) {
		if (isScan) {
			if (path.isDirectory()) {
				File[] files = path.listFiles();
				if (files == null || files.length == 0) {
					progress += baseProgress;
				} else {
					double b = baseProgress / files.length;
					for (File f : files) {
						getFileList(f, b, onGetFileListener);
					}
				}
				onGetFileListener.onGetAFile(path, false, progress);
			} else {
				progress += baseProgress;
				onGetFileListener.onGetAFile(path, true, progress);
			}
		}
	}

	public static interface OnGetFileListener {
		public void onGetAFile(File src, boolean isFile, double progress);

		public void onFileScanfinish();
	}
}

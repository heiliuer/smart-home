package shared.heiliuer.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 关于zip文件的解压，压缩，直接读取的帮助类
 * 
 * @author Heiliuer
 * 
 */
public class ZipHelper {

	/**
	 * 不解压直接读取文件压缩文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void readZipFile(String file, ReadZipHandler handler)
			throws Exception {
		if (handler == null)
			return;
		ZipFile zf = new ZipFile(file);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				handler.readADirect(zf, ze);
			} else {
				System.out.println("file - " + ze.getName() + " : "
						+ ze.getSize() + " bytes");
				long size = ze.getSize();
				if (size > 0) {
					handler.readAFile(zf, ze);
					// 读取文件
					// BufferedReader br = new BufferedReader(
					// new InputStreamReader(zf.getInputStream(ze)));
				}
			}
		}
		zf.close();
		zin.closeEntry();
		zin.close();
	}

	/**
	 * 获知zip包里第一级目录所含文件数目
	 * 
	 * @param zipFile
	 * @return
	 */
	public static int getZipFileNum(String zipFile) {
		try {
			return new ZipFile(zipFile).size();
		} catch (IOException e) {
		}
		return 0;

	}

	/**
	 * 直接读取zip文件时的事件处理接口
	 * 
	 * @author Heiliuer
	 * 
	 */
	public static interface ReadZipHandler {
		public void readADirect(ZipFile zf, ZipEntry ze);

		public void readAFile(ZipFile zf, ZipEntry ze);
	}

	/**
	 * 输入流缓冲区大小
	 */
	private static final int BUFFER = 2048;
	/**
	 * 方法递归是，记录是否是文件夹
	 */
	private static boolean flag = false;

	/**
	 * ZipSubdirectory函数将一个指定目录（包括它子目录）压缩成一个同名压缩文件(这里称为"ORIGIN")
	 * 
	 * @param myDir
	 * @return
	 * @throws IOException
	 */
	public static File zip(File myDir) throws IOException {
		// 创建缓冲输入流BufferedInputStream
		BufferedInputStream origin = null;
		// 创建ZipOutputStream对象，将向它传递希望写入文件的输出流
		File zipFile = new File("D:/" + myDir.getName() + ".zip");
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos,
				BUFFER));
		// dirContents[]获取当前目录(myDir)所有文件对象（包括子目录名)
		File dirContents[] = myDir.listFiles();
		// 创建临时文件tempFile,使用后删除
		File tempFile = null;
		try {
			// 处理当前目录所有文件对象，包括子目录
			for (int i = 0; i < dirContents.length; i++) {
				// 使用递归方法将当前目录的子目录转成一个ZIP文件，并作为一个ENTRY加进"ORIGIN"
				if (dirContents[i].isDirectory()) {
					tempFile = zip(dirContents[i]);
					flag = true;
				}
				// 如果当前文件不是子目录
				else {
					tempFile = dirContents[i];
					// flag标记tempFile是否由子目录压缩成的ZIP文件
					flag = false;
				}
				System.out.println("Compress file: " + tempFile.getName());
				FileInputStream fis = new FileInputStream(tempFile);
				origin = new BufferedInputStream(fis, BUFFER);
				// 为被读取的文件创建压缩条目
				ZipEntry entry = new ZipEntry(tempFile.getName());
				byte data[] = new byte[BUFFER];
				int count;
				// 在向ZIP输出流写入数据之前，必须首先使用out.putNextEntry(entry); 方法安置压缩条目对象
				out.putNextEntry(entry);
				// 向ZIP 文件写入数据
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				// tempFile是临时生成的ZIP文件,删除它
				if (flag == true) {
					flag = tempFile.delete();
					System.out.println("Delete file:" + tempFile.getName()
							+ flag);
				}
				// 关闭输入流
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		// 递归返回
		return zipFile;
	}

	/**
	 * 解压
	 * 
	 * @param zipPath
	 *            zip文件路径
	 * @param outPath
	 *            输出文件夹路径
	 */
	public static void unZip(String zipPath, String outPath) {
		try {
			ZipInputStream Zin = new ZipInputStream(
					new FileInputStream(zipPath));
			BufferedInputStream Bin = new BufferedInputStream(Zin);
			File Fout = null;
			ZipEntry entry;
			try {
				while ((entry = Zin.getNextEntry()) != null
						&& !entry.isDirectory()) {
					Fout = new File(outPath, entry.getName());
					if (!Fout.exists()) {
						(new File(Fout.getParent())).mkdirs();
					}
					FileOutputStream out = new FileOutputStream(Fout);
					BufferedOutputStream Bout = new BufferedOutputStream(out);
					int b;
					while ((b = Bin.read()) != -1) {
						Bout.write(b);
					}
					Bout.close();
					out.close();
					System.out.println(Fout + "解压成功");
				}
				Bin.close();
				Zin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

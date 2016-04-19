package shared.heiliuer.media;

import android.text.TextUtils;

public class SongCell {
	private String songName;
	private String singerName;
	private String sex;
	private String newSong;
	private String type;
	private String path;

	public SongCell() {

	}

	public SongCell(String singerName) {
		this.singerName = singerName;
	}

	public SongCell(String songName, String singerName, String sex,
			String newSong, String type, String path) {
		this.songName = songName;
		this.singerName = singerName;
		this.sex = sex;
		this.newSong = newSong;
		this.type = type;
		this.path = path;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNewSong() {
		return newSong;
	}

	public void setNewsong(String newsong) {
		this.newSong = newsong;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof SongCell))
			return false;
		SongCell cell = (SongCell) o;
		if (TextUtils.isEmpty(cell.getPath())) {
			return false;
		}
		return cell.getPath().equals(path);
	}

	@Override
	public String toString() {
		return songName + "-" + singerName;
	}
}

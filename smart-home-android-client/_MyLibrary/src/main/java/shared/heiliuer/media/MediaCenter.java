package shared.heiliuer.media;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;

public class MediaCenter implements OnPreparedListener, OnCompletionListener {

	public interface OnPlayStateChangedListener {
		public void playSateChanged(boolean isToPlay);

		public void posChanged(int pos);
	}

	public interface OnProgressChangedListener {
		public void progressChanged(int cur, int total);

		public void reset();
	}

	private static MediaCenter mediaCenter;

	public static MediaCenter getMediaCenter() {
		if (mediaCenter == null)
			mediaCenter = new MediaCenter();
		return mediaCenter;
	}

	private SongCell current;

	public SongCell getCurrent() {
		return current;
	}

	private boolean hasInit, hasPrepared = true;

	private OnPlayStateChangedListener onPlayStateChangedListener;

	private OnProgressChangedListener onProgressChangedListener;

	private MediaPlayer player;

	private Vector<SongCell> playList;

	private Timer timer;

	private MediaCenter() {
		playList = new Vector<SongCell>();
		player = new MediaPlayer();
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
	}

	/**
	 * 添加歌曲
	 * 
	 * @param cell
	 * @return
	 */
	public int add(SongCell cell) {
		return addInPos(cell, playList.size());
	}

	/**
	 * 添加歌曲
	 * 
	 * @param cell
	 * @return
	 */
	public int addInPos(SongCell cell, int pos) {
		if (pos < 0 || pos > playList.size())
			return -1;
		String path = cell.getPath();
		for (int i = 0; i < playList.size(); i++) {
			if (playList.get(i).getPath().equals(path))
				return i;
		}
		playList.add(pos, cell);
		return pos;
	}

	/**
	 * 绑定视频播放holder
	 * 
	 * @param holder
	 */
	public void bindSurfaceHolder(SurfaceHolder holder) {
		if (holder.isCreating())
			player.setDisplay(holder);
	}

	/**
	 * 删除歌曲
	 * 
	 * @param pos
	 * @return
	 */
	public boolean delete(int pos) {
		if (pos < 0 || pos >= playList.size())
			return false;
		boolean isCurrent = playList.remove(pos) == current;
		if (isCurrent && (player.isPlaying() || next() == -1)) {
			reset();
		}
		return true;
	}

	/**
	 * 
	 * @param pos
	 * @return
	 */
	public int moveUp(int pos) {
		if (pos <= 0 || pos >= playList.size())
			return pos;
		playList.insertElementAt(playList.remove(pos), pos - 1);
		return pos - 1;
	}

	/**
	 * 
	 * @param pos
	 * @return
	 */
	public int moveDown(int pos) {
		if (pos < 0 || pos >= playList.size() - 1)
			return pos;
		playList.insertElementAt(playList.remove(pos), pos + 1);
		return pos + 1;
	}

	public void destroy() {
		player.release();
	}

	synchronized public int getCurrentPos() {
		if (current == null)
			return -1;
		return playList.indexOf(current);
	}

	public Vector<SongCell> getPlayList() {
		return playList;
	}

	public boolean isInited() {
		return hasInit;
	}

	public boolean isPlaying() {
		return player.isPlaying();
	}

	/**
	 * 切歌
	 * 
	 * @return the currentPos if succeed or -1 if failed
	 */
	public int next() {
		if (playList.size() == 0) {
			return -1;
		}
		int currentPos = getCurrentPos();
		if (currentPos >= playList.size() - 1) {
			return resetData(0) ? 0 : -1;
		}
		return resetData(currentPos + 1) ? currentPos + 1 : -1;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		next();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		hasPrepared = true;
		hasInit = true;
		if (onPlayStateChangedListener != null) {
			onPlayStateChangedListener.posChanged(getCurrentPos());
			onPlayStateChangedListener.playSateChanged(true);
		}
	}

	public void pause() {
		player.pause();
	}

	public void play() {
		player.start();
	}

	public int playAgain() {
		player.seekTo(0);
		return getCurrentPos();
	}

	/**
	 * 暂停播放歌曲
	 */
	public boolean playOrPause() {
		boolean toPlay = !player.isPlaying();
		if (toPlay)
			player.start();
		else
			player.pause();
		if (onPlayStateChangedListener != null) {
			onPlayStateChangedListener.playSateChanged(toPlay);
		}
		return toPlay;
	}

	/**
	 * 定位播放
	 */
	public boolean playPos(int pos) {
		if (pos < 0 || pos >= playList.size())
			return false;
		return resetData(pos);
	}

	public void reset() {
		player.reset();
		hasInit = false;
	}

	private boolean resetData(int pos) {
		if (!hasPrepared) {
			return false;
		}
		hasPrepared = false;
		try {
			if (onPlayStateChangedListener != null) {
				onPlayStateChangedListener.playSateChanged(false);
			}
			reset();
			current = playList.get(pos);
			player.setDataSource(current.getPath());
			player.prepareAsync();
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param percent
	 *            0-1000 千分数
	 */
	public void seek(int percent) {
		player.seekTo((int) (player.getDuration() * (float) percent / 1000));
	}

	public void setOnPlayStateChangedListener(
			OnPlayStateChangedListener onPlayStateChangedListener) {
		this.onPlayStateChangedListener = onPlayStateChangedListener;
	}

	public void setOnProgressChangedListener(
			OnProgressChangedListener onProgressChangedListener) {
		this.onProgressChangedListener = onProgressChangedListener;
		if (onProgressChangedListener != null && timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (hasInit)
						MediaCenter.this.onProgressChangedListener
								.progressChanged(player.getCurrentPosition(),
										player.getDuration());
				}
			}, 0, 900);
		}
	}

	public void unBindSurfaceHolder() {
		player.setDisplay(null);
	}

}

package com.sam.music.player.utils;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundUtils {
	public static final int SOUND_EXPLOSION = 1;
	private SoundPool mSoundPool;
	private HashMap mSoundPoolMap;
	AudioManager mAudioManager = null;
	Context mContext;

	/*
	 * SoundPool is fairly bug ridden. Even though you're working with
	 * relatively small sound files, you're better option is to still use
	 * MediaPlayer
	 * 
	 * SoundPool doesn't play sounds >100K in SDK 1.5
	 */

	public SoundUtils(Context context) {
		mContext = context;
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap();
		mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public void addSound(int index, int nSoundResID) {
		mSoundPoolMap.put(index, mSoundPool.load(mContext, nSoundResID, 1));
	}

	public void playSound(int sound) {
		float streamVolumeCurrent = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;

		mSoundPool.play((Integer) mSoundPoolMap.get(sound), volume, volume, 1,
				-1, 1f);
	}

}

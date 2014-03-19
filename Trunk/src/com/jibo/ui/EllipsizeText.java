package com.jibo.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 文字显示的长度超过设置的最大长度时，显示省略后缀
 * 
 * @author simon
 *
 */

public class EllipsizeText extends TextView {

	private static final String TAG = "simon";
	
	/**省略后缀*/
	private static final String ELLIPSIS = "...";
	/***字体的大小*/
	private float lineSpacingMultiplier = 1.0f;
	/**行间距*/
	private float lineAdditionalVerticalPadding = 0.0f;
	/** 当前是否为收起状态(显示省略号状态) */
	private boolean isEllipsized;
	/** 是否需要重绘 */
	private boolean isStale;
	/** setText()时锁住TextChange方法触发 */
	private boolean programmaticChange;
	/** 所有字体 */
	private String fullText;
	/** 设置的最大行数的初始值 */
	private int originalMaxLines;
	/** 当前TextView的最大行数 */
	private int maxLines = -1;
	/** 当前TextView是否具有伸展收缩布局，就是当前textView内容是否超过最大行数，未超过，则隐藏收缩伸展按钮 */
	private boolean isShowExpandBtn;
	
	private EllipsizeListener ellipsizeListener;

	public interface EllipsizeListener {
		void ellipsizeStateChanged(boolean ellipsized, boolean isShowExpandBtn);
	}

	public void setEllipsizeLinstener(EllipsizeListener listener) {
		ellipsizeListener = listener;
	}

	public EllipsizeText(Context context) {
		super(context);
	}

	public EllipsizeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EllipsizeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 是否在文字过多的时候显示省略符号
	public boolean isEllipsized() {
		return isEllipsized;
	}

	//设置文本显示最大行数
	@Override
	public void setMaxLines(int maxLines) {
		super.setMaxLines(maxLines);
		this.originalMaxLines = maxLines;
		this.maxLines = maxLines;
		isStale = true;
	}

	public int getMaxLines() {
		return maxLines;
	}

	@Override
	public void setLineSpacing(float add, float mult) {
		this.lineAdditionalVerticalPadding = add;
		this.lineSpacingMultiplier = mult;
		super.setLineSpacing(add, mult);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		super.onTextChanged(text, start, before, after);
		if (!programmaticChange) {
			fullText = text.toString();
			isStale = true;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.v(TAG, "onDraw");
		if (isStale) {
			super.setEllipsize(null);
			resetText();
			Log.v(TAG, "resetText");
		}
		super.onDraw(canvas);
	}

	/**
	 * 改变伸展<->收缩状态
	 * 
	 * @param ellipsized
	 */
	public void changeShowText(boolean ellipsized) {
		//当前为收缩状态时，点击按钮全部显示，设置maxLines越大越好，这里设置为1000，保证字体全部能完整
		super.setMaxLines(ellipsized ? 1000 : originalMaxLines);
		this.maxLines = ellipsized ? -1 : originalMaxLines;
		isStale = true;
		invalidate();
	}

	/**
	 * 重设显示文本
	 */
	private void resetText() {
//		setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
//		TextPaint tp = getPaint();
//		tp.setFakeBoldText(true);
		int maxLines = getMaxLines();
		String workingText = fullText;
		Log.v(TAG, "workingText=" + workingText);
		boolean ellipsized = false;
		/**当前maxLines==-1时，需要全部显示*/
		if (maxLines != -1) {
			Layout layout = createWorkingLayout(workingText);
			int lines = layout.getLineCount();
			;
			Log.i(TAG, "行数>>>>>>>>>>>>>" + lines);
			if (lines > maxLines) {
				// 获取一行显示字符个数，然后截取字符串数
				workingText = fullText.substring(0,
						layout.getLineEnd(maxLines - 1)).trim()
						+ ELLIPSIS;
				Layout subStrLay = createWorkingLayout(workingText);

				int subLine = subStrLay.getLineCount();
				if (subLine > maxLines) {
					while (subLine > maxLines) {
						int lastSpace = workingText.length() - 1;
						Log.v(TAG, "lastSpace=" + lastSpace);
						if (lastSpace == -1) {
							break;
						}
						workingText = workingText.substring(0, lastSpace);
						subStrLay = createWorkingLayout(workingText + ELLIPSIS);
						subLine = subStrLay.getLineCount();
						Log.v(TAG, "lastSpace workingText=" + workingText);
					}
					workingText = workingText + ELLIPSIS;
				}
				ellipsized = true;
				isShowExpandBtn = true;
			}
		}
		if (!workingText.equals(getText())) {
			programmaticChange = true;
			try {
				setText(workingText);
				invalidate();
			} finally {
				programmaticChange = false;
			}
		}
		isStale = false;
		if (ellipsized != isEllipsized) {
			isEllipsized = ellipsized;
			ellipsizeListener
					.ellipsizeStateChanged(ellipsized, isShowExpandBtn);
		}
	}

	/**
	 * 返回一个指定显示字体的静态layout，以便获取行数信息
	 * 
	 * @param workingText 需显示的字体
	 * @return
	 */
	private Layout createWorkingLayout(String workingText) {
		// 参数依次为：字符串资源，画笔，layout的宽度，layout的样式，字体的大小，行间距
		return new StaticLayout(workingText, getPaint(), getWidth()
				- getPaddingLeft() - getPaddingRight(), Alignment.ALIGN_NORMAL,
				lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
	}

}

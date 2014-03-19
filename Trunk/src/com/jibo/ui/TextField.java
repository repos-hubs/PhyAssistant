package com.jibo.ui;

import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

import com.jibo.common.DeviceInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextField extends TextView {
	private Paint mPaint;
	private ArrayList<String> strList = new ArrayList<String>();// 内容行集
	private int screenWidth;// 内容所占宽度
	private int x;// 内容显示起始x坐标
	private int y = 30;// 显示起始y坐标
	private int mFontHeight;// 每行字体占用高度
	private int m_iTextHeight;// 内容高度
	private int lineCount;// 显示所需行数
	private WindowManager wm;
	private String mString;// 显示内容

	private int textSize;

	private boolean flag;// 适用于“不良反应”模块，flag = true 表示 内容显示后下方不需要空两行，默认最后空两行
	private boolean isChinese = true;// 内容是否为中文，默认true中文

	public TextField(Context context, AttributeSet attrs) {
		super(context, attrs);

		int resourceid;
		resourceid = attrs.getAttributeResourceValue(null, "labelText", 0);
		if (resourceid == 0) {
			mString = attrs.getAttributeValue(null, "labelText");
		} else {
			mString = getResources().getString(resourceid);
		}
	}

	public TextField(Context context, AttributeSet set, String a) {
		super(context, set);
		mString = a.trim();
		mString = mString.replaceAll("\\n\\s*", "\n");
		init(context);
	}

	public TextField(Context context, AttributeSet set, String a, boolean flag) {
		super(context, set);
		x = 20;
		mString = a.trim();
		this.flag = flag;
		mString = mString.replaceAll("\\n\\s*", "\n");
		init(context);
	}

	public TextField(Context context, AttributeSet set, String a, int textSize,
			boolean flag) {
		super(context, set);
		this.textSize = textSize;
		this.flag = flag;
		mString = a.trim();
		mString = mString.replaceAll("\\n\\s*", "\n");
		init(context);
	}

	public TextField(Context context, AttributeSet set, String a, boolean flag,
			int width) {
		super(context, set);
		mString = a.trim();
		mString = mString.replaceAll("\\n\\s*", "\n");
		//暂时不对英文进行排版
//		this.isChinese = isChinese(mString);
		this.flag = flag;
		this.screenWidth = width;
		init(context);
	}

	public void init(Context context) {
		StringBuilder sb = new StringBuilder(mString);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '\n' || c == '\r') {
				sb.insert(i + 1, "        ").toString();
			}
		}
//		if (context instanceof RelatedArticles) {

//		} else {
			mString = sb.insert(0, "        ").toString();
//		}
		initComponent(context);
		initString();
	}

	public void initComponent(Context context) {
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		m_iTextHeight = 2000;
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(40);

		// int pixel = Util.getResolution();
		screenWidth = screenWidth == 0 ? (int) (wm.getDefaultDisplay()
				.getWidth() - DeviceInfo.instance.getScale() * 20) - x
				: (int) (screenWidth - DeviceInfo.instance.getScale() * 20) - x;
		mPaint.setTextSize(textSize == 0 ? screenWidth / 16 : textSize);
		// 锯齿效果
		mPaint.setAntiAlias(true);

		FontMetrics fm = mPaint.getFontMetrics();// 默认字体属性
		mFontHeight = (int) Math.ceil(fm.descent - fm.top) + 4;// 获取字体高度
	}

	public void initString() {

		int istart = 0;
		char ch;
		int w = 0;

		if (isChinese) {
			for (int i = 0; i < mString.length(); i++) {
				ch = mString.charAt(i);
				float[] widths = new float[1];
				String srt = String.valueOf(ch);
				mPaint.getTextWidths(srt, widths);
				if (ch == '\n') {
					lineCount++;
					strList.add(mString.substring(istart, i));
					istart = i + 1;
					w = 0;
				} else {
					w += (int) (Math.ceil(widths[0]));
					if (w > screenWidth) {
						lineCount++;
						strList.add(mString.substring(istart, i));
						istart = i;
						i--;
						w = 0;
					} else {
						if (i == (mString.length() - 1)) {
							lineCount++;
							strList.add(mString.substring(istart,
									mString.length()));
						}
					}
				}
			}
		} else {
			for (int i = 0; i < mString.length(); i++) {
				ch = mString.charAt(i);
				float[] widths = new float[1];
				String srt = String.valueOf(ch);
				mPaint.getTextWidths(srt, widths);
				if (ch == '\n') {
					lineCount++;
					strList.add(mString.substring(istart, i));
					istart = i + 1;
					w = 0;
				} else {
					w += (int) (Math.ceil(widths[0]));
					if (w > screenWidth) {
						lineCount++;

						if (ch == ' ' || mString.charAt(i - 1) == ' ') {
							strList.add(mString.substring(istart, i));
							istart = i;
						} else if (mString.charAt(i - 2) == ' ') {
							strList.add(mString.substring(istart, i - 2));
							istart = i - 2;
						} else {
							if (p.matcher(mString.substring(istart, i - 1))
									.find())
								strList.add(mString.substring(istart, i - 1)
										+ "-");
							else
								strList.add(mString.substring(istart, i - 1));
							istart = i - 1;
						}

						i--;
						w = 0;
					} else {
						if (i == (mString.length() - 1)) {
							lineCount++;
							strList.add(mString.substring(istart,
									mString.length()));
						}
					}
				}
			}
		}

		int line = flag ? lineCount : lineCount + 2;// 默认空两行
		m_iTextHeight = line * mFontHeight + 10;
	}

	Pattern p = Pattern.compile("[a-zA-Z]");

	/**
	 * 检查字符是否是中文
	 * 
	 * @param c
	 * @return
	 */
	private boolean isChinese(String str) {
		String s = str.replaceAll(" ", "");
		int length = s.length();
		int count = 0;
		for (int i = 0; i < 100; i++)
			count += isChineseWithChar(s.charAt((int) (Math.random() * length)));
		if (count > 50)
			return true;
		return false;
	}

	private int isChineseWithChar(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return 1;
		}
		return 0;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int measuredHeight = measureHeight(m_iTextHeight);
		int measuredWidth = measureWidth(widthMeasureSpec);
		this.setMeasuredDimension(measuredWidth, measuredHeight);
		this.setLayoutParams(new LinearLayout.LayoutParams(measuredWidth,
				measuredHeight));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private int measureWidth(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		return result;
	}

	private int measureHeight(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = m_iTextHeight;

		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}

		return result;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.alpha(0));
		canvas.setViewport(screenWidth, screenWidth);
		for (int i = 0, j = 0; i < lineCount; i++, j++) {
			canvas.drawText((strList.get(i)), x, y + mFontHeight * j, mPaint);
		}
	}

	public void setText(CharSequence text, Context context) {
		x = 20;
		initComponent(context);
		mString = text.toString();
		mString = mString.replaceAll(" ", "");
		mString = mString.replaceAll("\t", "");
		StringBuilder sb = new StringBuilder(mString);
		for (int i = 0; i < mString.length(); i++) {
			char c = mString.charAt(i);
			if (c == '\n') {

			}
		}
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		initString();
	}
}

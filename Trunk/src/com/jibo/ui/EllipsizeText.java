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
 * ������ʾ�ĳ��ȳ������õ���󳤶�ʱ����ʾʡ�Ժ�׺
 * 
 * @author simon
 *
 */

public class EllipsizeText extends TextView {

	private static final String TAG = "simon";
	
	/**ʡ�Ժ�׺*/
	private static final String ELLIPSIS = "...";
	/***����Ĵ�С*/
	private float lineSpacingMultiplier = 1.0f;
	/**�м��*/
	private float lineAdditionalVerticalPadding = 0.0f;
	/** ��ǰ�Ƿ�Ϊ����״̬(��ʾʡ�Ժ�״̬) */
	private boolean isEllipsized;
	/** �Ƿ���Ҫ�ػ� */
	private boolean isStale;
	/** setText()ʱ��סTextChange�������� */
	private boolean programmaticChange;
	/** �������� */
	private String fullText;
	/** ���õ���������ĳ�ʼֵ */
	private int originalMaxLines;
	/** ��ǰTextView��������� */
	private int maxLines = -1;
	/** ��ǰTextView�Ƿ������չ�������֣����ǵ�ǰtextView�����Ƿ񳬹����������δ������������������չ��ť */
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

	// �Ƿ������ֹ����ʱ����ʾʡ�Է���
	public boolean isEllipsized() {
		return isEllipsized;
	}

	//�����ı���ʾ�������
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
	 * �ı���չ<->����״̬
	 * 
	 * @param ellipsized
	 */
	public void changeShowText(boolean ellipsized) {
		//��ǰΪ����״̬ʱ�������ťȫ����ʾ������maxLinesԽ��Խ�ã���������Ϊ1000����֤����ȫ��������
		super.setMaxLines(ellipsized ? 1000 : originalMaxLines);
		this.maxLines = ellipsized ? -1 : originalMaxLines;
		isStale = true;
		invalidate();
	}

	/**
	 * ������ʾ�ı�
	 */
	private void resetText() {
//		setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
//		TextPaint tp = getPaint();
//		tp.setFakeBoldText(true);
		int maxLines = getMaxLines();
		String workingText = fullText;
		Log.v(TAG, "workingText=" + workingText);
		boolean ellipsized = false;
		/**��ǰmaxLines==-1ʱ����Ҫȫ����ʾ*/
		if (maxLines != -1) {
			Layout layout = createWorkingLayout(workingText);
			int lines = layout.getLineCount();
			;
			Log.i(TAG, "����>>>>>>>>>>>>>" + lines);
			if (lines > maxLines) {
				// ��ȡһ����ʾ�ַ�������Ȼ���ȡ�ַ�����
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
	 * ����һ��ָ����ʾ����ľ�̬layout���Ա��ȡ������Ϣ
	 * 
	 * @param workingText ����ʾ������
	 * @return
	 */
	private Layout createWorkingLayout(String workingText) {
		// ��������Ϊ���ַ�����Դ�����ʣ�layout�Ŀ�ȣ�layout����ʽ������Ĵ�С���м��
		return new StaticLayout(workingText, getPaint(), getWidth()
				- getPaddingLeft() - getPaddingRight(), Alignment.ALIGN_NORMAL,
				lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
	}

}

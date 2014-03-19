/*     */package com.jibo.v4.accessibility;

/*     */
/*     */import android.view.accessibility.AccessibilityEvent;

/*     */
/*     */public class AccessibilityEventCompat
/*     */{
	/* 53 */private static final AccessibilityEventVersionImpl IMPL = new AccessibilityEventStubImpl();
	/*     */public static final int TYPE_VIEW_HOVER_ENTER = 128;
	/*     */public static final int TYPE_VIEW_HOVER_EXIT = 256;
	/*     */public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
	/*     */public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
	/*     */public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;
	/*     */public static final int TYPE_VIEW_SCROLLED = 4096;
	/*     */public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;

	/*     */
	/*     */public static int getRecordCount(AccessibilityEvent event)
	/*     */{
		/* 105 */return IMPL.getRecordCount(event);
		/*     */}

	/*     */
	/*     */public static void appendRecord(AccessibilityEvent event,
			AccessibilityRecordCompat record)
	/*     */{
		/* 117 */IMPL.appendRecord(event, record.getImpl());
		/*     */}

	/*     */
	/*     */public static AccessibilityRecordCompat getRecord(
			AccessibilityEvent event, int index)
	/*     */{
		/* 127 */return new AccessibilityRecordCompat(IMPL.getRecord(event,
				index));
		/*     */}

	/*     */
	/*     */static class AccessibilityEventStubImpl
	/*     */implements AccessibilityEventCompat.AccessibilityEventVersionImpl
	/*     */{
		/*     */public void appendRecord(AccessibilityEvent event, Object record)
		/*     */{
			/*     */}

		/*     */
		/*     */public Object getRecord(AccessibilityEvent event, int index)
		/*     */{
			/* 39 */return null;
			/*     */}

		/*     */
		/*     */public int getRecordCount(AccessibilityEvent event) {
			/* 43 */return 0;
			/*     */}
		/*     */
	}

	/*     */
	/*     */static abstract interface AccessibilityEventVersionImpl
	/*     */{
		/*     */public abstract int getRecordCount(
				AccessibilityEvent paramAccessibilityEvent);

		/*     */
		/*     */public abstract void appendRecord(
				AccessibilityEvent paramAccessibilityEvent, Object paramObject);

		/*     */
		/*     */public abstract Object getRecord(
				AccessibilityEvent paramAccessibilityEvent, int paramInt);
		/*     */
	}
	/*     */
}

/*
 * Location: d:\我的文档\桌面\lewa.os.jar Qualified Name:
 * android.support.lewa.view.accessibility.AccessibilityEventCompat JD-Core
 * Version: 0.6.0
 */
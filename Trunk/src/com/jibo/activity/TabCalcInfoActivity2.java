package com.jibo.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.dao.FormulaDao;
import com.jibo.dao.UnitValuesDao;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.entity.Formula;
import com.jibo.entity.UnitValues;
import com.jibo.entity.Units;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;

public class TabCalcInfoActivity2 extends BaseSearchActivity implements
		OnClickListener {
	private List<Units> calcList;

	/***
	 * 对应units表中的uType字段，表示这个unit属性以何种UI方式展现
	 */
	public static final int U_PANEL_TITLE = 1;
	public static final int U_RG_HORIZONTAL = 2;
	public static final int U_RG_VERTICAL = 3;
	public static final int U_CHECKBOX = 4;
	public static final int U_SPINNER = 5;
	public static final int U_EDITTEXT = 6;
	public static final int U_TEXTFIELD = 7;
	public static final int U_NO_RESULT = 8;
	public static final int U_TEXT_RESULT = 9;

	public final int f_input_width = 100;

	private FavoritDataAdapter tabAdpt;
	private Button collectBtn;

	private LinearLayout lltBody;
	private Button btnConfirm;
	private Button btnCancel;
	private int width;
	private int text_title_size = 17;
	private int result_title_size = 20;
	private HashMap<Integer, LinearLayout> lltMap;
	private HashMap<Integer, View> viewMap;
	private HashMap<Integer, View> unitMap;
	private HashMap<Integer, View> defaultMap;
	private LayoutParams mainLP;
	private String tabCalcName;
	private int calcId;

	private ArrayList<LinearLayout> noResultList;
	private ArrayList<LinearLayout> textResultList;
	public ArrayList<EditText> edtArray = new ArrayList<EditText>();
	public ArrayList<TextView> txtArray = new ArrayList<TextView>();
	public ArrayList<CheckBox> boxArray = new ArrayList<CheckBox>();
	private EditText keyboardLoad;

	private int count;
	private final int isPosition = 0;
	private final int isNo = 1;
	private final int isSymbol = 2;
	private View keyboardView;
	private RelativeLayout rltMainView;
	private int maxOrder;
	private ScrollView sv;
	
	//tips特性显示的单选按钮，clone时注意check属性
	public RadioGroup radioGroup;

	private HashMap<Integer, Units> orderListMap = new HashMap<Integer, Units>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = LayoutInflater.from(this);
		keyboardView = inflater.inflate(R.layout.no_keyboard, null);
		rltMainView = (RelativeLayout) inflater.inflate(R.layout.tab_calc_info,
				null);
		setContentView(rltMainView);
		super.onCreate(savedInstanceState);
		inits();

		boolean splitIsTrue = false;// 是否有分割线
		int length = 0;
		if(calcList != null)
		length = calcList.size();
		orderListMap = new HashMap<Integer, Units>();
		for (int i = 0; i < length; i++) {
			Units en = calcList.get(i);
			p(i + "   id   " + en.getOrder());
			//生成视图
			LinearLayout view = generateBodyView(en);
			view.setTag(en);
			orderListMap.put(en.getOrder(), en);
			if (en.getType() == 7) {
				mainLP = new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
				mainLP.bottomMargin = 10;
			} else {
				mainLP = new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
				mainLP.bottomMargin = 0;
				boolean shouldAddSplit = false;
				if (en.getType() == 1) {
					if (i + 1 < length) {
						Units next = calcList.get(i + 1);
						if (next.getType() == 8 || next.getType() == 9) {
							shouldAddSplit = true;
						}
					}
				} else if (en.getType() == 8 || en.getType() == 9) {
					shouldAddSplit = true;
				}
				if (shouldAddSplit) {
					if (!splitIsTrue) {
						View splitView = new View(this);
						splitView.setBackgroundColor(Color.GRAY);
						LayoutParams splitParams = new LayoutParams(
								LayoutParams.FILL_PARENT, 2);
						splitParams.topMargin = 25;
						lltBody.addView(splitView, splitParams);
						splitIsTrue = true;
					}
				}
			}
			lltBody.addView(view, mainLP);
			view.setId(en.getOrder());
			lltMap.put(en.getOrder(), view);
		}

		if (tabAdpt.selectTabCalc(tabCalcName,
				SharedPreferencesMgr.getUserName()) > 0) {
			collectBtn.setBackgroundResource(R.drawable.btnunchg);
		}

		// resetInputItem();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.imgbtn_submit:
			computeResult();
			scrollToSpecifiedPosition("submit");
			break;
		case R.id.imgbtn_cancel:
			scrollToSpecifiedPosition("clear");
			clearValue();
			break;
		case R.id.favoritBtn:
			if (tabAdpt.selectTabCalc(tabCalcName,
					SharedPreferencesMgr.getUserName()) > 0) {
				if (tabAdpt.delTabCalc(tabCalcName,
						SharedPreferencesMgr.getUserName())) {
					Toast toast = Toast.makeText(this,
							this.getString(R.string.cancelFav),
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
					collectBtn.setBackgroundResource(R.drawable.btnchg);
				}

			} else {
				Toast toast = Toast.makeText(this,
						this.getString(R.string.favorite), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 220);
				toast.show();// tabCalcName
				tabAdpt.insertTabCalc(tabCalcName, tabCalcName,
						SharedPreferencesMgr.getUserName());
				collectBtn.setBackgroundResource(R.drawable.btnunchg);
			}
			break;
		}
	}

	public void clearValue() {
		for (EditText edtt : edtArray) {
			edtt.setText("");
		}
		for (TextView txt : txtArray) {
			txt.setText("");
		}
		for (CheckBox box : boxArray) {
			box.setChecked(false);
		}
	}

	public class InputTouchListner implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof EditText && !(v instanceof RadioButton)) {
				EditText view = (EditText) v;
				addKeyboard(rltMainView, TabCalcInfoActivity2.this, view,
						keyboardView);
			}

			return true;
		}
	}

	public void computeResult() {
		for (int i = 0; i < noResultList.size(); i++) {
			LinearLayout lltResult = noResultList.get(i);
			Units en = (Units) lltResult.getTag();
			List<UnitValues> resultList = daoSession
					.getUnitValuesDao()
					.queryBuilder()
					.where(UnitValuesDao.Properties.UnitId.eq(en.getId()
							.toString()), UnitValuesDao.Properties.Type.eq("5"))
					.list();
			String result = null;
			for (int j = 0; j < resultList.size(); j++) {
				String strArr[] = resultList.get(j).getKey().split(",");
				Stack<String> keyStack = new Stack<String>();
				for (int m = strArr.length - 1; m >= 0; m--) {
					keyStack.push(strArr[m]);
				}
				if (calculateKeyStack(keyStack)) {
					Stack<String> valueStack = new Stack<String>();
					String valueStr = resultList.get(j).getValueCn();
					String stringArr[] = valueStr.split(",");
					for (int m = stringArr.length - 1; m >= 0; m--) {
						valueStack.push(stringArr[m]);
					}
					result = calculateNoStack(valueStack);
					if (result != null) {
						int max = en.getMax() == null ? 0 : en.getMax();
						float noResult = -1000;
						boolean valueIsNo = false;
						try {
							noResult = Float.parseFloat(result);
							valueIsNo = true;
						} catch (Exception e) {

						}
						if (max != 0 && valueIsNo && noResult > max) {
							((TextView) viewMap.get(en.getOrder()))
									.setText(String.valueOf(max));
						} else {
							((TextView) viewMap.get(en.getOrder()))
									.setText(result);
						}

						break;
					} else {
						return;
					}
				} else {
					List<UnitValues> defaultList = daoSession
							.getUnitValuesDao()
							.queryBuilder()
							.where(UnitValuesDao.Properties.UnitId.eq(en
									.getId().toString()),
									UnitValuesDao.Properties.Type.eq("3"))
							.list();
					if (null != defaultList && defaultList.size() > 0) {
						((TextView) viewMap.get(en.getOrder()))
								.setText(defaultList.get(i).getValueCn());
					}
				}

			}
		}

		for (int i = 0; i < textResultList.size(); i++) {
			LinearLayout lltResult = textResultList.get(i);
			Units en = (Units) lltResult.getTag();
			List<UnitValues> resultList = daoSession
					.getUnitValuesDao()
					.queryBuilder()
					.where(UnitValuesDao.Properties.UnitId.eq(en.getId()
							.toString()), UnitValuesDao.Properties.Type.eq("1"))
					.list();
			for (int j = 0; j < resultList.size(); j++) {
				String strArr[] = resultList.get(j).getKey().split(",");
				Stack<String> textKeyStack = new Stack<String>();
				for (int m = strArr.length - 1; m >= 0; m--) {
					textKeyStack.push(strArr[m]);
				}
				if (calculateKeyStack(textKeyStack)) {
					String valueStr = resultList.get(j).getValueCn();
					((TextView) viewMap.get(en.getOrder())).setText(valueStr);
					break;
				} else {
					List<UnitValues> defaultList = daoSession
							.getUnitValuesDao()
							.queryBuilder()
							.where(UnitValuesDao.Properties.UnitId.eq(en
									.getId().toString()),
									UnitValuesDao.Properties.Type.eq("3"))
							.list();
					if (null != defaultList && defaultList.size() > 0) {
						((TextView) viewMap.get(en.getOrder()))
								.setText(defaultList.get(i).getValueCn());
					}
				}
			}

		}
	}

	private void resetInputItem() {
		for (Entry<Integer, View> en : unitMap.entrySet()) {
			LinearLayout lltItem = lltMap.get(en.getKey());
			Units entity = (Units) lltItem.getTag();
			((TextView) en.getValue()).setText(getUnitValue(entity));
		}
		for (Entry<Integer, View> en : defaultMap.entrySet()) {
			LinearLayout lltItem = lltMap.get(en.getKey());
			Units entity = (Units) lltItem.getTag();
			if (!"".equals(getDefaultValue(entity))) {
				((EditText) en.getValue()).setText(getDefaultValue(entity));
			}

		}

		for (int i = 0; i < noResultList.size(); i++) {
			LinearLayout lltResult = noResultList.get(i);
			lltResult.setVisibility(LinearLayout.VISIBLE);
			Units en = (Units) lltResult.getTag();
			List<UnitValues> resultList = daoSession
					.getUnitValuesDao()
					.queryBuilder()
					.where(UnitValuesDao.Properties.UnitId.eq(en.getId()
							.toString()), UnitValuesDao.Properties.Type.eq("5"))
					.list();
			boolean haveFormula = false;
			for (int j = 0; j < resultList.size(); j++) {
				String strArr[] = resultList.get(j).getKey().split(",");
				Stack<String> keyStack = new Stack<String>();
				for (int m = strArr.length - 1; m >= 0; m--) {
					keyStack.push(strArr[m]);
				}
				if (calculateKeyStack(keyStack)) {
					haveFormula = true;
					break;
				}
			}
			if (!haveFormula) {
				lltResult.setVisibility(LinearLayout.GONE);
			}
		}
	}

	public double formatValue(double a) {
		String str = Double.toString(a);
		String pStr = str.substring(str.indexOf(".") + 1);
		int noPosition = 0;
		for (int i = 0; i < pStr.length(); i++) {
			char c = pStr.charAt(i);
			if (c != '0') {
				noPosition = i;
				break;
			}
		}
		double result = div(a, 1, noPosition + 2);
		return result;
	}

	double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public int judgeString(String str) {
		int result = -1;
		Pattern p = Pattern
				.compile("([-]?\\d+)|([-]?\\d+?\\.\\d+(E[-]?\\d*)?)");
		Matcher matcher = p.matcher(str);
		if (str.startsWith("(")) {
			result = isPosition;
		} else if (matcher.matches()) {
			result = isNo;
		} else {
			result = isSymbol;
		}
		return result;
	}

	public String calculateNoStack(Stack<String> stack) {
		ArrayList<String> strList = new ArrayList<String>();
		Stack<String> finalStack = new Stack<String>();
		String str = "";
		String finalResult = "";
		while (!stack.isEmpty()) {
			String popStr = stack.pop().toString();

			switch (judgeString(popStr)) {
			case isPosition:
				str = getViewValue(Integer.parseInt(popStr.substring(1)));
				if ("".equals(str) || null == str) {
					Toast.makeText(this, getString(R.string.input_error),
							Toast.LENGTH_SHORT).show();
					return null;
				}
				strList.add(str);
				break;
			case isNo:
				str = popStr;
				if ("".equals(str))
					str = "0";
				strList.add(str);
				finalResult = str;
				break;
			case isSymbol:
				float result = 0;
				String value1 = "";
				String value2 = "";

				if (strList.size() >= 2) {
					value1 = strList.get(strList.size() - 2);
					value2 = strList.get(strList.size() - 1);
				} else if (strList.size() == 1) {
					value2 = strList.get(strList.size() - 1);
				}

				if ("-".equals(popStr)) {
					result = Float.parseFloat(value1)
							- Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}

				} else if ("+".equals(popStr)) {
					result = Float.parseFloat(value1)
							+ Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("*".equals(popStr)) {
					result = Float.parseFloat(value1)
							* Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));

					}
				} else if ("/".equals(popStr)) {
					result = Float.parseFloat(value1)
							/ Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("sqrt".equals(popStr)) {
					result = (float) Math.sqrt(Double.parseDouble(value2));
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("**".equals(popStr)) {
					result = (float) Math.pow(Float.parseFloat(value2), 2);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("ln".equals(popStr)) {
					result = (float) Math.log(Double.parseDouble(value2));
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("pow".equals(popStr)) {
					result = (float) formatValue(Math.pow(
							Float.parseFloat(value1), Float.parseFloat(value2)));
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("--".equals(popStr)) {
					for (int j = strList.size() - 1; j >= 0; j--) {
						finalStack.push(strList.get(j));
					}
					finalStack.push("---");
				}
				strList.clear();

				break;
			}
		}
		finalResult = formatValue(Double.parseDouble(finalResult)) + "";
		finalStack.push(finalResult);
		StringBuffer sb = new StringBuffer();
		while (!finalStack.isEmpty()) {
			sb.append(finalStack.pop() + " ");
		}
		String result[] = sb.toString().split(" ");
		finalResult = "";
		for (int i = result.length - 1; i >= 0; i--) {
			finalResult = finalResult + result[i] + " ";
		}
		return finalResult;
	}

	public boolean calculateKeyStack(Stack<String> stack) {
		ArrayList<String> strList = new ArrayList<String>();
		Stack<Object> finalStack = new Stack<Object>();
		String str = "";
		while (!stack.isEmpty()) {
			String popStr = stack.pop().toString();

			switch (judgeString(popStr)) {
			case isPosition:
				str = getViewValue(Integer.parseInt(popStr.substring(1)));
				if ("".equals(str))
					str = "0";
				strList.add(str);
				break;
			case isNo:
				str = popStr;
				if ("".equals(str))
					str = "0";
				strList.add(str);
				break;
			case isSymbol:
				float result = 0;
				String value1 = "";
				String value2 = "";

				if (strList.size() >= 2) {
					value1 = strList.get(strList.size() - 2);
					value2 = strList.get(strList.size() - 1);
				}

				if ("-".equals(popStr)) {
					result = Float.parseFloat(value1)
							- Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}

				} else if ("+".equals(popStr)) {
					result = Float.parseFloat(value1)
							+ Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("*".equals(popStr)) {
					result = Float.parseFloat(value1)
							* Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("/".equals(popStr)) {
					result = Float.parseFloat(value1)
							/ Float.parseFloat(value2);
					strList.remove(strList.size() - 1);
					strList.remove(strList.size() - 1);
					strList.add(String.valueOf(result));
					for (int j = strList.size() - 1; j >= 0; j--) {
						stack.push(strList.get(j));
					}
				} else if ("<".equals(popStr)) {
					finalStack.push(Float.parseFloat(value1) < Float
							.parseFloat(value2));
				} else if ("<=".equals(popStr)) {
					finalStack.push(Float.parseFloat(value1) <= Float
							.parseFloat(value2));
				} else if (">".equals(popStr)) {
					finalStack.push(Float.parseFloat(value1) > Float
							.parseFloat(value2));
				} else if (">=".equals(popStr)) {
					finalStack.push(Float.parseFloat(value1) >= Float
							.parseFloat(value2));
				} else if ("=".equals(popStr)) {
					finalStack.push(Float.parseFloat(value1) == Float
							.parseFloat(value2));
				} else if ("&".equals(popStr)) {
					finalStack.push("&");
				}
				strList.clear();

				break;
			}
		}
		boolean obj1;
		Object obj2;
		boolean obj3;
		while (!finalStack.isEmpty() && finalStack.size() > 2) {
			obj1 = (Boolean) finalStack.pop();
			obj2 = finalStack.pop();
			obj3 = (Boolean) finalStack.pop();
			if ("&".equals(obj2.toString())) {
				obj1 = obj1 && obj3;
			} else if ("|".equals(obj2.toString())) {
				obj1 = obj1 && obj3;
			}
			finalStack.push(obj1);
		}

		boolean result = (Boolean) finalStack.pop();
		return result;
	}

	public String getViewValue(int order) {
		String result = "";
		switch (orderListMap.get(order).getType()) {
		case U_PANEL_TITLE:
			break;
		case U_RG_HORIZONTAL:
			result = viewMap.get(order).getTag().toString();
			break;
		case U_RG_VERTICAL:
			result = viewMap.get(order).getTag().toString();
			break;
		case U_CHECKBOX:
			result = ((CheckBox) viewMap.get(order)).getTag().toString();
			break;
		case U_SPINNER:
			result = viewMap.get(order).getTag().toString();
			break;
		case U_EDITTEXT:
			EditText edt = (EditText) viewMap.get(order);
			result = edt.getText().toString();
			break;
		case U_TEXTFIELD:
			break;
		case U_NO_RESULT:
			TextView txt = (TextView) viewMap.get(order);
			result = txt.getText().toString();
			break;
		case U_TEXT_RESULT:
			break;
		}
		return result;
	}

	public class RGCheckedListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			RadioButton rb = (RadioButton) group.findViewById(checkedId);
			group.setTag(rb.getTag().toString());

			resetInputItem();
			computeResult();
		}
	}

	private class CBCheckedListener implements
			android.widget.CompoundButton.OnCheckedChangeListener {
		private Units en;

		public CBCheckedListener(Units en) {
			this.en = en;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			List<UnitValues> valueList = daoSession
					.getUnitValuesDao()
					.queryBuilder()
					.where(UnitValuesDao.Properties.UnitId.eq(en.getId()
							.toString()), UnitValuesDao.Properties.Type.eq("2"))
					.list();

			if (isChecked) {
				viewMap.get(en.getOrder())
						.setTag(valueList.get(1).getValueCn());
			} else {
				viewMap.get(en.getOrder())
						.setTag(valueList.get(0).getValueCn());
			}
		}
	}

	private class SpinnerSelectListener implements OnItemSelectedListener {
		private Units en = null;

		public SpinnerSelectListener(Units en) {
			this.en = en;
		}

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView txt = (TextView) arg1;
			viewMap.get(en.getOrder()).setTag(
					getSpinerValue(txt.getText().toString(), en.getId()));
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	private String getSpinerValue(String text, int unitId) {
		List<UnitValues> valueList1 = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(unitId),
						UnitValuesDao.Properties.Type.eq("1")).list();
		List<UnitValues> valueList2 = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(unitId),
						UnitValuesDao.Properties.Type.eq("2")).list();
		for (int i = 0; i < valueList1.size(); i++) {
			if (text.equals(valueList1.get(i).getValueCn())) {
				return valueList2.get(i).getValueCn();
			}
		}
		return "";
	}

	private LinearLayout generateBodyView(Units en) {
		LinearLayout view = null;
		switch (en.getType()) {
		case U_PANEL_TITLE:
			view = createPanelTitleItem(en);
			break;
		case U_RG_HORIZONTAL:
			view = createHorizontalRG(en);
			break;
		case U_RG_VERTICAL:
			view = createVerticalRG(en);
			break;
		case U_CHECKBOX:
			view = createCheckBox(en);
			break;
		case U_SPINNER:
			view = createSpinnerItem(en);
			break;
		case U_EDITTEXT:
			view = createInputItem(en);
			break;
		case U_TEXTFIELD:
			view = createTextField(en);
			break;
		case U_NO_RESULT:
			view = createNOResultItem(en);
			break;
		case U_TEXT_RESULT:
			view = createTextResult(en);
			break;
		}

		return view;
	}

	private LinearLayout createNOResultItem(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.VERTICAL);
		lltItem.setGravity(Gravity.CENTER);

		LinearLayout lltTitleItem = new LinearLayout(this);
		lltTitleItem.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout lltValueItem = new LinearLayout(this);
		lltValueItem.setOrientation(LinearLayout.HORIZONTAL);
		lltValueItem.setGravity(Gravity.CENTER);

		TextView txtTitle = new TextView(this);
		txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
		txtTitle.setText(en.getNameCn());
		txtTitle.setTextColor(Color.BLACK);
		txtTitle.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
		lltTitleItem.addView(txtTitle);

		TextView txtValue = new TextView(this);
		TextView txtUnit = new TextView(this);
		txtUnit.setTextColor(Color.BLACK);
		txtValue.setTextColor(Color.BLACK);
		txtValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		txtUnit.setText(getUnitValue(en));
		txtUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		unitMap.put(en.getOrder(), txtUnit);

		lltValueItem.addView(txtValue);
		lltValueItem.addView(txtUnit);

		lltItem.addView(lltTitleItem);
		lltItem.addView(lltValueItem);

		txtArray.add(txtValue);
		viewMap.put(en.getOrder(), txtValue);
		noResultList.add(lltItem);
		return lltItem;
	}

	private LinearLayout createTextField(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setBackgroundResource(R.drawable.academic_profile_content);
		lltItem.setOrientation(LinearLayout.VERTICAL);

		LinearLayout lltTitleItem = new LinearLayout(this);
		lltTitleItem.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout lltValueItem = new LinearLayout(this);
		lltValueItem.setOrientation(LinearLayout.HORIZONTAL);

		TextView txtTitle = new TextView(this);
		txtTitle.setText(en.getNameCn());
		txtTitle.setTextColor(Color.BLACK);
		txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txtTitle.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
		txtTitle.getPaint().setFakeBoldText(true);
		lltTitleItem.addView(txtTitle);

		TextView txtValue = new TextView(this);
		TextView txtUnit = new TextView(this);
		txtValue.setTextColor(Color.BLACK);
		txtUnit.setTextColor(Color.BLACK);
		txtValue.setText(daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("1")).list().get(0)
				.getValueCn());
		lltValueItem.addView(txtValue);
		lltValueItem.addView(txtUnit);

		lltItem.addView(lltTitleItem);
		lltItem.addView(lltValueItem);
		return lltItem;
	}

	private LinearLayout createInputItem(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.HORIZONTAL);
		lltItem.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams lpTitle = new LayoutParams(width / 3,
				LayoutParams.WRAP_CONTENT);
		LayoutParams lpEdt = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 1);
		LayoutParams lpUnit = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);

		TextView txtTitle = new TextView(this);
		txtTitle.setGravity(Gravity.CENTER_VERTICAL);
		txtTitle.setText(en.getNameCn().replaceAll("\\|", "\n"));
		txtTitle.setTextSize(text_title_size);
		txtTitle.setTextColor(Color.BLACK);
		EditText edtInput = new EditText(this);
		edtInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		edtInput.setText(getDefaultValue(en));
		edtInput.setOnTouchListener(new InputTouchListner());
		if (en.getOrder() == maxOrder) {
			edtInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
		}
		TextView txtUnit = new TextView(this);
		txtUnit.setGravity(Gravity.CENTER_VERTICAL);
		txtUnit.setTextColor(Color.BLACK);
		txtUnit.setText(getUnitValue(en));
		txtUnit.setMinWidth(width / 6);

		lltItem.addView(txtTitle, lpTitle);
		lltItem.addView(edtInput, lpEdt);
		lltItem.addView(txtUnit, lpUnit);

		defaultMap.put(en.getOrder(), edtInput);
		unitMap.put(en.getOrder(), txtUnit);
		viewMap.put(en.getOrder(), edtInput);
		return lltItem;
	}

	private LinearLayout createPanelTitleItem(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.HORIZONTAL);
		TextView txt = new TextView(this);
		txt.setTextSize(result_title_size);
		txt.setText(en.getNameCn());
		txt.setTextColor(Color.parseColor("#003D79"));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		lltItem.addView(txt);
		viewMap.put(en.getOrder(), txt);
		return lltItem;
	}

	private LinearLayout createSpinnerItem(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.HORIZONTAL);
		lltItem.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams lpTitle = new LayoutParams(width / 3,
				LayoutParams.WRAP_CONTENT);
		LayoutParams lpSp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 1);

		TextView txt = new TextView(this);
		txt.setTextSize(text_title_size);
		txt.setText(en.getNameCn());
		txt.setTextColor(Color.BLACK);
		Spinner sp = new Spinner(this);
		ArrayAdapter<UnitValues> adapter = new ArrayAdapter<UnitValues>(this,
				android.R.layout.simple_spinner_item, daoSession
						.getUnitValuesDao()
						.queryBuilder()
						.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
								UnitValuesDao.Properties.Type.eq("1")).list());
		sp.setOnItemSelectedListener(new SpinnerSelectListener(en));
		adapter.setDropDownViewResource(R.layout.list_item_text);
		sp.setAdapter(adapter);
		lltItem.addView(txt, lpTitle);
		lltItem.addView(sp, lpSp);

		viewMap.put(en.getOrder(), sp);
		return lltItem;
	}

	private LinearLayout createCheckBox(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 1);
		LayoutParams lp1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp1.rightMargin = 20;
		TextView txt = new TextView(this);
		String[] kk = en.getNameCn().split("\\|");
		String value = "";
		for (String s : kk) {
			value += s + "\n";
		}
		txt.setText(value);
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_title_size);
		txt.setTextColor(Color.BLACK);

		CheckBox cb = new CheckBox(this);
		cb.setTextColor(Color.BLACK);
		cb.setOnCheckedChangeListener(new CBCheckedListener(en));
		cb.setTag(daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("2")).list().get(0)
				.getValueCn());

		viewMap.put(en.getOrder(), cb);
		boxArray.add(cb);
		lltItem.addView(txt, lp);
		lltItem.addView(cb, lp1);

		return lltItem;
	}

	private LinearLayout createTextResult(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		lltItem.setOrientation(LinearLayout.VERTICAL);
		TextView txt = new TextView(this);
		txt.setTextColor(Color.BLACK);
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		lltItem.addView(txt, params);

		viewMap.put(en.getOrder(), txt);
		txtArray.add(txt);
		textResultList.add(lltItem);
		return lltItem;
	}

	private LinearLayout createHorizontalRG(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.HORIZONTAL);
		lltItem.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams lpTitle = new LayoutParams(width / 3,
				LayoutParams.WRAP_CONTENT);
		if ("IOF骨质疏松症风险一分钟自测".equals(tabCalcName)) {
			lpTitle = new LayoutParams(width / 2, LayoutParams.WRAP_CONTENT);
		}
		List<UnitValues> strContentList = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("1")).list();
		List<UnitValues> strValueList = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("2")).list();
		RadioGroup rg = new RadioGroup(this);
		rg.setOnCheckedChangeListener(new RGCheckedListener());
		rg.setOrientation(LinearLayout.HORIZONTAL);
		
		for (int i = 0; i < strContentList.size(); i++) {
			RadioButton rb = new RadioButton(this);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.rightMargin = 10;
			lp.leftMargin = 10;
			rb.setText(strContentList.get(i).toString());
			rb.setTag(strValueList.get(i).toString());
			rb.setTextColor(Color.BLACK);
			rg.addView(rb, lp);
		}
		((RadioButton) rg.getChildAt(0)).setChecked(true);
		if(((RadioButton)rg.getChildAt(0)).getText().toString().trim().equals("SI")){
			radioGroup = rg;
		}

		TextView txtTitle = new TextView(this);
		txtTitle.setText(en.getNameCn());
		txtTitle.setTextSize(text_title_size);
		txtTitle.setTextColor(Color.BLACK);

		lltItem.addView(txtTitle, lpTitle);
		lltItem.addView(rg);
		viewMap.put(en.getOrder(), rg);
		return lltItem;
	}

	private LinearLayout createVerticalRG(Units en) {
		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOrientation(LinearLayout.VERTICAL);
		lltItem.setGravity(Gravity.CENTER_VERTICAL);
		List<UnitValues> strContentList = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("1")).list();
		List<UnitValues> strValueList = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("2")).list();
		RadioGroup rg = new RadioGroup(this);
		rg.setOnCheckedChangeListener(new RGCheckedListener());
		rg.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < strContentList.size(); i++) {
			RadioButton rb = new RadioButton(this);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.rightMargin = 10;
			lp.leftMargin = 10;
			rb.setText(strContentList.get(i).toString());
			rb.setTag(strValueList.get(i).toString());
			rb.setTextColor(Color.BLACK);
			rg.addView(rb, lp);
		}
		((RadioButton) rg.getChildAt(0)).setChecked(true);

		TextView txtTitle = new TextView(this);
		txtTitle.setText(en.getNameCn());
		txtTitle.setTextSize(text_title_size);
		txtTitle.setTextColor(Color.BLACK);
		txtTitle.setBackgroundResource(R.drawable.calc_tag);
		txtTitle.setGravity(Gravity.CENTER);

		lltItem.addView(txtTitle, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		lltItem.addView(rg);
		viewMap.put(en.getOrder(), rg);
		return lltItem;
	}

	public String getDefaultValue(Units en) {
		String result = "";
		List<UnitValues> unitsList = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("3")).list();
		for (int j = 0; j < unitsList.size(); j++) {
			String strArr[] = unitsList.get(j).getKey().split(",");
			Stack<String> keyStack = new Stack<String>();
			for (int m = strArr.length - 1; m >= 0; m--) {
				keyStack.push(strArr[m]);
			}
			if (calculateKeyStack(keyStack)) {
				result = unitsList.get(j).toString();
			}
		}

		return result;
	}

	public String getUnitValue(Units en) {
		String result = "";
		List<UnitValues> unitsList = daoSession
				.getUnitValuesDao()
				.queryBuilder()
				.where(UnitValuesDao.Properties.UnitId.eq(en.getId()),
						UnitValuesDao.Properties.Type.eq("4")).list();
		for (int j = 0; j < unitsList.size(); j++) {
			String strArr[] = unitsList.get(j).getKey().split(",");
			Stack<String> keyStack = new Stack<String>();
			for (int m = strArr.length - 1; m >= 0; m--) {
				keyStack.push(strArr[m]);
			}
			if (calculateKeyStack(keyStack)) {
				result = unitsList.get(j).toString();
			}
		}

		return result;
	}


	public void inits() {
		tabAdpt = new FavoritDataAdapter(this);
		lltMap = new HashMap<Integer, LinearLayout>();
		viewMap = new HashMap<Integer, View>();
		unitMap = new HashMap<Integer, View>();
		defaultMap = new HashMap<Integer, View>();
		noResultList = new ArrayList<LinearLayout>();
		textResultList = new ArrayList<LinearLayout>();
		lltBody = (LinearLayout) findViewById(R.id.llt_body);
		TextView txtCalcName = (TextView) findViewById(R.id.calc_name);
		btnConfirm = (Button) findViewById(R.id.imgbtn_submit);
		btnCancel = (Button) findViewById(R.id.imgbtn_cancel);
		sv = (ScrollView) findViewById(R.id.sv_body);
		TextView txt = (TextView) findViewById(R.id.txt_header_title);
		collectBtn = (Button) findViewById(R.id.favoritBtn);

		txt.setText(getString(R.string.calculator));
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		collectBtn.setOnClickListener(this);
		mSearchEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				removeKeyboard(rltMainView);
				return false;
			}
		});
		width = getWindowManager().getDefaultDisplay().getWidth();

		Formula entity = null;
		String name = getIntent().getStringExtra("name");
		if (name != null) {
			List<Formula> list = daoSession.getFormulaDao().queryBuilder()
					.where(FormulaDao.Properties.Name.eq(name)).list();
			if (null != list && list.size() > 0)
				entity = list.get(0);
		} else {
			List<Formula> list = daoSession.getFormulaDao().queryBuilder()
					.where(FormulaDao.Properties.Id.eq(getIntent().getIntExtra("id", -1))).list();
			if (null != list && list.size() > 0)
				entity = list.get(0);
		}
		if (entity != null) {
			tabCalcName = entity.getName();
			calcId = entity.getId();
			txtCalcName.setText(tabCalcName);
			calcList = daoSession.getUnitsDao()._queryFormula_UnitsList(calcId);
			for (Units obj : calcList) {// 输入框的最大位置，点击到该位置时，需要弹出计算
				if (obj.getType() == 6)
					if (obj.getOrder() > maxOrder)
						maxOrder = obj.getOrder();
			}
		}
	}

	/**
	 * @author Rafeal Piao
	 * @Description Add keyboard to layout
	 * @param lltView
	 *            , mContext, edtTmp, keyboardView
	 * @return void
	 */
	public void addKeyboard(RelativeLayout lltView, Context mContext,
			EditText edtTmp, View keyboardView) {
		edtTmp.setInputType(InputType.TYPE_NULL);
		edtTmp.requestFocus();

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		if (keyboardLoad != edtTmp) {
			if (keyboardView.getParent() != null) {
				lltView.removeView(keyboardView);
			}
			lltView.addView(keyboardView, lp);
			keyboardLoad = edtTmp;
		} else {
			if (keyboardView.getParent() == null) {
				lltView.addView(keyboardView, lp);
				keyboardLoad = edtTmp;
			}
		}
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus()
					.getWindowToken(), 0);
		}

		StringBuffer strBuffer = new StringBuffer(edtTmp.getText().toString());
		ImageButton imgbtnNo0 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no0);
		ImageButton imgbtnNo1 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no1);
		ImageButton imgbtnNo2 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no2);
		ImageButton imgbtnNo3 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no3);
		ImageButton imgbtnNo4 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no4);
		ImageButton imgbtnNo5 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no5);
		ImageButton imgbtnNo6 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no6);
		ImageButton imgbtnNo7 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no7);
		ImageButton imgbtnNo8 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no8);
		ImageButton imgbtnNo9 = (ImageButton) lltView
				.findViewById(R.id.imgbtn_no9);
		ImageButton imgbtnPoint = (ImageButton) lltView
				.findViewById(R.id.imgbtn_point);
		ImageButton imgbtnC = (ImageButton) lltView.findViewById(R.id.imgbtn_c);
		ImageButton imgbtnCA = (ImageButton) lltView
				.findViewById(R.id.imgbtn_ca);
		ImageButton imgbtnNext = (ImageButton) lltView
				.findViewById(R.id.imgbtn_next);

		imgbtnNo0.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo1.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo2.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo3.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo4.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo5.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo6.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo7.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo8.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNo9.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnC.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnCA.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnPoint.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));
		imgbtnNext.setOnClickListener(new KeyboardListener(mContext, edtTmp,
				strBuffer, imgbtnNext, keyboardView, lltView));

		imgbtnNo0.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo1.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo2.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo3.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo4.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo5.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo6.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo7.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo8.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNo9.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnC.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnCA.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnPoint.setOnTouchListener(new KeyBoardTouchListener(edtTmp));
		imgbtnNext.setOnTouchListener(new KeyBoardTouchListener(edtTmp));

		if (edtTmp.getImeOptions() == EditorInfo.IME_ACTION_DONE) {
			imgbtnNext.setTag("calculate");
			imgbtnNext.setBackgroundResource(R.drawable.keyboard_calculate);
		} else {
			imgbtnNext.setTag("next");
			imgbtnNext.setBackgroundResource(R.drawable.keyboard_next);
		}
	}

	/**
	 * @author Rafeal Piao
	 * @Description Number Keyboard Touch Listener
	 * @date 2011-10-19
	 */
	private class KeyBoardTouchListener implements View.OnTouchListener {
		EditText edt;

		public KeyBoardTouchListener(EditText edt) {
			this.edt = edt;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int bgHoverId = -1;
			int bgNormalId = -1;
			switch (v.getId()) {
			case R.id.imgbtn_no0:
				bgHoverId = R.drawable.no0_hover;
				bgNormalId = R.drawable.no_0;
				break;
			case R.id.imgbtn_no1:
				bgHoverId = R.drawable.no1_hover;
				bgNormalId = R.drawable.no_1;
				break;
			case R.id.imgbtn_no2:
				bgHoverId = R.drawable.no2_hover;
				bgNormalId = R.drawable.no_2;
				break;
			case R.id.imgbtn_no3:
				bgHoverId = R.drawable.no3_hover;
				bgNormalId = R.drawable.no_3;
				break;
			case R.id.imgbtn_no4:
				bgHoverId = R.drawable.no4_hover;
				bgNormalId = R.drawable.no_4;
				break;
			case R.id.imgbtn_no5:
				bgHoverId = R.drawable.no5_hover;
				bgNormalId = R.drawable.no_5;
				break;
			case R.id.imgbtn_no6:
				bgHoverId = R.drawable.no6_hover;
				bgNormalId = R.drawable.no_6;
				break;
			case R.id.imgbtn_no7:
				bgHoverId = R.drawable.no7_hover;
				bgNormalId = R.drawable.no_7;
				break;
			case R.id.imgbtn_no8:
				bgHoverId = R.drawable.no8_hover;
				bgNormalId = R.drawable.no_8;
				break;
			case R.id.imgbtn_no9:
				bgHoverId = R.drawable.no9_hover;
				bgNormalId = R.drawable.no_9;
				break;
			case R.id.imgbtn_c:
				bgHoverId = R.drawable.c_hover;
				bgNormalId = R.drawable.no_c;
				break;
			case R.id.imgbtn_ca:
				bgHoverId = R.drawable.ca_hover;
				bgNormalId = R.drawable.no_ca;
				break;
			case R.id.imgbtn_next:
				if (edt.getImeOptions() == EditorInfo.IME_ACTION_DONE
						|| "calculate".equals(v.getTag())) {
					bgHoverId = R.drawable.calculate_hover;
					bgNormalId = R.drawable.keyboard_calculate;
				} else {
					bgHoverId = R.drawable.next_hover;
					bgNormalId = R.drawable.keyboard_next;
				}

				break;
			case R.id.imgbtn_point:
				bgHoverId = R.drawable.point_hover;
				bgNormalId = R.drawable.point;
				break;
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_MOVE) {
				v.setBackgroundResource(bgHoverId);
			} else {
				v.setBackgroundResource(bgNormalId);
			}
			return false;
		}
	}

	/**
	 * @author Rafeal Piao
	 * @Description Customize keyboard listener
	 * @date 2011-10-18
	 */
	private class KeyboardListener implements View.OnClickListener {
		EditText edtTarget;
		StringBuffer strBuffer;
		Context mContext;
		boolean isClicked = false;
		ImageButton imgbtn;
		View keyboard;
		RelativeLayout llt;

		public KeyboardListener(Context mContext, EditText edt,
				StringBuffer str, ImageButton imgbtnNext, View keyboardView,
				RelativeLayout lltView) {
			this.mContext = mContext;
			edtTarget = (EditText) ((Activity) mContext).getCurrentFocus();
			strBuffer = str;
			imgbtn = imgbtnNext;
			this.keyboard = keyboardView;
			this.llt = lltView;
		}

		/**
		 * @description switch input box's focus, move to next
		 * @param void
		 * @return void
		 */
		public void switchFocusToNext() {
			new Thread() {
				public void run() {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
					switchFocusToNextHandler.sendEmptyMessage(0);
				};
			}.start();
		}

		private Handler switchFocusToNextHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (((Activity) mContext).getCurrentFocus() instanceof EditText) {
					edtTarget = (EditText) ((Activity) mContext)
							.getCurrentFocus();
					edtTarget.requestFocus();
					if (edtTarget.getImeOptions() == EditorInfo.IME_ACTION_DONE) {
						imgbtn.setTag("calculate");
						imgbtn.setBackgroundResource(R.drawable.keyboard_calculate);
					} else {
						imgbtn.setTag("next");
						imgbtn.setBackgroundResource(R.drawable.keyboard_next);
					}
				}
			}

		};

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgbtn_no0:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(0);
				break;
			case R.id.imgbtn_no1:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(1);
				break;
			case R.id.imgbtn_no2:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(2);
				break;
			case R.id.imgbtn_no3:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(3);
				break;
			case R.id.imgbtn_no4:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(4);
				break;
			case R.id.imgbtn_no5:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(5);
				break;
			case R.id.imgbtn_no6:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(6);
				break;
			case R.id.imgbtn_no7:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(7);
				break;
			case R.id.imgbtn_no8:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(8);
				break;
			case R.id.imgbtn_no9:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(9);
				break;
			case R.id.imgbtn_c:
				isClicked = true;
				clearStrBuffer(strBuffer);
				if (strBuffer.length() != 0) {
					strBuffer.deleteCharAt(strBuffer.length() - 1);
				}
				break;
			case R.id.imgbtn_ca:
				isClicked = true;
				clearStrBuffer(strBuffer);
				if (strBuffer.length() != 0) {
					strBuffer.delete(0, strBuffer.length());
				}
				clearValue();
				scrollToSpecifiedPosition("clear");
				break;
			case R.id.imgbtn_point:
				isClicked = true;
				clearStrBuffer(strBuffer);
				strBuffer.append(".");
				break;
			case R.id.imgbtn_next:
				if (((Activity) mContext).getCurrentFocus() instanceof EditText) {
					EditText edt = (EditText) ((Activity) mContext)
							.getCurrentFocus();
					if (edt.getImeOptions() != EditorInfo.IME_ACTION_DONE) {
						switchFocusToNext();
						if (isClicked) {
							clearStrBuffer(strBuffer);
						}
					}
				}
				isClicked = false;
				count = 1;
				if (edtTarget.getImeOptions() == EditorInfo.IME_ACTION_DONE
						|| "calculate".equals(v.getTag())) {
					computeResult();
					if (keyboard.getParent() != null) {
						llt.removeView(keyboard);
					}
					scrollToSpecifiedPosition("submit");
				}
				break;
			}
			// 判断当前页面焦点是否在edittext上，如果在，做一些处理
			if (((Activity) mContext).getCurrentFocus() instanceof EditText) {
				edtTarget = (EditText) ((Activity) mContext).getCurrentFocus();
				edtTarget.requestFocus();
				edtArray.add(edtTarget);
				if (edtTarget.getImeOptions() == EditorInfo.IME_ACTION_DONE) {
					imgbtn.setTag("calculate");
					imgbtn.setBackgroundResource(R.drawable.keyboard_calculate);
				} else {
					imgbtn.setTag("next");
					imgbtn.setBackgroundResource(R.drawable.keyboard_next);
				}
				if (!isClicked) {
					strBuffer = new StringBuffer(edtTarget.getText().toString());
				} else {
					clearStrBuffer(strBuffer);
					edtTarget.setText(strBuffer.toString());
					edtTarget.setSelection(edtTarget.getText().length());
					// edtTarget.setSelection(strBuffer.toString().length());
				}
			}

		}
	}

	/**
	 * @description Scroll to specified position when click submit and clear
	 *              button
	 * @param String
	 *            action
	 * @return void
	 * @Exception
	 */
	private void scrollToSpecifiedPosition(String action) {
		// TODO
		if ("submit".equals(action)) {
//			if (noResultList.size() > 0) {
//				if (null != noResultList.get(0)) {
//					int[] location = new int[2];
//					int[] titleLocation = new int[2];
//					noResultList.get(0).getLocationOnScreen(location);
//					noResultList.get(0).getLocationOnScreen(titleLocation);
//					int y = (int) (location[1] - titleLocation[1] - 50 * app
//							.getDeviceInfo().getScale());
//					if (y < 0) {
//						y = 0;
//					}
//					sv.scrollBy(0, y);
//				}
//			} else {
				if (textResultList.size() > 0) {
					int[] location = new int[2];
					textResultList.get(0).getLocationOnScreen(location);
					int y = location[1];
					sv.scrollTo(0, y);
				}
//			}
		} else {
			sv.scrollTo(0, 0);
		}
	}

	public void clearStrBuffer(StringBuffer strBuffer) {
		if (count == 1) {
			count = 0;
			strBuffer.delete(0, strBuffer.length());
		}
	}

	/**
	 * @description When click search input box remove the customize keyboard
	 * @param lltMainView
	 * @return void
	 */
	public void removeKeyboard(RelativeLayout view) {
		if (view.getChildAt(4) != null) {
			view.removeView(view.getChildAt(4));
		}
	}

	public void p(Object o) {
		System.out.println("" + o);
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	@SuppressWarnings("unused")
	private Mask mask;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(radioGroup != null)
		radioGroup.post(new Runnable() {
			@Override
			public void run() {
				mask = (Mask) findViewById(R.id.mask);
				mask = new Mask(TabCalcInfoActivity2.this, null);
				TipHelper.registerTips(TabCalcInfoActivity2.this, 1);
				TipHelper.runSegments(TabCalcInfoActivity2.this);
				TabCalcInfoActivity2.this.findViewById(R.id.closeTips).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TipHelper.sign(false, true);
						TipHelper.disableTipViewOnScreenVisibility();
					}
				});
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
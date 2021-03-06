package com.oto.edyd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.oto.edyd.model.SelectDepartment;
import com.oto.edyd.utils.Common;
import com.oto.edyd.utils.Constant;
import com.oto.edyd.utils.CusProgressDialog;
import com.oto.edyd.utils.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yql on 2015/10/27.
 */
public class OilCardApplicationActivity extends Activity implements View.OnClickListener{

    private LinearLayout back; //返回
    private EditText carId; //车牌号
    private EditText setPassword; //设置密码
    private EditText phoneNumber; //联系人电话
    private EditText limitOil; //限定油品
    private EditText limitAddOilAmount; //限定每次加油量
    private EditText limitDailyOilAmount; //限日加油金额
    private TextView submit; //提交
    private RadioGroup isNeedPassword; //是否需要密码
    private RadioGroup isSmsRemind; //是否需要短信提醒
    private EditText company; //公司
    private EditText etDepartment; //部门
    private LinearLayout companyDepartment; //公司部门布局
    private TextView txPasswordFlag; //是否需要密码标识
    private RadioButton needPassword; //不需要密码
    private RadioButton needMessage; //需要短信

    private SelectDepartment selectDepartment;
    private CusProgressDialog oilDialog; //过度
    private boolean isPassword = false; //是否需要密码
    private boolean isMessage = false; //是否需要短信

    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oil_card_application);
        initFields();
        controlShowInterface();

        submit.setOnClickListener(this);
        back.setOnClickListener(this);
        etDepartment.setOnClickListener(this);
        isNeedPassword.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                String isCheckText = rb.getText().toString();

                if (isCheckText.equals("是")) {
                    isPassword = true;
                    setPassword.setEnabled(true);
                    txPasswordFlag.setVisibility(View.VISIBLE);
                } else {
                    isPassword = false;
                    setPassword.setText("");
                    setPassword.setEnabled(false);
                    txPasswordFlag.setVisibility(View.GONE);
                }
            }
        });
        isSmsRemind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                String isCheckText = rb.getText().toString();

                if (isCheckText.equals("是")) {
                    isMessage = true;
                } else {
                    isMessage = false;
                }
            }
        });

        carId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s == null)) {
                    String inputContent = s.toString();
                    if (inputContent != null && !(inputContent.equals(""))) {
                        String phone = phoneNumber.getText().toString();
                        if (phone != null && !(phone.equals(""))) {
                            submit.setEnabled(true); //设置按钮可用
                            submit.setBackgroundResource(R.drawable.border_corner_login_enable);
                        }
                    } else {
                        submit.setBackgroundResource(R.drawable.border_corner_login);
                        submit.setEnabled(false); //设置按钮不可用
                    }
                }
            }
        });
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!(s == null)) {
                    String inputContent = s.toString();
                    if(inputContent != null && !(inputContent.equals(""))){
                        String card = carId.getText().toString();
                        if(card != null && !(card.equals(""))){
                            submit.setEnabled(true); //设置按钮可用
                            submit.setBackgroundResource(R.drawable.border_corner_login_enable);
                        }
                    } else {
                        submit.setBackgroundResource(R.drawable.border_corner_login);
                        submit.setEnabled(false); //设置按钮不可用
                    }
                }
            }
        });
    }

    private void initFields() {
        back = (LinearLayout) findViewById(R.id.back);
        carId = (EditText) findViewById(R.id.car_id);
        setPassword = (EditText) findViewById(R.id.set_password);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        limitOil = (EditText) findViewById(R.id.limit_oil);
        limitAddOilAmount = (EditText) findViewById(R.id.limit_add_oil_amount);
        limitDailyOilAmount = (EditText) findViewById(R.id.limit_daily_oil_amount);
        isNeedPassword = (RadioGroup) findViewById(R.id.is_need_password);
        isSmsRemind = (RadioGroup) findViewById(R.id.is_sms_remind);
        submit = (TextView) findViewById(R.id.submit);
        company = (EditText) findViewById(R.id.company);
        etDepartment = (EditText) findViewById(R.id.department);
        companyDepartment = (LinearLayout) findViewById(R.id.company_department);
        txPasswordFlag = (TextView) findViewById(R.id.tx_password_flag);
        needPassword = (RadioButton) findViewById(R.id.need_password);
        needMessage = (RadioButton) findViewById(R.id.need_message);

        isNeedPassword.check(needPassword.getId());
        isSmsRemind.check(needMessage.getId());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.submit:
                //验证车牌号
                confirmCarNumber(carId.getText().toString());
                break;
            case R.id.back: //返回
                finish();
                break;
            case R.id.department: //部门
                intent = new Intent(getApplicationContext(), SelectDepartmentActivity.class);
                startActivityForResult(intent, 0x20);
                break;
        }
    }

    /**
     * 校验表单
     */
    private void verify() {
        boolean isCheckedNeedPassword = isPassword; //检查是否需要密码
        boolean isCheckedSmsRemind = isMessage; //是否短信提醒
        String carNumber = carId.getText().toString();

        carNumber = carNumber.replace(" ", "");
//        if(carNumber != null && carNumber.equals("")) {
//            Toast.makeText(getApplicationContext(), "车牌号格式不正确", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            if(carNumber.length() < 7) {
//                Toast.makeText(getApplicationContext(), "车牌号必须为7位", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }

        if(carNumber != null && carNumber.equals("")) {
            Toast.makeText(getApplicationContext(), "车牌号不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
            Matcher matcher = pattern.matcher(carNumber);
            if(!matcher.matches()) {
                Toast.makeText(getApplicationContext(), "车牌号格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(isCheckedNeedPassword) {
            String password = (setPassword.getText().toString()).trim();
            password = password.replaceAll(" ", "");
            if(password != null && password.equals("")) {
                Toast.makeText(getApplicationContext(), "密码已设置不能为空", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if(password.length() < 4) {
                    Toast.makeText(getApplicationContext(), "密码位数要求4-6位整数", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        String mPhone = (phoneNumber.getText().toString()).trim();
        mPhone = mPhone.replaceAll(" ", "");
        if(mPhone != null && mPhone.equals("")) {
            Toast.makeText(getApplicationContext(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Pattern pattern = Pattern.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$"); //匹配手机
            Matcher matcher = pattern.matcher(mPhone);
            if(!matcher.matches()) {
                Toast.makeText(getApplicationContext(), "手机号码格式不对", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int accountType = getEnterpriseAccountType();
        if(accountType == 1 || accountType == 2 ) {
            String department = (etDepartment.getText().toString()).trim();
            department = department.replaceAll(" ", "");
            if(department != null && department.equals("")) {
                Toast.makeText(getApplicationContext(), "部门不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        applyOilCard();
    }
    /**
     * 申请油卡
     */
    private void applyOilCard() {
        int is_sms, is_need_password;
        String car_id = carId.getText().toString();
        String set_password = setPassword.getText().toString();
        String mobile = phoneNumber.getText().toString();
        String limit_oil = limitOil.getText().toString();
        String every_add_oil = limitAddOilAmount.getText().toString();
        String every_daily_oil_amount = limitDailyOilAmount.getText().toString();

        if(isPassword) { //需要密码
            is_need_password = 1;
        } else { //不需要密码
            is_need_password = 2;
        }

        if(isMessage) { //需要短信
            is_sms = 1;
        } else { //不需要短信
            is_sms = 2;
        }

        common = new Common(getSharedPreferences(Constant.LOGIN_PREFERENCES_FILE, Context.MODE_PRIVATE));
        String sessionUuid = common.getStringByKey(Constant.SESSION_UUID);

        //个人
        String accountID = common.getStringByKey("ACCOUNT_ID");
        String userName = common.getStringByKey(Constant.USER_NAME); //用户账号

        //公司
        String enterpriseID = common.getStringByKey(Constant.ENTERPRISE_ID);
        String enterpriseName = common.getStringByKey(Constant.ENTERPRISE_NAME);

        String url = Constant.ENTRANCE_PREFIX + "insertOilCard.json?carId="+car_id+"&sessionUuid="+sessionUuid+"&setPwd="+String.valueOf(is_need_password)+"&pwd="+set_password+
                "&setMessage="+is_sms+"&mobile="+mobile+"&oilNum="+limit_oil+"&everyTimeOil="+every_add_oil+"&everyDayOil="+every_daily_oil_amount+"&failCause=null"+
                "&accountType=0";

        if(enterpriseID.equals("0") || enterpriseID.equals("3")) {
            url = url + "&realAccountId="+accountID + "&accountMobile="+userName;
        } else {
            url = url + "&tenantId="+selectDepartment.getTenantId() + "&orgCode="+selectDepartment.getOrgCode()+
            "&orgName=" + selectDepartment.getText() +"&enterpriseId=" + enterpriseID + "&enterpriseName=" + enterpriseName;
        }

        OkHttpClientManager.getAsyn(url, new ApplyResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                //请求成功
                JSONObject oilSON;
                try {
                    oilSON = new JSONObject(response);
                    if (!oilSON.getString("status").equals(Constant.LOGIN_SUCCESS_STATUS)) {
                        //验证申请油品是否成功
                        Toast.makeText(getApplicationContext(), "申请失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "申请成功", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取组织信息
     */
    private void inquireOrganizations() {
        String url = "";
    }

    public abstract class ApplyResultCallback<T> extends OkHttpClientManager.ResultCallback<T>{
        @Override
        public void onBefore() {
            //请求之前操作
            oilDialog = new CusProgressDialog(OilCardApplicationActivity.this, "正在申请...");
            oilDialog.getLoadingDialog().show();
        }

        @Override
        public void onAfter() {
            //请求之后要做的操作
            oilDialog.getLoadingDialog().dismiss();
        }
    }

    /**
     * 界面显示控制
     */
    private void controlShowInterface() {
        boolean isHide = true;
        switch (getEnterpriseAccountType()) {
            case 0:
                isHide = true;
                break;
            case 1:
                isHide = false;
                break;
            case 2:
                isHide = false;
                break;
            case 3:
                isHide = true;
                break;
        }

        if(isHide) {
            companyDepartment.setVisibility(View.GONE);

        } else {
            companyDepartment.setVisibility(View.VISIBLE);
            String enterpriseName = common.getStringByKey("enterprise_name");
            company.setText(enterpriseName);
        }
    }

    /**
     * 判断是否是企业用户
     * @return
     */
    private int getEnterpriseAccountType() {
        common = new Common(getSharedPreferences(Constant.LOGIN_PREFERENCES_FILE, Context.MODE_PRIVATE));
        // String role = common.getStringByKey(getString(R.string.role_id));
        String role = common.getStringByKey(getString(R.string.role_id));
        if(role.equals("")){
            Toast.makeText(this, "角色错误", Toast.LENGTH_SHORT).show();
            return 4;
        }
        return Integer.valueOf(role);
    }

    /**
     * activity成功返回
     * @param requestCode 启动码
     * @param resultCode 返回码
     * @param data 数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0x22:
                selectDepartment = (SelectDepartment) data.getSerializableExtra("department");
                etDepartment.setText(selectDepartment.getText());
                break;
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x40: //卡号已存在
                    Toast.makeText(getApplicationContext(), "卡号已注册", Toast.LENGTH_SHORT).show();
                    break;
                case 0x41: //不存在
                    verify();
                    break;
            }
        }
    };

    /**
     * 验证车牌号
     * @param cardId 车牌号
     */
    private void confirmCarNumber(String cardId) {
        String sessionUuid = common.getStringByKey(Constant.SESSION_UUID);
        String url = Constant.ENTRANCE_PREFIX + "inqueryOilCardByCarId.json?sessionUuid=" + sessionUuid + "&carId=" + cardId;
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                JSONObject roleJSON;
                JSONArray roleArray;
                try {
                    roleJSON = new JSONObject(response);
                    String status = roleJSON.getString("status");
                    if(!status.equals(Constant.LOGIN_SUCCESS_STATUS)) {
                        //角色类型请求失败
                        //Toast.makeText(getApplicationContext(), "角色用户请求失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    roleArray = roleJSON.getJSONArray("rows");
                    Message message = new Message();
                    if(roleArray.length() > 0) {
                        message.what = 0x40; //卡号已存在
                    } else {
                        message.what = 0x41; //卡号不存在
                    }
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

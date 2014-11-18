package com.asset.tracker;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.asset.tracker.common.BaseActivity;
import com.asset.tracker.utils.Constant;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class LoginActivity extends BaseActivity {

	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private TextView mForgotPasswordTextView;
	protected LoginActivity context;

	private ArrayList<String> emailIdList = new ArrayList<String>();
	private ArrayList<String> passwordList = new ArrayList<String>();
	private ArrayList<String> nameList = new ArrayList<String>();
	private boolean stopFlag = false;
	private boolean showErrorFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		context = this;
		initControls();
	}

	private void initControls(){
		mEmailEditText = (EditText) findViewById(R.id.EmailEditText);
		mPasswordEditText = (EditText) findViewById(R.id.PasswordEditText);
		mForgotPasswordTextView = (TextView) findViewById(R.id.ForgotPasswordTextView);
		findViewById(R.id.LoginButton).setOnClickListener(mClickListener);
		mForgotPasswordTextView.setOnClickListener(mClickListener);

		Firebase.setAndroidContext(this);
	}

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int view = v.getId();
			if(view == R.id.LoginButton){
				loginValidation();

			}
		}
	};

	private void loginValidation(){
		String emailVal = mEmailEditText.getText().toString();
		String passwordVal = mPasswordEditText.getText().toString();

		if(emailVal.trim().length() == 0){
			showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_email_blank));
			return;
		}else if(isValidEmail(emailVal) == false){
			showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_email_invalid));
			return;
		}else if(passwordVal.trim().length() == 0){
			showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_password_blank));
			return;
		}else{
			if(isNetworkAvailable(context)){
				//getRecordFromServer(emailVal, passwordVal);
				showErrorFlag = false;
				stopFlag = false;
				clearingList();

				getRecordFromServerNew(emailVal, passwordVal);

			}else{
				showAlertDialog(context, getString(R.string.app_name), getString(R.string.internet_error));
				return;
			}

		}
	}

	public final static boolean isValidEmail(String emailVal) {
		if (TextUtils.isEmpty(emailVal)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches();
		}
	}

	private void getRecordFromServer(final String email, final String password) {
		Constant.loginRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				if(snapshot.hasChildren()){
					System.out.println("here is val -- " + snapshot.getChildrenCount());
					Map<String, Object> newPost = (Map<String, Object>) snapshot.getValue();

					String emailVal = email;
					String passwordVal = password;
					System.out.println("here is " + newPost.get("email").toString());
					if(newPost.get("email").toString().equalsIgnoreCase(emailVal)){
						if(newPost.get("password").toString().equalsIgnoreCase(passwordVal)){
							showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_logged_in));
						}else{
							showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_error));
						}
					}else{
						showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_error));
					}

				}else{
					showAlertDialog(context, getString(R.string.app_name), getString(R.string.login_no_record));
					System.out.println("No Records.");
					return;
				}
			}
			@Override
			public void onCancelled(FirebaseError firebaseError) {
				showAlertDialog(context, getString(R.string.app_name), firebaseError.getMessage());
			}
		});
	}

	private void getRecordFromServerNew(final String email, final String password){
		/*Constant.baseNewUrl.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				if(snapshot.hasChildren()){
					{

						for (DataSnapshot child : snapshot.getChildren()) {
							System.out.println("Data .. " + child.getKey());

							Firebase baseNewUrl = new Firebase("https://popping-fire-4741.firebaseio.com/users/"+child.getKey());
							System.out.println("get Data " + snapshot.child(child.getKey().toString()).getValue());
							System.out.println("base url ... " + baseNewUrl);
							String var = baseNewUrl.getRef().toString();
							var = baseNewUrl.child("/{email}/").toString();
							System.out.println("variable .. " + var);
						}
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub

			}
		});*/

		Constant.baseNewUrl.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChild) {
				int val = 0;
				//	System.out.println("User " + snapshot.getKey());
				if(snapshot.hasChildren()){
					val++;
					for (DataSnapshot level1 : snapshot.getChildren()) {
						String listKey = level1.getKey().toString();
						//	System.out.println("Values  " + level1.getKey() + " : " + snapshot.child(level1.getKey().toString()).getValue());
						if(level1.getKey().toString().equalsIgnoreCase("email")){
							emailIdList.add(snapshot.child(level1.getKey().toString()).getValue().toString());
						}else if(level1.getKey().toString().equalsIgnoreCase("password")){
							passwordList.add(snapshot.child(level1.getKey().toString()).getValue().toString());
						}else if(level1.getKey().toString().equalsIgnoreCase("name")){
							nameList.add(snapshot.child(level1.getKey().toString()).getValue().toString());
						}

						if(snapshot.child(listKey).hasChildren()){
							for(DataSnapshot level2 : snapshot.child(listKey).getChildren()){
								//		System.out.println("List Values  " + level2.getKey() + " : " + snapshot.child(listKey).child(level2.getKey().toString()).getValue());
								String innerListKey = level2.getKey().toString();
								if(snapshot.child(listKey).child(innerListKey).hasChildren()){
									for(DataSnapshot level3 : snapshot.child(listKey).child(innerListKey).getChildren()){
										//				System.out.println("Inner List Values  " + level3.getKey() + " : " + snapshot.child(listKey).child(innerListKey).child(level3.getKey().toString()).getValue());
									}
								}
							}
						}
					}
				}
				if(stopFlag == false){
					int tempVal = 0;
					if(emailIdList.contains(email)){
						stopFlag = true;
						tempVal = emailIdList.indexOf(email);
						showErrorFlag = true;
					}
					if(passwordList.get(tempVal).equalsIgnoreCase(password)){
						stopFlag = true;
						showErrorFlag = true;
						showAlertDialog(context, getString(R.string.app_name), nameList.get(tempVal) + " Logged in Successfully!");
					}
				}
				/*if(showErrorFlag == false && stopFlag == false){
					stopFlag = true;
					showAlertDialog(context, getString(R.string.app_name), " Invalid email or password");
				}*/
			}


			// ....

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("changed called");
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("moved called");
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				System.out.println("removed called");
			}
		});
	}

	private void clearingList(){
		emailIdList.clear();
		nameList.clear();
		passwordList.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

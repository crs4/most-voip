package most.voip.example.remote_config;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button btnLogin = (Button)findViewById(R.id.buttonLogin);
		btnLogin.setOnClickListener(onClickListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	
	private OnClickListener onClickListener = new OnClickListener() {

	     @Override
	     public void onClick(final View v) {
	         switch(v.getId()){
	             case R.id.buttonLogin:
	                  //DO something
	            	 EditText username   = (EditText)findViewById(R.id.editText1);
	            	 EditText password   = (EditText)findViewById(R.id.editText2);
	            	 Log.d("VoipConfigDemo", "In OnClick with user: " + username.getText() + " - and password: " + password.getText());
	             break;
//	             case R.id.button2:
//	                  //DO something
//	             break;
//	             case R.id.button3:
//	                  //DO something
//	             break;
	         }

	   }
	 
	};
}

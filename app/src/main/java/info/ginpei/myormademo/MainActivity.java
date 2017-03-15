package info.ginpei.myormademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import info.ginpei.myormademo.basicModels.OrmaDatabase;
import info.ginpei.myormademo.basicModels.User;
import info.ginpei.myormademo.basicModels.User_Relation;
import info.ginpei.myormademo.basicModels.User_Selector;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "G#MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Create a new row in the user table.
     * Called from a button.
     *
     * @param view Button view.
     */
    public void createUser(View view) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare a model
        // (The ID will be set automatically by Orma.)
        final User user = new User();
        user.name = "User Name";

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // insert the model to your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "createUser: Inserting...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                long assignedId = userRelation.inserter()
                        .execute(user);
                user.id = assignedId;  // the ID is generated automatically

                Log.d(TAG, "createUser: Inserted.");

                // show the result
                // (Something like Toast, View.setText needs to run on the UI thread. :D )
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Inserted a row!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * Read all rows from the user table.
     * Called from a button.
     *
     * @param view Button view.
     */
    public void readAllUsers(View view) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare a model
        // (The ID will be set automatically by Orma.)
        final User user = new User();
        user.name = "User Name";

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // select the models from your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "readAllUsers: Reading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                User_Selector userSelector = userRelation.selector();
                final int count = userSelector.count();
                for (User user : userSelector) {
                    Log.d(TAG, "readAllUsers: Here is the user whose ID is " + user.id + " and name is " + user.name);
                }

                Log.d(TAG, "readAllUsers: Read.");

                // show the result
                // (It needs to run on the UI thread. :D )
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Read " + count + " user(s)!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}

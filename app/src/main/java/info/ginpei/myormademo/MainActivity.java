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
import info.ginpei.myormademo.util.InputDialogBuilder;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MyOrmaDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called from a button.
     *
     * @param view Button view.
     */
    public void createButton_click(View view) {
        // show a dialog and receive new user's name
        InputDialogBuilder.show(this, "Create", "Input new user's name", new InputDialogBuilder.Callback() {
            @Override
            public void onClick(String result) {
                if (result != null && !result.isEmpty()) {
                    // OK let's make it!
                    createUser(result);
                }
            }
        });
    }

    /**
     * Insert a new row into user table.
     *
     * @param userName New user's name.
     */
    private void createUser(String userName) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare a model
        // (The ID will be set automatically by Orma.)
        final User user = new User();
        user.name = userName;

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

                Log.d(TAG, "createUser: Inserted with the ID " + assignedId + ".");

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
     * Called from a button.
     *
     * @param view Button view.
     */
    public void readAllButton_click(View view) {
        // OK look over!
        readAllUsers();
    }

    /**
     * Select all rows from user table.
     */
    private void readAllUsers() {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

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
                if (count > 0) {
                    for (User user : userSelector) {
                        Log.d(TAG, "readAllUsers: Here is the user whose ID is " + user.id + " and name is " + user.name + ".");
                    }
                } else {
                    Log.d(TAG, "readUser: There are no users.");
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

    /**
     * Called from a button.
     *
     * @param view Button view.
     */
    public void readButton_click(View view) {
        InputDialogBuilder.show(this, "Read", "Input user's ID", new InputDialogBuilder.Callback() {
            @Override
            public void onClick(String result) {
                if (result != null && !result.isEmpty()) {
                    long id = Long.parseLong(result);

                    // OK let's find the guy!
                    readUser(id);
                }
            }
        });
    }

    /**
     * Select one row for the specified condition from user table.
     *
     * @param id User's ID.
     */
    private void readUser(final long id) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // select the models from your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "readUser: Reading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                User_Selector userSelector = userRelation.selector()
                        .idEq(id);
                final int count = userSelector.count();
                if (count > 0) {
                    User user = userSelector.get(0);
                    Log.d(TAG, "readUser: Here is the user whose ID is " + user.id + " and name is " + user.name + ".");
                } else {
                    Log.d(TAG, "readUser: There are no users whose ID is " + id + ".");
                }

                Log.d(TAG, "readUser: Read.");

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

    /**
     * Called from a button.
     *
     * @param view Button view.
     */
    public void updateAllButton_click(View view) {
        InputDialogBuilder.show(this, "Update", "Input new name for all users", new InputDialogBuilder.Callback() {
            @Override
            public void onClick(String result) {
                if (result != null && !result.isEmpty()) {
                    // OK beat them!
                    updateAllUsers(result);
                }
            }
        });
    }

    /**
     * Update all rows in user table with specified values.
     *
     * @param userName New name.
     */
    private void updateAllUsers(final String userName) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // select the models from your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "updateAllUsers: Updating...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = userRelation.updater()
                        .name(userName)
                        .execute();

                Log.d(TAG, "updateUser: Updated " + count + " user(s).");

                // show the result
                // (It needs to run on the UI thread. :D )
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Updated " + count + " user(s)!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * Called from a button.
     *
     * @param view Button view.
     */
    public void updateButton_click(View view) {
        InputDialogBuilder.show(this, "Update (1/2)", "Input user's ID", new InputDialogBuilder.Callback() {
            @Override
            public void onClick(String result) {
                if (result != null && !result.isEmpty()) {
                    final long id = Long.parseLong(result);

                    InputDialogBuilder.show(MainActivity.this, "Update (2/2)", "Input new name for the user #" + id, new InputDialogBuilder.Callback() {
                        @Override
                        public void onClick(String result) {
                            if (result != null && !result.isEmpty()) {
                                // OK beat it!
                                updateUser(id, result);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Update the row specified by ID in user table.
     *
     * @param id       ID.
     * @param userName User's name.
     */
    private void updateUser(final long id, final String userName) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // select the models from your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "updateUser: Updating...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = userRelation.updater()
                        .idEq(id)
                        .name(userName)
                        .execute();

                Log.d(TAG, "updateUser: Updated " + count + " user(s).");

                // show the result
                // (It needs to run on the UI thread. :D )
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Updated " + count + " user(s)!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * Called from a button.
     *
     * @param view Button view.
     */
    public void deleteAllButton_click(View view) {
        // OK kill'em all!
        deleteAllUsers();
    }

    /**
     * Delete all rows in user table.
     */
    private void deleteAllUsers() {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // select the models from your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "deleteAllUsers: Deleting...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = userRelation.deleter()
                        .execute();
                Log.d(TAG, "deleteAllUsers: Deleted " + count + " user(s).");

                // show the result
                // (It needs to run on the UI thread. :D )
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Deleted " + count + " user(s)!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * Called from a button.
     *
     * @param view Button view.
     */
    public void deleteButton_click(View view) {
        InputDialogBuilder.show(this, "Delete", "Input user's ID", new InputDialogBuilder.Callback() {
            @Override
            public void onClick(String result) {
                if (result != null && !result.isEmpty()) {
                    long id = Long.parseLong(result);

                    // OK search and destroy!
                    deleteUser(id);
                }
            }
        });
    }

    /**
     * Delete one row for the specified condition from user table.
     *
     * @param id User's ID.
     */
    private void deleteUser(final long id) {
        // Q: Why final?
        // A: Because they are used in the other thread.
        //    You need to make sure they won't change since the thread runs asynchronously.

        // Note: The specified row may not exist in the table.
        //       But who cares?

        // prepare Orma
        OrmaDatabase orma = OrmaDatabase.builder(this).build();
        final User_Relation userRelation = orma.relationOfUser();

        // select the models from your database
        // (It needs to run on a worker thread.)
        Log.d(TAG, "deleteUser: Deleting...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = userRelation.deleter()
                        .idEq(id)
                        .execute();
                Log.d(TAG, "deleteUser: Deleted " + count + " user(s).");

                // show the result
                // (It needs to run on the UI thread. :D )
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Deleted " + count + " user(s)!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}

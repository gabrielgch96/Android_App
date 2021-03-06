package example.gabo.com.testapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteFishScreen extends AppCompatActivity{

    FishDatabaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrar);

        Log.d("RECEPTION","Received "+ getIntent().getStringExtra(MainActivity.MESSAGE_ID));

        controller = new FishDatabaseController(this.getBaseContext());

        String[] arg = {getIntent().getStringExtra("id")};
        FishEntry fishtemp = searchNameFish(FishEntry.COLUMN_ID+"=?",arg);
        ((TextView)findViewById(R.id.speciesInput)).setText(fishtemp.getSpecies());;
        ((TextView)findViewById(R.id.amountInput)).setText(Float.toString(fishtemp.getAmount()));
        ((TextView)findViewById(R.id.searchNameField)).setText(fishtemp.getName());
    }

    public void onDestroy()
    {
        super.onDestroy();
        controller.close();
    }

    public void goBack(View view){
        this.finish();
    }

    public void deleteFish(View view){
        TextView nameToSearch = findViewById(R.id.searchNameField);
        String[] arg ={nameToSearch.getText().toString()};
        Log.d("DEBUG,"," arg:"+arg[0]);
        FishEntry fish = new FishEntry();
        fish.setName(nameToSearch.getText().toString());
        final FishEntry fish2 = searchNameFish(FishEntry.COLUMN_NAME+"=?", arg);
        if(fish2 != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Warning");
            builder.setMessage("Are you sure to delete this movie?");
            builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long deletion = controller.delete(fish2);
                        Log.d("Database", "Deleted: "+deletion);
                        goBack(null);
                    }
                });
            builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    
                    }
                });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    public FishEntry searchNameFish(String selection, String[] arg){
        FishEntry fisho = new FishEntry();
        Log.d("database","fishy y arg"+arg[0]);
        Cursor cursor = controller.selectFishes(selection,arg);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(FishEntry.COLUMN_NAME));
            String amount = cursor.getString(cursor.getColumnIndex(FishEntry.COLUMN_AMOUNT));
            String species = cursor.getString(cursor.getColumnIndex(FishEntry.COLUMN_SPECIES));
            String id = cursor.getString(cursor.getColumnIndex(FishEntry.COLUMN_ID));
            Log.d("Query Result", "Found: "+name+ ", "+amount+ ", "+species);

            fisho.setName(name);
            fisho.setAmount(Float.parseFloat(amount));
            fisho.setSpecies(species);
            fisho.setId(Integer.parseInt(id));
            return fisho;
        }
        else
        {
            Toast.makeText(this, "No movie found...", Toast.LENGTH_SHORT);
        }
        return null;
    }

    public void displayFish(View view){
        TextView fishName = findViewById(R.id.searchNameField);
        String[] arg = {fishName.getText().toString()};
        FishEntry fishy = searchNameFish(FishEntry.COLUMN_NAME+"=?", arg);
        //Log.d("Query Result", "Found: "+fishy.getName()+ ", "+fishy.getAmount()+ ", "+fishy.getSpecies());
        if(fishy != null) {
            TextView fishSpecies = findViewById(R.id.speciesInput);
            TextView amountNum = findViewById(R.id.amountInput);
            fishName.setText(fishy.getName());
            fishSpecies.setText(fishy.getSpecies());
            amountNum.setText(Float.toString(fishy.getAmount()));
        }
    }
}

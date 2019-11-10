package com.portfolio.andaya_jae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReading;

    EditText etEnterUserID;
    EditText etEnterSystolic;
    EditText etEnterDiastolic;

    ListView listViewReading;
    ListView listViewSummary;
    List<Reading> readingList;
    List<Summary> summaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReading = FirebaseDatabase.getInstance().getReference("reading");

        etEnterUserID = findViewById(R.id.etEnterUserID);
        etEnterSystolic = findViewById(R.id.etEnterSystolic);
        etEnterDiastolic = findViewById(R.id.etEnterDiastolic);

        listViewReading = findViewById(R.id.listViewReading);
        readingList = new ArrayList<Reading>();
        summaryList = new ArrayList<Summary>();

        listViewReading.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Reading reading = readingList.get(position);
                showUpdateDialog(reading);
                return false;
            }
        });

        listViewSummary = findViewById(R.id.listViewSummary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReading.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readingList.clear();
                for (DataSnapshot readingSnapshot : dataSnapshot.getChildren()) {
                    Reading reading = readingSnapshot.getValue(Reading.class);
                    readingList.add(reading);
                }

                ReadingListAdapter readingAdapter = new ReadingListAdapter(MainActivity.this, readingList);
                listViewReading.setAdapter(readingAdapter);

                summaryList = generateSummary((ArrayList)readingList);

                SummaryListAdapter summaryAdapter = new SummaryListAdapter(MainActivity.this, summaryList);
                listViewSummary.setAdapter(summaryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }

    public void makeCrisisToast() {
        Toast.makeText(this, "Consult your doctor immediately", Toast.LENGTH_LONG).show();
    }

    public void addReading(View view) throws ParseException {

        if (TextUtils.isEmpty(etEnterUserID.getText()) || TextUtils.isEmpty(etEnterSystolic.getText())
            || TextUtils.isEmpty(etEnterDiastolic.getText())) {
            Toast.makeText(this, "You must enter valid information", Toast.LENGTH_LONG).show();
            return;
        }

        String userID = etEnterUserID.getText().toString();
        Long systolic = Long.parseLong(etEnterSystolic.getText().toString());
        Long diastolic = Long.parseLong(etEnterDiastolic.getText().toString());

        String id = databaseReading.push().getKey();
        final Reading reading = new Reading(id, userID, getCurrentDateTime(), systolic, diastolic);

        final Task setValueTask = databaseReading.child(id).setValue(reading);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (reading.getColor() == Color.RED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Consult your doctor immediately").setTitle("Hypertensive Crisis");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                Toast.makeText(MainActivity.this, "Reading is added", Toast.LENGTH_LONG).show();
                etEnterUserID.setText("");
                etEnterSystolic.setText("");
                etEnterDiastolic.setText("");
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateReading(Reading reading) {
        DatabaseReference dbRef = databaseReading.child(reading.getId());

        Task setValueTask = dbRef.setValue(reading);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this, "Reading updated", Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteReading(String id) {
        DatabaseReference dbRef = databaseReading.child(id);

        Task removeTask = dbRef.removeValue();
        removeTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this, "Reading deleted", Toast.LENGTH_LONG).show();
            }
        });
        removeTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to delete reading", Toast.LENGTH_SHORT);
            }
        });
    }

    public void showUpdateDialog(final Reading reading) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etUpdateUserID = dialogView.findViewById(R.id.etUpdateUserID);
        final EditText etUpdateSystolic = dialogView.findViewById(R.id.etUpdateSystolic);
        final EditText etUpdateDiastolic = dialogView.findViewById(R.id.etUpdateDiastolic);

        etUpdateUserID.setText(reading.getUserID());
        etUpdateSystolic.setText(reading.getSystolic().toString());
        etUpdateDiastolic.setText(reading.getDiastolic().toString());

        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Update Reading");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = etUpdateUserID.getText().toString().trim();
                Long systolic = Long.parseLong(etUpdateSystolic.getText().toString());
                Long diastolic = Long.parseLong(etUpdateDiastolic.getText().toString());

                Reading newReading = new Reading(reading.getId(), userID, reading.getDate(), systolic, diastolic);
                updateReading(newReading);

                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReading(reading.getId());
                alertDialog.dismiss();
            }
        });
    }

    public ArrayList<Summary> generateSummary(ArrayList<Reading> readingList) {

        ArrayList<Summary> summaryList = new ArrayList<>();
        HashMap<String, ArrayList<Reading>> hashMap = new HashMap<>();

        for (Reading reading : readingList) {
            if (hashMap.containsKey(reading.getUserID())) {
                ArrayList arrayList = hashMap.get(reading.getUserID());
                arrayList.add(reading);
                hashMap.put(reading.getUserID(), arrayList);
            } else {
                ArrayList arrayList = new ArrayList<Reading>();
                arrayList.add(reading);
                hashMap.put(reading.getUserID(), arrayList);
            }
        }

        for (HashMap.Entry<String, ArrayList<Reading>> entry : hashMap.entrySet()) {
            summaryList.add(new Summary(entry.getValue()));
        }

        return summaryList;
    }
}

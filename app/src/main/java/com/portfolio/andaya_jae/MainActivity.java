package com.portfolio.andaya_jae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReading;

    EditText etEnterUserID;
    EditText etEnterSystolic;
    EditText etEnterDiastolic;

    TextView tvEnterDate;
    TextView tvEnterTime;

    ListView listViewReading;
    List<Reading> readingList;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReading = FirebaseDatabase.getInstance().getReference("reading");

        etEnterUserID = findViewById(R.id.etEnterUserID);
        etEnterSystolic = findViewById(R.id.etEnterSystolic);
        etEnterDiastolic = findViewById(R.id.etEnterDiastolic);

        tvEnterDate = findViewById(R.id.tvEnterDate);
        tvEnterTime = findViewById(R.id.tvEnterTime);

        getCurrentDateTime();
        setDateTime();

        listViewReading = findViewById(R.id.listViewReading);
        readingList = new ArrayList<Reading>();

        listViewReading.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Reading reading = readingList.get(position);
                showUpdateDialog(reading);
                return false;
            }
        });
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
                ReadingListAdapter adapter = new ReadingListAdapter(MainActivity.this, readingList);
                listViewReading.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    private void setDateTime() {
        tvEnterDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        tvEnterTime.setText(mHour + ":" + mMinute);
    }

    public void selectDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        setDateTime();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void selectTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        setDateTime();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void addReading(View view) throws ParseException {

        if (TextUtils.isEmpty(etEnterUserID.getText()) || TextUtils.isEmpty(etEnterSystolic.getText())
            || TextUtils.isEmpty(etEnterDiastolic.getText())) {
            Toast.makeText(this, "You must enter valid informations", Toast.LENGTH_LONG).show();
            return;
        }

        String userID = etEnterUserID.getText().toString();
        Long systolic = Long.parseLong(etEnterSystolic.getText().toString());
        Long diastolic = Long.parseLong(etEnterDiastolic.getText().toString());
        Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(mDay + "/" + (mMonth+1) + "/" + mYear + " " + mHour + ":" + mMinute);

        String id = databaseReading.push().getKey();
        Reading reading = new Reading(id, userID, date, systolic, diastolic);

        Task setValueTask = databaseReading.child(id).setValue(reading);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
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

        final int[] uYear = new int[1];
        final int[] uMonth = new int[1];
        final int[] uDay = new int[1];
        final int[] uHour = new int[1];
        final int[] uMinute = new int[1];
        uYear[0] = Integer.parseInt(new SimpleDateFormat("yyyy").format(reading.getDate()));
        uMonth[0] = Integer.parseInt(new SimpleDateFormat("MM").format(reading.getDate())) - 1;
        uDay[0] = Integer.parseInt(new SimpleDateFormat("dd").format(reading.getDate()));
        uHour[0] = Integer.parseInt(new SimpleDateFormat("hh").format(reading.getDate()));
        uMinute[0] = Integer.parseInt(new SimpleDateFormat("mm").format(reading.getDate()));

        final TextView tvUpdateDate = dialogView.findViewById(R.id.tvUpdateDate);
        final TextView tvUpdateTime = dialogView.findViewById(R.id.tvUpdateTime);

        tvUpdateDate.setText(uDay[0] + "/" + (uMonth[0] + 1) + "/" + uYear[0]);
        tvUpdateTime.setText(uHour[0] + ":" + uMinute[0]);

        Button btnUpdateDate = dialogView.findViewById(R.id.btnUpdateDate);
        Button btnUpdateTime = dialogView.findViewById(R.id.btnUpdateTime);

        btnUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                uYear[0] = year;
                                uMonth[0] = monthOfYear;
                                uDay[0] = dayOfMonth;
                                int displayMonth = monthOfYear + 1;
                                tvUpdateDate.setText(uDay[0] + "/" + displayMonth + "/" + uYear[0]);
                            }
                        }, uYear[0], uMonth[0], uDay[0]);
                datePickerDialog.show();
            }
        });

        btnUpdateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                uHour[0] = hourOfDay;
                                uMinute[0] = minute;
                                tvUpdateTime.setText(uHour[0] + ":" + uMinute[0]);
                            }
                        }, uHour[0], uMinute[0], false);
                timePickerDialog.show();
            }
        });

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
                Date date = new Date();
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy hh:mm")
                            .parse(uDay[0] + "/" + uMonth[0] + "/" + uYear[0] + " " + uHour[0] + ":" + uMinute[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Reading newReading = new Reading(reading.getId(), userID, date, systolic, diastolic);
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
}

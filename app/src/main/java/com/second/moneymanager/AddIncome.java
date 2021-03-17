package com.second.moneymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

import static com.second.moneymanager.AndroidBug5497Workaround.assistActivity;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SHORT;

public class AddIncome extends AppCompatActivity {

    RecyclerView recyclerView;

    //Firebase..
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseRecyclerOptions<Data> options;
    FirebaseRecyclerAdapter<Data,MyRecyclerViewHolder> adapter;

    EditText etAmount, etNotes, etCategory;
    Button btnAdd, btnCancel, btnDate;
    private Calendar myCalendar = Calendar.getInstance();
    private int day, month, year;
    SharedPreferences prefs = null;
    public static final int requestCodeForIncomeCategories = 1;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        assistActivity(AddIncome.this);


        btnDate = findViewById(R.id.btnDate);
        btnAdd = findViewById(R.id.btnAdd);
        etCategory = findViewById(R.id.etCategory);
        btnCancel = findViewById(R.id.btnCancel);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);

        day = myCalendar.get(Calendar.DAY_OF_MONTH);
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);

        prefs = getSharedPreferences("com.mycompany.MoneyManager", MainActivity.MODE_PRIVATE);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                AddIncome.this.finish();
            }
        });
        btnDate.setText(year + "-" + myCalendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + "-" + day);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog();
            }

            void DateDialog() {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(MONTH, monthOfYear);
                        btnDate.setText(year + "-" + myCalendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + "-" + dayOfMonth);
                    }
                };

                DatePickerDialog dpDialog = new DatePickerDialog(AddIncome.this, listener, year, month, day);
                dpDialog.show();
            }
        });

        etAmount.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etAmount.setHint("");
                return false;
            }

        });

        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    etAmount.setHint("Amount");
                }
            }
        });


        etNotes.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etNotes.setHint("");
                return false;
            }

        });

        etNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    etNotes.setHint("Notes");
                }
            }
        });

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddIncome.this,
                        com.second.moneymanager.Categories.class);
                startActivityForResult(intent, requestCodeForIncomeCategories);

                FirebaseDatabase.getInstance().getReference();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference =firebaseDatabase.getReference("Income");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCom();
                displayComment();
            }

            private void AddCom() {
                String type = etCategory.getText().toString();
                String note = etNotes.getText().toString();
                int amount = etAmount.getTextAlignment();
                long date = btnDate.getBottom();


                Data data = new Data(type,amount,note,date);

                databaseReference.push() //Use this method to create unique id of comment
                        .setValue(data);

                adapter.notifyDataSetChanged();

            }

            private void displayComment() {
                options =
                        new FirebaseRecyclerOptions.Builder<Data>()
                                .setQuery(databaseReference,Data.class)
                                .build();

                adapter=
                        new FirebaseRecyclerAdapter<Data, MyRecyclerViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position, @NonNull Data model) {
                                holder.etCategoryExpense.setText(model.getType());
                                holder.etPrice.setText(model.getAmount());
                                holder.etNotesExpense.setText(model.getNote());

                            }

                            @NonNull
                            @Override
                            public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                                View itemview = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_add_expense,parent,false);
                                return new MyRecyclerViewHolder(itemview);
                            }
                        };

                adapter.startListening();
                recyclerView.setAdapter(adapter);



                if (btnDate.getText().toString().isEmpty()) {
                    Toast.makeText(AddIncome.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                } else if (etAmount.getText().toString().isEmpty()) {
                    Toast.makeText(AddIncome.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                } else if (etNotes.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddIncome.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                } else if (etCategory.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddIncome.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                } else {
                    if (etAmount.getText().toString().trim().charAt(0) == '.') {
                        etAmount.setText("0" + etAmount.getText().toString().trim());
                    }
                    String notes = etNotes.getText().toString().trim();
                    double sum = Double.parseDouble(etAmount.getText().toString().trim());
                    String category = etCategory.getText().toString().trim();
                    String dateFromInput = btnDate.getText().toString().trim();

                    StringTokenizer tokens = new StringTokenizer(dateFromInput, "-");
                    int yearFromButton = Integer.parseInt(tokens.nextToken());
                    String monthFromButton = tokens.nextToken();
                    int dayFromButton = Integer.parseInt(tokens.nextToken());

                    try {
                        ExpensesDB db = new ExpensesDB(AddIncome.this);
                        db.open();
                        db.createEntryIncome(sum, dayFromButton, myCalendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), yearFromButton, category, notes);
                        MyApplication app = (MyApplication) AddIncome.this.getApplication();
                        app.addIncomeToItems(new Income(sum, dayFromButton, myCalendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), yearFromButton, null, category, notes));
                        db.close();
                        Toast.makeText(AddIncome.this, "Succesfully saved", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Toast.makeText(AddIncome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    AddIncome.this.finish();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeForIncomeCategories) {
            if (resultCode == RESULT_OK) {
                if (prefs.getBoolean("addedNoNewIncomeCategory", true)) {

                    etCategory.setText(data.getStringExtra("categoryIncome"));
                } else {

                    etCategory.setText(data.getStringExtra("IncomeOrExpense"));
                }
            }
        }
    }
}
